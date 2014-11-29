package sk.hackcraft.netinterface.discovery;

import java.net.InetAddress;

import sk.hackcraft.util.MessageQueue;

public class UDPServerFinder implements ServerFinder
{	
	private final int broadcastPort;

	private final MessageQueue eventQueue;
	private EventListener listener;
	
	private boolean started = false;
	
	private ListenWorker listenWorker;
	private Thread listenWorkerThread;
	
	public UDPServerFinder(MessageQueue eventQueue, int broadcastPort)
	{
		this.eventQueue = eventQueue;
		
		this.broadcastPort = broadcastPort;
	}
	
	@Override
	public void setEventListener(EventListener listener)
	{
		this.listener = listener;
	}
	
	@Override
	public void start()
	{
		if (started)
		{
			return;
		}
		
		started = true;
		
		listenWorker = new ListenWorker();
		listenWorkerThread = new Thread(listenWorker);
		listenWorkerThread.start();
	}

	@Override
	public void stop()
	{
		if (!started)
		{
			return;
		}
		
		started = false;
		
		//listenWorker.stop();
	}
	
	private void onBeaconReceived(final InetAddress address, final String name)
	{
		eventQueue.post(new Runnable()
		{
			@Override
			public void run()
			{
				listener.onServerBeaconReceived(name, address);
			}
		});
	}
	
	private class ListenWorker implements Runnable
	{
		@Override
		public void run()
		{			
			/*while (!isStopped())
			{
				DatagramSocket socket = null;
				try
				{
					socket = new DatagramSocket(broadcastPort);
					socket.setBroadcast(true);
					
					byte data[] = new byte[1];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					
					while (!isStopped())
					{
						socket.receive(packet);

						InetAddress address = packet.getAddress();
						
						onBeaconReceived(address, "Generic Name");
					}
				}
				catch (Exception e)
				{	
					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException ie)
					{
					}
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
	};
}
