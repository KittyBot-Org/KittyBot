package de.anteiku.kittybot.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Config{
	
	private static final String SEPARATOR = "=";
	private final File file;
	private final Map<String, String> config;
	
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
			e.printStackTrace();
		}
	}
	
	public String get(String key){
		return config.get(key);
	}
	
}
