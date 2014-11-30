package sk.hackcraft.multibox2.net.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;

public class GetPlaylistResultData
{
	private final List<MultimediaItem> playlist;
	
	public GetPlaylistResultData(List<MultimediaItem> playlist)
	{
		this.playlist = Collections.unmodifiableList(new LinkedList<MultimediaItem>(playlist));
	}
	
	public List<MultimediaItem> getPlaylist()
	{
		return playlist;
	}
}
