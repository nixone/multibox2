package sk.hackcraft.multibox.android.client;

import java.util.LinkedList;

import sk.hackcraft.multibox.android.client.HostService.ProvidingBinder;
import sk.hackcraft.multibox.model.Server;
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

	static protected final String ACTION_EXTRA_KEY = "actionCode";

	static protected final int ACTION_NONE = 0;
	static public final int ACTION_START = 1;
	static public final int ACTION_PLAY = 2;
	static public final int ACTION_PAUSE = 3;
	static public final int ACTION_CLOSE = 4;
	static public final int ACTION_OPEN = 5;

	static public Intent createIntent(Context context, int actionCode)
	{
		Intent intent = new Intent(context, ControlHostServiceActivity.class);
		intent.putExtra(ACTION_EXTRA_KEY, actionCode);
		return intent;
	}

	private LinkedList<Integer> receivedActionCodes = new LinkedList<Integer>();

	private HostServiceConnection hostServiceConnection = new HostServiceConnection();

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		handleActionIntent(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent newIntent)
	{
		super.onNewIntent(newIntent);
		handleActionIntent(newIntent);
	}

	private void handleActionIntent(Intent intent)
	{
		int actionCode = intent.getIntExtra(ACTION_EXTRA_KEY, ACTION_NONE);

		Log.e(TAG, "Received action code " + actionCode);

		receivedActionCodes.add(actionCode);

		processActions();
	}
	
	protected void doActionStart()
	{
		Log.e(TAG, "Starting");
		hostServiceConnection.service.start();
		
		MultiBoxApplication app = (MultiBoxApplication)getApplication();
		app.createServerConnection("localhost");
		
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(MainActivity.EXTRA_KEY_SERVER_NAME, "Your device");
		startActivity(intent);
	}

	protected void doActionPlay()
	{
		Log.e(TAG, "Playing");
		hostServiceConnection.service.play();
	}

	protected void doActionPause()
	{
		Log.e(TAG, "Pausing");
		hostServiceConnection.service.pause();
	}

	protected void doActionClose()
	{
		Log.e(TAG, "Closing");
		hostServiceConnection.service.close();
	}

	protected void doActionOpen()
	{
		Log.e(TAG, "Opening");
		MultiBoxApplication app = (MultiBoxApplication)getApplication();
		Server server = app.getServer();
		
		if(server != null)
		{
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra(MainActivity.EXTRA_KEY_SERVER_NAME, "Your device");
			startActivity(intent);
		}
	}
	
	private void processActions()
	{
		if (!hostServiceConnection.serviceBound)
		{
			return;
		}

		for (int receivedActionCode : receivedActionCodes)
		{
			Log.e(TAG, "Received action " + receivedActionCode);

			switch (receivedActionCode)
			{
				case ACTION_START:
					doActionStart();
					break;
				case ACTION_PLAY:
					doActionPlay();
					break;
				case ACTION_PAUSE:
					doActionPause();
					break;
				case ACTION_CLOSE:
					doActionClose();
					break;
				case ACTION_OPEN:
					doActionOpen();
					break;
				case ACTION_NONE:
				default:
					break;
			}
		}
		receivedActionCodes.clear();
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		startService(new Intent(this, HostService.class));
		bindService(new Intent(this, HostService.class), hostServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop()
	{
		if (hostServiceConnection.serviceBound)
		{
			hostServiceConnection.serviceBound = false;
			unbindService(hostServiceConnection);
		}

		super.onStop();
	}

	private class HostServiceConnection implements ServiceConnection
	{
		private boolean serviceBound = false;
		private HostService service = null;

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1)
		{
			serviceBound = true;
			service = ((ProvidingBinder) arg1).getService();
			service.start();

			processActions();

			ControlHostServiceActivity.this.finish();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0)
		{
			serviceBound = false;
			service = null;
		}
	}
}
