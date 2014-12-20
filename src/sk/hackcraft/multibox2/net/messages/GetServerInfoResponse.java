package sk.hackcraft.multibox2.net.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GetServerInfoResponse
{
	@JsonProperty
	private String serverName;
	
	protected GetServerInfoResponse() {
		// empty for jackson
	}
	
	public GetServerInfoResponse(String serverName) {
		this.serverName = serverName;
	}
	
	@JsonIgnore
	public String getServerName() {
		return this.serverName;
	}
}
