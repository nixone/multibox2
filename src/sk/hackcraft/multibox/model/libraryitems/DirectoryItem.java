package sk.hackcraft.multibox.model.libraryitems;

import java.util.LinkedList;
import java.util.List;

import sk.hackcraft.multibox.model.GenericLibraryItem;
import sk.hackcraft.multibox.model.LibraryItem;
import sk.hackcraft.multibox.model.LibraryItemType;

public class DirectoryItem extends GenericLibraryItem
{
	private List<LibraryItem> content;
	
	public DirectoryItem(long id, String name)
	{
		super(id, LibraryItemType.DIRECTORY, name);
		
		this.content = new LinkedList<LibraryItem>();
	}
	
	public void addItem(LibraryItem item)
	{
		content.add(item);
	}
	
	public List<LibraryItem> getItems()
	{
		return new LinkedList<LibraryItem>(content);
	}
}
