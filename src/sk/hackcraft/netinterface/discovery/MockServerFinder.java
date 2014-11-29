package sk.hackcraft.netinterface.discovery;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

import sk.hackcraft.util.MessageQueue;


public class MockServerFinder implements ServerFinder
{
	private final MessageQueue eventQueue;
	private EventListener listener;
	
	private DiscoveryWorker worker;
	private Thread workerThread;

	public MockServerFinder(MessageQueue eventQueue)
	{
		this.eventQueue = eventQueue;
		
		this.listener = new ServerFinderEventAdapter();
	}
	
	@Override
	public void setEventListener(EventListener listener)
	{
		this.listener = listener;
	}
	
	@Override
	public void start()
	{
		worker = new DiscoveryWorker();

		workerThread = new Thread(worker);
		workerThread.start();
	}

	@Override
	public void stop()
	{
		worker.stop();
	}

	private class DiscoveryWorker implements Runnable
	{
		private final Random random;
		
		private volatile boolean run;
		
		public DiscoveryWorker()
		{
			random = new Random();
			
			run = true;
		}
		
		public void stop()
		{
			run = false;
		}
		
		@Override
		public void run()
		{
			while (run)
			{
				int delay = random.nextInt(2000) + 200;
				String names[] = {
						"1.2.3.4",
						"1.2.3.6",
						"1.2.3.5",
						"google.sk",
						"localhost",
				};
				
				try
				{
					Thread.sleep(delay);
				}
				catch (InterruptedException e)
				{
				}
				
				int maxServersCount = names.length;
				int index = random.nextInt(maxServersCount);
				
				final String name = Integer.toString(index);
				final String serverName = names[index];
				final InetAddress address;
				try
				{
					address = InetAddress.getByName(serverName);
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
				
				eventQueue.post(new Runnable()
				{
					@Override
					public void run()
					{
						listener.onServerBeaconReceived(name, address);
					}
				});
			}
		}
	}
}
