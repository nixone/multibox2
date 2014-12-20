package sk.hackcraft.multibox.net.host.handlers;

import sk.hackcraft.multibox.net.MessageTypes;
import sk.hackcraft.multibox.net.host.DuplexJacksonMessageHandler;
import sk.hackcraft.multibox.net.messages.Empty;

public class PingHandler extends DuplexJacksonMessageHandler<Empty, Empty>
{
	public PingHandler()
	{
		super(Empty.class, MessageTypes.PING);
	}

	@Override
	public Empty handle(Empty request)
	{
		return new Empty();
	}
}
