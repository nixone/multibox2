package sk.hackcraft.multibox.net.host;

import sk.hackcraft.netinterface.message.Message;

public interface MessageHandler
{
	public Message handle(Message message);
}
