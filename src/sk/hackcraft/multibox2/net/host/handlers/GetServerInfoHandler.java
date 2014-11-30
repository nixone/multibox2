package sk.hackcraft.multibox2.net.host.handlers;

import sk.hackcraft.multibox2.net.MessageTypes;
import sk.hackcraft.multibox2.net.host.JsonMessageHandler;
import sk.hackcraft.multibox2.net.host.messages.Empty;
import sk.hackcraft.multibox2.net.host.messages.GetServerInfoResponse;

public class GetServerInfoHandler extends JsonMessageHandler<Empty, GetServerInfoResponse>
{
	private String serverName;
	
	public GetServerInfoHandler(String serverName)
	{
		super(Empty.class, MessageTypes.GET_SERVER_INFO);
		this.serverName = serverName;
	}

	@Override
	public GetServerInfoResponse handleJson(Empty request)
	{
		return new GetServerInfoResponse(serverName);
	}
}
