package sk.hackcraft.multibox2.android.client;

import java.io.IOException;
import java.util.concurrent.ThreadFactory;

import sk.hackcraft.multibox2.android.host.LibraryView;
import sk.hackcraft.multibox2.android.host.Player;
import sk.hackcraft.multibox2.net.MessageTypes;
import sk.hackcraft.multibox2.net.NetworkStandards;
import sk.hackcraft.multibox2.net.host.Host;
import sk.hackcraft.multibox2.net.host.handlers.AddLibraryItemToPlaylistHandler;
import sk.hackcraft.multibox2.net.host.handlers.GetLibraryItemHandler;
import sk.hackcraft.multibox2.net.host.handlers.GetPlayerStateHandler;
import sk.hackcraft.multibox2.net.host.handlers.GetPlaylistHandler;
import sk.hackcraft.multibox2.net.host.handlers.GetServerInfoHandler;
import sk.hackcraft.multibox2.net.host.handlers.PingHandler;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class HostService extends Service
{	
	static public final String TAG = HostService.class.getName();
	
	public class ProvidingBinder extends Binder {
		public HostService getService() {
			return HostService.this;
		}
	}
	
	private Player player = null;
	private LibraryView library = null;
	private Host host = null;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(player != null) {
			return super.onStartCommand(intent, flags, startId);
		}
		
		player = new Player();
		player.pause();
		
		library = new LibraryView();
		library.load(getContentResolver());
		
		host = new Host(NetworkStandards.SOCKET_PORT);
		host.setMessageHandler(MessageTypes.ADD_LIBRARY_ITEM_TO_PLAYLIST, new AddLibraryItemToPlaylistHandler(library, player));
		host.setMessageHandler(MessageTypes.GET_LIBRARY_ITEM, new GetLibraryItemHandler(library));
		host.setMessageHandler(MessageTypes.GET_PLAYER_STATE, new GetPlayerStateHandler(player));
		host.setMessageHandler(MessageTypes.GET_PLAYLIST, new GetPlaylistHandler(player));
		host.setMessageHandler(MessageTypes.GET_SERVER_INFO, new GetServerInfoHandler(android.os.Build.MODEL));
		host.setMessageHandler(MessageTypes.PING, new PingHandler());
		
		try {
			host.start(new ThreadFactory()
			{
				@Override
				public Thread newThread(Runnable r)
				{
					return new Thread(r);
				}
			});
		} catch(IOException e) {
			Log.e(TAG, "Couldnt start local host", e);
			host = null;
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(player != null) {
			player.destroy();
			player = null;
		}
		
		if(host != null) {
			host.stop();
			host = null;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return new ProvidingBinder();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public LibraryView getLibrary() {
		return library;
	}
}
