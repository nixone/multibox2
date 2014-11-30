package sk.hackcraft.multibox2.net.host.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AddLibraryItemToPlaylistRequest
{
	protected long multimediaId;
	
	@JsonIgnore
	public long getMultimediaId() {
		return multimediaId;
	}
}
