package sk.hackcraft.multibox.model.host;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import sk.hackcraft.multibox.model.LibraryItem;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

public class LibraryView
{
	private HashMap<Long, Song> songsById = new HashMap<Long, Song>();
	private HashMap<String, Song> songsByPath = new HashMap<String, Song>();
	private HashSet<Song> songs = new HashSet<Song>();
	
	public void refresh(ContentResolver contentResolver) {
		HashSet<Song> songs = new HashSet<Song>();

		Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
		if(cursor!=null && cursor.moveToFirst()) {
			int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
			int artistColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
			int pathColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA);
			int albumColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM);
			int durationColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);
			
			do {
				String artistName = cursor.getString(artistColumn);
				String albumName = cursor.getString(albumColumn);
				String title = cursor.getString(titleColumn);
				
				long id = cursor.getLong(idColumn);
				String path = cursor.getString(pathColumn);
				
				long duration = cursor.getLong(durationColumn);
				int length = (int)Math.round(duration / (double)1000);
				
				Song song = new Song(id, artistName, albumName, title, length, path);
				songs.add(song);
			} while(cursor.moveToNext());
		}
		cursor.close();
		
		synchronized(this) {
			this.songs.clear();
			songsById.clear();
			songsByPath.clear();
			
			this.songs.addAll(songs);
			
			for(Song song : songs) {
				songsById.put(song.getId(), song);
				songsByPath.put(song.getPath(), song);
			}
		}
	}
	
	public Collection<LibraryItem> getLibraryItems() {
		synchronized(this) {
			return new HashSet<LibraryItem>(songs);
		}
	}
	
	public LibraryItem getItem(long id) {
		synchronized(this) {
			return songsById.get(id);
		}
	}
	
	public Song getSong(long id) {
		synchronized(this) {
			return songsById.get(id);
		}
	}
}
