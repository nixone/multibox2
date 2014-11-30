package sk.hackcraft.multibox2.net.host.handlers;

import sk.hackcraft.multibox2.model.host.Player;
import sk.hackcraft.multibox2.net.MessageTypes;
import sk.hackcraft.multibox2.net.host.JsonMessageHandler;
import sk.hackcraft.multibox2.net.host.messages.Empty;
import sk.hackcraft.multibox2.net.host.messages.GetPlayerStateResponse;

public class GetPlayerStateHandler extends JsonMessageHandler<Empty, GetPlayerStateResponse>
{
	private Player player;
	
	public GetPlayerStateHandler(Player player)
	{
		super(Empty.class, MessageTypes.GET_PLAYER_STATE);
		this.player = player;
	}

	@Override
	public GetPlayerStateResponse handleJson(Empty request)
	{
		return new GetPlayerStateResponse(
				player.getPlayingSong(), 
				player.getPlaybackPosition(), 
				player.isPlaying()
		);
	}
}
