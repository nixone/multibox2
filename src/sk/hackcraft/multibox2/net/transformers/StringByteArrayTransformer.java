package sk.hackcraft.multibox2.net.transformers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import sk.hackcraft.netinterface.message.transformer.DataTransformer;

public class StringByteArrayTransformer implements DataTransformer<String, byte[]>
{
	private Charset charset;
	
	public StringByteArrayTransformer() {
		this(Charset.forName("UTF-8"));
	}
	
	public StringByteArrayTransformer(Charset charset) {
		this.charset = charset;
	}
	
	@Override
	public byte[] transform(String input)
	{
		try {
			ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
			DataOutputStream output = new DataOutputStream(resultStream);
			
			byte[] stringBytes = input.getBytes(charset);
			output.writeInt(stringBytes.length);
			output.write(stringBytes);
			
			return resultStream.toByteArray();
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
