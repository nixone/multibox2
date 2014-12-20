package sk.hackcraft.multibox.android.host;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import sk.hackcraft.multibox.model.libraryitems.DirectoryItem;

public class Album extends DirectoryItem
{
	public Album(long id, String name)
	{
		super(id, name);
	}
}
