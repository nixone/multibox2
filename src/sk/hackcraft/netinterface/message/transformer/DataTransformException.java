package sk.hackcraft.netinterface.message.transformer;

import java.io.IOException;

public class DataTransformException extends IOException
{
	private static final long serialVersionUID = 1L;

	public DataTransformException(Throwable cause)
	{
		super(cause);
	}
}
