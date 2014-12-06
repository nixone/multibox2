package sk.hackcraft.multibox2.model;

import java.util.Comparator;

import sk.hackcraft.multibox2.net.serializers.LibraryItemDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = LibraryItemDeserializer.class)
public interface LibraryItem
{
	static public final Comparator<LibraryItem> COMPARE_BY_NAME = new Comparator<LibraryItem>()
	{
		@Override
		public int compare(LibraryItem lhs, LibraryItem rhs)
		{
			return lhs.getName().compareTo(rhs.getName());
		}
		
	};
	
	public long getId();
	public LibraryItemType getType();
	public String getName();
}
