package sk.hackcraft.netinterface.message;

import java.io.IOException;

public interface MessageReceiver
{
	public void receive(byte content[]) throws IOException;
}