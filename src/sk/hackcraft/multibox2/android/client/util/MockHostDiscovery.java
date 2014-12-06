package sk.hackcraft.multibox2.android.client.util;

public class MockHostDiscovery implements HostDiscovery
{
	@Override
	public void start(final DiscoveryListener listener)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try {
					Thread.sleep(2000);
					listener.onStarted();
					Thread.sleep(2000);
					listener.onHostFound("localhost", "ME");
					Thread.sleep(2000);
					listener.onHostFound("8.8.8.8", "Google");
					Thread.sleep(2000);
					listener.onHostFound("1.2.3.4", "Example");
					Thread.sleep(2000);
					listener.onEnded();
				} catch(InterruptedException e) {
					return;
				}
			}
		}).start();
	}
}
