package sk.hackcraft.multibox2.android.client.util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import sk.hackcraft.multibox.util.SelectedServersStorage;
import sk.hackcraft.util.Log;
import sk.hackcraft.util.MessageQueue;
import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonCacheSelectedServersStorage implements SelectedServersStorage
{
	private static final String FILE_NAME = "selected_servers.cache.json";
	private static final int SERVER_ENTRIES_LIMIT = 5;
	
	private final Context context;
	private final MessageQueue messageQueue;
	private final Log log;
	
	public JsonCacheSelectedServersStorage(Context context, MessageQueue messageQueue, Log log)
	{
		this.context = context;
		this.messageQueue = messageQueue;
		this.log = log;
	}
	
	@Override
	public void requestServerSave(final String address, final String name, final SaveResultListener listener)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					saveServer(address, name);
					
					messageQueue.post(new Runnable()
					{
						@Override
						public void run()
						{
							listener.onResult(true);
						}
					});
				}
				catch (IOException e)
				{
					log.print("Can't save server: " + e.getMessage());
					
					messageQueue.post(new Runnable()
					{
						@Override
						public void run()
						{
							listener.onResult(false);
						}
					});
				}
			}
		}).start();
	}

	@Override
	public void requestServersList(final ServersListListener listener)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					final List<ServerEntry> servers = getServersList();
					
					messageQueue.post(new Runnable()
					{
						@Override
						public void run()
						{
							listener.onServersReceived(servers);
						}
					});
				}
				catch (IOException e)
				{
					log.print("Can't get saved servers: " + e.getMessage());

					messageQueue.post(new Runnable()
					{
						@Override
						public void run()
						{
							listener.onFailure();
						}
					});
				}
				
			}
		}).start();
	}
	
	private File getCacheFile() throws IOException
	{
		File cacheDirectory = context.getCacheDir();
		File file = new File(cacheDirectory, FILE_NAME);
		
		if (!file.exists())
		{
			createCacheFile(file);
		}
		
		return file;
	}
	
	private void createCacheFile(File file) throws IOException
	{
		file.createNewFile();
		
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode node = objectMapper.createObjectNode();
		node.putArray("savedServers");
		
		objectMapper.writeValue(file,  node);
	}
	
	private ObjectNode readCacheFile(ObjectMapper mapper) throws IOException
	{
		File file = getCacheFile();
		return (ObjectNode)mapper.readTree(file);
	}
	
	private void saveToCacheFile(ObjectMapper mapper, JsonNode rootNode) throws IOException
	{
		File file = getCacheFile();
		mapper.writeValue(file, rootNode);
	}
	
	private ServerEntry jsonToServerEntry(ObjectNode object)
	{
		String address = object.path("address").asText();
		String name = object.path("name").asText();
		
		return new ServerEntry(address, name);
	}
	
	private ObjectNode serverEntryToJsonObject(JsonNodeFactory factory, ServerEntry serverEntry)
	{
		ObjectNode node = new ObjectNode(factory);
		
		node.put("address", serverEntry.getAddress());
		node.put("name", serverEntry.getName());
		
		return node;
	}
	
	private void saveServer(String address, String name) throws IOException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode rootNode = (ObjectNode)readCacheFile(objectMapper);

		List<ServerEntry> serverEntriesList = new LinkedList<ServerEntry>();
		
		ServerEntry newServer = new ServerEntry(address, name);
		serverEntriesList.add(newServer);
		
		ArrayNode savedServersNode = (ArrayNode)rootNode.path("savedServers");
		List<ServerEntry> savedServerEntries = readSavedServerEntries(savedServersNode, SERVER_ENTRIES_LIMIT - 1);
		
		serverEntriesList.addAll(savedServerEntries);
		
		ArrayNode newSavedServersNode = serverEntriesToJsonArray(serverEntriesList);
		rootNode.put("savedServers", newSavedServersNode);
		
		saveToCacheFile(objectMapper, rootNode);
	}
	
	private List<ServerEntry> readSavedServerEntries(ArrayNode savedServersNode, int limit)
	{
		List<ServerEntry> serverEntries = new LinkedList<ServerEntry>();
		
		for (JsonNode server : savedServersNode)
		{
			if (limit == 0)
			{
				break;
			}
			
			ServerEntry serverEntry = jsonToServerEntry((ObjectNode)server);
			
			serverEntries.add(serverEntry);
			
			limit--;
		}
		
		return serverEntries;
	}
	
	private ArrayNode serverEntriesToJsonArray(List<ServerEntry> serverEntriesList)
	{
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ArrayNode serverEnttriesArrayNode = new ArrayNode(factory);
		
		for (ServerEntry serverEntry : serverEntriesList)
		{
			ObjectNode node = serverEntryToJsonObject(factory, serverEntry);
			
			serverEnttriesArrayNode.add(node);
		}
		
		return serverEnttriesArrayNode;
	}
	
	private List<ServerEntry> getServersList() throws IOException
	{
		List<ServerEntry> servers = new LinkedList<ServerEntry>();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = readCacheFile(objectMapper);
		
		for (JsonNode server : rootNode.path("savedServers"))
		{
			ServerEntry serverEntry = jsonToServerEntry((ObjectNode)server);
			servers.add(serverEntry);
		}
		
		return servers;
	}
}
