package sk.hackcraft.multibox.android.host;

import java.util.HashMap;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import sk.hackcraft.multibox.android.host.LibraryView.NewItemListener;
import sk.hackcraft.multibox.model.libraryitems.DirectoryItem;
import sk.hackcraft.util.KeyGenerator;

public class Artist extends DirectoryItem
{
	private HashMap<String, Album> albums = new HashMap<String, Album>();
	
	public Artist(long id, String name)
	{
		super(id, name);
	}
	
	protected Album getOrCreateAlbum(KeyGenerator<Long> idGenerator, NewItemListener newItemListener, String albumName) {
		if(!albums.containsKey(albumName)) {
			Album album = new Album(idGenerator.generateKey(), albumName);
			newItemListener.onNewItem(album);
			addItem(album);
			albums.put(albumName, album);
		}
		return albums.get(albumName);
	}
}
