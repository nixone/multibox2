package sk.hackcraft.util;

import java.util.LinkedList;
import java.util.List;

public class Event<D>
{
	public static class Data
	{
		public int x;
		public String name;
		
		public Data(int x, String name)
		{
			this.x = x;
			this.name = name;
		}
	}
	
	public static interface Data2
	{
		public int getX();
		public String getName();
	}
	
	public static void main(String[] args)
	{
		Event<Data> event = new Event<Data>();
		
		event.addListener(new Listener<Event.Data>()
		{
			@Override
			public void onEvent(Data data)
			{
				System.out.println(data.x);
			}
		});
		
		event.addListener(new Listener<Event.Data>()
		{
			@Override
			public void onEvent(Data data)
			{
				System.out.println(data.name);
			}
		});
		
		event.fire(new Data(5, "hups"));
		
		Event<Data2> event2 = new Event<Data2>();
		event2.addListener(new Listener<Event.Data2>()
		{
			@Override
			public void onEvent(Data2 data)
			{
				System.out.println(data.getX());
			}
		});
		
		event2.fire(new Data2()
		{
			@Override
			public int getX()
			{
				return 3;
			}
			
			@Override
			public String getName()
			{
				return "huhu";
			}
		});
	}
	
	private List<Listener<D>> listeners;
	
	public Event()
	{
		listeners = new LinkedList<Event.Listener<D>>();
	}
	
	public void addListener(Listener<D> listener)
	{
		listeners.add(listener);
	}
	
	public void removeListener(Listener<D> listener)
	{
		listeners.remove(listener);
	}
	
	public void fire(D data)
	{
		for (Listener<D> listener : listeners)
		{
			listener.onEvent(data);
		}
	}
	
	public interface Listener<D>
	{
		public void onEvent(D data);
	}
}
