package sk.hackcraft.multibox2.net.host.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;

public class GetPlaylistResponse
{
	@JsonProperty
	private List<MultimediaItem> playlist;
	
	protected GetPlaylistResponse() {
		// empty for jackson
	}
	
	public GetPlaylistResponse(Collection<? extends MultimediaItem> playlist) {
		this.playlist = new ArrayList<MultimediaItem>(playlist);
	}
	
	@JsonIgnore
	public List<MultimediaItem> getPlaylist() {
		return new ArrayList<MultimediaItem>(playlist);
	}
}
