package sk.hackcraft.util;

import java.util.concurrent.PriorityBlockingQueue;

public class SimpleEventLoop implements Runnable, MessageQueue
{	
	public static SimpleEventLoop startInThread()
	{
		SimpleEventLoop eventLoop = new SimpleEventLoop();
		new Thread(eventLoop).start();
		
		return eventLoop;
	}
	
	private boolean run;
	
	private long nextId;
	private long lastTimeStamp;
	private PriorityBlockingQueue<Message> messages;
	
	public SimpleEventLoop()
	{
		this.run = true;
		this.nextId = 1;
		this.lastTimeStamp = System.currentTimeMillis();
		
		messages = new PriorityBlockingQueue<Message>();
	}
	
	private long generateId()
	{
		return nextId++;
	}
	
	private long generateRunTime(long delay)
	{
		long timeStamp = System.currentTimeMillis();
		if (timeStamp < lastTimeStamp)
		{
			timeStamp = lastTimeStamp;
		}
		else
		{
			lastTimeStamp = timeStamp;
		}
		
		return timeStamp + delay;
	}
	
	@Override
	public void post(Runnable runnable)
	{
		long id = generateId();
		long runTime = generateRunTime(0);
		
		lastTimeStamp = runTime;

		Message message = new Message(id, runTime, runnable);
		messages.add(message);
		
		synchronized (this)
		{
			notify();
		}
	}
	
	public void postDelayed(Runnable runnable, long delay)
	{
		long id = generateId();
		long runTime = generateRunTime(delay);
		
		messages.add(new Message(id, runTime, runnable));
		
		synchronized (this)
		{
			notify();
		}
	};
	
	public void postStop()
	{
		post(new Runnable()
		{
			@Override
			public void run()
			{
				SimpleEventLoop.this.run = false;
			}
		});
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			while (!messages.isEmpty())
			{
				long timeStamp = System.currentTimeMillis();
				
				Message message = messages.peek();

				if (message.getRunTime() <= timeStamp)
				{
					message.getCode().run();
					messages.remove();
				}
			}
			
			if (!run)
			{
				break;
			}
			
			try
			{
				synchronized (this)
				{
					if (!messages.isEmpty())
					{
						Message message = messages.peek();
						long waitTime = message.getRunTime() - System.currentTimeMillis();
	
						wait(waitTime);
					}
					else
					{
						wait();
					}
				}
			}
			catch (InterruptedException e)
			{
			}
		}
	}
}
