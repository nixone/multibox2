package sk.hackcraft.multibox2.net.host;

import sk.hackcraft.netinterface.message.Message;

public interface MessageHandler
{
	public Message handle(Message message);
}
