package sk.hackcraft.multibox.net.host;

import sk.hackcraft.netinterface.message.MessageType;
import sk.hackcraft.netinterface.message.transformer.ByteArrayStringTransformer;
import sk.hackcraft.netinterface.message.transformer.DataTransformer;
import sk.hackcraft.netinterface.message.transformer.JacksonStringTransformer;
import sk.hackcraft.netinterface.message.transformer.JoinTransformer;
import sk.hackcraft.netinterface.message.transformer.StringByteArrayTransformer;
import sk.hackcraft.netinterface.message.transformer.StringJacksonTransformer;

public abstract class DuplexJacksonMessageHandler<TRequest, TResponse> extends RequestResponseMessageHandler<TRequest, TResponse>
{
	private DataTransformer<byte[], TRequest> requestDecoder;
	
	private DataTransformer<TResponse, byte[]> responseEncoder;
	
	public DuplexJacksonMessageHandler(Class<TRequest> requestClasz, MessageType responseMessageType)
	{
		super(responseMessageType);
		
		requestDecoder = new JoinTransformer<byte[], String, TRequest>(
			new ByteArrayStringTransformer(),
			new StringJacksonTransformer<TRequest>(requestClasz)
		);
		
		responseEncoder = new JoinTransformer<TResponse, String, byte[]>(
			new JacksonStringTransformer<TResponse>(),
			new StringByteArrayTransformer()
		);
	}

	@Override
	public DataTransformer<byte[], TRequest> getRequestDecoder()
	{
		return requestDecoder;
	}

	@Override
	public DataTransformer<TResponse, byte[]> getResponseEncoder()
	{
		return responseEncoder;
	}
}
