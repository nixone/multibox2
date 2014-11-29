package sk.hackcraft.multibox.net.data;

import sk.hackcraft.multibox.model.libraryitems.MultimediaItem;

public class GetPlayerStateResultData
{
	private final MultimediaItem multimedia;
	private final int playbackPosition;
	private final boolean playing;
	
	public GetPlayerStateResultData(MultimediaItem multimedia, int playbackPosition, boolean playing)
	{
		this.multimedia = multimedia;
		this.playbackPosition = playbackPosition;
		this.playing = playing;
	}
	
	public MultimediaItem getMultimedia()
	{
		return multimedia;
	}

	public int getPlaybackPosition()
	{
		return playbackPosition;
	}

	public boolean isPlaying()
	{
		return playing;
	}
}
