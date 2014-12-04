package sk.hackcraft.multibox2.net.host.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AddLibraryItemToPlaylistRequest
{
	@JsonProperty
	private long multimediaId;
	
	@JsonIgnore
	public long getMultimediaId() {
		return multimediaId;
	}
}
