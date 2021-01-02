package de.kittybot.kittybot.objects;

import net.dv8tion.jda.api.utils.data.DataObject;

public class LavalinkNode{

	private final String host;
	private final int port;
	private final String password;

	public LavalinkNode(DataObject node){
		this.host = node.getString("host", null);
		this.port = node.getInt("port", -1);
		this.password = node.getString("password", null);
	}

	public String getHost(){
		return host;
	}

	public int getPort(){
		return port;
	}

	public String getPassword(){
		return password;
	}

}
