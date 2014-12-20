package sk.hackcraft.multibox.android.client;

import sk.hackcraft.multibox.model.LibraryItem;
import sk.hackcraft.multibox.model.libraryitems.DirectoryItem;
import sk.hackcraft.multibox.model.libraryitems.MultimediaItem;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PlayingTestingThread extends Thread
{
	@Override
	public void run() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			LibraryItem item1 = new MultimediaItem(1, "Name", 1250);
			LibraryItem item2 = new MultimediaItem(2, "Name2", 1234);
			LibraryItem item3 = new MultimediaItem(3, "Name3", 100);
			
			DirectoryItem dir = new DirectoryItem(4, "Dir");
			dir.addItem(item1);
			dir.addItem(item2);
			dir.addItem(item3);
			
			String encoded = mapper.writeValueAsString(dir);
			
			output(encoded);
			
			dir = (DirectoryItem)mapper.readValue(encoded, LibraryItem.class);
			
			output(dir.getClass().getName());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void output(Object anything) {
		Log.v("STDOUT", String.valueOf(anything));
	}
}
