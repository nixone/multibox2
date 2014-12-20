package sk.hackcraft.netinterface.message;

public class EmptyMessage implements Message
{
	private final MessageType messageType;
	
	public EmptyMessage(MessageType messageType)
	{
		this.messageType = messageType;
	}

	@Override
	public MessageType getType()
	{
		return messageType;
	}

	@Override
	public byte[] getContent()
	{
		return new byte[0];
	}
}
