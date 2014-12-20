package sk.hackcraft.multibox2.net.transformers;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import sk.hackcraft.netinterface.message.transformer.DataTransformer;

public class ByteArrayStringTransformer implements DataTransformer<byte[], String>
{
	private Charset charset;
	
	public ByteArrayStringTransformer() {
		this(Charset.forName("UTF-8"));
	}
	
	public ByteArrayStringTransformer(Charset charset) {
		this.charset = charset;
	}

	@Override
	public String transform(byte[] output)
	{
		if(output.length == 0) {
			return "";
		}
		
		try {
			DataInputStream input = new DataInputStream(new ByteArrayInputStream(output));
			
			byte[] stringBytes = new byte[input.readInt()];
			input.read(stringBytes);
			
			return new String(stringBytes, charset);
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
