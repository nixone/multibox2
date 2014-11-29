package sk.hackcraft.multibox.net.transformers;

import sk.hackcraft.multibox.net.data.GetServerInfoResultData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetServerInfoDecoder extends JacksonMessageDecoder<GetServerInfoResultData>
{
	@Override
	public GetServerInfoResultData decodeJson(ObjectMapper objectMapper, String jsonString) throws Exception
	{
		JsonNode rootNode = objectMapper.readTree(jsonString);
		
		String serverName = rootNode.path("serverName").asText();
		
		return new GetServerInfoResultData(serverName);
	}
}
