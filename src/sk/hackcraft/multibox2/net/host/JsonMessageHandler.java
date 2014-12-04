package sk.hackcraft.multibox2.net.host;

import java.io.IOException;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import sk.hackcraft.multibox2.net.host.handlers.GetPlayerStateHandler;
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
	
	private StringByteArrayCoder stringByteCoder = new StringByteArrayCoder();
	
	public JsonMessageHandler(Class<TRequest> clasz, MessageType responseType) {
		this.clasz = clasz;
		this.responseType = responseType;
		
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	}
	
	@Override
	public Message handle(Message request)
	{
		Log.d(TAG, this.getClass().getName()+" handling the message");
		
		byte[] requestContentBytes = null;
		
		try {
			requestContentBytes = request.getContent();
		} catch(IOException e) {
			Log.e(TAG, "IOException while reading message content", e);
			return null;
		}
		
		TResponse response = null;
		if(requestContentBytes.length == 0) {
			log("Request came totally empty");
			response = handleJson(null);
		} else {
			String requestContentString = stringByteCoder.decode(requestContentBytes);
			
			TRequest decoded = null;
			try {
				decoded = decode(objectMapper, requestContentString);
			} catch(Exception e) {
				Log.e(TAG, "Exception during response json mapping", e);
				return null;
			}

			response = handleJson(decoded);
		}
		
		if(response == null) {
			return null;
		}
		
		String responseContentString = null;
		try {
			responseContentString = encode(objectMapper, response);
		} catch(Exception e) {
			Log.e(TAG, "Exception during request json mapping", e);
			return null;
		}
		
		log("Response: "+responseContentString);
		
		byte[] responseContentBytes = stringByteCoder.encode(responseContentString);
		
		return messageFactory.createMessage(responseType, responseContentBytes);
	}
	
	public String encode(ObjectMapper mapper, TResponse response) throws Exception {
		return objectMapper.writeValueAsString(response);
	}
	
	public TRequest decode(ObjectMapper mapper, String request) throws Exception  {
		return objectMapper.readValue(request, clasz);
	}
	
	public abstract TResponse handleJson(TRequest request);
	
	private void log(String description) {
		Log.d(TAG, description);
	}
}
