package de.kittybot.kittybot.main;

import de.kittybot.kittybot.objects.enums.Environment;
import de.kittybot.kittybot.objects.exceptions.MissingConfigValuesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main{

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String... args) throws InterruptedException{
		try{
			LOG.info("Starting KittyBot in " + Environment.getCurrent() + " mode...");
			LOG.info("\n" +
				"\n" +
				"         _   ___ _   _        ______       _   \n" +
				"        | | / (_) | | |       | ___ \\     | |  \n" +
				"        | |/ / _| |_| |_ _   _| |_/ / ___ | |_ \n" +
				"        |    \\| | __| __| | | | ___ \\/ _ \\| __|\n" +
				"        | |\\  \\ | |_| |_| |_| | |_/ / (_) | |_ \n" +
				"        \\_| \\_/_|\\__|\\__|\\__, \\____/ \\___/ \\__|\n" +
				"                          __/ |                \n" +
				"                         |___/                 \n" +
				"\n" +
				"        https://github.com/KittyBot-Org/KittyBot\n"
			);
			new KittyBot();
		}
		catch(LoginException | IOException | MissingConfigValuesException e){
			LOG.error("Error while starting KittyBot", e);
			System.exit(-1);
		}
	}

}
