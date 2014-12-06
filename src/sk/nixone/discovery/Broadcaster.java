package sk.nixone.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ThreadFactory;

public class Broadcaster
{
	public interface HostFoundListener {
		public void onHostFound(InetAddress address);
	}
	
	private class BroadcastWorker extends BackgroundCyclicWorker {
		public BroadcastWorker(int cycleTimeout)
		{
			super(cycleTimeout);
		}

		@Override
		public void onStart() {}

		@Override
		public void onCycle() {
			// broadcast on every single address
			for(InetAddress address : NetworkUtilities.getBroadcastAddresses()) {
				DatagramPacket packet = new DatagramPacket(toSend, toSend.length, address, protocol.getPort());
				try {
					broadcastSocket.send(packet);
				} catch(IOException e) {
					e.printStackTrace();
					// nothing critical, continue
					continue;
				}
			}
		}

		@Override
		public void onEnd() {}
	}
	
	private class ReceiveWorker extends BackgroundCyclicWorker {

		public ReceiveWorker(int cycleTimeout) {
			super(cycleTimeout);
		}

		@Override
		public void onStart() {}

		@Override
		public void onCycle()
		{
			// try to listen for an answer
			DatagramPacket receivePacket = new DatagramPacket(toReceive, toReceive.length);
			try {
				broadcastSocket.receive(receivePacket);
			} catch(IOException e) {
				// nothing, ignore
				e.printStackTrace();
				return;
			}
			
			// we received something we need to check it and decide
			if(receivePacket.getLength() == toReceive.length) {
				boolean areSame = true;
				for(int i=0; i<toReceive.length; i++) {
					areSame &= toReceive[i] == toExpect[i];
				}
				if(areSame) {
					hostFoundListener.onHostFound(receivePacket.getAddress());
				}
			}
		}

		@Override
		public void onEnd() {}
	}
	
	private DiscoveryProtocol protocol;
	private DatagramSocket broadcastSocket = null;
	private HostFoundListener hostFoundListener = null;
	
	private byte[] toSend;
	private byte[] toExpect;
	private byte[] toReceive;
	
	private BroadcastWorker broadcaster;
	private ReceiveWorker receiver;
	
	public Broadcaster(DiscoveryProtocol protocol, HostFoundListener hostFoundListener) {
		this.protocol = protocol;
		this.hostFoundListener = hostFoundListener;
		
		toSend = protocol.getQuestionData();
		toExpect = protocol.getAnswerData();
		toReceive = new byte[toExpect.length];
		
		broadcaster = new BroadcastWorker(protocol.getBroadcastTimeout());
		receiver = new ReceiveWorker(100);
	}
	
	public void start(ThreadFactory threadFactory) throws IOException {
		broadcastSocket = new DatagramSocket();
		broadcastSocket.setBroadcast(true);
		
		receiver.start(threadFactory);
		broadcaster.start(threadFactory);
	}
	
	public void stop() {
		broadcaster.stop();
		receiver.stop();
	}
}
