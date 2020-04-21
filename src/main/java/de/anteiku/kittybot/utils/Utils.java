package de.anteiku.kittybot.utils;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Role;

import java.util.*;

public class Utils{
	
	private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	public static String generate(int length){
		StringBuilder builder = new StringBuilder();
		while(length-- != 0){
			builder.append(CHARS.charAt((int)(Math.random() * CHARS.length())));
		}
		return builder.toString();
	}
	
	public static Set<String> toSet(List<Role> roles){
		Set<String> set = new HashSet<>();
		for(Role role : roles){
			set.add(role.getId());
		}
		return set;
	}
	
	public static Map<String, String> toMap(List<Role> roles, List<Emote> emotes) {
		Map<String, String> map = new HashMap<>();
		int i = 0;
		for(Role role : roles) {
			if(emotes.size() <= i) {
				break;
			}
			map.put(role.getId(), emotes.get(i).getId());
			i++;
		}
		return map;
	}
	
	public static String[] subArray(String[] array, int start){
		return subArray(array, start, array.length);
	}
	
	public static String[] subArray(String[] array, int start, int end){
		String[] strings = new String[end - start];
		int a = 0;
		for(int i = 0; i < array.length; i++){
			if(i >= start && i <= end){
				strings[a] = array[i];
				a++;
			}
		}
		return strings;
	}
	
}
