package sk.hackcraft.multibox.net.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadMultimediaResponse
{
	@JsonProperty
	private boolean result;
	
	@JsonProperty
	private long multimediaId;
	
	protected UploadMultimediaResponse() {
		// jackson
	}
	
	public UploadMultimediaResponse(boolean result) {
		this.result = result;
	}
	
	public UploadMultimediaResponse(boolean result, long multimediaId) {
		this.multimediaId = multimediaId;
		this.result = result;
	}
	
	@JsonIgnore
	public long getMultimediaId() {
		return multimediaId;
	}
}
