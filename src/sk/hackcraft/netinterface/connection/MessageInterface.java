package sk.hackcraft.netinterface.connection;

import java.io.Closeable;
import java.io.IOException;

import sk.hackcraft.netinterface.message.Message;


public interface MessageInterface extends Closeable
{
	public void sendMessage(Message message) throws IOException;
	public Message waitForMessage() throws IOException;
}
