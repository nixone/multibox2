package sk.hackcraft.multibox2.net.host.handlers;

import sk.hackcraft.multibox2.model.host.LibraryView;
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
		return new GetLibraryItemResponse(
				library.getItem(
							request.getItemId()
						)
		);
	}
}
