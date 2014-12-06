package sk.hackcraft.multibox2.net;

import sk.nixone.discovery.DiscoveryProtocol;

public class NetworkStandards
{
	public static final int SOCKET_PORT = 13110;
	
	public static final DiscoveryProtocol DISCOVERY_PROTOCOL = new DiscoveryProtocol(13111);
	
	public static final int DISCOVERY_TIMEOUT = 10000;
}
