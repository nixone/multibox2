package sk.hackcraft.multibox2.android.client;

import android.os.Bundle;
import sk.hackcraft.multibox2.model.Library;
import sk.hackcraft.multibox2.model.Playlist;
import sk.hackcraft.multibox2.model.Server;

public class ServerLibraryFragment extends LibraryFragment
{
	private Library library;
	private Playlist playlist;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		MultiBoxApplication app = (MultiBoxApplication) getActivity().getApplication();
		Server server = app.getServer();

		library = server.getLibrary();
		playlist = server.getPlaylist();

		super.onCreate(savedInstanceState);
	}

	@Override
	public Library getLibrary()
	{
		return library;
	}

	@Override
	public Playlist getPlaylist()
	{
		return playlist;
	}
}
