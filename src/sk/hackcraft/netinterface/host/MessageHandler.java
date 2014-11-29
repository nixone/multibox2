package sk.hackcraft.netinterface.host;

import sk.hackcraft.netinterface.message.Message;

public interface MessageHandler
{
	public Message handle(Message message);
}
