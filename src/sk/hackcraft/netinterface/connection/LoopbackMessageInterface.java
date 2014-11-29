package sk.hackcraft.netinterface.connection;

import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import sk.hackcraft.netinterface.message.Message;


public class LoopbackMessageInterface implements MessageInterface
{
	private BlockingDeque<Message> messagesPipe;
	
	public LoopbackMessageInterface()
	{
		this.messagesPipe = new LinkedBlockingDeque<Message>();
	}
	
	@Override
	public void close() throws IOException
	{
	}

	@Override
	public void sendMessage(Message message) throws IOException
	{
		messagesPipe.add(message);
	}

	@Override
	public Message waitForMessage() throws IOException
	{
		try
		{
			return messagesPipe.take();
		}
		catch (InterruptedException e)
		{
			throw new IOException(e);
		}
	}
}
