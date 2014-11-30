package sk.hackcraft.multibox2.net;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import sk.hackcraft.netinterface.connection.AsynchronousMessageInterface;
import sk.hackcraft.netinterface.connection.IncomingMessagesRouter;
import sk.hackcraft.netinterface.connection.MessageInterface;
import sk.hackcraft.netinterface.connection.MessageInterfaceFactory;
import sk.hackcraft.netinterface.message.Message;
import sk.hackcraft.netinterface.message.MessageReceiver;
import sk.hackcraft.netinterface.message.MessageType;
import sk.hackcraft.util.Log;
import sk.hackcraft.util.MessageQueue;

public class AutoManagingAsynchronousSocketInterface implements AsynchronousMessageInterface
{
	private AsynchronousMessageInterface.SeriousErrorListener seriousErrorListener;

	private final MessageInterfaceFactory messageInterfaceFactory;
	
	private final MessageQueue messageQueue;
	private final Log log;
	
	private BlockingQueue<MessageEntry> pendingMessages;
	private IncomingMessagesRouter incomingMessagesRouter;

	private MessageInterface messageInterface;
	
	private Thread senderWorkerThread;
	private Thread receiveWorkerThread;
	private Thread usageGuarderThread;
	
	private final Object networkingStateChangeLock;
	
	private boolean active;
	private boolean connecting;
	
	private volatile long lastNetworkActivity;
	private static final long NETWORK_INACTIVITY_TIMEOUT = TimeUnit.SECONDS.toMillis(15);
	
	private volatile boolean shouldClose;
	
	public AutoManagingAsynchronousSocketInterface(MessageInterfaceFactory messageInterfaceFactory, MessageQueue messageQueue, Log log)
	{
		this.messageInterfaceFactory = messageInterfaceFactory;
		
		this.messageQueue = messageQueue;
		this.log = log;
		
		this.pendingMessages = new LinkedBlockingQueue<MessageEntry>();
		this.incomingMessagesRouter = new IncomingMessagesRouter();
		
		this.active = false;
		this.connecting = false;
		
		networkingStateChangeLock = new Object();
		
		this.shouldClose = false;
	}
	
	@Override
	public synchronized void close()
	{
		if (active)
		{
			shouldClose = true;
			usageGuarderThread.interrupt();
		}
	}
	
	@Override
	public void setSeriousErrorListener(SeriousErrorListener listener)
	{
		this.seriousErrorListener = listener;
	}

	@Override
	public void sendMessage(Message message)
	{
		sendMessage(message, null);
	}
	
	@Override
	public void sendMessage(Message message, MessageSendListener listener)
	{
		MessageEntry entry = new MessageEntry(message, listener);
		pendingMessages.add(entry);
		
		onNewPendingMessage();
	}

	@Override
	public void setMessageReceiver(MessageType messageType, MessageReceiver receiver)
	{
		incomingMessagesRouter.setReceiver(messageType, receiver);
	}
	
	private void updateLastNetworkActivityTimeStamp()
	{
		long actualTime = System.currentTimeMillis();
		
		if (actualTime > lastNetworkActivity)
		{
			lastNetworkActivity = actualTime;
		}
	}

	private void onNewPendingMessage()
	{
		log.print("New pending message.");
		
		updateLastNetworkActivityTimeStamp();
		
		checkAndSpawnNetworking();
	}
	
	private synchronized void checkAndSpawnNetworking()
	{
		if (active || connecting)
		{
			return;
		}
		
		log.print("Starting networking...");
		
		connecting = true;
		
		Runnable networkingInitCode = new Runnable()
		{
			@Override
			public void run()
			{
				synchronized (networkingStateChangeLock)
				{
					try
					{
						lastNetworkActivity = System.currentTimeMillis();

						messageInterface = messageInterfaceFactory.create();
						
						usageGuarderThread = new Thread(new UsageGuarder());
						usageGuarderThread.start();
						
						senderWorkerThread = new Thread(new SenderWorker());
						senderWorkerThread.start();
						
						receiveWorkerThread = new Thread(new ReceiveWorker());
						receiveWorkerThread.start();
						
						log.print("Networking was started.");
						
						synchronized (AutoManagingAsynchronousSocketInterface.this)
						{
							active = true;
						}
					}
					catch (Exception e)
					{
						log.print("Serious error: " + e.getMessage());
						
						if (seriousErrorListener != null)
						{
							seriousErrorListener.onSeriousError(e.getMessage());
						}
					}
					finally
					{
						synchronized (AutoManagingAsynchronousSocketInterface.this)
						{
							connecting = false;
						}
					}
				}
			}
		};
		
		new Thread(networkingInitCode).start();
	}
	
	private synchronized void cleanNetworking()
	{
		if (!active)
		{
			return;
		}
		
		log.print("Stopping networking...");
		
		active = false;
		
		Runnable networkingCleanupCode = new Runnable()
		{
			@Override
			public void run()
			{
				synchronized (networkingStateChangeLock)
				{
					senderWorkerThread.interrupt();
					receiveWorkerThread.interrupt();
					usageGuarderThread.interrupt();
					
					try
					{
						messageInterface.close();
					}
					catch (IOException e)
					{
						throw new RuntimeException(e);
					}
					
					try
					{
						senderWorkerThread.join();
						receiveWorkerThread.join();
					}
					catch (InterruptedException e)
					{
						throw new RuntimeException(e);
					}
					
					senderWorkerThread = null;
					receiveWorkerThread = null;
					usageGuarderThread = null;
					
					log.print("Networking was stopped.");
				}
			}
		};
		
		new Thread(networkingCleanupCode).start();
	}
	
	private class MessageEntry
	{
		private final Message message;
		private final MessageSendListener sendListener;
		
		public MessageEntry(Message message, MessageSendListener sendListener)
		{
			this.message = message;
			this.sendListener = sendListener;
		}
		
		public Message getMessage()
		{
			return message;
		}
		
		public MessageSendListener getSendListener()
		{
			return sendListener;
		}
	}
	
	private class SenderWorker implements Runnable
	{
		@Override
		public void run()
		{
			while (true)
			{
				MessageEntry entry;
				
				try
				{
					entry = pendingMessages.take();
				}
				catch (InterruptedException e)
				{
					log.print("Sender worker interrupted.");
					
					return;
				}
				
				Message message = entry.getMessage();
				
				boolean sendingResult;
				try
				{
					messageInterface.sendMessage(message);
					sendingResult = true;
					
					log.print("Message " + message.getType() + " sended.");
				}
				catch (IOException e)
				{
					log.print("Sender worker problem: " + e.getMessage());
					
					sendingResult = false;
				}

				final MessageSendListener listener = entry.getSendListener();
				if (listener != null)
				{
					final boolean finalSendingResult = sendingResult;
					
					messageQueue.post(new Runnable()
					{
						@Override
						public void run()
						{
							listener.onFinish(finalSendingResult);
						}
					});
				}
				
				if (!sendingResult)
				{
					cleanNetworking();
					return;
				}
			}
		};
	}
	
	private class ReceiveWorker implements Runnable
	{
		@Override
		public void run()
		{
			while (true)
			{
				Message message;
				
				try
				{
					message = messageInterface.waitForMessage();
					
					updateLastNetworkActivityTimeStamp();
					
					MessageType type = message.getType();
					byte content[] = message.getContent();
					
					log.print("Message " + type + " received.");
					
					incomingMessagesRouter.receiveMessage(type, content);
				}
				catch (IOException e)
				{
					log.print("Receive worker problem: " + e.getMessage());
					
					if (!Thread.currentThread().isInterrupted())
					{
						cleanNetworking();
					}
					
					return;
				}
			}
		}
	}
	
	private class UsageGuarder implements Runnable
	{
		@Override
		public void run()
		{
			boolean run = true;
			
			while (run)
			{
				if (shouldClose)
				{
					log.print("Close request.");
					
					cleanNetworking();
					return;
				}
				else if (lastNetworkActivity + NETWORK_INACTIVITY_TIMEOUT < System.currentTimeMillis())
				{
					log.print("Network is inactive for long time.");
					
					cleanNetworking();
					return;
				}
				else
				{
					try
					{
						long sleepMillis = (lastNetworkActivity + NETWORK_INACTIVITY_TIMEOUT) - System.currentTimeMillis();
						
						if (sleepMillis < 0)
						{
							sleepMillis = 0;
						}
						
						Thread.sleep(sleepMillis);
					}
					catch (InterruptedException e)
					{
					}
				}
			}
		}
	}
}
