package de.anteiku.kittybot.utils;

public class RandomKey{
	
	private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	private RandomKey(){
	
	}
	
	public static String generate(){
		return generate(32);
	}
	
	public static String generate(int length){
		StringBuilder builder = new StringBuilder();
		while(length-- != 0){
			builder.append(CHARS.charAt((int)(Math.random() * CHARS.length())));
		}
		return builder.toString();
	}
	
}
