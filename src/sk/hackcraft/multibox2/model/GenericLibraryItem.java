package sk.hackcraft.multibox2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GenericLibraryItem implements LibraryItem
{
	@JsonProperty
	private long id;
	
	@JsonProperty
	private LibraryItemType type;
	
	@JsonProperty
	private String name;

	protected GenericLibraryItem() {
		// for jackson
	}
	
	public GenericLibraryItem(long id, LibraryItemType type, String name)
	{
		this.id = id;
		this.type = type;
		this.name = name;
	}
	
	@Override
	@JsonIgnore
	public long getId()
	{
		return id;
	}
	
	@Override
	@JsonIgnore
	public LibraryItemType getType()
	{
		return type;
	}

	@Override
	@JsonIgnore
	public String getName()
	{
		return name;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		
		if (!(obj instanceof GenericLibraryItem))
		{
			return false;
		}
		
		GenericLibraryItem item = (GenericLibraryItem)obj;
		return equals(item);
	}
	
	public boolean equals(GenericLibraryItem item)
	{
		return item.getId() == getId();
	}
	
	@Override
	public int hashCode()
	{
		return Long.valueOf(id).hashCode();
	}
}
