package sk.nixone.discovery;

import java.io.IOException;
import java.util.concurrent.ThreadFactory;

public abstract class BackgroundCyclicWorker
{
	private class Worker implements Runnable {
		@Override
		public void run()
		{
			onStart();
			
			while(true) {
				synchronized(BackgroundCyclicWorker.this) {
					if(!running) {
						break;
					}
				}
				
				onCycle();
				
				try {
					Thread.sleep(cycleTimeout);
				} catch(InterruptedException e) {
					// nothing, just continue as nothing had happened
				}
			}
			
			onEnd();
		}
		
	}
	
	private boolean running;
	private Thread worker;
	private int cycleTimeout = 1000;
	
	public BackgroundCyclicWorker(int cycleTimeout) {
		this.cycleTimeout = cycleTimeout;
	}
	
	public void start(ThreadFactory threadFactory) throws IOException {
		synchronized(this) {
			worker = threadFactory.newThread(new Worker());
			
			running = true;
			worker.start();
		}
	}
	
	public void stop() {
		synchronized(this) {
			running = false;
		}
		
		worker = null;
	}
	
	abstract public void onStart();
	
	abstract public void onCycle();
	
	abstract public void onEnd();
}
