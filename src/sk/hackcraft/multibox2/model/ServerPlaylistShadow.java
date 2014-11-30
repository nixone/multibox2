package sk.hackcraft.multibox2.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;
import sk.hackcraft.multibox2.net.ServerInterface;
import sk.hackcraft.multibox2.net.ServerInterface.ServerInterfaceEventAdapter;
import sk.hackcraft.util.MessageQueue;
import sk.hackcraft.util.PeriodicWorkDispatcher;

public class ServerPlaylistShadow implements Playlist
{
	private final ServerInterface serverInterface;
	private final MessageQueue messageQueue;
	
	private List<MultimediaItem> actualPlaylist;
	
	private final List<Playlist.PlaylistEventListener> playlistListeners;
	
	private final ServerListener serverListener;
	
	private final PeriodicWorkDispatcher stateChecker;
	
	public ServerPlaylistShadow(ServerInterface serverInterface, MessageQueue messageQueue)
	{
		this.serverInterface = serverInterface;
		this.messageQueue = messageQueue;
		
		playlistListeners = new LinkedList<Playlist.PlaylistEventListener>();
		
		serverListener = new ServerListener();
		
		actualPlaylist = new LinkedList<MultimediaItem>();
		
		stateChecker = new PeriodicWorkDispatcher(messageQueue, 5000)
		{
			@Override
			protected void doWork()
			{
				ServerPlaylistShadow.this.serverInterface.requestPlaylistUpdate();
			}
		};
	}
	
	@Override
	public void init()
	{
		serverInterface.registerEventListener(serverListener);
		serverInterface.requestPlaylistUpdate();
		
		stateChecker.start();
	}

	@Override
	public void close()
	{
		serverInterface.unregisterEventListener(serverListener);
		
		stateChecker.stop();
	}
	
	@Override
	public void registerListener(PlaylistEventListener listener)
	{
		playlistListeners.add(listener);
	}
	
	@Override
	public void unregisterListener(PlaylistEventListener listener)
	{
		playlistListeners.remove(listener);
	}

	@Override
	public List<MultimediaItem> getItems()
	{
		return Collections.unmodifiableList(actualPlaylist);
	}
	
	@Override
	public void addItem(long itemId)
	{
		serverInterface.addLibraryItemToPlaylist(itemId);
	}
	
	private void updatePlaylist(List<MultimediaItem> playlist)
	{
		actualPlaylist.clear();
		actualPlaylist.addAll(playlist);
		
		final List<MultimediaItem> playlistCopy = new LinkedList<MultimediaItem>(actualPlaylist);
		
		for (final Playlist.PlaylistEventListener listener : playlistListeners)
		{
			messageQueue.post(new Runnable()
			{
				@Override
				public void run()
				{
					listener.onPlaylistChanged(playlistCopy);
				}
			});
		}
	}
	
	private class ServerListener extends ServerInterfaceEventAdapter
	{
		@Override
		public void onPlaylistReceived(List<MultimediaItem> playlist)
		{
			updatePlaylist(playlist);
		}
		
		@Override
		public void onAddingLibraryItemToPlaylistResult(final boolean result, final MultimediaItem multimedia)
		{
			if (!result)
			{
				return;
			}
			
			if (actualPlaylist.isEmpty())
			{
				return;
			}
			
			List<MultimediaItem> updatedPlaylist = new LinkedList<MultimediaItem>(actualPlaylist);
			updatedPlaylist.add(multimedia);
			
			updatePlaylist(updatedPlaylist);
			
			for (final Playlist.PlaylistEventListener listener : playlistListeners)
			{
				messageQueue.post(new Runnable()
				{
					@Override
					public void run()
					{
						listener.onItemAdded(result, multimedia);
					}
				});
			}
		}
	}
}
