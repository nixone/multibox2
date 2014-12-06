package sk.nixone.discovery;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class NetworkUtilities
{
	static public List<InetAddress> getBroadcastAddresses() {
		LinkedList<InetAddress> broadcastAddresses = new LinkedList<InetAddress>();
		
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			
			while(interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				
				try {
					if(iface.isLoopback() || !iface.isUp()) {
						continue;
					}
					
					for(InterfaceAddress address : iface.getInterfaceAddresses()) {
						InetAddress broadcast = address.getBroadcast();
						if(broadcast != null) {
							broadcastAddresses.add(broadcast);
						}
					}
				} catch(IOException e) {
					e.printStackTrace();
					// just ignore this interface
					continue;
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
			// nothing to do here... just send an empty broadcast addresses
		}
			
		return broadcastAddresses;
	}
}
