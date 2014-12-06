package sk.hackcraft.multibox2.android.client.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ThreadFactory;

import sk.hackcraft.multibox2.model.Server;
import sk.hackcraft.multibox2.model.Server.ServerInfoListener;
import sk.hackcraft.multibox2.net.AutoManagingAsynchronousSocketInterface;
import sk.hackcraft.multibox2.net.NetworkServerInterface;
import sk.hackcraft.multibox2.net.NetworkStandards;
import sk.hackcraft.multibox2.net.ServerInterface;
import sk.hackcraft.netinterface.connection.AsynchronousMessageInterface;
import sk.hackcraft.netinterface.connection.MessageInterface;
import sk.hackcraft.netinterface.connection.MessageInterfaceFactory;
import sk.hackcraft.netinterface.connection.SocketMessageInterface;
import sk.hackcraft.util.SimpleEventLoop;
import sk.nixone.discovery.Broadcaster;
import sk.nixone.discovery.NewHostFoundNotifier;

public class ServerBroadcastHostDiscovery implements HostDiscovery
{
	private Broadcaster broadcaster;
	
	private DiscoveryListener listener = null;
	
	private NewHostFoundNotifier hostFoundListener = new NewHostFoundNotifier()
	{
		@Override
		public void onNewHostFound(InetAddress address)
		{
			final DiscoveryListener localListener = ServerBroadcastHostDiscovery.this.listener;
			
			if(localListener != null) {
				final String serverAddress = address.getCanonicalHostName();
				
				getServerName(serverAddress, new ServerInfoListener()
				{
					@Override
					public void onServerNameReceived(String serverName)
					{
						localListener.onHostFound(serverAddress, serverName);
					}
				});
				
			}
		}
	};
	
	public ServerBroadcastHostDiscovery() {
		broadcaster = new Broadcaster(NetworkStandards.DISCOVERY_PROTOCOL, hostFoundListener);
	}
	
	@Override
	public void start(final DiscoveryListener listener)
	{
		this.listener = listener;
		
		new Thread(new Runnable() {
			@Override
			public void run()
			{	
				try {
					broadcaster.start(new ThreadFactory()
					{
						@Override
						public Thread newThread(Runnable arg0)
						{
							return new Thread(arg0);
						}
					});
					
					listener.onStarted();
					Thread.sleep(NetworkStandards.DISCOVERY_TIMEOUT);
					broadcaster.stop();
					listener.onEnded();
				} catch(IOException e) {
					return;
				} catch(InterruptedException e) {
					return;
				}
			}
			
		}).start();
	}
	
	public void getServerName(final String serverAddress, ServerInfoListener listener) {
		SystemLog log = new SystemLog();
		SimpleEventLoop eventLoop = SimpleEventLoop.startInThread();
		MessageInterfaceFactory factory = new MessageInterfaceFactory()
		{
			@Override
			public MessageInterface create() throws IOException
			{
				Socket socket = new Socket(serverAddress, NetworkStandards.SOCKET_PORT);
				return new SocketMessageInterface(socket);
			}
		};
		
		AsynchronousMessageInterface messageInterface = new AutoManagingAsynchronousSocketInterface(factory, eventLoop, log);
		ServerInterface serverInterface = new NetworkServerInterface(messageInterface, eventLoop);
		
		Server server = new Server(serverInterface, eventLoop, log);
		
		server.requestInfo(listener);
	}
}
