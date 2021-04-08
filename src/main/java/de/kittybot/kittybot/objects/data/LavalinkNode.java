package de.kittybot.kittybot.objects.data;

import net.dv8tion.jda.api.utils.data.DataObject;

public class LavalinkNode{

	private final String host, protocol;
	private final int port;
	private final String password;

	public LavalinkNode(DataObject node){
		this.host = node.getString("host", null);
		this.protocol = node.getString("protocol", "ws");
		this.port = node.getInt("port", -1);
		this.password = node.getString("password", null);
	}

	public String getHost(){
		return this.host;
	}

	public String getProtocol(){
		return this.protocol;
	}

	public int getPort(){
		return this.port;
	}

	public String getPassword(){
		return this.password;
	}

}
