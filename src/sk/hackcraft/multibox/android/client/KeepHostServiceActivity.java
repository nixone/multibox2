package sk.hackcraft.multibox.android.client;

import sk.hackcraft.multibox.android.client.HostService.ProvidingBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class KeepHostServiceActivity extends Activity {
	static private final String TAG = KeepHostServiceActivity.class.getName();
	
	private HostServiceConnection hostServiceConnection = new HostServiceConnection();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		createAndConnectHostService();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		disconnectHostService();
	}

	private void createAndConnectHostService() {
		startService(new Intent(this, HostService.class));
		bindService(new Intent(this, HostService.class), hostServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void disconnectHostService() {
		if(hostServiceConnection.serviceBound) {
			hostServiceConnection.service.notifyOfDisconnectedActivity();
			hostServiceConnection.serviceBound = false;
			unbindService(hostServiceConnection);
		}
	}
	
	public class HostServiceConnection implements ServiceConnection {
		private boolean serviceBound = false;
		private HostService service = null;
		
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1)
		{
			serviceBound = true;
			service = ((ProvidingBinder)arg1).getService();
			service.notifyOfConnectedActivity();
		}
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			
			serviceBound = false;
			service = null;
		}
	}
}
