package sk.hackcraft.util;

public interface MessageQueue
{
	public void post(Runnable runnable);
	public void postDelayed(Runnable runnable, long delay);
}