package sk.hackcraft.multibox2.android.client.util;

import sk.hackcraft.util.Log;

public class SystemLog implements Log
{
	private static final String TAG = "MultiBox";
	
	@Override
	public void print(String message)
	{
		android.util.Log.d(TAG, message);
	}

	@Override
	public void printf(String format, Object... arguments)
	{
		String message = String.format(format, arguments);
		android.util.Log.d(TAG, message);
	}
}
