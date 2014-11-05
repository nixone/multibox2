package sk.hackcraft.netinterface.connection;

import java.io.IOException;

public interface MessageInterfaceFactory
{
	public MessageInterface create() throws IOException;
}
