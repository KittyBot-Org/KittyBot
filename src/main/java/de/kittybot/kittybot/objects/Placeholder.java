package de.kittybot.kittybot.objects;

public class Placeholder{

	private final String name, value;

	public Placeholder(String name, String value){
		this.name = name;
		this.value = value;
	}

	public String getName(){
		return "${" + this.name + "}";
	}

	public String getValue(){
		return this.value;
	}

}
