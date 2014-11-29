package sk.hackcraft.multibox.util;

import java.util.List;

public interface SelectedServersStorage
{
	public void requestServerSave(String address, String name, SaveResultListener listener);
	public void requestServersList(ServersListListener listener);
	
	public static class ServerEntry
	{
		private String address;
		private String name;

		public ServerEntry(String address, String name)
		{
			this.address = address;
			this.name = name;
		}
		
		public String getAddress()
		{
			return address;
		}
		
		public String getName()
		{
			return name;
		}
	}
	
	public interface SaveResultListener
	{
		public void onResult(boolean saved);
	}
	
	public interface ServersListListener
	{
		public void onServersReceived(List<ServerEntry> servers);
		public void onFailure();
	}
}
