package sk.hackcraft.multibox2.net.host;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import android.util.Log;
import sk.hackcraft.netinterface.message.Message;
import sk.hackcraft.netinterface.message.MessageFactory;
import sk.hackcraft.netinterface.message.MessageType;
import sk.hackcraft.netinterface.message.MessageTypeFactory;

public class HostClient
{
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	
	private boolean hasKnownMessageType = false;
	private int messageTypeId;
	private boolean hasKnownDataSize = false;
	private int readDataSize = 0;
	private int completeDataSize = 0;
	private byte[] readData = null;
	
	public HostClient(Socket socket) throws IOException {
		this.socket = socket;
		
		input = new DataInputStream(socket.getInputStream());
		output = new DataOutputStream(socket.getOutputStream());
	}
	
	public Message tryToReadMessage(MessageFactory messageFactory, MessageTypeFactory messageTypeFactory) throws IOException {
		boolean hasIntegerAvailable = input.available() >= Integer.SIZE / 8;
		
		if(!hasKnownMessageType) {
			if(hasIntegerAvailable) {
				hasKnownMessageType = true;
				messageTypeId = input.readInt();
				hasIntegerAvailable = input.available() >= Integer.SIZE / 8;
			} else {
				return null;
			}
		}
		if(!hasKnownDataSize) {
			if(hasIntegerAvailable) {
				hasKnownDataSize = true;
				completeDataSize = input.readInt();
				readDataSize = 0;
				readData = new byte[completeDataSize];
			} else {
				return null;
			}
		}
		int bytesReadable = input.available();
		if(hasKnownMessageType && hasKnownDataSize && bytesReadable > 0) {
			int bytesToRead = Math.min(bytesReadable, completeDataSize - readDataSize);
			input.read(readData, readDataSize, bytesToRead);
			readDataSize += bytesToRead;
		}
		
		if(readDataSize == completeDataSize) {
			MessageType type = messageTypeFactory.createMessageType(messageTypeId);
			Message message = messageFactory.createMessage(type, readData);
			
			readData = null;
			hasKnownDataSize = false;
			hasKnownMessageType = false;
			
			return message;
		}
		
		return null;
	}
	
	public void send(Message message) throws IOException {
		byte[] content = message.getContent();
		int messageType = message.getType().toTypeId();
		int messageLength = content.length;
		
		output.writeInt(messageType);
		output.writeInt(messageLength);
		output.write(content);
	}
	
	public void tryToClose() throws IOException {
		socket.close();
	}
}
