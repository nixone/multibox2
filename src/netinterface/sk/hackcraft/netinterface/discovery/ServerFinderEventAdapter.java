package sk.hackcraft.netinterface.discovery;

import java.net.InetAddress;

import sk.hackcraft.netinterface.discovery.ServerFinder.EventListener;

public class ServerFinderEventAdapter implements EventListener
{
	@Override
	public void onServerBeaconReceived(String name, InetAddress address)
	{
	}
}
