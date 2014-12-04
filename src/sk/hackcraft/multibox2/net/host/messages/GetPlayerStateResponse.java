package sk.hackcraft.multibox2.net.host.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;

public class GetPlayerStateResponse
{
	@JsonProperty
	private MultimediaItem multimedia;
	
	@JsonProperty
	private int playbackPosition;
	
	@JsonProperty
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
