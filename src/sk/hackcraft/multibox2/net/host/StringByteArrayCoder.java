package sk.hackcraft.multibox2.net.host;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class StringByteArrayCoder implements Coder<String, byte[]>
{
	private Charset charset;
	
	public StringByteArrayCoder() {
		this(Charset.forName("UTF-8"));
	}
	
	public StringByteArrayCoder(Charset charset) {
		this.charset = charset;
	}
	
	@Override
	public byte[] encode(String input)
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

	@Override
	public String decode(byte[] output)
	{
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
