package sk.hackcraft.netinterface.connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sk.hackcraft.netinterface.message.MessageReceiver;
import sk.hackcraft.netinterface.message.MessageType;

/**
 * @author moergil
 * This class works as simple abstract map, which associates message type
 * with concrete message receiver. Main purpose is to synchronize access
 * to receivers, because they will be mostly used in multithread environment.
 */
public class IncomingMessagesRouter
{
	private final Map<Integer, MessageReceiver> receivers;
	
	public IncomingMessagesRouter()
	{
		receivers = new HashMap<Integer, MessageReceiver>();
	}
	
	public synchronized void setReceiver(MessageType type, MessageReceiver receiver)
	{
		receivers.put(type.toTypeId(), receiver);
	}
	
	public void receiveMessage(MessageType type, byte content[]) throws IOException
	{
		MessageReceiver receiver;
		
		synchronized (this)
		{
			receiver = receivers.get(type.toTypeId());
		}
		
		receiver.receive(content);
	}
}
