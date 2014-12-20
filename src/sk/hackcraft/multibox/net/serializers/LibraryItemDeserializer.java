package sk.hackcraft.multibox.net.serializers;

import java.io.IOException;

import sk.hackcraft.multibox.model.LibraryItem;
import sk.hackcraft.multibox.model.LibraryItemType;
import sk.hackcraft.multibox.model.libraryitems.DirectoryItem;
import sk.hackcraft.multibox.model.libraryitems.MultimediaItem;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LibraryItemDeserializer extends JsonDeserializer<LibraryItem>
{
	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public LibraryItem deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException
	{
		JsonNode itemNode = parser.getCodec().readTree(parser);
		
		LibraryItemType type = LibraryItemType.valueOf(itemNode.get("type").asText());
		long id = itemNode.get("id").asLong();
		String name = itemNode.get("name").asText();
		
		if (type == LibraryItemType.MULTIMEDIA)
		{
			int length = itemNode.get("length").asInt();
			
			return new MultimediaItem(id, name, length);
		}
		else if(type == LibraryItemType.DIRECTORY)
		{
			DirectoryItem directory = new DirectoryItem(id, name);			
			
			for(JsonNode subItemNode : itemNode.get("items")) {
				LibraryItem subItem = mapper.readValue(subItemNode.toString(), LibraryItem.class);
				
				directory.addItem(subItem);
			}
			
			return directory;
		}

		throw new IOException("Can't process other than multimedia or directory");
	}
}
