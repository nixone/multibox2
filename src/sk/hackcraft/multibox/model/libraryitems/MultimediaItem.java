package sk.hackcraft.multibox.model.libraryitems;

import sk.hackcraft.multibox.model.LibraryItem;
import sk.hackcraft.multibox.model.LibraryItemType;

public class MultimediaItem implements LibraryItem
{
	private final long id;
	private final String name;
	private final int length;
	
	public MultimediaItem(long id, String name, int length)
	{
		this.id = id;
		this.name = name;
		this.length = length;
	}
	
	public MultimediaItem(Builder builder)
	{
		this.id = builder.id;
		this.name = builder.name;
		this.length = builder.length;
	}
	
	public MultimediaItem(MultimediaItem multimedia)
	{
		this.id = multimedia.id;
		this.name = multimedia.name;
		this.length = multimedia.length;
	}
	
	public long getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public LibraryItemType getType()
	{
		return LibraryItemType.MULTIMEDIA;
	}
	
	public int getLength()
	{
		return length;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof MultimediaItem))
		{
			return false;
		}
		
		MultimediaItem multimedia = (MultimediaItem)o;
		
		return equals(multimedia);
	}
	
	private boolean equals(MultimediaItem multimedia)
	{
		return multimedia.getId() == id;
	}
	
	@Override
	public int hashCode()
	{
		return Long.valueOf(id).hashCode();
	}
	
	@Override
	public String toString()
	{
		return "#" + getId() + " " + getName();
	}
	
	public static class Builder
	{
		private long id;
		private String name;
		private int length;
		
		public Builder setId(long id)
		{
			this.id = id;
			return this;
		}
		
		public Builder setName(String name)
		{
			this.name = name;
			return this;
		}
		
		public Builder setLength(int length)
		{
			this.length = length;
			return this;
		}

		public MultimediaItem create()
		{
			return new MultimediaItem(this);
		}
	}
}
