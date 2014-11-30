package sk.hackcraft.multibox2.net.transformers;

import sk.hackcraft.multibox2.model.GenericLibraryItem;
import sk.hackcraft.multibox2.model.LibraryItem;
import sk.hackcraft.multibox2.model.LibraryItemType;
import sk.hackcraft.multibox2.model.libraryitems.DirectoryItem;
import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;
import sk.hackcraft.multibox2.net.data.GetLibraryItemData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GetLibraryItemDecoder extends JacksonMessageDecoder<GetLibraryItemData>
{
	@Override
	public GetLibraryItemData decodeJson(ObjectMapper objectMapper, String jsonString) throws Exception
	{
		ObjectNode rootNode = (ObjectNode)objectMapper.readTree(jsonString);

		ObjectNode libraryItemObjectNode = (ObjectNode)rootNode.get("libraryItem");
		
		long id = libraryItemObjectNode.get("id").asLong();
		LibraryItemType type = LibraryItemType.valueOf(libraryItemObjectNode.get("type").asText());
		String name = libraryItemObjectNode.get("name").asText();
	
		LibraryItem libraryItem;
		switch (type)
		{
			case DIRECTORY:
				libraryItem = createDirectory(id, name, libraryItemObjectNode);
				break;
			case MULTIMEDIA:
				libraryItem = createMultimedia(id, name, libraryItemObjectNode);
				break;
			default:
				throw new IllegalArgumentException("Invalid library item type received from server.");
		}
		
		return new GetLibraryItemData(libraryItem);
	}

	private LibraryItem createMultimedia(long id, String name, ObjectNode rootNode)
	{
		int length = rootNode.path("length").asInt();
		
		return new MultimediaItem(id, name, length);
	}

	private LibraryItem createDirectory(long id, String name, ObjectNode rootNode)
	{
		DirectoryItem directory = new DirectoryItem(id, name);
		
		for (JsonNode node : rootNode.get("items"))
		{
			long itemId = node.get("id").asLong();
			LibraryItemType itemType = LibraryItemType.valueOf(node.get("type").asText());
			String itemName = node.get("name").asText();
			
			GenericLibraryItem libraryItem = new GenericLibraryItem(itemId, itemType, itemName);
			directory.addItem(libraryItem);
		}
		
		return directory;
	}
}
