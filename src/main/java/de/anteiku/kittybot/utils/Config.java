package de.anteiku.kittybot.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Config{
	
	private static final String SEPARATOR = "=";
	private File file;
	private Map<String, String> config;
	
	public Config(String filePath){
		file = new File(filePath);
		config = new HashMap<>();
		if(file.exists()){
			load();
		}
	}
	
	public boolean exists(){
		return file.exists();
	}
	
	private void load(){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null){
				int i = line.indexOf(SEPARATOR);
				if(i != -1){
					config.put(line.substring(0, i), line.substring(i + 1));
				}
			}
			reader.close();
		}
		catch(IOException e){
			Logger.error(e);
		}
	}
	
	public String get(String key){
		return config.get(key);
	}
	
}
