package sk.hackcraft.multibox2.android.host;

import java.util.HashMap;
import java.util.HashSet;

import sk.hackcraft.multibox2.model.Library;
import sk.hackcraft.multibox2.model.LibraryItem;
import sk.hackcraft.multibox2.model.libraryitems.DirectoryItem;
import sk.hackcraft.util.KeyGenerator;
import sk.hackcraft.util.UniqueLongKeyGenerator;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

public class LibraryView implements Library
{
	static public interface NewItemListener
	{
		public void onNewItem(LibraryItem item);
	}

	private NewItemListener newItemListener = new NewItemListener()
	{
		@Override
		public void onNewItem(LibraryItem item)
		{
			items.put(item.getId(), item);
		}
	};

	private KeyGenerator<Long> idGenerator = new UniqueLongKeyGenerator(Library.ROOT_DIRECTORY+1);

	private HashMap<Long, LibraryItem> items = new HashMap<Long, LibraryItem>();
	private HashMap<String, Artist> artists = new HashMap<String, Artist>();

	private DirectoryItem rootItem = new DirectoryItem(Library.ROOT_DIRECTORY, "Library");
	
	private HashSet<LibraryEventListener> listeners = new HashSet<Library.LibraryEventListener>();

	public LibraryView()
	{
		newItemListener.onNewItem(rootItem);
	}

	public void load(ContentResolver contentResolver)
	{
		HashSet<Song> songs = new HashSet<Song>();

		Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
		if (cursor != null && cursor.moveToFirst())
		{
			int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			int artistColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
			int pathColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA);
			int albumColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM);
			int durationColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);

			do
			{
				String artistName = cursor.getString(artistColumn);
				String albumName = cursor.getString(albumColumn);
				String title = cursor.getString(titleColumn);

				String path = cursor.getString(pathColumn);

				long duration = cursor.getLong(durationColumn);
				int length = (int) Math.round(duration / (double) 1000);

				Song song = new Song(idGenerator.generateKey(), artistName, albumName, title, length, path);
				songs.add(song);
			} while (cursor.moveToNext());
		}
		cursor.close();

		synchronized (this)
		{
			for (Song song : songs)
			{
				Artist artist = getOrCreateArtist(song.getArtistName());
				Album album = artist.getOrCreateAlbum(idGenerator, newItemListener, song.getAlbumName());
				album.addItem(song);

				newItemListener.onNewItem(song);
			}
		}
	}

	private Artist getOrCreateArtist(String name)
	{
		if (!artists.containsKey(name))
		{
			Artist artist = new Artist(idGenerator.generateKey(), name);
			newItemListener.onNewItem(artist);
			rootItem.addItem(artist);
			artists.put(name, artist);
		}
		return artists.get(name);
	}

	public LibraryItem getItem(long id)
	{
		synchronized (this)
		{
			return items.get(id);
		}
	}

	@Override
	public void init()
	{
		// nothing
	}

	@Override
	public void close()
	{
		// nothing
	}

	@Override
	public void requestItem(long id)
	{
		LibraryItem item = getItem(id);
		
		HashSet<LibraryEventListener> listenersToInvoke = new HashSet<Library.LibraryEventListener>();
		synchronized(listeners) {
			listenersToInvoke.addAll(listeners);
		}
		for(LibraryEventListener listener : listenersToInvoke) {
			listener.onItemReceived(item);
		}
	}

	@Override
	public void registerLibraryEventListener(LibraryEventListener listener)
	{
		synchronized(listeners) {
			listeners.add(listener);
		}
	}

	@Override
	public void unregisterLibraryEventListener(LibraryEventListener listener)
	{
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}
}
