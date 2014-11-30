package sk.hackcraft.multibox2.android.client;

import java.io.IOException;
import java.net.Socket;

import sk.hackcraft.multibox2.android.client.util.HandlerEventLoop;
import sk.hackcraft.multibox2.android.client.util.JsonCacheSelectedServersStorage;
import sk.hackcraft.multibox2.android.client.util.SystemLog;
import sk.hackcraft.multibox2.model.Server;
import sk.hackcraft.multibox2.net.AutoManagingAsynchronousSocketInterface;
import sk.hackcraft.multibox2.net.NetworkServerInterface;
import sk.hackcraft.multibox2.net.NetworkStandards;
import sk.hackcraft.multibox2.net.ServerInterface;
import sk.hackcraft.multibox2.util.SelectedServersStorage;
import sk.hackcraft.netinterface.connection.AsynchronousMessageInterface;
import sk.hackcraft.netinterface.connection.MessageInterface;
import sk.hackcraft.netinterface.connection.MessageInterfaceFactory;
import sk.hackcraft.netinterface.connection.SocketMessageInterface;
import sk.hackcraft.util.Log;
import sk.hackcraft.util.MessageQueue;
import android.app.Application;
import android.content.Intent;
 
public class MultiBoxApplication extends Application
{	
	private MessageQueue eventLoop;
	private Log log;
	
	private ServerInterface serverInterface;
	
	private Server server;

	@Override
	public void onCreate()
	{
		super.onCreate();
		
		eventLoop = new HandlerEventLoop();
		log = new SystemLog();
	}
	
	public SelectedServersStorage getSelectedServersStorage()
	{
		return new JsonCacheSelectedServersStorage(this, eventLoop, log);
	}
	
	public void createServerConnection(final String address)
	{
		MessageInterfaceFactory factory = new MessageInterfaceFactory()
		{
			@Override
			public MessageInterface create() throws IOException
			{
				Socket socket = new Socket(address, NetworkStandards.SOCKET_PORT);
				return new SocketMessageInterface(socket);
			}
		};
		
		AsynchronousMessageInterface messageInterface = new AutoManagingAsynchronousSocketInterface(factory, eventLoop, log);
		serverInterface = new NetworkServerInterface(messageInterface, eventLoop);
		serverInterface.registerEventListener(new ServerInterface.ServerInterfaceEventAdapter()
		{
			@Override
			public void onDisconnect()
			{
				destroyServerConnection();
				startConnectActivityAfterDisconnect();
			}
		});
		
		//serverInterface = new MockServerInterface(eventLoop);
		
		server = new Server(serverInterface, eventLoop, log);
	}
	
	public void destroyServerConnection()
	{
		if (serverInterface == null)
		{
			return;
		}
		
		serverInterface.close();
		serverInterface = null;
		
		server = null;
	}
	
	public boolean hasActiveConnection()
	{
		return serverInterface != null;
	}
	
	public Log getLog()
	{
		return log;
	}
	
	public Server getServer()
	{
		return server;
	}
	
	private void startConnectActivityAfterDisconnect()
	{
		Intent intent = new Intent(this, ServerSelectActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		intent.putExtra(ServerSelectActivity.EXTRA_KEY_DISCONNECT, true);
		
		startActivity(intent);
	}
}
