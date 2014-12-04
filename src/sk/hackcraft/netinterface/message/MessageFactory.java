package sk.hackcraft.netinterface.message;

import java.io.IOException;

public class MessageFactory
{
	public Message createMessage(final MessageType messageType, final byte[] content) {
		return new Message()
		{
			@Override
			public MessageType getType()
			{
				return messageType;
			}
			
			@Override
			public byte[] getContent() throws IOException
			{
				return content;
			}
			
			@Override
			public String toString() {
				return "Message(length="+content.length+", type="+messageType+", content='"+new String(content)+"')";
			}
		};
	}
}
