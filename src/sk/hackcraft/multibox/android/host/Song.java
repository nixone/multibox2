package sk.hackcraft.multibox.android.host;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import sk.hackcraft.multibox.model.libraryitems.MultimediaItem;

public class Song extends MultimediaItem
{
	private String title;
	private String path;
	private String albumName;
	private String artistName;
	
	public Song(long id, String artistName, String albumName, String title, int length, String path)
	{
		super(id, title, length);
		this.path = path;
		this.title = title;
		this.artistName = artistName;
		this.albumName = albumName;
	}
	
	public String getArtistName() {
		return artistName;
	}
	
	public String getAlbumName() {
		return albumName;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public String getTitle() {
		return title;
	}
}
