package sk.hackcraft.multibox2.android.client;

import java.util.Iterator;

import sk.hackcraft.multibox2.model.LibraryItem;
import sk.hackcraft.multibox2.model.host.LibraryView;
import sk.hackcraft.multibox2.model.host.Player;
import sk.hackcraft.multibox2.model.host.Song;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class HostService extends Service
{	
	public class ProvidingBinder extends Binder {
		public HostService getService() {
			return HostService.this;
		}
	}
	
	private Player player = null;
	private LibraryView library;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(player != null) {
			return super.onStartCommand(intent, flags, startId);
		}
		
		Log.e("HOSTSERVICE", "Starting service");
		
		player = new Player();
		player.pause();
		
		library = new LibraryView();
		library.refresh(getContentResolver());
		
		Iterator<LibraryItem> it = library.getLibraryItems().iterator();
		while(it.hasNext()) {
			Song s = (Song)it.next();
			
			if(s.getArtistName().equals("Sum 41")) {
				player.add(s);
			}
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		if(player != null) {
			player.destroy();
			player = null;
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
