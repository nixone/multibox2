package sk.hackcraft.multibox.android.client;

import java.util.List;

import sk.hackcraft.multibox.android.client.util.ActivityTransitionAnimator;
import sk.hackcraft.multibox.android.client.util.HostDiscovery.DiscoveryListener;
import sk.hackcraft.multibox.model.Server;
import sk.hackcraft.multibox.util.SelectedServersStorage;
import sk.hackcraft.multibox.util.SelectedServersStorage.ServerEntry;
import sk.hackcraft.multibox.android.client.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ServerSelectActivity extends Activity
{
	static public final String TAG = ServerSelectActivity.class.getName();
	
	public static final String EXTRA_KEY_DISCONNECT = "disconnect";
	
	public static final String SAVED_STATE_KEY_DISCONNECT_NOTIFICATION = "disconnectNotification";
	
	private MultiBoxApplication application;
	
	private EditText serverAddressInputField;
	private LinearLayout predefinedServersList;
	private LinearLayout lastServersList;
	private LinearLayout discoveredServersList;
	private View disconnectNotification;

	private SelectedServersStorage lastServersStorage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	
		startService(new Intent(this, HostService.class));
	
		setTitle(R.string.select_device);
		
		application = (MultiBoxApplication)getApplication();

		setContentView(R.layout.activity_server_select);
		
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
		
		predefinedServersList = (LinearLayout)findViewById(R.id.predefined_servers_list);
		lastServersList = (LinearLayout)findViewById(R.id.last_servers_list);
		discoveredServersList = (LinearLayout)findViewById(R.id.discovered_servers_list);
		
		createPredefinedServers();
		
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
	}
	
	@Override
	protected void onResume()
	{		
		super.onResume();
		
		if (application.hasActiveConnection())
		{
			application.destroyServerConnection();
		}

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
	
	private void createServerViewInfo(int backgroundResource, LinearLayout layout, final String serverAddress, final String serverName, View.OnClickListener listener)
	{
		LayoutInflater viewInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View serverItemView = viewInflater.inflate(R.layout.item_server, null);
		
		LinearLayout background = (LinearLayout)serverItemView.findViewById(R.id.server_background);
		int pT = background.getPaddingTop(), pB = background.getPaddingBottom(), pL = background.getPaddingLeft(), pR = background.getPaddingRight();
		background.setBackgroundResource(backgroundResource);
		background.setPadding(pL, pT, pR, pB);
		
		TextView serverNameView = (TextView)serverItemView.findViewById(R.id.server_name_view);
		TextView serverAddressView = (TextView)serverItemView.findViewById(R.id.server_address_view);
		
		serverNameView.setText(serverName);
		serverAddressView.setText(serverAddress);
		
		serverItemView.setOnClickListener(listener);
		
		layout.addView(serverItemView);
	}
	
	private void createPredefinedServers()
	{
		createServerViewInfo(
				R.drawable.server_local,
				predefinedServersList, 
				"", 
				"Your device",
				new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						connectToLocalServer();
					}
				}
		);
	}
	
	private void createDiscoveredServerView(final String serverAddress, final String serverName) {
		createServerViewInfo(
				R.drawable.server_online,
				discoveredServersList, 
				serverAddress, 
				serverName,
				new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						onServerSelected(serverAddress);
					}
				}
		);
	}
	
	private void createLastServersList(List<ServerEntry> servers)
	{
		lastServersList.removeAllViews();
		
		for (final ServerEntry server : servers)
		{
			createServerViewInfo(
					R.drawable.server_history,
					lastServersList, 
					server.getAddress(), 
					server.getName(),
					new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							onServerSelected(server.getAddress());
						}
					}
			);
		}
	}
	
	private void onManualConnectRequested()
	{
		String address = serverAddressInputField.getText().toString();
		connectToServer(address);
	}
	
	private void connectToLocalServer() {
		startActivity(ControlHostServiceActivity.createIntent(this, ControlHostServiceActivity.ACTION_START));
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
}
