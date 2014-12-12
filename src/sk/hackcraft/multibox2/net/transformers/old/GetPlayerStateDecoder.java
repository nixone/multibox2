package sk.hackcraft.multibox2.net.transformers.old;

import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;
import sk.hackcraft.multibox2.net.data.GetPlayerStateResultData;
import sk.hackcraft.multibox2.util.JsonValues;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GetPlayerStateDecoder extends JacksonMessageDecoder<GetPlayerStateResultData>
{
	@Override
	public GetPlayerStateResultData decodeJson(ObjectMapper objectMapper, String jsonString) throws Exception
	{
		JsonNode rootNode = objectMapper.readTree(jsonString);

		if (rootNode.get("multimedia").asText().equals(JsonValues.NULL))
		{
			return new GetPlayerStateResultData(null, 0, false);
		}
		else
		{
			ObjectNode multimediaObjectNode = (ObjectNode)rootNode.path("multimedia");
			
			if (multimediaObjectNode.asText().equals(JsonValues.NULL))
			{
				return new GetPlayerStateResultData(null, 0, false);
			}
			else
			{
				long id = multimediaObjectNode.path("id").asLong();
				String name = multimediaObjectNode.path("name").asText();
				int length = multimediaObjectNode.path("length").asInt();
				
				MultimediaItem multimedia = new MultimediaItem(id, name, length);
				
				int playbackPosition = rootNode.path("playbackPosition").asInt();
				boolean playing = rootNode.path("playing").asBoolean();
				
				return new GetPlayerStateResultData(multimedia, playbackPosition, playing);
			}
		}
	}
}