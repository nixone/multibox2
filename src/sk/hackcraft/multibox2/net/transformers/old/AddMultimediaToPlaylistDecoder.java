package sk.hackcraft.multibox2.net.transformers.old;

import sk.hackcraft.multibox2.model.LibraryItemType;
import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;
import sk.hackcraft.multibox2.net.data.AddMultimediaToPlaylistResultData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AddMultimediaToPlaylistDecoder extends JacksonMessageDecoder<AddMultimediaToPlaylistResultData>
{
	@Override
	public AddMultimediaToPlaylistResultData decodeJson(ObjectMapper objectMapper, String jsonString) throws Exception
	{
		JsonNode rootNode = objectMapper.readTree(jsonString);
		
		boolean result = rootNode.path("result").asBoolean();
		
		if (!result)
		{
			return new AddMultimediaToPlaylistResultData(result, null);
		}
		else
		{
			JsonNode multimediaNode = rootNode.get("multimedia");
			
			long id = multimediaNode.get("id").asLong();
			LibraryItemType type = LibraryItemType.valueOf(multimediaNode.get("type").asText());
			
			if (type != LibraryItemType.MULTIMEDIA)
			{
				throw new IllegalArgumentException("Only MULTIMEDIA type is supported for now.");
			}
			
			String name = multimediaNode.get("name").asText();
			int length = multimediaNode.get("length").asInt();
			
			MultimediaItem multimedia = new MultimediaItem(id, name, length);
			
			return new AddMultimediaToPlaylistResultData(result, multimedia);
		}
	}
}
