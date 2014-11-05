package sk.hackcraft.netinterface.message;

import java.io.IOException;

public interface Message
{
	public MessageType getType();
	public byte[] getContent() throws IOException;
}