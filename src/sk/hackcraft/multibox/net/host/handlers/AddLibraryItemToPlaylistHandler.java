package sk.hackcraft.multibox.net.host.handlers;

import sk.hackcraft.multibox.android.host.LibraryView;
import sk.hackcraft.multibox.android.host.Player;
import sk.hackcraft.multibox.android.host.Song;
import sk.hackcraft.multibox.model.LibraryItem;
import sk.hackcraft.multibox.net.MessageTypes;
import sk.hackcraft.multibox.net.host.DuplexJacksonMessageHandler;
import sk.hackcraft.multibox.net.messages.AddLibraryItemToPlaylistRequest;
import sk.hackcraft.multibox.net.messages.AddLibraryItemToPlaylistResponse;

public class AddLibraryItemToPlaylistHandler extends DuplexJacksonMessageHandler<AddLibraryItemToPlaylistRequest, AddLibraryItemToPlaylistResponse>
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
	public AddLibraryItemToPlaylistResponse handle(AddLibraryItemToPlaylistRequest request)
	{
		LibraryItem item = library.getItem(request.getMultimediaId());
		
		if(item == null || !(item instanceof Song)) {
			return new AddLibraryItemToPlaylistResponse(false, null);
		}

		player.add((Song)item);

		return new AddLibraryItemToPlaylistResponse(true, (Song)item);
	}
}
