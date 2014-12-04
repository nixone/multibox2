package sk.hackcraft.multibox2.net.host.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GetServerInfoResponse
{
	@JsonProperty
	private String serverName;
	
	public GetServerInfoResponse(String serverName) {
		this.serverName = serverName;
	}
	
	@JsonIgnore
	public String getServerName() {
		return this.serverName;
	}
}
