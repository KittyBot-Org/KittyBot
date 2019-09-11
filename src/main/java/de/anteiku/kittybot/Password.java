package de.anteiku.kittybot;

public class Password{
	
	private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	public static String generate(int length){
		StringBuilder builder = new StringBuilder();
		while(length-- != 0){
			builder.append(CHARS.charAt((int)(Math.random() * CHARS.length())));
		}
		return builder.toString();
	}
	
	public static String generate(){
		return generate(12);
	}
	
}
