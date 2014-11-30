package sk.hackcraft.multibox2;

import java.io.IOException;

import sk.hackcraft.multibox2.net.AutoManagingAsynchronousSocketInterface;
import sk.hackcraft.netinterface.connection.AsynchronousMessageInterface;
import sk.hackcraft.netinterface.connection.LoopbackMessageInterface;
import sk.hackcraft.netinterface.connection.MessageInterface;
import sk.hackcraft.netinterface.connection.MessageInterfaceFactory;
import sk.hackcraft.netinterface.message.Message;
import sk.hackcraft.netinterface.message.MessageReceiver;
import sk.hackcraft.netinterface.message.MessageType;
import sk.hackcraft.util.Log;
import sk.hackcraft.util.ManualEventLoop;
import sk.hackcraft.util.PrintStreamLog;

public class TestAMANI
{
	public static void main(String[] args)
	{
		MessageInterfaceFactory messageInterfaceFactory = new MessageInterfaceFactory()
		{
			@Override
			public MessageInterface create() throws IOException
			{
				return new LoopbackMessageInterface();
			}
		};
		
		ManualEventLoop eventLoop = new ManualEventLoop();
		Log log = PrintStreamLog.createNamedDebugLog("ASI");
		
		final Log mainLog = PrintStreamLog.createDebugLog();
		
		mainLog.print("Start.");
		
		AsynchronousMessageInterface asi = new AutoManagingAsynchronousSocketInterface(messageInterfaceFactory, eventLoop, log);
		asi.setSeriousErrorListener(new AsynchronousMessageInterface.SeriousErrorListener()
		{
			@Override
			public void onSeriousError(String errorDescription)
			{
				mainLog.print(errorDescription);
			}
		});
		
		asi.setMessageReceiver(new MessageType()
		{
			@Override
			public int toTypeId()
			{
				return 1;
			}
		}, new MessageReceiver()
		{
			@Override
			public void receive(byte[] content)
			{
				mainLog.print("Received: " + new String(content));
			}
		});
		
		asi.sendMessage(new Message()
		{
			@Override
			public MessageType getType()
			{
				return new MessageType()
				{
					@Override
					public int toTypeId()
					{
						return 1;
					}
				};
			}
			
			@Override
			public byte[] getContent()
			{
				return "hello world".getBytes();
			}
		});
		
		try
		{
			Thread.sleep(20000);
		}
		catch (InterruptedException e)
		{
		}
		
		asi.sendMessage(new Message()
		{
			@Override
			public MessageType getType()
			{
				return new MessageType()
				{
					@Override
					public int toTypeId()
					{
						return 1;
					}
				};
			}
			
			@Override
			public byte[] getContent()
			{
				return "hello world 2".getBytes();
			}
		});
		
		mainLog.print("End.");
	}
}
