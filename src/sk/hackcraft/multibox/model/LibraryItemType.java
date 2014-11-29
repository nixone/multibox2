package sk.hackcraft.multibox.model;

public enum LibraryItemType
{
	DIRECTORY(1),
	MULTIMEDIA(2),
	BACK_NAVIGATION(3);
	
	private final long id;
	
	private LibraryItemType(long id)
	{
		this.id = id;
	}
	
	public long getId()
	{
		return id;
	}
}
