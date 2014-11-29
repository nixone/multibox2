package sk.hackcraft.multibox.model;

public interface Library
{
	public static final long ROOT_DIRECTORY = 0;
	
	public void init();
	public void close();

	public void requestItem(long id);
	
	public void registerLibraryEventListener(LibraryEventListener listener);
	public void unregisterLibraryEventListener(LibraryEventListener listener);
	
	public interface LibraryEventListener
	{
		public void onItemReceived(LibraryItem item);
	}
}
