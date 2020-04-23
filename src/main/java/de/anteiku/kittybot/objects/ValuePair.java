package de.anteiku.kittybot.objects;

public class ValuePair<K, V>{

	private final String key;
	private final String value;

	public <K extends String, V extends String> ValuePair(K key, V value){
		this.key = key;
		this.value = value;
	}

	public String getKey(){
		return key;
	}

	public String getValue(){
		return value;
	}

}
