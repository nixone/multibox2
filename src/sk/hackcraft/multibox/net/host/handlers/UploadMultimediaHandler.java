package sk.hackcraft.multibox.net.host.handlers;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.util.Log;
import sk.hackcraft.multibox.android.client.util.FileUtil;
import sk.hackcraft.multibox.android.host.LibraryView;
import sk.hackcraft.multibox.android.host.Song;
import sk.hackcraft.multibox.net.host.RequestResponseMessageHandler;
import sk.hackcraft.multibox.net.messages.UploadMultimediaResponse;
import sk.hackcraft.netinterface.message.MessageType;
import sk.hackcraft.netinterface.message.transformer.DataTransformer;
import sk.hackcraft.netinterface.message.transformer.IdentityTransformer;
import sk.hackcraft.netinterface.message.transformer.JacksonStringTransformer;
import sk.hackcraft.netinterface.message.transformer.JoinTransformer;
import sk.hackcraft.netinterface.message.transformer.StringByteArrayTransformer;

public class UploadMultimediaHandler extends RequestResponseMessageHandler<byte[], UploadMultimediaResponse>
{
	static private final String TAG = UploadMultimediaHandler.class.getName();
	
	private LibraryView library;
	private Context context;

	public UploadMultimediaHandler(MessageType responseMessageType, LibraryView library, Context context)
	{
		super(responseMessageType);
		this.library = library;
		this.context = context;
	}

	@Override
	public UploadMultimediaResponse handle(byte[] request)
	{
		try {
			File file = FileUtil.writeTempFile(context, request);
			Song song = library.upload(file);
			
			return new UploadMultimediaResponse(true, song.getId());
		} catch(IOException e) {
			Log.w(TAG, "IOException during upload to library", e);
			return new UploadMultimediaResponse(false);
		}
	}

	@Override
	public DataTransformer<byte[], byte[]> getRequestDecoder()
	{
		return new IdentityTransformer<byte[]>();
	}

	@Override
	public DataTransformer<UploadMultimediaResponse, byte[]> getResponseEncoder()
	{
		return new JoinTransformer<UploadMultimediaResponse, String, byte[]>(
				new JacksonStringTransformer<UploadMultimediaResponse>(), 
				new StringByteArrayTransformer()
		);
	}
}
