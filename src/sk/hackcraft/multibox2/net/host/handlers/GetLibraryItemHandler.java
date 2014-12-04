package sk.hackcraft.multibox2.net.host.handlers;

import java.util.Collection;

import sk.hackcraft.multibox2.android.host.LibraryView;
import sk.hackcraft.multibox2.model.LibraryItem;
import sk.hackcraft.multibox2.model.libraryitems.DirectoryItem;
import sk.hackcraft.multibox2.net.MessageTypes;
import sk.hackcraft.multibox2.net.host.JsonMessageHandler;
import sk.hackcraft.multibox2.net.host.messages.GetLibraryItemRequest;
import sk.hackcraft.multibox2.net.host.messages.GetLibraryItemResponse;

public class GetLibraryItemHandler extends JsonMessageHandler<GetLibraryItemRequest, GetLibraryItemResponse>
{
	private LibraryView library;
	
	public GetLibraryItemHandler(LibraryView library)
	{
		super(GetLibraryItemRequest.class, MessageTypes.GET_LIBRARY_ITEM);
		this.library = library;
	}

	@Override
	public GetLibraryItemResponse handleJson(GetLibraryItemRequest request)
	{
		if(request.getItemId() == 0) {
			Collection<LibraryItem> items = library.getLibraryItems();
			
			DirectoryItem dir = new DirectoryItem(0, "All songs");
			for(LibraryItem item : items) {
				dir.addItem(item);
			}
			
			return new GetLibraryItemResponse(dir);
		} else {
			return new GetLibraryItemResponse(
					library.getItem(
								request.getItemId()
							)
			);
		}
	}
}
