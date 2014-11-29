package sk.hackcraft.netinterface.connection;

import sk.hackcraft.netinterface.message.Message;
import sk.hackcraft.netinterface.message.MessageReceiver;
import sk.hackcraft.netinterface.message.MessageType;

public interface AsynchronousMessageInterface
{
	public void close();
	
	public void setSeriousErrorListener(SeriousErrorListener listener);
	
	public void sendMessage(Message message);
	public void sendMessage(Message message, MessageSendListener listener);
	
	public void setMessageReceiver(MessageType messageType, MessageReceiver receiver);
	
	public interface MessageSendListener
	{
		public void onFinish(boolean success);
	}
	
	public interface SeriousErrorListener
	{
		public void onSeriousError(String errorDescription);
	}
}
