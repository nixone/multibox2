package sk.hackcraft.multibox2.net.data;

public class GetServerInfoResultData
{
	private final String serverName;
	
	public GetServerInfoResultData(String serverName)
	{
		this.serverName = serverName;
	}
	
	public String getServerName()
	{
		return serverName;
	}
}
