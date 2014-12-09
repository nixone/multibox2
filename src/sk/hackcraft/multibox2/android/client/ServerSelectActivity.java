package sk.hackcraft.multibox2.android.client;

import java.net.InetAddress;
import java.util.List;

import sk.hackcraft.multibox2.android.client.HostService.ProvidingBinder;
import sk.hackcraft.multibox2.android.client.util.ActivityTransitionAnimator;
import sk.hackcraft.multibox2.android.client.util.HostDiscovery.DiscoveryListener;
import sk.hackcraft.multibox2.model.Server;
import sk.hackcraft.multibox2.util.SelectedServersStorage;
import sk.hackcraft.multibox2.util.SelectedServersStorage.ServerEntry;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ServerSelectActivity extends Activity
{
	static public final String TAG = ServerSelectActivity.class.getName();
	
	public static final String EXTRA_KEY_DISCONNECT = "disconnect";
	
	public static final String SAVED_STATE_KEY_DISCONNECT_NOTIFICATION = "disconnectNotification";
	
	private MultiBoxApplication application;
	
	private EditText serverAddressInputField;
	private LinearLayout lastServersList;
	private LinearLayout discoveredServersList;
	private View disconnectNotification;
	private Button localServerButton;

	private SelectedServersStorage lastServersStorage;
	
	private HostServiceConnection hostServiceConnection = new HostServiceConnection();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		application = (MultiBoxApplication)getApplication();

		setContentView(R.layout.activity_server_select);
		
		localServerButton = (Button)findViewById(R.id.local_server);
		localServerButton.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				onServerSelected("localhost");
			}
		});
		
		serverAddressInputField = (EditText)findViewById(R.id.server_address_input_field);
		serverAddressInputField.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if (actionId == EditorInfo.IME_ACTION_GO)
				{
					onManualConnectRequested();
					return true;
				}
				else
				{
					return false;
				}
			}
		});
		
		
		
		disconnectNotification = findViewById(R.id.disconnect_notification);
		
		lastServersList = (LinearLayout)findViewById(R.id.last_servers_list);
		discoveredServersList = (LinearLayout)findViewById(R.id.discovered_servers_list);

		lastServersStorage = application.getSelectedServersStorage();
		
		boolean showDisconnectNotification = false;
		
		Intent startIntent = getIntent();
		if (startIntent.getBooleanExtra(EXTRA_KEY_DISCONNECT, false))
		{
			showDisconnectNotification = true;
		}
		
		if (savedInstanceState != null)
		{
			showDisconnectNotification = savedInstanceState.getBoolean(SAVED_STATE_KEY_DISCONNECT_NOTIFICATION);
		}
		
		if (showDisconnectNotification)
		{
			disconnectNotification.setVisibility(View.VISIBLE);
			disconnectNotification.bringToFront();
		}
		
		Button connectButton = (Button)findViewById(R.id.button_server_connect);
		connectButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onManualConnectRequested();
			}
		});
		
		createAndConnectHostService();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		disconnectHostService();
	}
	
	@Override
	protected void onResume()
	{		
		super.onResume();
		
		if (application.hasActiveConnection())
		{
			application.destroyServerConnection();
		}
		
		View discoveredServersFieldset = findViewById(R.id.discovered_servers_fieldset);
		discoveredServersFieldset.setVisibility(View.GONE);
		discoveredServersList.removeAllViews();
		
		((MultiBoxApplication)getApplication()).requestServerDiscovery(new DiscoveryListener()
		{
			@Override
			public void onStarted() {}
			
			@Override
			public void onHostFound(final String address, final String name)
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						createDiscoveredServerView(address, name);
					}
				});
			}
			
			@Override
			public void onEnded() { }
		});
		
		lastServersStorage.requestServersList(new SelectedServersStorage.ServersListListener()
		{
			@Override
			public void onServersReceived(List<ServerEntry> servers)
			{
				if (servers.isEmpty())
				{
					return;
				}
				
				createLastServersList(servers);
				
				View lastServersFieldset = findViewById(R.id.last_servers_fieldset);
				lastServersFieldset.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onFailure()
			{
			}
		});
	}
	
	@Override
	protected void onRestart()
	{
		super.onRestart();
		
		serverAddressInputField.setText("");
	}
	
	@Override
	public void onUserInteraction()
	{
		super.onUserInteraction();
		
		if (disconnectNotification.getVisibility() == View.VISIBLE)
		{
			disconnectNotification.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		boolean showDisconnectNotification = disconnectNotification.getVisibility() == View.VISIBLE; 
		outState.putBoolean(SAVED_STATE_KEY_DISCONNECT_NOTIFICATION, showDisconnectNotification);
	}

	private void createAndConnectHostService() {
		Log.e(TAG, "Starting and connecting the host service...");
		startService(new Intent(this, HostService.class));
		bindService(new Intent(this, HostService.class), hostServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void disconnectHostService() {
		Log.e(TAG, "Disconnecting host service");
		if(hostServiceConnection.serviceBound) {
			hostServiceConnection.service.getPlayer().pause();
			hostServiceConnection.serviceBound = false;
			unbindService(hostServiceConnection);
		}
	}
	
	private void createDiscoveredServerView(final String serverAddress, final String serverName) {
		View discoveredServersFieldset = findViewById(R.id.discovered_servers_fieldset);
		discoveredServersFieldset.setVisibility(View.VISIBLE);
		
		LayoutInflater viewInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View serverItemView = viewInflater.inflate(R.layout.item_server, null);
		
		TextView serverNameView = (TextView)serverItemView.findViewById(R.id.server_name_view);
		TextView serverAddressView = (TextView)serverItemView.findViewById(R.id.server_address_view);
		
		serverNameView.setText(serverName);
		serverAddressView.setText(serverAddress);
		
		serverItemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onServerSelected(serverAddress);
			}
		});
		
		discoveredServersList.addView(serverItemView);
	}
	
	private void createLastServersList(List<ServerEntry> servers)
	{
		lastServersList.removeAllViews();
		
		for (final ServerEntry server : servers)
		{
			LayoutInflater viewInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			View serverItemView = viewInflater.inflate(R.layout.item_server, null);
			
			TextView serverNameView = (TextView)serverItemView.findViewById(R.id.server_name_view);
			TextView serverAddressView = (TextView)serverItemView.findViewById(R.id.server_address_view);
			
			String name = server.getName();
			serverNameView.setText(name);
			
			String address = server.getAddress();
			serverAddressView.setText(address);
			
			serverItemView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					String address = server.getAddress();
					onServerSelected(address);
				}
			});
			
			lastServersList.addView(serverItemView);
		}
	}
	
	private void onManualConnectRequested()
	{
		String address = serverAddressInputField.getText().toString();
		connectToServer(address);
	}
	
	private void onServerSelected(String address)
	{
		connectToServer(address);
	}
	
	private void connectToServer(final String address)
	{
		application.createServerConnection(address);
		
		Server server = application.getServer();
		server.requestInfo(new Server.ServerInfoListener()
		{
			@Override
			public void onServerNameReceived(String name)
			{
				lastServersStorage.requestServerSave(address, name, new SelectedServersStorage.SaveResultListener()
				{
					@Override
					public void onResult(boolean saved)
					{
					}
				});
				
				startMainActivity(name);
			}
		});
	}
	
	private void startMainActivity(String serverName)
	{
		Intent intent = new Intent(this, MainActivity.class);
		
		intent.putExtra(MainActivity.EXTRA_KEY_SERVER_NAME, serverName);
		
		startActivity(intent);
		ActivityTransitionAnimator.runStartActivityAnimation(this);
	}
	
	public class HostServiceConnection implements ServiceConnection {
		private boolean serviceBound = false;
		private HostService service = null;
		
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1)
		{
			serviceBound = true;
			service = ((ProvidingBinder)arg1).getService();
			service.getPlayer().play();
			
			
		}
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			serviceBound = false;
			service = null;
		}
	}
}
