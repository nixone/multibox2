package sk.hackcraft.multibox2.net.data;

import sk.hackcraft.multibox2.model.LibraryItem;

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
