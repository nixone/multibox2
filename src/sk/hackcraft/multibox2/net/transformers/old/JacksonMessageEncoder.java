package sk.hackcraft.multibox2.net.transformers.old;

import sk.hackcraft.netinterface.message.transformer.DataTransformException;
import sk.hackcraft.netinterface.message.transformer.DataTransformer;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JacksonMessageEncoder<D> implements DataTransformer<D, String>
{
	@Override
	public String transform(D data) throws DataTransformException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		
		try
		{
			return encodeJson(objectMapper, data);
		}
		catch (Exception e)
		{
			throw new DataTransformException(e);
		}
	}
	
	public abstract String encodeJson(ObjectMapper objectMapper, D data) throws Exception;
}
