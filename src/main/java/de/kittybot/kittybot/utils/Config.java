package de.kittybot.kittybot.utils;

import de.kittybot.kittybot.exceptions.MissingConfigValuesException;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Config{

	private final DataObject config;

	public Config(String path) throws IOException{
		File config = new File(path);
		if(!config.exists()){
			throw new IOException();
		}
		this.config = DataObject.fromJson(Files.readAllBytes(config.toPath()));
	}

	public void checkMandatoryValues(String... keys) throws MissingConfigValuesException{
		var missingKeys = new HashSet<String>();
		for(var key : keys){
			if(!this.config.hasKey(key)){
				missingKeys.add(key);
			}
		}
		if(!missingKeys.isEmpty()){
			throw new MissingConfigValuesException(missingKeys);
		}
	}

	public boolean hasKey(String key){
		try{
			return !this.config.getString(key).isBlank();
		}
		catch(ParsingException ignored){
		}
		return false;
	}

	public String getString(String key) throws ParsingException{
		return this.config.getString(key);
	}

	public long getLong(String key) throws ParsingException{
		return this.config.getLong(key, 0);
	}

	public byte[] getBytes(String key){
		return this.config.getString(key).getBytes(StandardCharsets.UTF_8);
	}

	public List<DataObject> getArray(String key) throws ParsingException{
		var list = new ArrayList<DataObject>();
		var set = this.config.getArray(key);
		for(int i = 0; i < set.length(); i++){
			try{
				list.add(set.getObject(i));
			}
			catch(ParsingException ignored){
			}
		}
		return list;
	}

	public Set<Long> getLongSet(String key) throws ParsingException{
		var set = this.config.getArray(key);
		var newSet = new HashSet<Long>();
		for(int i = 0; i < set.length(); i++){
			try{
				newSet.add(set.getLong(i));
			}
			catch(ParsingException ignored){
			}
		}
		return newSet;
	}

	public int getInt(String key) throws ParsingException{
		return this.config.getInt(key);
	}

}
