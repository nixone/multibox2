package sk.hackcraft.multibox.net;

import java.util.List;

import sk.hackcraft.multibox.model.LibraryItem;
import sk.hackcraft.multibox.model.libraryitems.MultimediaItem;

public interface ServerInterface
{
	public void close();
	
	public void registerEventListener(ServerInterfaceEventListener listener);
	public void unregisterEventListener(ServerInterfaceEventListener listener);
	
	public void requestServerInfo();
	
	public void requestPlayerUpdate();
	public void requestPlaylistUpdate();
	
	public void requestLibraryItem(long itemId);
	public void addLibraryItemToPlaylist(long itemId);
	
	public interface ServerInterfaceEventListener
	{
		public void onDisconnect();
		
		public void onServerInfoReceived(String serverName);
		
		public void onPlayerUpdateReceived(MultimediaItem multimedia, int playbackPosition, boolean playing);
		public void onPlaylistReceived(List<MultimediaItem> playlist);
		
		public void onLibraryItemReceived(LibraryItem item);
		
		public void onAddingLibraryItemToPlaylistResult(boolean result, MultimediaItem multimedia);
	}
	
	public class ServerInterfaceEventAdapter implements ServerInterfaceEventListener
	{
		@Override
		public void onDisconnect()
		{
		}
		
		@Override
		public void onServerInfoReceived(String serverName)
		{
		}

		@Override
		public void onPlayerUpdateReceived(MultimediaItem multimedia, int playbackPosition, boolean playing)
		{
		}

		@Override
		public void onPlaylistReceived(List<MultimediaItem> playlist)
		{
		}

		@Override
		public void onLibraryItemReceived(LibraryItem item)
		{
		}

		@Override
		public void onAddingLibraryItemToPlaylistResult(boolean result, MultimediaItem multimedia)
		{
		}
	}
}
