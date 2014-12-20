package sk.hackcraft.netinterface.message.transformer;


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
