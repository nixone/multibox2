package sk.hackcraft.multibox2.model.host;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class Player implements OnCompletionListener
{
	static public final String TAG = Player.class.getName();
	
	enum State {
		PLAYING, PAUSED;
	}
	
	public interface Listener {
		public void onSongAdded(Player player, Song song);
		public void onSongFinished(Player player, Song song);
		public void onSongStarted(Player player, Song song);
	}
	
	private LinkedList<Song> songs = new LinkedList<Song>();
	
	private State desiredState = State.PLAYING;
	
	private Song playingSong = null;
	
	private MediaPlayer mediaPlayer = new MediaPlayer();
	
	private HashSet<Listener> listeners = new HashSet<Player.Listener>();
	
	public Player() {
		mediaPlayer.setOnCompletionListener(this);
	}
	
	public void addListener(Listener listener) {
		synchronized(listeners) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(Listener listener) {
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}
	
	public void add(Song song) {
		synchronized(this) {
			songs.add(song);
		}
		
		HashSet<Listener> listenersToInvoke = new HashSet<Player.Listener>();
		synchronized(listeners) {
			listenersToInvoke.addAll(listeners);
		}
		for(Listener listener : listenersToInvoke) {
			listener.onSongAdded(this, song);
		}
		
		if(desiredState == State.PLAYING) {
			ensurePlaying();
		}
	}
	
	public void pause() {
		desiredState = State.PAUSED;
		ensurePaused();
	}
	
	public void play() {
		desiredState = State.PLAYING;
		ensurePlaying();
	}
	
	private void ensurePaused() {
		if(mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}
	}
	
	private void ensurePlaying() {
		synchronized(this) {
			if(mediaPlayer.isPlaying()) {
				return;
			}
			if(playingSong != null && !mediaPlayer.isPlaying()) {
				mediaPlayer.start();
				return;
			}
	
			playingSong = null;
		
			while(true) {
				if(playingSong != null || songs.isEmpty()) {
					break;
				}
				playingSong = songs.getFirst();
				
				try {
					mediaPlayer.reset();
					mediaPlayer.setDataSource(playingSong.getPath());
					mediaPlayer.prepare();
					mediaPlayer.start();
				} catch(IOException e) {
					songs.removeFirst();
					playingSong = null;
					Log.d(TAG, "IOException while trying to play a song", e);
				}
			}
		}
		
		HashSet<Listener> listenersToInvoke = new HashSet<Player.Listener>();
		synchronized(listeners) {
			listenersToInvoke.addAll(listeners);
		}
		for(Listener listener : listenersToInvoke) {
			listener.onSongStarted(this, playingSong);
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp)
	{
		HashSet<Listener> listenersToInvoke = new HashSet<Player.Listener>();
		synchronized(listeners) {
			listenersToInvoke.addAll(listeners);
		}
		for(Listener listener : listenersToInvoke) {
			listener.onSongFinished(this, playingSong);
		}
		
		synchronized(this) {
			songs.removeFirst();
			playingSong = null;
			if(desiredState == State.PLAYING) {
				ensurePlaying();
			}
		}
	}
	
	public int getSize() {
		return songs.size();
	}
	
	public void destroy() {
		synchronized(this) {
			songs.clear();
			playingSong = null;
			desiredState = State.PAUSED;
			mediaPlayer.stop();
			mediaPlayer.release();
		}
	}
	
	public boolean isPlaying() {
		return playingSong != null;
	}
	
	public int getPlaybackPosition() {
		synchronized(this) {
			if(playingSong == null) {
				return 0;
			}
			return mediaPlayer.getCurrentPosition();
		}
	}
	
	public Song getPlayingSong() {
		return playingSong;
	}

	public Collection<Song> getSongs()
	{
		return new ArrayList<Song>(songs);
	}
}
