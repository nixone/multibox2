package sk.hackcraft.multibox2.net.host;

import java.io.IOException;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import sk.hackcraft.netinterface.message.Message;
import sk.hackcraft.netinterface.message.MessageFactory;
import sk.hackcraft.netinterface.message.MessageType;

public abstract class JsonMessageHandler<TRequest, TResponse> implements MessageHandler
{
	static private String TAG = JsonMessageHandler.class.getName();
	
	private Class<TRequest> clasz = null;
	private MessageType responseType = null;
	private MessageFactory messageFactory = new MessageFactory();
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public JsonMessageHandler(Class<TRequest> clasz, MessageType responseType) {
		this.clasz = clasz;
		this.responseType = responseType;
		
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	}
	
	@Override
	public Message handle(Message request)
	{
		byte[] requestContent = null;
		
		try {
			requestContent = request.getContent();
		} catch(IOException e) {
			Log.e(TAG, "IOException while reading message content", e);
			return null;
		}
		
		TResponse response = null;
		if(requestContent.length == 0) {
			response = handleJson(null);
		} else {
			TRequest decoded = null;
			try {
				decoded = decode(objectMapper, requestContent);
			} catch(Exception e) {
				Log.e(TAG, "Exception during response json mapping", e);
				return null;
			}

			response = handleJson(decoded);
		}
		
		if(response == null) {
			return null;
		}
		
		byte[] responseContent = null;
		try {
			responseContent = encode(objectMapper, response);
		} catch(Exception e) {
			Log.e(TAG, "Exception during request json mapping", e);
			return null;
		}
		
		return messageFactory.createMessage(responseType, responseContent);
	}
	
	public byte[] encode(ObjectMapper mapper, TResponse response) throws Exception {
		return objectMapper.writeValueAsBytes(response);
	}
	
	public TRequest decode(ObjectMapper mapper, byte[] request) throws Exception  {
		return objectMapper.readValue(request, clasz);
	}
	
	public abstract TResponse handleJson(TRequest request);
}
