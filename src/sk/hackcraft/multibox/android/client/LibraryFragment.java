package sk.hackcraft.multibox.android.client;

import java.util.List;
import java.util.Stack;

import sk.hackcraft.multibox.model.Library;
import sk.hackcraft.multibox.model.LibraryItem;
import sk.hackcraft.multibox.model.LibraryItemType;
import sk.hackcraft.multibox.model.Playlist;
import sk.hackcraft.multibox.model.libraryitems.DirectoryItem;
import sk.hackcraft.multibox.android.client.R;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public abstract class LibraryFragment extends Fragment implements BackPressedListener
{
	private static final String SAVED_STATE_KEY_HISTORY = "history";
	
	private Library library;
	private LibraryListener libraryListener;
	
	private Playlist playlist;
	
	private ListView contentView;
	
	private DirectoryContentAdapter contentAdapter;

	private Stack<Long> history;
	
	BackPressedEvent backPressedEvent;
	private BackPressedListener backPressedListener;
	
	public abstract Library getLibrary();
	public abstract void onMultimediaItemSelected(long multimediaId);
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		backPressedEvent = (BackPressedEvent)activity;
		backPressedListener = new BackPressedListener()
		{
			@Override
			public boolean onBackPressed()
			{
				if (canNavigateBack())
				{
					navigateBack();
					return true;
				}
				else
				{
					return false;
				}
			}
		};
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		
		Activity activity = getActivity();
		library = getLibrary();
		
		libraryListener = new LibraryListener();
		library.registerLibraryEventListener(libraryListener);
		
		contentAdapter = new DirectoryContentAdapter(activity);
		
		history = new Stack<Long>();

		if (savedInstanceState != null)
		{
			long historyEntries[] = savedInstanceState.getLongArray(SAVED_STATE_KEY_HISTORY);
			for (Long id : historyEntries)
			{
				history.push(id);
			}
		}
		else
		{
			history.push(Library.ROOT_DIRECTORY);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View layout = inflater.inflate(R.layout.fragment_library, container, false);
		
		contentView = (ListView)layout.findViewById(R.id.directory_content);
		
		return layout;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		contentView.setAdapter(contentAdapter);
		contentView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				LibraryItem item = contentAdapter.getItem(position);

				long itemId = item.getId();
				
				switch (item.getType())
				{
					case DIRECTORY:
						history.push(itemId);
						requestDirectory(itemId);
						break;
					case MULTIMEDIA:
						String messageTemplate = getString(R.string.notice_queying_multimedia);
						String message = String.format(messageTemplate, item.getName());
						Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
						
						onMultimediaItemSelected(itemId);
						break;
					case BACK_NAVIGATION:
						navigateBack();
						break;
				}
			}
		});
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		library.init();
		
		if (!history.isEmpty())
		{
			long id = history.peek();
			library.requestItem(id);
		}
		
		
		backPressedEvent.registerBackPressedListener(backPressedListener);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		backPressedEvent.unregisterBackPressedListener(backPressedListener);
		
		library.close();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		library.unregisterLibraryEventListener(libraryListener);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		
		long historyEntries[] = new long[history.size()];
		for (int i = history.size() - 1; i >= 0; i--)
		{
			historyEntries[i] = history.get(i);
		}
		
		outState.putLongArray(SAVED_STATE_KEY_HISTORY, historyEntries);
	}
	
	private boolean canNavigateBack()
	{
		return history.size() > 1;
	}
	
	private void navigateBack()
	{
		history.pop();
		
		if (!history.isEmpty())
		{
			long id = history.peek();
			requestDirectory(id);
		}
	}
	
	@Override
	public boolean onBackPressed()
	{
		if (canNavigateBack())
		{
			navigateBack();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void requestDirectory(long id)
	{
		library.requestItem(id);
	}

	private void setDirectory(DirectoryItem directory)
	{
		if (directory.getId() != history.peek())
		{
			return;
		}
		
		contentAdapter.clear();
		
		List<LibraryItem> items = directory.getEnclosedItems();
		contentAdapter.addAll(items);
		
		contentAdapter.notifyDataSetChanged();
	}
	
	private class DirectoryContentAdapter extends ArrayAdapter<LibraryItem>
	{
		public DirectoryContentAdapter(Context context)
		{
			super(context, R.layout.item_library);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			LibraryItem libraryItem = getItem(position);
			
			Context context = getContext();
			LayoutInflater viewInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			View multimediaItemView = viewInflater.inflate(R.layout.item_library, null);

			TextView multimediaNameView = (TextView)multimediaItemView.findViewById(R.id.library_item_name);
			
			// TODO
			String name;
			if (libraryItem.getType() == LibraryItemType.DIRECTORY)
			{
				name = "[" + libraryItem.getName()+"]";
			}
			else
			{
				name = libraryItem.getName();
			}

			multimediaNameView.setText(name);
			
			return multimediaItemView;
		}
	}
	
	private class LibraryListener implements Library.LibraryEventListener
	{
		@Override
		public void onItemReceived(final LibraryItem item)
		{
			if (item.getType() == LibraryItemType.DIRECTORY)
			{
				DirectoryItem directory = (DirectoryItem)item;
				setDirectory(directory);
			}
			else
			{
				// TODO
			}
		}
	}
}
