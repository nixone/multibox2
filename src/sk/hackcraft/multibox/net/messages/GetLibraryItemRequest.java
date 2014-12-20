package sk.hackcraft.multibox.net.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GetLibraryItemRequest
{
	@JsonProperty
	private long itemId = 0L;
	
	protected GetLibraryItemRequest() {
		// jackson
	}
	
	public GetLibraryItemRequest(long itemId) {
		this.itemId = itemId;
	}
	
	@JsonIgnore
	public long getItemId() {
		return itemId;
	}
}
