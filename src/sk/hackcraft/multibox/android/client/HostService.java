package sk.hackcraft.multibox.android.client;

import java.io.IOException;
import java.util.concurrent.ThreadFactory;

import sk.hackcraft.multibox.android.client.util.HandlerEventLoop;
import sk.hackcraft.multibox.android.host.LibraryView;
import sk.hackcraft.multibox.android.host.Player;
import sk.hackcraft.multibox.android.host.Song;
import sk.hackcraft.multibox.net.MessageTypes;
import sk.hackcraft.multibox.net.NetworkStandards;
import sk.hackcraft.multibox.net.host.Host;
import sk.hackcraft.multibox.net.host.handlers.AddLibraryItemToPlaylistHandler;
import sk.hackcraft.multibox.net.host.handlers.GetLibraryItemHandler;
import sk.hackcraft.multibox.net.host.handlers.GetPlayerStateHandler;
import sk.hackcraft.multibox.net.host.handlers.GetPlaylistHandler;
import sk.hackcraft.multibox.net.host.handlers.GetServerInfoHandler;
import sk.hackcraft.multibox.net.host.handlers.PingHandler;
import sk.hackcraft.multibox.net.host.handlers.UploadMultimediaHandler;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class HostService extends Service
{
	static public final int NOTIFICATION_ID = NetworkStandards.SOCKET_PORT;

	static public final String TAG = HostService.class.getName();

	public class ProvidingBinder extends Binder
	{
		public HostService getService()
		{
			return HostService.this;
		}
	}
	
	private class PlayerListener implements Player.Listener {
		private HandlerEventLoop messageQueue = new HandlerEventLoop();
		private Runnable refreshNotification = new Runnable()
		{
			
			@Override
			public void run()
			{
				refreshNotification();
			}
		};
		
		@Override
		public void onSongAdded(Player player, Song song)
		{
			messageQueue.post(refreshNotification);
		}

		@Override
		public void onSongFinished(Player player, Song song)
		{
			messageQueue.post(refreshNotification);
		}

		@Override
		public void onSongStarted(Player player, Song song)
		{
			messageQueue.post(refreshNotification);
		}
	}

	private Player player = null;
	private LibraryView library = null;
	private Host host = null;
	private Notification notification = null;
	private WifiManager.WifiLock acquiredWifiLock = null;
	
	private int nextNotificationActionRequestCode = 1;
	
	private Notification buildNotification()
	{
		Intent closeIntent = ControlHostServiceActivity.createIntent(this, ControlHostServiceActivity.ACTION_CLOSE);
		Intent openIntent = ControlHostServiceActivity.createIntent(this, ControlHostServiceActivity.ACTION_OPEN);
		Intent playIntent = ControlHostServiceActivity.createIntent(this, ControlHostServiceActivity.ACTION_PLAY);
		Intent pauseIntent = ControlHostServiceActivity.createIntent(this, ControlHostServiceActivity.ACTION_PAUSE);
		
		PendingIntent pendingCloseIntent = PendingIntent.getActivity(this, nextNotificationActionRequestCode++, closeIntent, 0);
		PendingIntent pendingOpenIntent = PendingIntent.getActivity(this, nextNotificationActionRequestCode++, openIntent, 0);
		PendingIntent pendingPlayIntent = PendingIntent.getActivity(this, nextNotificationActionRequestCode++, playIntent, 0);
		PendingIntent pendingPauseIntent = PendingIntent.getActivity(this, nextNotificationActionRequestCode++, pauseIntent, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle("MultiBox")
			.setContentIntent(pendingOpenIntent);
			
		if(player.isPlaying())
		{
			Song playingSong = player.getPlayingSong();
			
			if(player.getDesiredState() == Player.State.PLAYING)
			{
				builder.setContentText(playingSong.getTitle());
			}
			else if(player.getDesiredState() == Player.State.PAUSED)
			{
				builder.setContentText(playingSong.getTitle()+" (paused)");
			}
			
			builder.setProgress(100, 30, true);
		}
		else
		{
			builder.setContentText("Not playing.");
		}
		
		switch(player.getDesiredState())
		{
			case PLAYING:
				builder.addAction(R.drawable.pause, "Pause", pendingPauseIntent);
				break;
			case PAUSED:
				builder.addAction(R.drawable.play, "Play", pendingPlayIntent);
				break;
		}
		
		builder.addAction(R.drawable.stop, "Close", pendingCloseIntent);

		return builder.build();
	}
	
	private void refreshNotification()
	{
		Notification buildNotification = buildNotification();
		
		if(notification == null)
		{
			notification = buildNotification;
			startForeground(NOTIFICATION_ID, notification);
		}
		else
		{
			notification = buildNotification;
			
			NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			manager.notify(NOTIFICATION_ID, notification);
		}
	}
	
	public void start()
	{
		if (host != null)
		{
			return;
		}

		player = new Player();
		player.addListener(new PlayerListener());
		player.play();

		library = new LibraryView(getContentResolver());
		library.load();

		host = new Host(NetworkStandards.SOCKET_PORT);
		host.setMessageHandler(MessageTypes.ADD_LIBRARY_ITEM_TO_PLAYLIST, new AddLibraryItemToPlaylistHandler(library, player));
		host.setMessageHandler(MessageTypes.GET_LIBRARY_ITEM, new GetLibraryItemHandler(library));
		host.setMessageHandler(MessageTypes.GET_PLAYER_STATE, new GetPlayerStateHandler(player));
		host.setMessageHandler(MessageTypes.GET_PLAYLIST, new GetPlaylistHandler(player));
		host.setMessageHandler(MessageTypes.GET_SERVER_INFO, new GetServerInfoHandler(android.os.Build.MODEL));
		host.setMessageHandler(MessageTypes.PING, new PingHandler());
		host.setMessageHandler(MessageTypes.UPLOAD_MULTIMEDIA, new UploadMultimediaHandler(MessageTypes.UPLOAD_MULTIMEDIA, library, getApplicationContext()));

		try
		{
			host.start(new ThreadFactory()
			{
				@Override
				public Thread newThread(Runnable r)
				{
					return new Thread(r);
				}
			});
		} catch (IOException e)
		{
			Log.e(TAG, "Couldnt start local host", e);

			player.destroy();

			host = null;
			return;
		}

		acquireWifiLock();

		refreshNotification();
	}

	public void close()
	{
		if (host == null)
		{
			return;
		}

		stopForeground(true);

		releaseWifiLock();

		try
		{
			host.stop();
		} catch (IOException e)
		{
			e.printStackTrace();
			// nothing, continue
		}

		player.destroy();
		player = null;
		host = null;
		notification = null;
	}

	public void play()
	{
		if(player != null)
		{
			player.play();
		}
		refreshNotification();
	}
	
	public void pause()
	{
		if(player != null)
		{
			player.pause();
		}
		refreshNotification();
	}

	private void acquireWifiLock()
	{
		if (acquiredWifiLock != null)
		{
			return;
		}

		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wifiManager != null)
		{
			acquiredWifiLock = wifiManager.createWifiLock(HostService.class.getName());
			acquiredWifiLock.acquire();
		}
	}

	private void releaseWifiLock()
	{
		if (acquiredWifiLock == null)
		{
			return;
		}
		acquiredWifiLock.release();
		acquiredWifiLock = null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		acquiredWifiLock = null;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new ProvidingBinder();
	}

	public Player getPlayer()
	{
		return player;
	}

	public LibraryView getLibrary()
	{
		return library;
	}

}
