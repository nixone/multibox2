package sk.hackcraft.multibox.android.client.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

public class FileUtil
{
	static public File writeTempFile(Context context, byte[] data) throws IOException {
		File outputDirectory = context.getCacheDir();
		File outputFile = File.createTempFile(FileUtil.class.getName(), ".tmp", outputDirectory);
		
		FileOutputStream output = new FileOutputStream(outputFile);
		output.write(data);
		output.close();
		
		return outputFile;
	}
}
