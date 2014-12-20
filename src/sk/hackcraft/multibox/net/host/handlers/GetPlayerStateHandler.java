package sk.hackcraft.multibox.net.host.handlers;

import sk.hackcraft.multibox.android.host.Player;
import sk.hackcraft.multibox.net.MessageTypes;
import sk.hackcraft.multibox.net.host.DuplexJacksonMessageHandler;
import sk.hackcraft.multibox.net.messages.Empty;
import sk.hackcraft.multibox.net.messages.GetPlayerStateResponse;

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
