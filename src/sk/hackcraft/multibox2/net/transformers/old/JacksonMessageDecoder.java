package sk.hackcraft.multibox2.net.transformers.old;

import sk.hackcraft.netinterface.message.transformer.DataTransformException;
import sk.hackcraft.netinterface.message.transformer.DataTransformer;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JacksonMessageDecoder<D> implements DataTransformer<String, D>
{
	@Override
	public D transform(String jsonString) throws DataTransformException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		
		try
		{
			return decodeJson(objectMapper, jsonString);
		}
		catch (Exception e)
		{
			throw new DataTransformException(e);
		}
	}
	
	public abstract D decodeJson(ObjectMapper objectMapper, String jsonString) throws Exception;
}
