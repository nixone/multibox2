package sk.hackcraft.multibox2.model;

import sk.hackcraft.multibox2.net.serializers.LibraryItemDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = LibraryItemDeserializer.class)
public interface LibraryItem
{
	public long getId();
	public LibraryItemType getType();
	public String getName();
}
