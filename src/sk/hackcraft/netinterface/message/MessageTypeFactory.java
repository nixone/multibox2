package sk.hackcraft.netinterface.message;

public class MessageTypeFactory
{
	public MessageType createMessageType(final int id) {
		return new MessageType() {
			@Override
			public int toTypeId()
			{
				return id;
			}
		};
	}
}
