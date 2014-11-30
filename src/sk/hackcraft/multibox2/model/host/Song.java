package sk.hackcraft.multibox2.model.host;

import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;

public class Song extends MultimediaItem
{
	private String artistName;
	private String albumName;
	private String title;
	
	private String path;
	
	public Song(long id, String artistName, String albumName, String title, int length, String path)
	{
		super(id, artistName+" - "+albumName+" - "+title, length);
		this.path = path;
		this.artistName = artistName;
		this.albumName = albumName;
		this.title = title;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public String getArtistName() {
		return artistName;
	}
	
	public String getAlbumName() {
		return albumName;
	}
	
	public String getTitle() {
		return title;
	}
}
