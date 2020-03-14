package de.anteiku.kittybot.utils;

public class ValuePair<K, V>{
	
	private String key, value;
	
	public <K extends String, V extends String> ValuePair(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
}
