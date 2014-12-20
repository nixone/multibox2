package sk.hackcraft.multibox.net.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import sk.hackcraft.multibox.model.libraryitems.MultimediaItem;

public class AddLibraryItemToPlaylistResponse
{
	@JsonProperty
	private boolean result;
	
	@JsonProperty
	private MultimediaItem multimedia;
	
	protected AddLibraryItemToPlaylistResponse() {
		// empty for jackson
	}
	
	public AddLibraryItemToPlaylistResponse(boolean result, MultimediaItem multimedia) {
		this.result = result;
		this.multimedia = multimedia;
	}
	
	@JsonIgnore
	public boolean isSuccess() {
		return result;
	}
	
	@JsonIgnore
	public MultimediaItem getMultimedia() {
		return multimedia;
	}
}
