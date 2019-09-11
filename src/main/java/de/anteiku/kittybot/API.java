package de.anteiku.kittybot;

import com.google.gson.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import okhttp3.Request;
import okhttp3.Response;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class API{
	
	public static List<String> toList(List<Role> roles){
		List<String> list = new ArrayList<>();
		for(Role role : roles){
			list.add(role.getId());
		}
		return list;
	}
	
	public static String[] toArray(List<Role> roles){
		String[] list = new String[]{};
		int i = 0;
		for(Role role : roles){
			list[i] = role.getId();
			i++;
		}
		return list;
	}
	
	public static String[] toArray(Set<String> list){
		String[] array = new String[list.size()];
		int i = 0;
		for(String string : list){
			array[i] = string;
			i++;
		}
		return array;
	}
	
	public static List<String> channelsToList(List<TextChannel> channels){
		List<String> list = new ArrayList<>();
		for(TextChannel channel : channels){
			list.add(channel.getId());
		}
		return list;
	}
	
	public static List<String> toList(String[] array){
		List<String> list = new ArrayList<>();
		for(String string : array){
			list.add(string);
		}
		return list;
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
	
	public static boolean contains(String string, String[] strings){
		for(String s : strings){
			if(s.equalsIgnoreCase(string)){
				return true;
			}
		}
		return false;
	}
	
	public static String getIdByMention(String mention){
		if((mention.startsWith("<@!")) && (mention.endsWith(">"))){
			return mention.substring(3, mention.length() - 1);
		}
		if((mention.startsWith("<@")) && (mention.endsWith(">"))){
			return mention.substring(2, mention.length() - 1);
		}
		return null;
	}
	
	public static String getMeme(String search){
		String rawUrl = "https://api.imgur.com/3/gallery/search/{{top}}/{{all}}/{{1}}?q=" + search;
		try{
			URL url = new URL(rawUrl);
			URLConnection request = url.openConnection();
			request.setRequestProperty("Authorization", "Client-ID " + KittyBot.IMGUR_CLIENT_ID);
			request.connect();
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(new InputStreamReader((InputStream)request.getContent()));
			JsonArray array = root.getAsJsonObject().get("data").getAsJsonArray();
			
			return array.get(new Random().nextInt(array.size() - 1)).getAsJsonObject().get("images").getAsJsonArray().get(0).getAsJsonObject().get("link").getAsString();
		}
		catch(IOException e){
			Logger.error(e);
		}
		return "";
	}
	
	public static String getNameByUser(Member member){
		if(member.getNickname() != null){
			return member.getNickname();
		}
		return member.getUser().getName();
	}
	
	public static String getNeko(String search){
		try{
			Request request = new Request.Builder().url("https://nekos.life/api/v2/img/" + search).build();
			Response response = KittyBot.client.newCall(request).execute();
			JsonParser jp = new JsonParser();
			return jp.parse(response.body().string()).getAsJsonObject().get("url").getAsString();
		}
		catch(IOException e){
			Logger.error(e);
		}
		return null;
		
	}
	
	public static String getRandomAnteikuImage(String image){
		try{
			URL url = new URL("http://anteiku.de:9000/" + image);
			URLConnection request = url.openConnection();
			request.connect();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String string = in.readLine();
			in.close();
			return string;
		}
		catch(IOException e){
			Logger.error(e);
		}
		return "";
	}
	
	public static String getRandomImage(String search){
		String rawUrl = "https://api.unsplash.com/photos/random/?client_id=" + KittyBot.UNSPLASH_CLIENT_ID + "&query=" + search;
		try{
			URL url = new URL(rawUrl);
			URLConnection request = url.openConnection();
			request.connect();
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(new InputStreamReader((InputStream)request.getContent()));
			
			return root.getAsJsonObject().get("urls").getAsJsonObject().get("regular").getAsString();
		}
		catch(JsonIOException | JsonSyntaxException | IOException e){
			Logger.error(e);
		}
		return "";
	}
	
	public static Message sendPicture(TextChannel channel, String url, Color color){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(color);
		eb.setImage(url);
		
		return channel.sendMessage(eb.build()).complete();
	}
	
	public static Message sendTTSMessage(TextChannel channel, String text){
		MessageBuilder mb = new MessageBuilder();
		mb.setTTS(true);
		mb.setContent(text);
		return channel.sendMessage(mb.build()).complete();
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
				string = Emotes.ZERO;
				break;
			case 1:
				string = Emotes.ONE;
				break;
			case 2:
				string = Emotes.TWO;
				break;
			case 3:
				string = Emotes.THREE;
				break;
			case 4:
				string = Emotes.FOUR;
				break;
			case 5:
				string = Emotes.FIVE;
				break;
			case 6:
				string = Emotes.SIX;
				break;
			case 7:
				string = Emotes.SEVEN;
				break;
			case 8:
				string = Emotes.EIGHT;
				break;
			case 9:
				string = Emotes.NINE;
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
