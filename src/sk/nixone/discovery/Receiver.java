package sk.nixone.discovery;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class Receiver extends BackgroundCyclicWorker
{
	private DatagramSocket listeningSocket;
	
	private byte[] toSend;
	private byte[] toExpect;
	private byte[] toReceive;
	
	public Receiver(DiscoveryProtocol protocol) throws IOException
	{
		super(100);
		
		toSend = protocol.getAnswerData();
		toExpect = protocol.getQuestionData();
		toReceive = new byte[toExpect.length];
		
		listeningSocket = new DatagramSocket(protocol.getPort());
	}

	@Override
	public void onStart()
	{
	}

	@Override
	public void onCycle()
	{
		DatagramPacket packet = new DatagramPacket(toReceive, toReceive.length);
		try {
			listeningSocket.receive(packet);
		} catch(IOException e) {
			e.printStackTrace();
			// return as if nothing, don't care
			return;
		}
		
		if(packet.getLength() == toReceive.length) {
			boolean areSame = true;
			for(int i=0; i<toReceive.length; i++) {
				areSame &= toReceive[i] == toExpect[i];
			}
			if(areSame) {
				try {
					DatagramPacket sendingPacket = new DatagramPacket(toSend, toSend.length, packet.getSocketAddress());
					listeningSocket.send(sendingPacket);
				} catch(IOException e) {
					e.printStackTrace();
					// same as above, don't care
				}
			}
		}
	}

	@Override
	public void onEnd()
	{
	}

	@Override
	public void stop() {
		super.stop();
		listeningSocket.close();
	}
}
