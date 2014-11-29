package sk.hackcraft.multibox;

public class TestThread
{
	public static void main(String[] args)
	{
		Runnable testCode = new Runnable()
		{
			@Override
			public void run()
			{
				for (int i = 0; i < 10000; i++)
				{
					System.out.println(".");
				}
				
				Thread.currentThread().interrupt();
				
				try
				{
					synchronized (this)
					{
					wait();
					}
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		};
		
		Thread t = new Thread(testCode);
		t.start();
	
	}
}
