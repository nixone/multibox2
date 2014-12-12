package sk.hackcraft.multibox2.net;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import sk.hackcraft.multibox2.model.LibraryItem;
import sk.hackcraft.multibox2.model.libraryitems.MultimediaItem;
import sk.hackcraft.multibox2.net.data.AddMultimediaToPlaylistResultData;
import sk.hackcraft.multibox2.net.data.GetLibraryItemData;
import sk.hackcraft.multibox2.net.data.GetPlayerStateResultData;
import sk.hackcraft.multibox2.net.data.GetPlaylistResultData;
import sk.hackcraft.multibox2.net.data.GetServerInfoResultData;
import sk.hackcraft.multibox2.net.data.LibraryItemIdData;
import sk.hackcraft.multibox2.net.data.MultimediaItemIdData;
import sk.hackcraft.multibox2.net.transformers.old.AddMultimediaToPlaylistDecoder;
import sk.hackcraft.multibox2.net.transformers.old.DataStructJacksonEncoder;
import sk.hackcraft.multibox2.net.transformers.old.GetLibraryItemDecoder;
import sk.hackcraft.multibox2.net.transformers.old.GetPlayerStateDecoder;
import sk.hackcraft.multibox2.net.transformers.old.GetPlaylistDecoder;
import sk.hackcraft.multibox2.net.transformers.old.GetServerInfoDecoder;
import sk.hackcraft.netinterface.connection.AsynchronousMessageInterface;
import sk.hackcraft.netinterface.connection.AsynchronousMessageInterface.SeriousErrorListener;
import sk.hackcraft.netinterface.message.DataStringMessage;
import sk.hackcraft.netinterface.message.DataStringMessageReceiver;
import sk.hackcraft.netinterface.message.EmptyMessage;
import sk.hackcraft.netinterface.message.Message;
import sk.hackcraft.netinterface.message.MessageReceiver;
import sk.hackcraft.netinterface.message.transformer.DataTransformer;
import sk.hackcraft.util.MessageQueue;

public class NetworkServerInterface implements ServerInterface
{
	private final AsynchronousMessageInterface messageInterface;
	private final MessageQueue messageQueue;
	
	private final List<ServerInterfaceEventListener> serverListeners;
	
	public NetworkServerInterface(AsynchronousMessageInterface messageInterface, MessageQueue messageQueue)
	{
		this.messageInterface = messageInterface;
		this.messageQueue = messageQueue;
		
		messageInterface.setSeriousErrorListener(new NetworkSeriousErrorListener());
		
		this.serverListeners = new LinkedList<ServerInterfaceEventListener>();

		messageInterface.setMessageReceiver(MessageTypes.GET_PLAYER_STATE, new GetPlayerStateReceiver(messageQueue));
		messageInterface.setMessageReceiver(MessageTypes.GET_PLAYLIST, new GetPlaylistReceiver(messageQueue));
		messageInterface.setMessageReceiver(MessageTypes.ADD_LIBRARY_ITEM_TO_PLAYLIST, new AddMultimediaToPlaylistReceiver(messageQueue));
		messageInterface.setMessageReceiver(MessageTypes.GET_LIBRARY_ITEM, new GetLibraryItemReceiver(messageQueue));
		messageInterface.setMessageReceiver(MessageTypes.GET_SERVER_INFO, new GetServerInfoReceiver(messageQueue));
	}
	
	@Override
	public void close()
	{
		messageInterface.close();
	}

	@Override
	public void registerEventListener(ServerInterfaceEventListener listener)
	{
		serverListeners.add(listener);
	}

	@Override
	public void unregisterEventListener(ServerInterfaceEventListener listener)
	{
		serverListeners.remove(listener);
	}
	
	@Override
	public void requestServerInfo()
	{
		Message message = new EmptyMessage(MessageTypes.GET_SERVER_INFO);
		messageInterface.sendMessage(message);
	}

	@Override
	public void requestPlayerUpdate()
	{
		Message message = new EmptyMessage(MessageTypes.GET_PLAYER_STATE);
		messageInterface.sendMessage(message);
	}

	@Override
	public void requestPlaylistUpdate()
	{
		Message message = new EmptyMessage(MessageTypes.GET_PLAYLIST);
		messageInterface.sendMessage(message);
	}

	@Override
	public void requestLibraryItem(long itemId)
	{
		LibraryItemIdData data = new LibraryItemIdData(itemId);
		Message message = new DataStringMessage<LibraryItemIdData>(MessageTypes.GET_LIBRARY_ITEM, data)
		{
			@Override
			protected DataTransformer<LibraryItemIdData, String> createEncoder()
			{
				return new DataStructJacksonEncoder<LibraryItemIdData>();
			}
		};
		
		messageInterface.sendMessage(message);
	}
	
	@Override
	public void addLibraryItemToPlaylist(long itemId)
	{
		MultimediaItemIdData data = new MultimediaItemIdData(itemId);
		Message message = new DataStringMessage<MultimediaItemIdData>(MessageTypes.ADD_LIBRARY_ITEM_TO_PLAYLIST, data)
		{
			@Override
			protected DataTransformer<MultimediaItemIdData, String> createEncoder()
			{
				return new DataStructJacksonEncoder<MultimediaItemIdData>();
			}
		};
		
		messageInterface.sendMessage(message);
	}
	
	private class NetworkSeriousErrorListener implements SeriousErrorListener
	{
		@Override
		public void onSeriousError(String errorDescription)
		{
			List<ServerInterfaceEventListener> listenersCopy = new LinkedList<ServerInterfaceEventListener>(serverListeners);
			for (final ServerInterface.ServerInterfaceEventListener listener : listenersCopy)
			{
				messageQueue.post(new Runnable()
				{
					@Override
					public void run()
					{
						listener.onDisconnect();
					}
				});
			}
		}
	}
	
	private class GetPlayerStateReceiver extends DataStringMessageReceiver<GetPlayerStateResultData>
	{
		public GetPlayerStateReceiver(MessageQueue messageQueue)
		{
			super(messageQueue);
		}
		
		@Override
		protected DataTransformer<String, GetPlayerStateResultData> createParser()
		{
			return new GetPlayerStateDecoder();
		}

		@Override
		public void onResult(GetPlayerStateResultData result)
		{
			MultimediaItem multimedia = result.getMultimedia();
			int playbackPosition = result.getPlaybackPosition();
			boolean playing = result.isPlaying();
			
			for (ServerInterfaceEventListener listener : serverListeners)
			{
				listener.onPlayerUpdateReceived(multimedia, playbackPosition, playing);
			}
		}
	}
	
	private class GetPlaylistReceiver extends DataStringMessageReceiver<GetPlaylistResultData>
	{
		public GetPlaylistReceiver(MessageQueue messageQueue)
		{
			super(messageQueue);
		}

		@Override
		protected DataTransformer<String, GetPlaylistResultData> createParser()
		{
			return new GetPlaylistDecoder();
		}

		@Override
		protected void onResult(GetPlaylistResultData result)
		{
			List<MultimediaItem> playlist = result.getPlaylist();
			
			for (ServerInterfaceEventListener listener : serverListeners)
			{
				listener.onPlaylistReceived(playlist);
			}
		}
	}
	
	private class AddMultimediaToPlaylistReceiver extends DataStringMessageReceiver<AddMultimediaToPlaylistResultData>
	{
		public AddMultimediaToPlaylistReceiver(MessageQueue messageQueue)
		{
			super(messageQueue);
		}

		@Override
		protected DataTransformer<String, AddMultimediaToPlaylistResultData> createParser()
		{
			return new AddMultimediaToPlaylistDecoder();
		}

		@Override
		protected void onResult(AddMultimediaToPlaylistResultData result)
		{
			final boolean success = result.isSuccess();
			final MultimediaItem multimedia = result.getMultimedia();
			
			for (ServerInterfaceEventListener listener : serverListeners)
			{
				listener.onAddingLibraryItemToPlaylistResult(success, multimedia);
			}
		}
	}
	
	private class GetLibraryItemReceiver extends DataStringMessageReceiver<GetLibraryItemData>
	{
		public GetLibraryItemReceiver(MessageQueue messageQueue)
		{
			super(messageQueue);
		}

		@Override
		protected DataTransformer<String, GetLibraryItemData> createParser()
		{
			return new GetLibraryItemDecoder();
		}

		@Override
		protected void onResult(GetLibraryItemData result)
		{
			final LibraryItem libraryItem = result.getLibraryItem();
			
			for (ServerInterfaceEventListener listener : serverListeners)
			{
				listener.onLibraryItemReceived(libraryItem);
			}
		}
	}
	
	private class GetServerInfoReceiver extends DataStringMessageReceiver<GetServerInfoResultData>
	{
		public GetServerInfoReceiver(MessageQueue messageQueue)
		{
			super(messageQueue);
		}

		@Override
		protected DataTransformer<String, GetServerInfoResultData> createParser()
		{
			return new GetServerInfoDecoder();
		}

		@Override
		protected void onResult(GetServerInfoResultData result)
		{
			final String serverName = result.getServerName();
			
			List<ServerInterfaceEventListener> listenersCopy = new LinkedList<ServerInterfaceEventListener>(serverListeners);
			for (ServerInterfaceEventListener listener : listenersCopy)
			{
				listener.onServerInfoReceived(serverName);
			}
		}
	}
}
