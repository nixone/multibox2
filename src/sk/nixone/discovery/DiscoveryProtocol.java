package sk.nixone.discovery;

import java.util.Arrays;

public class DiscoveryProtocol
{
	private byte[] question;
	
	private byte[] answer;
	
	private int port;
	
	private int broadcastTimeout;
	
	public DiscoveryProtocol(int port) {
		this("Question@sk.nixone.discovery".getBytes(), "Answer@sk.nixone.discovery".getBytes(), port, 1000);
	}
	
	public DiscoveryProtocol(byte[] question, byte[] answer, int port, int broadcastTimeout) {
		this.question = question;
		this.answer = answer;
		this.port = port;
		this.broadcastTimeout = broadcastTimeout;
	}
	
	public byte[] getQuestionData() {
		return Arrays.copyOf(question, question.length);
	}
	
	public byte[] getAnswerData() {
		return Arrays.copyOf(answer, answer.length);
	}
	
	public int getPort() {
		return port;
	}
	
	public int getBroadcastTimeout() {
		return broadcastTimeout;
	}
}
