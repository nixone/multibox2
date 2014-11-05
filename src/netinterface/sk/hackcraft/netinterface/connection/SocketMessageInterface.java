package sk.hackcraft.netinterface.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sk.hackcraft.netinterface.message.Message;
import sk.hackcraft.netinterface.message.MessageType;


public class SocketMessageInterface implements MessageInterface
{
	private final Socket socket;
	
	private final DataInputStream input;
	private final DataOutputStream output;

	public SocketMessageInterface(Socket socket) throws IOException
	{
		this.socket = socket;
		
		this.input = new DataInputStream(socket.getInputStream());
		this.output = new DataOutputStream(socket.getOutputStream());
	}

	@Override
	public void sendMessage(Message message) throws IOException
	{
		int type = message.getType().toTypeId();
		byte content[] = message.getContent();
		
		output.writeInt(type);
		output.writeInt(content.length);
		output.write(content);
	}

	@Override
	public Message waitForMessage() throws IOException
	{
		final int typeId = input.readInt();
		final MessageType type = new MessageType()
		{
			@Override
			public int toTypeId()
			{
				return typeId;
			}
		};
		
		int length = input.readInt();
		
		final byte content[] = new byte[length];
		input.readFully(content);

		return new Message()
		{
			@Override
			public MessageType getType()
			{
				return type;
			}
			
			@Override
			public byte[] getContent()
			{
				return content;
			}
		};
	}
	
	@Override
	public void close() throws IOException
	{
		socket.close();
	}
}
