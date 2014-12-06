package sk.hackcraft.multibox2.model.libraryitems;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import sk.hackcraft.multibox2.model.GenericLibraryItem;
import sk.hackcraft.multibox2.model.LibraryItem;
import sk.hackcraft.multibox2.model.LibraryItemType;
import sk.hackcraft.multibox2.net.serializers.LibraryItemDeserializer;

@JsonDeserialize(using = LibraryItemDeserializer.class)
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
		PriorityQueue<LibraryItem> queue = new PriorityQueue<LibraryItem>(1, LibraryItem.COMPARE_BY_NAME);
		queue.addAll(content);
		return new ArrayList<LibraryItem>(queue);
	}
}
