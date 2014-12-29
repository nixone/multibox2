package sk.hackcraft.util;

import java.util.HashSet;

public class Eventable<D>
{
	public interface Listener<D>
	{
		public void handle(D data);
	}
	
	private class Handler {
		private MessageQueue messageQueue;
		private Listener<D> listener;

		public Handler(Listener<D> listener, MessageQueue queue)
		{
			this.messageQueue = queue;
			this.listener = listener;
		}
		
		private void invoke(final D data) 
		{
			messageQueue.post(new Runnable()
			{
				@Override
				public void run()
				{
					listener.handle(data);
				}
			});
		}
	}
	
	private HashSet<Handler> handlers = new HashSet<Handler>();
	
	public void addListener(Listener<D> listener)
	{
		addListener(listener, new MessageQueue()
		{
			@Override
			public void postDelayed(Runnable runnable, long delay)
			{
			}
			
			@Override
			public void post(Runnable runnable)
			{
				runnable.run();
			}
		});
	}
	
	public void addListener(Listener<D> listener, MessageQueue queue)
	{
		Handler handler = new Handler(listener, queue);
		
		synchronized(handlers)
		{
			handlers.add(handler);
		}
	}
	
	public void invoke(D data)
	{
		HashSet<Handler> handlersToInvoke = new HashSet<Eventable<D>.Handler>();
		
		synchronized(handlers)
		{
			handlersToInvoke.addAll(handlers);
		}
		
		for(Handler handler : handlersToInvoke)
		{
			handler.invoke(data);
		}
	}
}
