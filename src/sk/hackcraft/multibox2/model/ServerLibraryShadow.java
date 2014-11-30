package sk.hackcraft.multibox2.model;

import java.util.LinkedList;
import java.util.List;

import sk.hackcraft.multibox2.net.ServerInterface;
import sk.hackcraft.multibox2.net.ServerInterface.ServerInterfaceEventAdapter;
import sk.hackcraft.util.MessageQueue;
import sk.hackcraft.util.PeriodicWorkDispatcher;

public class ServerLibraryShadow implements Library
{	
	private final ServerInterface serverInterface;
	private final MessageQueue messageQueue;
	
	private ServerListener serverListener;
	
	private final List<LibraryEventListener> libraryListeners;
	
	private final PeriodicWorkDispatcher stateChecker;
	
	private long lastRequestedId = Library.ROOT_DIRECTORY;

	public ServerLibraryShadow(ServerInterface serverInterface, MessageQueue messageQueue)
	{
		this.serverInterface = serverInterface;
		this.messageQueue = messageQueue;
		
		this.serverListener = new ServerListener();
		
		libraryListeners = new LinkedList<Library.LibraryEventListener>();
		
		stateChecker = new PeriodicWorkDispatcher(messageQueue, 5000)
		{
			@Override
			protected void doWork()
			{
				ServerLibraryShadow.this.serverInterface.requestLibraryItem(lastRequestedId);
			}
		};
	}
	
	@Override
	public void init()
	{
		serverInterface.registerEventListener(serverListener);
		
		stateChecker.start();
	}

	@Override
	public void close()
	{
		serverInterface.unregisterEventListener(serverListener);
		
		stateChecker.stop();
	}

	@Override
	public void requestItem(long id)
	{
		serverInterface.requestLibraryItem(id);
		
		lastRequestedId = id;
	}

	@Override
	public void registerLibraryEventListener(LibraryEventListener listener)
	{
		libraryListeners.add(listener);
	}

	@Override
	public void unregisterLibraryEventListener(LibraryEventListener listener)
	{
		libraryListeners.remove(listener);
	}
	
	private void notifyLibraryItemReceived(final LibraryItem item)
	{
		for (final Library.LibraryEventListener listener : libraryListeners)
		{
			messageQueue.post(new Runnable()
			{
				@Override
				public void run()
				{
					listener.onItemReceived(item);
				}
			});
		}
	}
	
	private class ServerListener extends ServerInterfaceEventAdapter
	{
		@Override
		public void onLibraryItemReceived(LibraryItem item)
		{
			notifyLibraryItemReceived(item);
		}
	}
}
