package sk.hackcraft.multibox2.net.host.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import sk.hackcraft.multibox2.model.LibraryItem;

public class GetLibraryItemResponse
{
	@JsonProperty
	private LibraryItem libraryItem;
	
	protected GetLibraryItemResponse() {
		// empty for jackson
	}
	
	public GetLibraryItemResponse(LibraryItem libraryItem) {
		this.libraryItem = libraryItem;
	}
	
	@JsonIgnore
	public LibraryItem getLibraryItem() {
		return libraryItem;
	}
}
