package sk.hackcraft.util;


public abstract class PeriodicWorkDispatcher
{
	private final MessageQueue messageQueue;
	private final long pauseDelay;
	
	private boolean active;
	
	public PeriodicWorkDispatcher(MessageQueue messageQueue, long pauseDelay)
	{
		this.messageQueue = messageQueue;
		this.pauseDelay = pauseDelay;
		this.active = false;
	}
	
	public void start()
	{
		if (active)
		{
			return;
		}
		
		active = true;
		
		messageQueue.post(new Runnable()
		{
			@Override
			public void run()
			{
				if (active)
				{
					doWork();
					messageQueue.postDelayed(this, pauseDelay);
				}
			}
		});
	}
	
	public void stop()
	{
		if (!active)
		{
			return;
		}
		
		active = false;
	}
	
	protected abstract void doWork();
}
