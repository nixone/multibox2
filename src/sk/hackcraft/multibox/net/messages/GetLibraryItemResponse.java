package sk.hackcraft.multibox.net.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import sk.hackcraft.multibox.model.LibraryItem;

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
