package sk.hackcraft.multibox2.net.host.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GetLibraryItemRequest
{
	@JsonProperty
	private long itemId;
	
	@JsonIgnore
	public long getItemId() {
		return itemId;
	}
}