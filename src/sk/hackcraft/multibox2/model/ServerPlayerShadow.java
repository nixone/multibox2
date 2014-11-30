package sk.hackcraft.multibox2.model;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;
import sk.hackcraft.multibox2.net.ServerInterface;
import sk.hackcraft.multibox2.net.ServerInterface.ServerInterfaceEventAdapter;
import sk.hackcraft.util.Log;
import sk.hackcraft.util.MessageQueue;
import sk.hackcraft.util.PeriodicWorkDispatcher;

public class ServerPlayerShadow implements Player
{
	private final ServerInterface serverInterface;
	private final MessageQueue messageQueue;
	private final Log log;
	
	private ServerListener serverListener;
	
	private long lastUpdateTimestamp;
	
	private boolean playing;
	private MultimediaItem activeMultimedia;
	private int playbackPosition;
	
	private final List<PlayerEventListener> playerListeners;
	
	private final PeriodicWorkDispatcher stateChecker;
	
	public ServerPlayerShadow(ServerInterface serverInterface, MessageQueue messageQueue, Log log)
	{
		this.serverInterface = serverInterface;
		this.messageQueue = messageQueue;
		this.log = log;
		
		this.lastUpdateTimestamp = System.currentTimeMillis();
		
		this.playing = false;
		this.activeMultimedia = null;
		this.playbackPosition = 0;
		
		this.playerListeners = new LinkedList<Player.PlayerEventListener>();
		
		this.serverListener = new ServerListener();
		
		stateChecker = new PeriodicWorkDispatcher(messageQueue, 5000)
		{
			@Override
			protected void doWork()
			{
				ServerPlayerShadow.this.serverInterface.requestPlayerUpdate();
			}
		};
	}
	
	@Override
	public void init()
	{
		serverInterface.registerEventListener(serverListener);
		serverInterface.requestPlayerUpdate();
		
		stateChecker.start();
	}

	@Override
	public void close()
	{
		serverInterface.unregisterEventListener(serverListener);
		
		stateChecker.stop();
	}

	@Override
	public boolean isPlaying()
	{
		return playing;
	}

	@Override
	public boolean hasActiveMultimedia()
	{
		return activeMultimedia != null;
	}

	@Override
	public int getPlaybackPosition()
	{
		int offset = (int)TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastUpdateTimestamp);
		return playbackPosition + offset;
	}

	@Override
	public MultimediaItem getActiveMultimedia()
	{
		return activeMultimedia;
	}

	@Override
	public void requestPlayingStateChange(boolean playing)
	{
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public void requestActiveMultimediaSkip()
	{
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public void registerPlayerEventListener(PlayerEventListener listener)
	{
		playerListeners.add(listener);
	}

	@Override
	public void unregisterListener(PlayerEventListener listener)
	{
		playerListeners.remove(listener);
	}
	
	private class ServerListener extends ServerInterfaceEventAdapter
	{
		@Override
		public void onPlayerUpdateReceived(final MultimediaItem multimedia, final int playbackPosition, final boolean playing)
		{
			if (!isStateValid(multimedia, playbackPosition, playing))
			{
				log.printf("Received invalid player state: %s %d %b", multimedia, playbackPosition, playing);
				return;
			}
			
			ServerPlayerShadow player = ServerPlayerShadow.this;
			
			lastUpdateTimestamp = System.currentTimeMillis();
			
			player.activeMultimedia = multimedia;
			player.playing = playing;
			player.playbackPosition = playbackPosition;
			
			messageQueue.post(new Runnable()
			{
				@Override
				public void run()
				{
					for (final Player.PlayerEventListener listener : playerListeners)
					{
						listener.update(multimedia, playbackPosition, playing);
					}
				}
			});
		}
		
		private boolean isStateValid(MultimediaItem multimedia, int playbackPosition, boolean playing)
		{
			if (multimedia == null && (playbackPosition != 0 || playing))
			{
				return false;
			}
			
			return true;
		}
	}
}
