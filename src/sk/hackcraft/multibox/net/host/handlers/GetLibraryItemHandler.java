package sk.hackcraft.multibox.net.host.handlers;

import sk.hackcraft.multibox.android.host.LibraryView;
import sk.hackcraft.multibox.net.MessageTypes;
import sk.hackcraft.multibox.net.host.DuplexJacksonMessageHandler;
import sk.hackcraft.multibox.net.messages.GetLibraryItemRequest;
import sk.hackcraft.multibox.net.messages.GetLibraryItemResponse;

public class GetLibraryItemHandler extends DuplexJacksonMessageHandler<GetLibraryItemRequest, GetLibraryItemResponse>
{
	private LibraryView library;
	
	public GetLibraryItemHandler(LibraryView library)
	{
		super(GetLibraryItemRequest.class, MessageTypes.GET_LIBRARY_ITEM);
		this.library = library;
	}

	@Override
	public GetLibraryItemResponse handle(GetLibraryItemRequest request)
	{
		return new GetLibraryItemResponse(
				library.getItem(
							request.getItemId()
						)
		);
	}
}
