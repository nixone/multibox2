package sk.hackcraft.multibox2.net.host.handlers;

import sk.hackcraft.multibox2.android.host.LibraryView;
import sk.hackcraft.multibox2.android.host.Player;
import sk.hackcraft.multibox2.android.host.Song;
import sk.hackcraft.multibox2.model.LibraryItem;
import sk.hackcraft.multibox2.net.MessageTypes;
import sk.hackcraft.multibox2.net.host.JsonMessageHandler;
import sk.hackcraft.multibox2.net.host.messages.AddLibraryItemToPlaylistRequest;
import sk.hackcraft.multibox2.net.host.messages.AddLibraryItemToPlaylistResponse;

public class AddLibraryItemToPlaylistHandler extends JsonMessageHandler<AddLibraryItemToPlaylistRequest, AddLibraryItemToPlaylistResponse>
{
	private LibraryView library;
	private Player player;
	
	public AddLibraryItemToPlaylistHandler(LibraryView library, Player player)
	{
		super(AddLibraryItemToPlaylistRequest.class, MessageTypes.ADD_LIBRARY_ITEM_TO_PLAYLIST);
		this.library = library;
		this.player = player;
	}

	@Override
	public AddLibraryItemToPlaylistResponse handleJson(AddLibraryItemToPlaylistRequest request)
	{
		LibraryItem item = library.getItem(request.getMultimediaId());
		
		if(item == null || !(item instanceof Song)) {
			return new AddLibraryItemToPlaylistResponse(false, null);
		}

		player.add((Song)item);

		return new AddLibraryItemToPlaylistResponse(true, (Song)item);
	}
}
