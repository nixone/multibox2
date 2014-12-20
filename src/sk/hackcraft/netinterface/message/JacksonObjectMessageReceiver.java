package sk.hackcraft.netinterface.message;

import sk.hackcraft.multibox2.net.transformers.ByteArrayStringTransformer;
import sk.hackcraft.multibox2.net.transformers.JoinTransformer;
import sk.hackcraft.multibox2.net.transformers.StringJacksonTransformer;
import sk.hackcraft.netinterface.message.transformer.DataTransformer;
import sk.hackcraft.util.MessageQueue;

public abstract class JacksonObjectMessageReceiver<R> extends ObjectMessageReceiver<R>
{
	private Class<R> clasz;
	
	public JacksonObjectMessageReceiver(MessageQueue messageQueue, Class<R> clasz)
	{
		super(messageQueue);
		this.clasz = clasz;
	}

	@Override
	public DataTransformer<byte[], R> getDecoder()
	{
		return new JoinTransformer<byte[], String, R>(
				new ByteArrayStringTransformer(),
				new StringJacksonTransformer<R>(clasz)
		);
	}

	@Override
	protected abstract void onResult(R result);

}
