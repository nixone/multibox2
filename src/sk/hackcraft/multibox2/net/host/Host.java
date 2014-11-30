package sk.hackcraft.multibox2.net.host;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

import android.util.Log;
import sk.hackcraft.netinterface.message.Message;
import sk.hackcraft.netinterface.message.MessageFactory;
import sk.hackcraft.netinterface.message.MessageType;
import sk.hackcraft.netinterface.message.MessageTypeFactory;

public class Host
{
	static private final String TAG = "SocketMessageServer";
	
	static public final int INACTIVITY_SLEEP_TIME = 100;
	
	private class AcceptHandler implements Runnable {
		@Override
		public void run()
		{
			while(true) {
				synchronized(Host.this) {
					if(!running) {
						return;
					}
				}
				try {
					HostClient client = new HostClient(serverSocket.accept());
					
					synchronized(clients) {
						clients.add(client);
					}
				} catch(IOException e) {
					Log.w(TAG, "While accepting new connection", e);
				}
			}
		}
		
	}
	
	private class DataHandler implements Runnable {
		@Override
		public void run()
		{
			while(true) {
				synchronized(Host.this) {
					while(!running) {
						return;
					}
				}
				
				HashSet<HostClient> clientsCopy = null;
				HashSet<HostClient> clientsToBeClosed = new HashSet<HostClient>();
				
				synchronized(clients) {
					clientsCopy = new HashSet<HostClient>(clients);
				}
				
				boolean readSomething = false;
				
				for(HostClient client : clientsCopy) {
					try {
						Message message = null;
						
						while((message = client.tryToReadMessage(messageFactory, messageTypeFactory)) != null) {
							readSomething = true;
							MessageHandler handler = null;
							
							synchronized(messageHandlers) {
								handler = messageHandlers.get(message.getType().toTypeId());
							}
							
							if(handler == null) {
								continue;
							}
							
							Message response = handler.handle(message);
							
							if(response == null) {
								continue;
							}
							
							client.send(response);
						}
					} catch(IOException e) {
						clientsToBeClosed.add(client);
						Log.d(TAG, "Client being disconnected because of data IOException", e);
					}
				}
				
				for(HostClient client : clientsToBeClosed) {
					try {
						synchronized(clients) {
							clients.remove(client);
						}
						client.tryToClose();
					} catch(IOException e) {
						Log.w(TAG, "IO problem while closing client", e);
					}
				}
				
				if(!readSomething) {
					try 
					{
						Thread.sleep(INACTIVITY_SLEEP_TIME);
					} catch(InterruptedException e) {
						Log.w(TAG, "Thread interrupted during sleep.", e);
					}
				}
			}
		}
	}
	
	private int port;
	private boolean running = false;
	
	private Thread acceptThread = null;
	private Thread dataThread = null;
	
	private ServerSocket serverSocket = null;
	
	private Set<HostClient> clients = new HashSet<HostClient>();
	
	private HashMap<Integer, MessageHandler> messageHandlers = new HashMap<Integer, MessageHandler>();
	
	private MessageFactory messageFactory = new MessageFactory();
	private MessageTypeFactory messageTypeFactory = new MessageTypeFactory();
	
	public Host(int port) {
		this.port = port;
	}
	
	public void start(ThreadFactory threadFactory) throws IOException {
		synchronized(this) {
			serverSocket = new ServerSocket(port);
			
			acceptThread = threadFactory.newThread(new AcceptHandler());
			dataThread = threadFactory.newThread(new DataHandler());
			
			acceptThread.start();
			dataThread.start();
			
			running = true;
		}
	}
	
	public void setMessageHandler(MessageType messageType, MessageHandler messageHandler) {
		synchronized(messageHandlers) {
			messageHandlers.put(messageType.toTypeId(), messageHandler);
		}
	}
	
	public void stop() {
		synchronized(this) {
			running = false;
		}
		try {
			acceptThread.join();
			dataThread.join();
		} catch(InterruptedException e) {
			return;
		}
	}
}

