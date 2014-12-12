package sk.hackcraft.multibox2.net.transformers.old;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;
import sk.hackcraft.multibox2.net.data.GetPlaylistResultData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GetPlaylistDecoder extends JacksonMessageDecoder<GetPlaylistResultData>
{
	@Override
	public GetPlaylistResultData decodeJson(ObjectMapper objectMapper, String jsonString) throws Exception
	{
		JsonNode rootNode = objectMapper.readTree(jsonString);
		Iterator<JsonNode> nodesIterator = rootNode.path("playlist").elements();
		
		List<MultimediaItem> playlist = new LinkedList<MultimediaItem>();
		while (nodesIterator.hasNext())
		{
			ObjectNode multimediaObjectNode = (ObjectNode)nodesIterator.next();

			long id = multimediaObjectNode.path("id").asLong();
			String name = multimediaObjectNode.path("name").asText();
			int length = multimediaObjectNode.path("length").asInt();
			
			MultimediaItem multimedia = new MultimediaItem(id, name, length);
			
			playlist.add(multimedia);
		}

		return new GetPlaylistResultData(playlist);
	}
}
