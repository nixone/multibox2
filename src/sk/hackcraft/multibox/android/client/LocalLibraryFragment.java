package sk.hackcraft.multibox.android.client;

import java.io.File;
import java.io.IOException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import sk.hackcraft.multibox.android.client.util.FileUtil;
import sk.hackcraft.multibox.android.host.LibraryView;
import sk.hackcraft.multibox.android.host.Song;
import sk.hackcraft.multibox.model.Library;
import sk.hackcraft.multibox.model.Playlist;
import sk.hackcraft.multibox.model.Server;
import sk.hackcraft.multibox.model.Server.MultimediaUploadedListener;

public class LocalLibraryFragment extends LibraryFragment implements MultimediaUploadedListener 
{
	static private final String TAG = LocalLibraryFragment.class.getName();
	
	private LibraryView library;
	private Playlist playlist = null;
	private Server server = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		server = ((MultiBoxApplication)getActivity().getApplication()).getServer();
		playlist = server.getPlaylist();
		
		library = new LibraryView(getActivity().getContentResolver());
		library.load();
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public Library getLibrary()
	{
		return library;
	}

	@Override
	public void onMultimediaItemSelected(long localMultimediaId)
	{
		Song song = (Song)library.getItem(localMultimediaId);
		final File songFile = new File(song.getPath());
		
		// read the file
		(new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... params)
			{
				try {
					byte[] multimediaData = FileUtil.readFile(songFile);
					
					// send it for upload
					server.uploadMultimedia(multimediaData, LocalLibraryFragment.this);
				} catch(IOException e) {
					e.printStackTrace();
				}
				
				return null;
			}
			
		}).execute();
	}

	@Override
	public void onMultimediaItemUploaded(long multimediaId)
	{
		playlist.addItem(multimediaId);
	}
}
