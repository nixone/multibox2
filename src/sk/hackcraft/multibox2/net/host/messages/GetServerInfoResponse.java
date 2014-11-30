package sk.hackcraft.multibox2.net.host.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GetServerInfoResponse
{
	protected String serverName;
	
	public GetServerInfoResponse(String serverName) {
		this.serverName = serverName;
	}
	
	@JsonIgnore
	public String getServerName() {
		return this.serverName;
	}
}
