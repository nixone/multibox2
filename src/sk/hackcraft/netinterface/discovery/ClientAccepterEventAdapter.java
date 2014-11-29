package sk.hackcraft.netinterface.discovery;

import sk.hackcraft.netinterface.discovery.ClientAccepter.EventListener;

public class ClientAccepterEventAdapter<I> implements EventListener<I>
{
	@Override
	public void onClientAccepted(I clientInterface)
	{
	}
}
