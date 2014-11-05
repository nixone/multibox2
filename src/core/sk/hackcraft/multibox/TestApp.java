package sk.hackcraft.multibox;

import sk.hackcraft.multibox.model.Player;
import sk.hackcraft.multibox.model.ServerPlayerShadow;
import sk.hackcraft.multibox.net.MockServerInterface;
import sk.hackcraft.util.Log;
import sk.hackcraft.util.ManualEventLoop;
import sk.hackcraft.util.PrintStreamLog;

public class TestApp
{
	public static void main(String[] args)
	{
		System.out.println("Go!");
		
		final Log log = new PrintStreamLog("APP", System.out);
		final ManualEventLoop eventLoop = new ManualEventLoop();
		final MockServerInterface server = new MockServerInterface(eventLoop);
		MockServerInterface.Controller controller = server.getController();
		
		eventLoop.post(new Runnable()
		{
			@Override
			public void run()
			{
				Player player = new ServerPlayerShadow(server, eventLoop, log);
				/*player.registerPlayerEventListener(new Player.PlayerEventListener()
				{
					@Override
					public void onPlayingStateChanged(boolean playing)
					{
						log.print("P: playing state changed: " + playing);
					}
					
					@Override
					public void onPlaybackPositionChanged(int newPosition)
					{
						log.print("P: position changed: " + newPosition);
					}

					@Override
					public void onMultimediaChanged(MultimediaItem newMultimedia)
					{
						log.print("P: multimedia changed: " + newMultimedia);
					}
				});*/

				player.init();
			}
		});
		eventLoop.processAllMessages();
		
		controller.addSongToPlaylist("Song 1", 10);
		eventLoop.processAllMessages();

		controller.addSongToPlaylist("Song 2", 8);
		eventLoop.processAllMessages();

		controller.addSongToPlaylist("Song 3", 11);
		eventLoop.processAllMessages();

		controller.setPlaybackPosition(4);
		eventLoop.processAllMessages();

		controller.finishSong();
		eventLoop.processAllMessages();
		
		controller.setPlaybackPosition(3);
		eventLoop.processAllMessages();

		controller.setPlaying(false);
		eventLoop.processAllMessages();

		controller.setPlaying(true);
		eventLoop.processAllMessages();

		controller.finishSong();
		eventLoop.processAllMessages();

		controller.finishSong();
		eventLoop.processAllMessages();

		controller.addRandomSongToPlaylist();
		eventLoop.processAllMessages();

		controller.addRandomSongToPlaylist();
		eventLoop.processAllMessages();

		controller.setPlaying(true);
		eventLoop.processAllMessages();

		controller.finishSong();
		eventLoop.processAllMessages();

		controller.finishSong();
		eventLoop.processAllMessages();
	}
}
