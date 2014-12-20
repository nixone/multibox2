package sk.hackcraft.multibox.net.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AddLibraryItemToPlaylistRequest
{
	@JsonProperty
	private long multimediaId;
	
	protected AddLibraryItemToPlaylistRequest() {
		// jackson
	}
	
	public AddLibraryItemToPlaylistRequest(long multimediaId)
	{
		this.multimediaId = multimediaId;
	}
	
	@JsonIgnore
	public long getMultimediaId() {
		return multimediaId;
	}
}
