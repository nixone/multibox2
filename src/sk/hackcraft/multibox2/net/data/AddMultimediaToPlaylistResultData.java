package sk.hackcraft.multibox2.net.data;

import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;

public class AddMultimediaToPlaylistResultData
{
	private final boolean success;
	private final MultimediaItem multimedia;
	
	public AddMultimediaToPlaylistResultData(boolean success, MultimediaItem multimedia)
	{
		this.success = success;
		this.multimedia = multimedia;
	}
	
	public boolean isSuccess()
	{
		return success;
	}
	
	public MultimediaItem getMultimedia()
	{
		return multimedia;
	}
}
