package sk.hackcraft.multibox2;

import java.io.File;
import java.io.IOException;

import sk.hackcraft.multibox2.net.data.GetLibraryItemData;
import sk.hackcraft.multibox2.net.data.GetPlayerStateResultData;
import sk.hackcraft.multibox2.net.data.GetPlaylistResultData;
import sk.hackcraft.multibox2.net.transformers.GetLibraryItemDecoder;
import sk.hackcraft.multibox2.net.transformers.GetPlayerStateDecoder;
import sk.hackcraft.multibox2.net.transformers.GetPlaylistDecoder;
import sk.hackcraft.netinterface.message.transformer.DataTransformException;
import sk.hackcraft.util.FileToStringReader;

public class TestJson
{
	public static void main(String[] args)
	{
		try
		{
			TestJson test = new TestJson();
			test.testLibraryItemReceive();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public String loadJson(String fileName) throws IOException
	{
		return new FileToStringReader().readFile(new File("./src/sk/hackcraft/multibox/" + fileName));
	}
	
	public void testPlayerStateReceive() throws DataTransformException, IOException
	{
		String json = loadJson("playerStateReceived.json");

		GetPlayerStateResultData data = new GetPlayerStateDecoder().transform(json);
		
		System.out.println(data);
	}
	
	public void testPlaylistReceive() throws DataTransformException, IOException
	{
		String json = loadJson("playlistReceived.json");
		
		GetPlaylistResultData data = new GetPlaylistDecoder().transform(json);
		
		System.out.println(data);
	}
	
	public void testLibraryItemReceive() throws DataTransformException, IOException
	{
		String json = loadJson("libraryItemReceived.json");

		GetLibraryItemData data = new GetLibraryItemDecoder().transform(json);
		
		System.out.println(data);
	}
}