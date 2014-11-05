package sk.hackcraft.multibox2.android.client.util;

import sk.hackcraft.util.MessageQueue;
import android.os.Handler;

public class HandlerEventLoop implements MessageQueue
{
	private final Handler handler;
	
	public HandlerEventLoop()
	{
		this.handler = new Handler();
	}
	
	@Override
	public void post(Runnable runnable)
	{
		handler.post(runnable);
	}

	@Override
	public void postDelayed(Runnable runnable, long delay)
	{
		handler.postDelayed(runnable, delay);
	}
}
