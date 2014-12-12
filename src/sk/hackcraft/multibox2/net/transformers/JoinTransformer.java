package sk.hackcraft.multibox2.net.transformers;

import sk.hackcraft.netinterface.message.transformer.DataTransformException;
import sk.hackcraft.netinterface.message.transformer.DataTransformer;

public class JoinTransformer<I, T, O> implements DataTransformer<I, O>
{
	private DataTransformer<I, T> firstTransformer;
	private DataTransformer<T, O> secondTransformer;
	
	public JoinTransformer(DataTransformer<I, T> firstTransformer, DataTransformer<T, O> secondTransformer) {
		this.firstTransformer = firstTransformer;
		this.secondTransformer = secondTransformer;
	}
	
	@Override
	public O transform(I input) throws DataTransformException
	{
		return secondTransformer.transform(firstTransformer.transform(input));
	}
}
