package sk.nixone.discovery;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ThreadFactory;

public class Tester
{
	public static void main(String[] args) throws IOException
	{
		DiscoveryProtocol protocol = new DiscoveryProtocol(1234);
		Receiver receiver = new Receiver(protocol);
		Broadcaster broadcaster = new Broadcaster(protocol, new NewHostFoundNotifier()
		{
			@Override
			public void onNewHostFound(InetAddress address)
			{
				System.out.println("New host found: "+address);
			}
		});
		
		ThreadFactory factory = new ThreadFactory() {

			@Override
			public Thread newThread(Runnable arg0)
			{
				return new Thread(arg0);
			}
			
		};
		
		receiver.start(factory);
		broadcaster.start(factory);
	}
}
