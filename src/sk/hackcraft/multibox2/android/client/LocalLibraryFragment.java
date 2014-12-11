package sk.hackcraft.multibox2.android.client;

import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import sk.hackcraft.multibox2.android.host.LibraryView;
import sk.hackcraft.multibox2.model.Library;
import sk.hackcraft.multibox2.model.Playlist;
import sk.hackcraft.multibox2.model.Server;
import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;

public class LocalLibraryFragment extends LibraryFragment
{
	private class LocalPlaylistShadow implements Playlist {

		@Override
		public void init()
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void close()
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void registerListener(PlaylistEventListener listener)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unregisterListener(PlaylistEventListener listener)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public List<MultimediaItem> getItems()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void addItem(long itemId)
		{
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private LibraryView library = new LibraryView();
	private Playlist serverPlaylist = null;
	private Playlist localPlaylistShadow = null;
	private Server server = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Server server = ((MultiBoxApplication)getActivity().getApplication()).getServer();
		serverPlaylist = server.getPlaylist();
		localPlaylistShadow = new LocalPlaylistShadow();
		
		library.load(getActivity().getContentResolver());
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public Library getLibrary()
	{
		return library;
	}
	
	@Override
	public Playlist getPlaylist() {
		return localPlaylistShadow;
	}
}
