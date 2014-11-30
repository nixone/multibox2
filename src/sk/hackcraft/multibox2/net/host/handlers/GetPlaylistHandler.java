package sk.hackcraft.multibox2.net.host.handlers;

import sk.hackcraft.multibox2.model.host.Player;
import sk.hackcraft.multibox2.net.MessageTypes;
import sk.hackcraft.multibox2.net.host.JsonMessageHandler;
import sk.hackcraft.multibox2.net.host.messages.Empty;
import sk.hackcraft.multibox2.net.host.messages.GetPlaylistResponse;

public class GetPlaylistHandler extends JsonMessageHandler<Empty, GetPlaylistResponse>
{
	private Player player;
	
	public GetPlaylistHandler(Player player)
	{
		super(Empty.class, MessageTypes.GET_PLAYLIST);
		this.player = player;
	}

	@Override
	public GetPlaylistResponse handleJson(Empty request)
	{
		return new GetPlaylistResponse(player.getSongs());
	}
}
