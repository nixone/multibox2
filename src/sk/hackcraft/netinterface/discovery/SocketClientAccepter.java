package sk.hackcraft.netinterface.discovery;

import java.io.IOException;
import java.net.Socket;

import sk.hackcraft.util.MessageQueue;

public class SocketClientAccepter<I> implements ClientAccepter<I>
{
	private final MessageQueue eventQueue;
	private EventListener<I> listener;
	private final SocketClientInterfaceFactory<I> clientInterfaceFactory;
	
	private final int listeningPort;
	
	private AcceptingWorker acceptingWorker;
	private Thread acceptingWorkerThread;
	
	public SocketClientAccepter(MessageQueue eventQueue, int listeningPort, SocketClientInterfaceFactory<I> clientInterfaceFactory)
	{
		this.eventQueue = eventQueue;
		this.listeningPort = listeningPort;
		
		listener = new ClientAccepterEventAdapter<I>();
		
		this.clientInterfaceFactory = clientInterfaceFactory;
	}
	
	@Override
	public void setEventListener(ClientAccepter.EventListener<I> listener)
	{
		this.listener = listener;
	}
	
	@Override
	public void start()
	{
		if (acceptingWorkerThread != null)
		{
			throw new RuntimeException("Client accepter already started.");
		}
		
		acceptingWorker = new AcceptingWorker();
		acceptingWorkerThread = new Thread(acceptingWorker);
		acceptingWorkerThread.start();
	}

	@Override
	public void stop()
	{
		if (acceptingWorkerThread == null)
		{
			throw new RuntimeException("Client accepter was not running.");
		}
		
		//acceptingWorker.stop();
		
		acceptingWorker = null;
		acceptingWorkerThread = null;
	}
	
	private void onClientConnected(final Socket socket)
	{
		eventQueue.post(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					I clientInterface = clientInterfaceFactory.create(socket);
					listener.onClientAccepted(clientInterface);
				}
				catch (IOException e)
				{
				}
			}
		});
	}
	
	public interface SocketClientInterfaceFactory<I>
	{
		public I create(Socket socket) throws IOException;
	}
	
	private class AcceptingWorker implements Runnable
	{
		@Override
		public void run()
		{
			/*while (!isStopped())
			{
				ServerSocket serverSocket = null;
				
				try
				{
					serverSocket = new ServerSocket(listeningPort);
					
					while (!isStopped())
					{
						Socket socket = serverSocket.accept();
						
						onClientConnected(socket);
					}
				}
				catch (IOException e)
				{
				}
				finally
				{
					if (serverSocket != null)
					{
						try
						{
							serverSocket.close();
						}
						catch (IOException e)
						{
						}
					}
				}
			}*/
		}
	}
}
