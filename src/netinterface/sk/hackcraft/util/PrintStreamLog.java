package sk.hackcraft.util;

import java.io.PrintStream;

public class PrintStreamLog implements Log
{
	public static PrintStreamLog createDebugLog()
	{
		return new PrintStreamLog("DEBUG", System.out);
	}
	
	public static PrintStreamLog createNamedDebugLog(String name)
	{
		return new PrintStreamLog(name, System.out);
	}
	
	private final String name;
	private final PrintStream stream;
	
	public PrintStreamLog(String name, PrintStream stream)
	{
		this.name = name;
		this.stream = stream;
	}
	
	@Override
	public void print(String message)
	{
		stream.println(name + ": " + message);
	}

	@Override
	public void printf(String format, Object... arguments)
	{
		format = "%s: " + format;
		
		stream.printf(format + "%n", name, arguments);
	}
}
