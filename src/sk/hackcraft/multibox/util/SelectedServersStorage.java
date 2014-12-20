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
		
		@Override
		public int hashCode() {
			return address.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof ServerEntry)) {
				return false;
			}
			
			return ((ServerEntry)obj).address.equals(address);
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
