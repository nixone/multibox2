package sk.hackcraft.multibox2.net.host;

import sk.hackcraft.netinterface.message.Message;
import sk.hackcraft.netinterface.message.MessageFactory;
import sk.hackcraft.netinterface.message.MessageType;
import sk.hackcraft.netinterface.message.transformer.DataTransformer;

public abstract class RequestResponseMessageHandler<TRequest, TResponse> implements MessageHandler
{
	private MessageFactory messageFactory = new MessageFactory();
	private MessageType responseMessageType;

	public RequestResponseMessageHandler(MessageType responseMessageType)
	{
		this.responseMessageType = responseMessageType;
	}

	@Override
	public Message handle(Message message)
	{
		try
		{
			TRequest request = getRequestDecoder().transform(message.getContent());
			TResponse response = handle(request);

			return messageFactory.createMessage(responseMessageType, getResponseEncoder().transform(response));
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public abstract TResponse handle(TRequest request);

	public abstract DataTransformer<byte[], TRequest> getRequestDecoder();

	public abstract DataTransformer<TResponse, byte[]> getResponseEncoder();
}
