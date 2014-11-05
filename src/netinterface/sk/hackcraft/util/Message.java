package sk.hackcraft.util;

public class Message implements Comparable<Message>
{
	private final long id;
	private final long runTime;
	private final Runnable code;
	
	public Message(long id, long runTime, Runnable code)
	{
		this.id = id;
		this.runTime = runTime;
		this.code = code;
	}
	
	public long getId()
	{
		return id;
	}
	
	public long getRunTime()
	{
		return runTime;
	}
	
	public Runnable getCode()
	{
		return code;
	}
	
	@Override
	public String toString()
	{
		return "Message " + id + " " + runTime;
	}

	@Override
	public int compareTo(Message message)
	{
		int result;
		if (getRunTime() == message.getRunTime())
		{
			result = (getId() < message.getId()) ? -1 : 1;
		}
		else
		{
			result = (getRunTime() < message.getRunTime()) ? -1 : 1;
		}
		
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof Message))
		{
			return false;
		}
		
		Message message = (Message)obj;
		
		return message.getId() == getId();
	}
	
	@Override
	public int hashCode()
	{
		return Long.valueOf(id).hashCode();
	}
}