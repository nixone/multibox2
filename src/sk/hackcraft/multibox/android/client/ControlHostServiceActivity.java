package sk.hackcraft.multibox.android.client;

import sk.hackcraft.multibox.android.client.HostService.ProvidingBinder;
import sk.hackcraft.multibox.android.client.util.ActivityTransitionAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ControlHostServiceActivity extends Activity
{
	static private final String TAG = ControlHostServiceActivity.class.getName();
	
	final static public String ACTION_EXTRA_NAME = ControlHostServiceActivity.class.getName() + "_ACTION";

	final static private int ACTION_NONE = 0;
	
	/**
	 * This action closes the host and terminates this activity
	 */
	final static public int ACTION_CLOSE = 1;

	/**
	 * This action starts the host and redirects the activity to MainActivity
	 */
	final static public int ACTION_START = 2;
	
	private int actionCode = ACTION_NONE;

	private HostServiceConnection hostServiceConnection = new HostServiceConnection();
	
	private MultiBoxApplication application = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		actionCode = intent.getIntExtra(ACTION_EXTRA_NAME, ACTION_NONE);
		
		application = (MultiBoxApplication)getApplication();
		
		startService(new Intent(this, HostService.class));
		bindService(new Intent(this, HostService.class), hostServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy()
	{
		if(hostServiceConnection.serviceBound) {
			hostServiceConnection.serviceBound = false;
			unbindService(hostServiceConnection);
		}
		
		super.onDestroy();
	}
	
	private void actionClose() {
		hostServiceConnection.service.close();
		
		Intent intent = new Intent(this, ServerSelectActivity.class);
		startActivityIfNeeded(intent, 0);
		
		finish();
	}
	
	private void actionStart() {
		hostServiceConnection.service.start();
		application.createServerConnection("localhost");
		
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(MainActivity.EXTRA_KEY_SERVER_NAME, "Your device");
		startActivity(intent);
		
		finish();
	}

	public class HostServiceConnection implements ServiceConnection
	{
		private boolean serviceBound = false;
		private HostService service = null;

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1)
		{
			serviceBound = true;
			service = ((ProvidingBinder) arg1).getService();
			
			if(actionCode == ACTION_CLOSE) {
				actionClose();
			} else if(actionCode == ACTION_START) {
				actionStart();
			}
			actionCode = ACTION_NONE;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0)
		{
			serviceBound = false;
			service = null;
		}
	}
}
