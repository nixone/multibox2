package sk.hackcraft.multibox.android.host;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import sk.hackcraft.util.Eventable;
import sk.hackcraft.util.MessageQueue;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class Player
{
	public enum State {
		PLAYING, PAUSED;
	}
	
	private class Playback implements OnCompletionListener {
		private Song song;
		private MediaPlayer mediaPlayer;
		
		public Playback(Song song) throws IOException
		{
			this.song = song;
			this.mediaPlayer = new MediaPlayer();
			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.setDataSource(song.getPath());
			mediaPlayer.prepare();
			mediaPlayer.start();
		}
		
		@Override
		public void onCompletion(MediaPlayer mp)
		{
			messageQueue.post(new Runnable()
			{
				@Override
				public void run()
				{
					currentPlayback = null;
					if(desiredState == State.PLAYING)
					{
						ensurePlaying();
					}
				}
			});
		}
	}

	private LinkedList<Song> songs = new LinkedList<Song>();
	private volatile State desiredState = State.PLAYING;
	private volatile Playback currentPlayback = null;

	private Eventable<Player> changedEvent = new Eventable<Player>();
	
	private MessageQueue messageQueue = null;
	
	private ArrayList<Song> publicPlaylist = new ArrayList<Song>();
	
	public Player(MessageQueue messageQueue)
	{
		this.messageQueue = messageQueue;
	}
	
	public Eventable<Player> getChangedEvent()
	{
		return changedEvent;
	}
	
	public void add(final Song song) {
		messageQueue.post(new Runnable()
		{
			@Override
			public void run()
			{
				songs.add(song);
				refreshPublicPlaylist();
				
				changedEvent.invoke(Player.this);
				
				if(desiredState == State.PLAYING) {
					ensurePlaying();
				}
			}
		});
	}
	
	public void pause()
	{
		messageQueue.post(new Runnable()
		{
			@Override
			public void run()
			{
				desiredState = State.PAUSED;
				ensurePaused();
				changedEvent.invoke(Player.this);
			}
		});
	}

	public void play()
	{
		messageQueue.post(new Runnable()
		{
			@Override
			public void run()
			{
				desiredState = State.PLAYING;
				ensurePlaying();
				changedEvent.invoke(Player.this);
			}
		});
	}
	
	private void ensurePaused()
	{
		Playback playback = currentPlayback;
		
		if(playback != null)
		{
			playback.mediaPlayer.pause();
		}
	}
	
	private void ensurePlaying()
	{
		if(currentPlayback != null)
		{
			currentPlayback.mediaPlayer.start();
		}
		else
		{
			while(currentPlayback == null && !songs.isEmpty())
			{
				Song toPlay = songs.poll();
				
				try
				{
					currentPlayback = new Playback(toPlay);
				}
				catch(IOException e)
				{
					// nothing, continue in cycle
				}
			}
			
			refreshPublicPlaylist();
		}
	}
	
	public void destroy()
	{
		messageQueue.post(new Runnable()
		{
			@Override
			public void run()
			{
				if(currentPlayback != null)
				{
					currentPlayback.mediaPlayer.release();
					currentPlayback = null;
				}
				
				songs.clear();
				refreshPublicPlaylist();
			}
		});
	}
	
	public boolean isPlaying()
	{
		Playback playback = currentPlayback;
		
		if(playback == null)
		{
			return false;
		}
		
		return playback.mediaPlayer.isPlaying();
	}
	
	public int getPlaybackPosition()
	{
		Playback playback = currentPlayback;
		
		if(playback == null)
		{
			return 0;
		}
		
		return playback.mediaPlayer.getCurrentPosition();
	}
	
	public List<Song> getPlaylist()
	{
		ArrayList<Song> result = new ArrayList<Song>();
		
		synchronized(publicPlaylist)
		{
			result.addAll(publicPlaylist);
		}
		
		return result;
	}
	
	public Song getPlayingSong()
	{
		Playback playback = currentPlayback;
		
		if(playback == null)
		{
			return null;
		}
		
		return playback.song;
	}
	
	private void refreshPublicPlaylist()
	{
		synchronized(publicPlaylist)
		{
			publicPlaylist.clear();
			publicPlaylist.addAll(songs);
		}
	}
	
	public State getDesiredState()
	{
		return desiredState;
	}
}
