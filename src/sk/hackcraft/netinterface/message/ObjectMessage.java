package sk.hackcraft.netinterface.message;

import java.io.IOException;

import sk.hackcraft.netinterface.message.transformer.DataTransformer;

public abstract class ObjectMessage<T> implements Message
{
	private final MessageType messageType;
	private final T object;

	public ObjectMessage(MessageType messageType, T object)
	{
		this.object = object;
		this.messageType = messageType;
	}

	public abstract DataTransformer<T, byte[]> getEncoder();
	
	@Override
	public MessageType getType()
	{
		return messageType;
	}

	@Override
	public byte[] getContent() throws IOException
	{
		return getEncoder().transform(object);
	}
}
