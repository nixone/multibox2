package sk.hackcraft.multibox2.net.serializers;

import java.io.IOException;

import sk.hackcraft.multibox2.model.LibraryItem;
import sk.hackcraft.multibox2.model.LibraryItemType;
import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class LibraryItemDeserializer extends JsonDeserializer<LibraryItem>
{
	@Override
	public LibraryItem deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException
	{
		JsonNode itemNode = parser.getCodec().readTree(parser);
		
		long id = itemNode.get("id").asLong();
		LibraryItemType type = LibraryItemType.valueOf(itemNode.get("type").asText());
		
		if (type != LibraryItemType.MULTIMEDIA)
		{
			throw new IllegalArgumentException("Only MULTIMEDIA type is supported for now.");
		}
		
		String name = itemNode.get("name").asText();
		int length = itemNode.get("length").asInt();
		
		return new MultimediaItem(id, name, length);
	}
}
