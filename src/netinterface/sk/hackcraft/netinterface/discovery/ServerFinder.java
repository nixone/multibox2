package sk.hackcraft.netinterface.discovery;

import java.net.InetAddress;

public interface ServerFinder
{
	public void setEventListener(EventListener listener);
	
	public void start();
	public void stop();

	public interface EventListener
	{
		public void onServerBeaconReceived(String name, InetAddress address);
	}
}
