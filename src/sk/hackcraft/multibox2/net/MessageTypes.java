package sk.hackcraft.multibox2.net;

import java.util.HashMap;
import java.util.Map;

import sk.hackcraft.netinterface.message.MessageType;

public enum MessageTypes implements MessageType
{
	GET_PLAYER_STATE(1),
	GET_PLAYLIST(2),
	GET_LIBRARY_ITEM(3),
	ADD_LIBRARY_ITEM_TO_PLAYLIST(4),
	GET_SERVER_INFO(5),
	PING(7);

	private static Map<Integer, MessageTypes> convertMap;
	
	static
	{
		convertMap = new HashMap<Integer, MessageTypes>();

		for (MessageTypes type : values())
		{
			convertMap.put(type.toTypeId(), type);
		}
	}
	
	private final int id;
	
	private MessageTypes(int id)
	{
		this.id = id;
	}
	
	@Override
	public int toTypeId()
	{
		return id;
	}
	
	@Override
	public String toString()
	{
		return name();
	}
	
	public class MessageParser implements Parser<MessageTypes>
	{
		@Override
		public MessageTypes parse(int value)
		{
			return convertMap.get(value);
		}
	}
}
