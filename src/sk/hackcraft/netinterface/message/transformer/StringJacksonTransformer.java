package sk.hackcraft.netinterface.message.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StringJacksonTransformer<T> implements DataTransformer<String, T>
{
	private Class<T> clasz;
	private ObjectMapper mapper;

	public StringJacksonTransformer(Class<T> clasz)
	{
		this(clasz, new ObjectMapper());
	}

	public StringJacksonTransformer(Class<T> clasz, ObjectMapper mapper)
	{
		this.clasz = clasz;
		this.mapper = mapper;
	}

	@Override
	public T transform(String input) throws DataTransformException
	{
		try
		{
			if (input.length() == 0)
			{
				return mapper.readValue("{}", clasz);
			}

			return mapper.readValue(input, clasz);
		} catch (Exception e)
		{
			throw new DataTransformException(e);
		}
	}
}
