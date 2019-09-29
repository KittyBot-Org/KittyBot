package de.anteiku.kittybot.config;

import de.anteiku.kittybot.utils.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Config{
	
	private static final String SEPARATOR = "=";
	private static final String NEWLINE = System.lineSeparator();
	private File file;
	private HashMap<String, String> values;
	
	public Config(String filePath){
		file = new File(filePath);
		if(!file.exists()){
			try{
				Logger.print("generating new config: '" + getName() + "'");
				if(!file.createNewFile()){
					Logger.print("Unable create config: '" + getName() + "'");
				}
			}
			catch(IOException e){
				e.printStackTrace();
			}
			values = loadDefault();
			save();
		}
		else{
			values = load();
		}
	}
	
	private HashMap<String, String> loadDefault(){
		HashMap<String, String> values = new HashMap<>();
		values.put("discord_token", "your discord token");
		values.put("discord_client_id", "your discord bot id");
		values.put("discord_client_secret", "your discord bot secret");
		values.put("host", "https://bla.de");
		values.put("unsplash_client_id", "your unsplash client id");
		values.put("default_prefix", ".");
		return values;
	}
	
	private HashMap<String, String> load(){
		HashMap<String, String> values = new HashMap<>();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null){
				
				int i = line.indexOf(SEPARATOR);
				if(i != - 1){
					String key = line.substring(0, i);
					String value = line.substring(i + 1);
					values.put(key, value);
				}
			}
			reader.close();
		}
		catch(IOException e){
			Logger.error(e);
		}
		return values;
	}
	
	public boolean empty(){
		return values.size() == 0;
	}
	
	public void save(){
		try{
			FileWriter writer = new FileWriter(file, false);
			for(Map.Entry<String, String> v : values.entrySet()){
				writer.write(v.getKey() + SEPARATOR + v.getValue() + NEWLINE);
				writer.flush();
			}
			writer.close();
		}
		catch(IOException e){
			Logger.error(e);
		}
	}
	
	public String getName(){
		return file.getName();
	}
	
	public String get(String key){
		String value = values.get(key);
		if(value == null){
			set(key, "");
			save();
			return "";
		}
		return value;
	}
	
	public void reload(){
		values = load();
	}
	
	public void set(String key, String value){
		values.put(key, value);
	}
	
}
