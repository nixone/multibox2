package sk.hackcraft.netinterface.message;

import sk.hackcraft.netinterface.message.transformer.DataTransformer;
import sk.hackcraft.netinterface.message.transformer.JacksonStringTransformer;
import sk.hackcraft.netinterface.message.transformer.JoinTransformer;
import sk.hackcraft.netinterface.message.transformer.StringByteArrayTransformer;

public class JacksonObjectMessage<T> extends ObjectMessage<T>
{
	public JacksonObjectMessage(MessageType messageType, T object)
	{
		super(messageType, object);
	}

	@Override
	public DataTransformer<T, byte[]> getEncoder()
	{
		return new JoinTransformer<T, String, byte[]>(
			new JacksonStringTransformer<T>(),
			new StringByteArrayTransformer()
		);
	}
}
