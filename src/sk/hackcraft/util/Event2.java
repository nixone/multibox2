package sk.hackcraft.util;

import java.util.LinkedList;
import java.util.List;

public class Event2<L>
{
	public static void main(String[] args)
	{
		Listener l = new Listener()
		{
			@Override
			public void something(int x, String hh)
			{
				System.out.println(x + " " + hh);
			}
		};
		
		Event2<Listener> event = new Event2<Listener>();
		event.addListener(l);
		
		// ...

		event.fire(new Poster<Listener>()
		{
			@Override
			public void post(Listener listener)
			{
				listener.something(3, "hh");
			}
		});
	}
	
	private List<L> listeners = new LinkedList<L>();

	public void addListener(L listener)
	{
		this.listeners.add(listener);
	}
	
	public void fire(Poster<L> poster)
	{
		for (L listener : listeners)
		{
			poster.post(listener);
		}
	}
}

interface Poster<L>
{
	public void post(L listener);
}

interface Listener
{
	public void something(int x, String hh);
}
