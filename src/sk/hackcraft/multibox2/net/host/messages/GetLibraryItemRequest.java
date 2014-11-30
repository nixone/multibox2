package sk.hackcraft.multibox2.net.host.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GetLibraryItemRequest
{
	protected long itemId;
	
	@JsonIgnore
	public long getItemId() {
		return itemId;
	}
}
