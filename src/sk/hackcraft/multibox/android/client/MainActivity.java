package sk.hackcraft.multibox.android.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import sk.hackcraft.multibox.android.client.util.ActivityTransitionAnimator;
import sk.hackcraft.multibox.model.Server;
import sk.hackcraft.multibox.android.client.R;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity implements BackPressedEvent
{
	private static final String SAVED_STATE_KEY_ACTIVE_TAB = "activeTab";
	private static final String SAVED_STATE_KEY_SERVER_NAME = "serverName";
	
	public static final String EXTRA_KEY_SERVER_NAME = "serverName";
	
	private Server server;
	
	private ViewPager viewPager;
	private TabsAdapter tabsAdapter;
	
	private ActionBar actionBar;
	
	private List<BackPressedListener> backPressedListeners;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		String serverName = intent.getStringExtra(EXTRA_KEY_SERVER_NAME);

		MultiBoxApplication application = (MultiBoxApplication)getApplication();
		server = application.getServer();
		
		backPressedListeners = new LinkedList<BackPressedListener>();
		
		actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setTitle(serverName);
		
		setContentView(R.layout.activity_main);

		viewPager = (ViewPager)findViewById(R.id.pager);
		
		tabsAdapter = new TabsAdapter(this, viewPager);

		Tab tab;
		
		tab = actionBar.newTab();
		tab.setText(R.string.playlist);
		tabsAdapter.addTab(tab, PlayerFragment.class);
		
		tab = actionBar.newTab();
		tab.setText(R.string.library_server);
		tabsAdapter.addTab(tab, ServerLibraryFragment.class);
		
		tab = actionBar.newTab();
		tab.setText(R.string.library_local);
		tabsAdapter.addTab(tab, LocalLibraryFragment.class);

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			@Override
			public void onPageScrollStateChanged(int state)
			{
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{
			}

			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
			}
		});
		
		viewPager.setAdapter(tabsAdapter);
		
		if (savedInstanceState != null)
		{
			restoreState(savedInstanceState);
		}
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		if (isFinishing())
		{
			ActivityTransitionAnimator.runFinishActivityAnimation(this);
		}
	}
	
	private void restoreState(Bundle savedInstanceState)
	{
		int activeTab = savedInstanceState.getInt(SAVED_STATE_KEY_ACTIVE_TAB);
		actionBar.setSelectedNavigationItem(activeTab);
		
		String serverName = savedInstanceState.getString(SAVED_STATE_KEY_SERVER_NAME);
		actionBar.setTitle(serverName);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.playlist, menu);
		return true;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		
		outState.putInt(SAVED_STATE_KEY_ACTIVE_TAB, actionBar.getSelectedNavigationIndex());
		outState.putString(SAVED_STATE_KEY_SERVER_NAME, (String)actionBar.getTitle());
	}
	
	@Override
	protected void onRestart()
	{
		super.onRestart();
		
		server.requestInfo(new Server.ServerInfoListener()
		{
			@Override
			public void onServerNameReceived(String name)
			{
				actionBar.setTitle(name);
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed()
	{
		Fragment fragment = getCurrentVisibleViewPagerFragment(Fragment.class);
		
		boolean backPressConsumed = false;
		
		if (fragment instanceof BackPressedListener)
		{
			BackPressedListener listener = (BackPressedListener)fragment;
			
			backPressConsumed = listener.onBackPressed();
		}
		
		if (!backPressConsumed)
		{
			super.onBackPressed();
		}
	}
	
	@Override
	public void registerBackPressedListener(BackPressedListener listener)
	{
		backPressedListeners.add(listener);
	}
	
	@Override
	public void unregisterBackPressedListener(BackPressedListener listener)
	{
		backPressedListeners.remove(listener);
	}
	
	private <F extends Fragment> F getCurrentVisibleViewPagerFragment(Class<F> fragmentClass)
	{
		int position = viewPager.getCurrentItem();
		return getViewPagerFragment(position, fragmentClass);
	}
	
	private <F extends Fragment> F getViewPagerFragment(int position, Class<F> fragmentClass)
	{
		FragmentManager fragmentManager = getFragmentManager();
		
		String tag = createViewPagerFragmentTag(position);
		Fragment fragment = fragmentManager.findFragmentByTag(tag);
		
		return fragmentClass.cast(fragment);
	}
	
	private String createViewPagerFragmentTag(int position)
	{
		return "android:switcher:" + R.id.pager + ":" + position;
	}
	
	private class TabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener
	{
		private final List<Class<? extends Fragment>> fragmentClasses;
		
		public TabsAdapter(Activity activity, ViewPager pager)
		{
			super(activity.getFragmentManager());
			
			fragmentClasses = new ArrayList<Class<? extends Fragment>>();
		}
		
		public void addTab(Tab tab, Class<? extends Fragment> fragmentClass)
		{
			fragmentClasses.add(fragmentClass);
			
			tab.setTag(fragmentClass);
			tab.setTabListener(this);
			
			actionBar.addTab(tab);
			
			notifyDataSetChanged();
		}

		@Override
		public Fragment getItem(int position)
		{
			// TODO change to that way, that fragments will be instantiated in activity and position will be enum
			Class<? extends Fragment> fragmentClass = fragmentClasses.get(position);
			
			return Fragment.instantiate(MainActivity.this, fragmentClass.getName());
		}

		@Override
		public int getCount()
		{
			return actionBar.getTabCount();
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft)
		{
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft)
		{
			Class<? extends Fragment> fragmentClass = (Class<? extends Fragment>)tab.getTag();
			
			for (int i = 0; i < fragmentClasses.size(); i++)
			{
				if (fragmentClasses.get(i) == fragmentClass)
				{
					viewPager.setCurrentItem(i);
					break;
				}
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft)
		{
		}
	}
}
