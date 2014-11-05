package sk.hackcraft.multibox.net.data;

import sk.hackcraft.multibox.model.LibraryItem;

public class GetLibraryItemData
{
	private final LibraryItem libraryItem;
	
	public GetLibraryItemData(LibraryItem libraryItem)
	{
		this.libraryItem = libraryItem;
	}
	
	public LibraryItem getLibraryItem()
	{
		return libraryItem;
	}
}
