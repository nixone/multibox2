package sk.hackcraft.multibox.model;

import java.util.List;

import sk.hackcraft.multibox.model.libraryitems.MultimediaItem;

public interface Playlist
{
	public void init();
	public void close();
	
	public void registerListener(PlaylistEventListener listener);
	public void unregisterListener(PlaylistEventListener listener);
	
	public List<MultimediaItem> getItems();
	
	public void addItem(long itemId);
	
	public interface PlaylistEventListener {
		public void onPlaylistChanged(List<MultimediaItem> newPlaylist);
		public void onItemAdded(boolean success, MultimediaItem multimedia);
	}
}
