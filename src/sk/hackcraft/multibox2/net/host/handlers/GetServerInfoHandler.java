package sk.hackcraft.multibox2.net.host.handlers;

import sk.hackcraft.multibox2.net.MessageTypes;
import sk.hackcraft.multibox2.net.host.DuplexJacksonMessageHandler;
import sk.hackcraft.multibox2.net.messages.Empty;
import sk.hackcraft.multibox2.net.messages.GetServerInfoResponse;

public class GetServerInfoHandler extends DuplexJacksonMessageHandler<Empty, GetServerInfoResponse>
{
	private String serverName;
	
	public GetServerInfoHandler(String serverName)
	{
		super(Empty.class, MessageTypes.GET_SERVER_INFO);
		this.serverName = serverName;
	}

	@Override
	public GetServerInfoResponse handle(Empty request)
	{
		return new GetServerInfoResponse(serverName);
	}
}
