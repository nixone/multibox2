package sk.hackcraft.netinterface.discovery;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class ServersList
{
	private final long disconnectTimeout;
	
	private Map<InetAddress, Long> servers;
	
	public ServersList(long disconnectTimeout)
	{
		this.disconnectTimeout = disconnectTimeout;
		
		servers = new HashMap<InetAddress, Long>();
	}
	
	public boolean hasServer(InetAddress address)
	{
		return servers.containsKey(address);
	}
	
	public void addServer(InetAddress address)
	{
		long actualTime = System.currentTimeMillis();
		
		servers.put(address, actualTime);
	}
	
	public void updateServerTimeStamp(InetAddress address)
	{
		addServer(address);
	}
	
	public void removeOutdatedServers()
	{
		Iterator<Map.Entry<InetAddress, Long>> it = servers.entrySet().iterator();
		
		while (it.hasNext())
		{
			Map.Entry<InetAddress, Long> entry = it.next();
			
			long actualTime = System.currentTimeMillis();
			long lastServerBeaconTime = entry.getValue();
			
			if (lastServerBeaconTime + disconnectTimeout < actualTime)
			{
				it.remove();
			}
		}
	}

	public void removeAllServers()
	{
		servers.clear();
	}
}