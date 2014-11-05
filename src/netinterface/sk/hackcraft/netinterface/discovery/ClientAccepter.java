package sk.hackcraft.netinterface.discovery;

public interface ClientAccepter<I>
{
	public void setEventListener(EventListener<I> listener);
	
	public void start();
	public void stop();
	
	public interface EventListener<I>
	{
		public void onClientAccepted(I clientInterface);
	}
}
