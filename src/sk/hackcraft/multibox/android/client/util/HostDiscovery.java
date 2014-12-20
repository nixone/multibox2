package sk.hackcraft.multibox.android.client.util;

public interface HostDiscovery
{
	public interface DiscoveryListener {
		public void onStarted();
		public void onHostFound(String address, String name);
		public void onEnded();
	}
	
	public void start(DiscoveryListener listener);
}
