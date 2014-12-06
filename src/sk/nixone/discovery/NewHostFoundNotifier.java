package sk.nixone.discovery;

import java.net.InetAddress;
import java.util.HashSet;

import sk.nixone.discovery.Broadcaster.HostFoundListener;

public abstract class NewHostFoundNotifier implements HostFoundListener
{
	private HashSet<InetAddress> previouslySeenHosts = new HashSet<InetAddress>();

	@Override
	public void onHostFound(InetAddress address)
	{
		if(!previouslySeenHosts.contains(address)) {
			previouslySeenHosts.add(address);
			onNewHostFound(address);
		}
	}
	
	abstract public void onNewHostFound(InetAddress address);
}
