package de.anteiku.kittybot.utils;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Role;

import java.util.*;

public class API{
	
	public static String[] toArray(List<Role> roles){
		String[] array = new String[roles.size()];
		int i = 0;
		for(Role role : roles){
			array[i] = role.getId();
			i++;
		}
		return array;
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
	
	
	public static String[] toArrayy(List<String> list){
		String[] array = new String[list.size()];
		int i = 0;
		for(String string : list){
			array[i] = string;
			i++;
		}
		return array;
	}
	
	public static long getMs(long start){
		return (System.nanoTime() - start) / 1000000;
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
	
	public static String parseDiscordEmoji(int value){
		String string = "";
		switch(value){
			case 0:
				string = ":zero:";
				break;
			case 1:
				string = ":one:";
				break;
			case 2:
				string = ":two:";
				break;
			case 3:
				string = ":three:";
				break;
			case 4:
				string = ":four:";
				break;
			case 5:
				string = ":five:";
				break;
			case 6:
				string = ":six:";
				break;
			case 7:
				string = ":seven:";
				break;
			case 8:
				string = ":eight:";
				break;
			case 9:
				string = ":nine:";
				break;
		}
		
		return string;
	}
	
	public static String parseEmoji(int value){
		String string = "";
		switch(value){
			case 0:
				string = "zero";
				break;
			case 1:
				string = "one";
				break;
			case 2:
				string = "two";
				break;
			case 3:
				string = "three";
				break;
			case 4:
				string = "four";
				break;
			case 5:
				string = "five";
				break;
			case 6:
				string = "six";
				break;
			case 7:
				string = "seven";
				break;
			case 8:
				string = "eight";
				break;
			case 9:
				string = "nine";
				break;
		}
		
		return string;
	}
	
	public static String parseEmote(int value){
		String string = "";
		switch(value){
			case 0:
				string = Emotes.ZERO.get();
				break;
			case 1:
				string = Emotes.ONE.get();
				break;
			case 2:
				string = Emotes.TWO.get();
				break;
			case 3:
				string = Emotes.THREE.get();
				break;
			case 4:
				string = Emotes.FOUR.get();
				break;
			case 5:
				string = Emotes.FIVE.get();
				break;
			case 6:
				string = Emotes.SIX.get();
				break;
			case 7:
				string = Emotes.SEVEN.get();
				break;
			case 8:
				string = Emotes.EIGHT.get();
				break;
			case 9:
				string = Emotes.NINE.get();
				break;
		}
		
		return string;
	}
	
	public static long parseTimeString(String pollTime){
		long time = 0;
		String[] args = pollTime.split(" ");
		
		for(String s : args){
			if(s.endsWith("h") && s.length() > 1){
				time += 1000 * 60 * 60 * Integer.parseInt(s.substring(0, s.length() - 1));
			}
			else if(s.endsWith("min")){
				time += 1000 * 60 * Integer.parseInt(s.substring(0, s.length() - 3));
			}
			else if(s.endsWith("s") && s.length() > 1){
				time += 1000 * Integer.parseInt(s.substring(0, s.length() - 1));
			}
		}
		return time;
	}
	
}
