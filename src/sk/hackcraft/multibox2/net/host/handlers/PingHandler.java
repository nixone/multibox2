package sk.hackcraft.multibox2.net.host.handlers;

import sk.hackcraft.multibox2.net.MessageTypes;
import sk.hackcraft.multibox2.net.host.JsonMessageHandler;
import sk.hackcraft.multibox2.net.host.messages.Empty;

public class PingHandler extends JsonMessageHandler<Empty, Empty>
{
	public PingHandler()
	{
		super(Empty.class, MessageTypes.PING);
	}

	@Override
	public Empty handleJson(Empty request)
	{
		return new Empty();
	}
}
