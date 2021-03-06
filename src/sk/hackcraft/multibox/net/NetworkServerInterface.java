package sk.hackcraft.multibox.net;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import sk.hackcraft.multibox.model.LibraryItem;
import sk.hackcraft.multibox.model.libraryitems.MultimediaItem;
import sk.hackcraft.multibox.net.messages.AddLibraryItemToPlaylistRequest;
import sk.hackcraft.multibox.net.messages.AddLibraryItemToPlaylistResponse;
import sk.hackcraft.multibox.net.messages.GetLibraryItemRequest;
import sk.hackcraft.multibox.net.messages.GetLibraryItemResponse;
import sk.hackcraft.multibox.net.messages.GetPlayerStateResponse;
import sk.hackcraft.multibox.net.messages.GetPlaylistResponse;
import sk.hackcraft.multibox.net.messages.GetServerInfoResponse;
import sk.hackcraft.multibox.net.messages.UploadMultimediaResponse;
import sk.hackcraft.netinterface.connection.AsynchronousMessageInterface;
import sk.hackcraft.netinterface.connection.AsynchronousMessageInterface.SeriousErrorListener;
import sk.hackcraft.netinterface.message.EmptyMessage;
import sk.hackcraft.netinterface.message.JacksonObjectMessage;
import sk.hackcraft.netinterface.message.JacksonObjectMessageReceiver;
import sk.hackcraft.netinterface.message.Message;
import sk.hackcraft.netinterface.message.MessageType;
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
		messageInterface.setMessageReceiver(MessageTypes.UPLOAD_MULTIMEDIA, new UploadMultimediaReceiver(messageQueue));
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
		GetLibraryItemRequest data = new GetLibraryItemRequest(itemId);
		
		Message message = new JacksonObjectMessage<GetLibraryItemRequest>(MessageTypes.GET_LIBRARY_ITEM, data);
		
		messageInterface.sendMessage(message);
	}
	
	@Override
	public void addLibraryItemToPlaylist(long itemId)
	{
		AddLibraryItemToPlaylistRequest data = new AddLibraryItemToPlaylistRequest(itemId);
		
		Message message = new JacksonObjectMessage<AddLibraryItemToPlaylistRequest>(MessageTypes.ADD_LIBRARY_ITEM_TO_PLAYLIST, data);
		
		messageInterface.sendMessage(message);
	}
	
	@Override
	public void uploadMultimedia(final byte[] multimediaData)
	{
		Message message = new Message()
		{
			@Override
			public MessageType getType()
			{
				return MessageTypes.UPLOAD_MULTIMEDIA;
			}
			
			@Override
			public byte[] getContent() throws IOException
			{
				return multimediaData;
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
	
	private class GetPlayerStateReceiver extends JacksonObjectMessageReceiver<GetPlayerStateResponse>
	{
		public GetPlayerStateReceiver(MessageQueue messageQueue)
		{
			super(messageQueue, GetPlayerStateResponse.class);
		}

		@Override
		public void onResult(GetPlayerStateResponse result)
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
	
	private class GetPlaylistReceiver extends JacksonObjectMessageReceiver<GetPlaylistResponse>
	{
		public GetPlaylistReceiver(MessageQueue messageQueue)
		{
			super(messageQueue, GetPlaylistResponse.class);
		}
		
		@Override
		protected void onResult(GetPlaylistResponse result)
		{
			List<MultimediaItem> playlist = result.getPlaylist();
			
			for (ServerInterfaceEventListener listener : serverListeners)
			{
				listener.onPlaylistReceived(playlist);
			}
		}
	}
	
	private class AddMultimediaToPlaylistReceiver extends JacksonObjectMessageReceiver<AddLibraryItemToPlaylistResponse>
	{
		public AddMultimediaToPlaylistReceiver(MessageQueue messageQueue)
		{
			super(messageQueue, AddLibraryItemToPlaylistResponse.class);
		}

		@Override
		protected void onResult(AddLibraryItemToPlaylistResponse result)
		{
			final boolean success = result.isSuccess();
			final MultimediaItem multimedia = result.getMultimedia();
			
			for (ServerInterfaceEventListener listener : serverListeners)
			{
				listener.onAddingLibraryItemToPlaylistResult(success, multimedia);
			}
		}
	}
	
	private class GetLibraryItemReceiver extends JacksonObjectMessageReceiver<GetLibraryItemResponse>
	{
		public GetLibraryItemReceiver(MessageQueue messageQueue)
		{
			super(messageQueue, GetLibraryItemResponse.class);
		}

		@Override
		protected void onResult(GetLibraryItemResponse result)
		{
			final LibraryItem libraryItem = result.getLibraryItem();
			
			for (ServerInterfaceEventListener listener : serverListeners)
			{
				listener.onLibraryItemReceived(libraryItem);
			}
		}
	}
	
	private class GetServerInfoReceiver extends JacksonObjectMessageReceiver<GetServerInfoResponse>
	{
		public GetServerInfoReceiver(MessageQueue messageQueue)
		{
			super(messageQueue, GetServerInfoResponse.class);
		}

		@Override
		protected void onResult(GetServerInfoResponse result)
		{
			final String serverName = result.getServerName();
			
			List<ServerInterfaceEventListener> listenersCopy = new LinkedList<ServerInterfaceEventListener>(serverListeners);
			for (ServerInterfaceEventListener listener : listenersCopy)
			{
				listener.onServerInfoReceived(serverName);
			}
		}
	}
	
	private class UploadMultimediaReceiver extends JacksonObjectMessageReceiver<UploadMultimediaResponse>
	{
		public UploadMultimediaReceiver(MessageQueue messageQueue)
		{
			super(messageQueue, UploadMultimediaResponse.class);
		}

		@Override
		protected void onResult(UploadMultimediaResponse result)
		{
			final long uploadedMultimediaId = result.getMultimediaId();
			
			List<ServerInterfaceEventListener> listenersCopy = new LinkedList<ServerInterfaceEventListener>(serverListeners);
			for (ServerInterfaceEventListener listener : listenersCopy)
			{
				listener.onMultimediaUploaded(uploadedMultimediaId);
			}
		}
	}
}
