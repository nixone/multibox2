package sk.hackcraft.netinterface.message;

import java.io.IOException;

import sk.hackcraft.netinterface.message.transformer.DataTransformer;
import sk.hackcraft.util.MessageQueue;

public abstract class ObjectMessageReceiver<T> implements MessageReceiver
{
	private MessageQueue messageQueue;
	
	public abstract DataTransformer<byte[], T> getDecoder();
	
	public ObjectMessageReceiver(MessageQueue messageQueue) {
		this.messageQueue = messageQueue;
	}
	
	@Override
	public void receive(byte[] content) throws IOException
	{
		final T result = getDecoder().transform(content);
		
		messageQueue.post(new Runnable()
		{
			@Override
			public void run()
			{
				onResult(result);
			}
		});
	}
	
	protected abstract void onResult(T result);
}
