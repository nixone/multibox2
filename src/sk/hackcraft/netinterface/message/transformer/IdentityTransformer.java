package sk.hackcraft.netinterface.message.transformer;

public class IdentityTransformer<T> implements DataTransformer<T, T>
{

	@Override
	public T transform(T input) throws DataTransformException
	{
		return input;
	}
}
