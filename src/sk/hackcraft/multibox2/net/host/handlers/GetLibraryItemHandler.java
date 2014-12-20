package sk.hackcraft.multibox2.net.host.handlers;

import sk.hackcraft.multibox2.android.host.LibraryView;
import sk.hackcraft.multibox2.net.MessageTypes;
import sk.hackcraft.multibox2.net.host.DuplexJacksonMessageHandler;
import sk.hackcraft.multibox2.net.messages.GetLibraryItemRequest;
import sk.hackcraft.multibox2.net.messages.GetLibraryItemResponse;

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
