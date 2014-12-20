package sk.hackcraft.multibox.net.host.handlers;

import sk.hackcraft.multibox.android.host.Player;
import sk.hackcraft.multibox.net.MessageTypes;
import sk.hackcraft.multibox.net.host.DuplexJacksonMessageHandler;
import sk.hackcraft.multibox.net.messages.Empty;
import sk.hackcraft.multibox.net.messages.GetPlaylistResponse;

public class GetPlaylistHandler extends DuplexJacksonMessageHandler<Empty, GetPlaylistResponse>
{
	private Player player;
	
	public GetPlaylistHandler(Player player)
	{
		super(Empty.class, MessageTypes.GET_PLAYLIST);
		this.player = player;
	}

	@Override
	public GetPlaylistResponse handle(Empty request)
	{
		return new GetPlaylistResponse(player.getSongs());
	}
}
