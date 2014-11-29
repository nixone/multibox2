package sk.hackcraft.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileToStringReader
{
	private static final String NEWLINE = "\n";
	
	public String readFile(File file) throws IOException
	{
		BufferedReader input = new BufferedReader(new FileReader(file));
		
		StringBuilder builder = new StringBuilder();
		
		String line;
		while ((line = input.readLine()) != null)
		{
			builder.append(line);
			builder.append(NEWLINE);
		}
		
		input.close();
		
		return builder.toString();
	}
}
