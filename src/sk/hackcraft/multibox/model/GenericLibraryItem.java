package sk.hackcraft.multibox.model;

public class GenericLibraryItem implements LibraryItem
{
	private final long id;
	private final LibraryItemType type;
	private final String name;

	public GenericLibraryItem(long id, LibraryItemType type, String name)
	{
		this.id = id;
		this.type = type;
		this.name = name;
	}
	
	@Override
	public long getId()
	{
		return id;
	}
	
	@Override
	public LibraryItemType getType()
	{
		return type;
	}

	@Override
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
