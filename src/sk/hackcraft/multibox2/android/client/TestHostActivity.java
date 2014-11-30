package sk.hackcraft.multibox2.android.client;

import sk.hackcraft.multibox2.android.client.HostService.ProvidingBinder;
import sk.hackcraft.multibox2.model.host.Player;
import sk.hackcraft.multibox2.model.host.Song;
import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TestHostActivity extends Activity
{
	private Player.Listener playerListener = new Player.Listener()
	{
		@Override
		public void onSongStarted(Player player, Song song)
		{
			nowPlaying.setText(song.getName());
			numSongs.setText(String.valueOf(player.getSize()));
		}
		
		@Override
		public void onSongFinished(Player player, Song song)
		{
			nowPlaying.setText("nothing");
			numSongs.setText(String.valueOf(player.getSize()));
		}
		
		@Override
		public void onSongAdded(Player player, Song song)
		{
			numSongs.setText(String.valueOf(player.getSize()));
		}
	};
	
	private ServiceConnection serviceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1)
		{
			serviceBound = true;
			service = ((ProvidingBinder)arg1).getService();
			service.getPlayer().addListener(playerListener);
			service.getPlayer().play();
		}
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			serviceBound = false;
			service = null;
		}
	};
	
	private boolean serviceBound = false;
	private HostService service = null;
	
	private Button playButton;
	private Button pauseButton;
	private TextView numSongs;
	private TextView nowPlaying;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_host);
		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		/*
		startService(new Intent(this, HostService.class));
		bindService(new Intent(this, HostService.class), serviceConnection, Context.BIND_AUTO_CREATE);
		
		Log.e("ACTIVITY", "Binding to service");*/
		
		//new NetworkingTestingThread().start();
		new PlayingTestingThread().start();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(serviceBound) {
			service.getPlayer().pause();
			service.getPlayer().removeListener(playerListener);
			serviceBound = false;
			unbindService(serviceConnection);
			Log.e("ACTIVITY", "Unbinding from service");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_host, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	static public class PlaceholderFragment extends Fragment
	{		
		public PlaceholderFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			final TestHostActivity activity = (TestHostActivity)getActivity();
			
			View rootView = inflater.inflate(R.layout.fragment_test_host,
					container, false);

			activity.playButton = (Button)rootView.findViewById(R.id.button_play);
			activity.pauseButton = (Button)rootView.findViewById(R.id.button_pause);
			activity.numSongs = (TextView)rootView.findViewById(R.id.num_songs);
			activity.nowPlaying = (TextView)rootView.findViewById(R.id.now_playing);
		
			activity.playButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					if(activity.serviceBound) {
						activity.service.getPlayer().play();
					}
				}
			});
			
			activity.pauseButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					if(activity.serviceBound) {
						activity.service.getPlayer().pause();
					}
				}
			});		
			
			return rootView;
		}
	}
}
