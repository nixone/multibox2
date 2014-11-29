package sk.hackcraft.multibox;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TestNetworkServer
{
	public static void main(String[] args)
	{
		while (true)
		{
			try
			{
				ServerSocket serverSocket = null;
				Socket socket = null;
				
				try
				{
					serverSocket = new ServerSocket(13111);
					serverSocket.setSoTimeout(300000);
					
					socket = serverSocket.accept();
					DataOutput output = new DataOutputStream(socket.getOutputStream());
					
					System.out.println("write");
					output.writeInt(2);
					
					byte[] content = "hw".getBytes(StandardCharsets.UTF_8);
					//output.writeInt(content.length);
					//output.write(content);
					
					output.writeInt(4);
					output.writeInt(2048);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				finally
				{
					if (serverSocket != null)
					{
						serverSocket.close();
					}
					
					if (socket != null)
					{
						socket.close();
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				
			}
			
			try
			{
				Thread.sleep(5000);
			}
			catch (InterruptedException e)
			{
				
			}
		}
	}
}
