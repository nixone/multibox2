package sk.hackcraft.netinterface.discovery;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class UDPServerBeacon implements ServerBeacon
{
	private final int broadcastPort;
	
	private volatile boolean started;
	
	private BroadcastWorker broadcastWorker;
	
	private Thread broadcastWorkerThread;

	private final long delayBetweenBroadcasts = 1000;
	
	public UDPServerBeacon(int broadcastPort)
	{
		this.broadcastPort = broadcastPort;
	}
	
	@Override
	public void start()
	{
		if (broadcastWorkerThread != null)
		{
			throw new RuntimeException("Server beacon is already started.");
		}
		
		started = true;
		
		broadcastWorker = new BroadcastWorker();
		//broadcastWorkerThread = new Thread(broadcastWorker);
		broadcastWorkerThread.start();
	}

	@Override
	public void stop()
	{
		if (broadcastWorkerThread == null)
		{
			throw new RuntimeException("Server beacon is not running.");
		}
		
		//broadcastWorker.stop();
		
		started = false;
	}
	
	private class BroadcastWorker implements Runnable
	{
		@Override
		public void run()
		{
			/*while (!isStopped())
			{
				DatagramSocket socket = null;
				
				try
				{	
					socket = new DatagramSocket();
					socket.setBroadcast(true);
					
					byte data[] = new byte[1];
					data[0] = 1;
					
					List<InetAddress> broadcastAddresses = getBroadcastAddresses();
					
					for (InetAddress broadcastAddress : broadcastAddresses)
					{
						DatagramPacket packet = new DatagramPacket(data, data.length, broadcastAddress, broadcastPort);
						socket.send(packet);
					}
					
					Thread.sleep(delayBetweenBroadcasts);
				}
				catch (Exception e)
				{
				}
				finally
				{
					if (socket != null)
					{
						socket.close();
					}
				}
			}*/
		}
		
		private List<InetAddress> getBroadcastAddresses() throws IOException
		{
			Enumeration<NetworkInterface> networkInterfacesEnumeration = NetworkInterface.getNetworkInterfaces();
			List<NetworkInterface> networkInterfaces = Collections.list(networkInterfacesEnumeration);

			List<InetAddress> broadcastAdresses = new LinkedList<InetAddress>();
			
			for (NetworkInterface networkInterface : networkInterfaces)
			{
				if (networkInterface.isLoopback())
				{
					continue;
				}
				
				if (!networkInterface.isUp())
				{
					continue;
				}
				
				String displayName = networkInterface.getDisplayName();
				
				if (!displayName.contains("wlan0") && !displayName.contains("eth0"))
				{
					continue;
				}
				
				List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
				
				for (InterfaceAddress interfaceAddress : interfaceAddresses)
				{
					InetAddress broadcastAddress = interfaceAddress.getBroadcast();
					
					if (broadcastAddress == null)
					{
						continue;
					}
					
					broadcastAdresses.add(broadcastAddress);
				}
			}
			
			return broadcastAdresses;
		}
	}
}
