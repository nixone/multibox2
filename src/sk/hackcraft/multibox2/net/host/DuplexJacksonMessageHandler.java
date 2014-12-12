package sk.hackcraft.multibox2.net.host;

import sk.hackcraft.multibox2.net.transformers.ByteArrayStringTransformer;
import sk.hackcraft.multibox2.net.transformers.JacksonStringTransformer;
import sk.hackcraft.multibox2.net.transformers.JoinTransformer;
import sk.hackcraft.multibox2.net.transformers.StringByteArrayTransformer;
import sk.hackcraft.multibox2.net.transformers.StringJacksonTransformer;
import sk.hackcraft.netinterface.message.MessageType;
import sk.hackcraft.netinterface.message.transformer.DataTransformer;

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
