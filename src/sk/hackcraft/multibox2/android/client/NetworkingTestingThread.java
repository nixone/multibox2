package sk.hackcraft.multibox2.android.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ThreadFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import sk.hackcraft.multibox2.net.MessageTypes;
import sk.hackcraft.multibox2.net.host.Host;
import sk.hackcraft.multibox2.net.host.handlers.PingHandler;
import sk.hackcraft.multibox2.net.host.messages.Empty;
import sk.hackcraft.netinterface.message.Message;
import sk.hackcraft.netinterface.message.MessageType;
import android.util.Log;

public class NetworkingTestingThread extends Thread
{
	@Override
	public void run() {
		int port = 1234;
		Message message = new Message()
		{
			private ObjectMapper mapper = new ObjectMapper();
			
			{
				mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			}
			
			@Override
			public MessageType getType()
			{
				return MessageTypes.PING;
			}
			
			@Override
			public byte[] getContent() throws IOException
			{
				return mapper.writeValueAsBytes(new Empty());
			}
		};
		
		try {
			Host host = new Host(port);
			host.setMessageHandler(MessageTypes.PING, new PingHandler());
			
			host.start(new ThreadFactory()
			{
				@Override
				public Thread newThread(Runnable arg0)
				{
					return new Thread(arg0);
				}
			});
			
			Socket client = new Socket("localhost", port);
			DataOutputStream output = new DataOutputStream(client.getOutputStream());
			DataInputStream input = new DataInputStream(client.getInputStream());
			
			output.writeInt(message.getType().toTypeId());
			byte [] content = message.getContent();
			output.writeInt(content.length);
			output.write(content);
			
			output(input.readInt());
			content = new byte[input.readInt()];
			input.read(content);
			
			output(new String(content));
			
			client.close();
		} catch(IOException e) {
			Log.e("EXCEPTION", "Exception", e);
		}
	}
	
	public void output(Object anything) {
		Log.v("STDOUT", String.valueOf(anything));
	}
}
