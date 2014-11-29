package sk.hackcraft.netinterface.connection;

import java.io.IOException;

public interface AsynchronousMessageInterfaceFactory
{
	public AsynchronousMessageInterface create() throws IOException;
}
