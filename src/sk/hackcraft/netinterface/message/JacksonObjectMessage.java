package sk.hackcraft.netinterface.message;

import sk.hackcraft.multibox2.net.transformers.JacksonStringTransformer;
import sk.hackcraft.multibox2.net.transformers.JoinTransformer;
import sk.hackcraft.multibox2.net.transformers.StringByteArrayTransformer;
import sk.hackcraft.netinterface.message.transformer.DataTransformer;

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
