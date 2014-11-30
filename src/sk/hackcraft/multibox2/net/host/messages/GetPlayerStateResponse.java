package sk.hackcraft.multibox2.net.host.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;

import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;

public class GetPlayerStateResponse
{
	private MultimediaItem multimedia;
	private int playbackPosition;
	private boolean playing;
	
	public GetPlayerStateResponse(MultimediaItem multimedia, int playbackPosition, boolean playing) {
		this.multimedia = multimedia;
		this.playbackPosition = playbackPosition;
		this.playing = playing;
	}
	
	@JsonIgnore
	public MultimediaItem getMultimedia() {
		return multimedia;
	}
	
	@JsonIgnore
	public int getPlaybackPosition() {
		return playbackPosition;
	}
	
	@JsonIgnore
	public boolean isPlaying() {
		return playing;
	}
}
