package sk.hackcraft.netinterface.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sk.hackcraft.netinterface.message.Message;
import sk.hackcraft.netinterface.message.MessageFactory;
import sk.hackcraft.netinterface.message.MessageType;
import sk.hackcraft.netinterface.message.MessageTypeFactory;


public class SocketMessageInterface implements MessageInterface
{
	private final MessageTypeFactory messageTypeFactory = new MessageTypeFactory();
	private final MessageFactory messageFactory = new MessageFactory();
	
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
		MessageType messageType = messageTypeFactory.createMessageType(input.readInt());

		int messageLength = input.readInt();
		
		byte content[] = new byte[messageLength];
		input.readFully(content);

		return messageFactory.createMessage(messageType, content);
	}
	
	@Override
	public void close() throws IOException
	{
		socket.close();
	}
}
