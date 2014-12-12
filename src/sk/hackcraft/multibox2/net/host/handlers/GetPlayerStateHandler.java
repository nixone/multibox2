package sk.hackcraft.multibox2.net.host.handlers;

import sk.hackcraft.multibox2.android.host.Player;
import sk.hackcraft.multibox2.net.MessageTypes;
import sk.hackcraft.multibox2.net.host.DuplexJacksonMessageHandler;
import sk.hackcraft.multibox2.net.host.messages.Empty;
import sk.hackcraft.multibox2.net.host.messages.GetPlayerStateResponse;

public class GetPlayerStateHandler extends DuplexJacksonMessageHandler<Empty, GetPlayerStateResponse>
{
	private Player player;
	
	public GetPlayerStateHandler(Player player)
	{
		super(Empty.class, MessageTypes.GET_PLAYER_STATE);
		this.player = player;
	}

	@Override
	public GetPlayerStateResponse handle(Empty request)
	{
		return new GetPlayerStateResponse(
				player.getPlayingSong(), 
				Math.round(player.getPlaybackPosition() / 1000f), 
				player.isPlaying()
		);
	}
}
