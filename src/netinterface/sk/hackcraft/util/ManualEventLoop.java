package sk.hackcraft.util;

import java.util.PriorityQueue;

public class ManualEventLoop implements MessageQueue
{
	private long nextId;
	private long lastTimeStamp;
	
	private PriorityQueue<Message> messages;
	
	public ManualEventLoop()
	{
		this.nextId = 1;
		this.lastTimeStamp = System.currentTimeMillis();
		
		this.messages = new PriorityQueue<Message>();
	}
	
	public long generateId()
	{
		return nextId++;
	}
	
	public long generateRunTime(long delay)
	{
		long runTime = System.currentTimeMillis();
		
		if (runTime < lastTimeStamp)
		{
			runTime = lastTimeStamp;
		}
		
		lastTimeStamp = runTime;
		
		return runTime + delay;
	}
	
	@Override
	public void post(Runnable runnable)
	{
		this.postDelayed(runnable, 0);
	}

	@Override
	public void postDelayed(Runnable runnable, long delay)
	{
		long id = generateId();
		long runTime = generateRunTime(delay);
		
		Message message = new Message(id, runTime, runnable);
		messages.add(message);
	}
	
	public void processAllMessages()
	{
		while (!messages.isEmpty())
		{
			Message message = messages.remove();
			
			message.getCode().run();
		}
	}
}
