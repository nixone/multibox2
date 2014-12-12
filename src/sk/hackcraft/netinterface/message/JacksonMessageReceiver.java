package sk.hackcraft.netinterface.message;

import com.fasterxml.jackson.databind.ObjectMapper;

import sk.hackcraft.netinterface.message.transformer.DataTransformException;
import sk.hackcraft.netinterface.message.transformer.DataTransformer;
import sk.hackcraft.util.MessageQueue;

public abstract class JacksonMessageReceiver<R> extends DataStringMessageReceiver<R>
{
	private Class<R> clasz;
	private ObjectMapper mapper;
	
	public JacksonMessageReceiver(MessageQueue messageQueue, Class<R> clasz)
	{
		super(messageQueue);
		this.clasz = clasz;
		this.mapper = new ObjectMapper();
	}

	@Override
	protected DataTransformer<String, R> createParser()
	{
		return new DataTransformer<String, R>()
		{
			@Override
			public R transform(String input) throws DataTransformException
			{
				try {
					return mapper.readValue(input, clasz);
				} catch(Exception e) {
					throw new DataTransformException(e);
				}
			}
		};
	}
}
