package sk.hackcraft.multibox2.net.host.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;

import sk.hackcraft.multibox2.model.LibraryItem;

public class GetLibraryItemResponse
{
	protected LibraryItem libraryItem;
	
	public GetLibraryItemResponse(LibraryItem libraryItem) {
		this.libraryItem = libraryItem;
	}
	
	@JsonIgnore
	public LibraryItem getLibraryItem() {
		return libraryItem;
	}
}
