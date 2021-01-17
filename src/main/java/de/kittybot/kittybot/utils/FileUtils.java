package de.kittybot.kittybot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils{

	private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

	private FileUtils(){}

	public static List<String> loadMessageFile(String fileName){
		var inputStream = FileUtils.class.getClassLoader().getResourceAsStream("messages/" + fileName + "_messages.txt");
		if(inputStream == null){
			LOG.error("Message file not found");
			return Collections.emptyList();
		}
		var reader = new BufferedReader(new InputStreamReader((inputStream), StandardCharsets.UTF_8));
		List<String> set = new ArrayList<>();
		try{
			String line;
			while((line = reader.readLine()) != null){
				set.add(line);
			}
			reader.close();
		}
		catch(IOException e){
			LOG.error("Error reading message file", e);
		}
		return set;
	}

}
