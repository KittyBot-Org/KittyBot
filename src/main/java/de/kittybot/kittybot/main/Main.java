package de.kittybot.kittybot.main;

import de.kittybot.kittybot.exceptions.MissingConfigValuesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main{

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String... args){
		try{
			LOG.info("Starting KittyBot...");
			LOG.info("""
					
					         _   ___ _   _        ______       _
					        | | / (_) | | |       | ___ \\     | |
					        | |/ / _| |_| |_ _   _| |_/ / ___ | |_
					        |    \\| | __| __| | | | ___ \\/ _ \\| __|
					        | |\\  \\ | |_| |_| |_| | |_/ / (_) | |_ 
					        \\_| \\_/_|\\__|\\__|\\__, \\____/ \\___/ \\__|
					                          __/ |
					                         |___/
					
					         https://github.com/KittyBot-Org/KittyBot
			""");
			new KittyBot();
		}
		catch(LoginException | InterruptedException | IOException | MissingConfigValuesException e){
			LOG.error("Error while starting KittyBot", e);
		}
	}

}
