package sk.hackcraft.netinterface.message.transformer;

public interface DataTransformer<I, O>
{
	public O transform(I input) throws DataTransformException;
}
