package sk.hackcraft.multibox2.model.libraryitems;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import sk.hackcraft.multibox2.model.GenericLibraryItem;
import sk.hackcraft.multibox2.model.LibraryItem;
import sk.hackcraft.multibox2.model.LibraryItemType;
import sk.hackcraft.multibox2.net.serializers.LibraryItemDeserializer;

@JsonDeserialize(using = LibraryItemDeserializer.class)
public class DirectoryItem extends GenericLibraryItem
{
	@JsonProperty
	private List<LibraryItem> items = new LinkedList<LibraryItem>();
	
	protected DirectoryItem() {
		// for jackson
	}
	
	public DirectoryItem(long id, String name)
	{
		super(id, LibraryItemType.DIRECTORY, name);
	}
	
	@JsonIgnore
	public void addItem(LibraryItem item)
	{
		items.add(item);
	}
	
	@JsonIgnore
	public List<LibraryItem> getEnclosedItems()
	{
		PriorityQueue<LibraryItem> queue = new PriorityQueue<LibraryItem>(1, LibraryItem.COMPARE_BY_NAME);
		queue.addAll(items);
		return new ArrayList<LibraryItem>(queue);
	}
}
