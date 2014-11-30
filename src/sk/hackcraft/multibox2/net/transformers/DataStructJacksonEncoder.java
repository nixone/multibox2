package sk.hackcraft.multibox2.net.transformers;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DataStructJacksonEncoder<D> extends JacksonMessageEncoder<D>
{
	@Override
	public String encodeJson(ObjectMapper objectMapper, D data) throws Exception
	{
		return objectMapper.writeValueAsString(data);
	}
}
