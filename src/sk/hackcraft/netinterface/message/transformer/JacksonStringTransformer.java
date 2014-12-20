package sk.hackcraft.netinterface.message.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonStringTransformer<T> implements DataTransformer<T, String>
{
	private ObjectMapper mapper;
	
	public JacksonStringTransformer() {
		this(new ObjectMapper());
	}
	
	public JacksonStringTransformer(ObjectMapper mapper) {
		this.mapper = mapper;
	}
	
	@Override
	public String transform(T input) throws DataTransformException
	{
		try {
			return mapper.writeValueAsString(input);
		} catch(Exception e) {
			throw new DataTransformException(e);
		}
	}
}
