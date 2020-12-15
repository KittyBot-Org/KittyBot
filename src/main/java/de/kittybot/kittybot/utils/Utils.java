package de.kittybot.kittybot.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.MiscUtil;

public class Utils{

	public static int getUserCount(JDA jda){
		//noinspection ConstantConditions
		return jda.getGuildCache().applyStream(guildStream -> guildStream.mapToInt(Guild::getMemberCount).sum());
	}

	public static boolean isSnowflake(String id){
		try{
			MiscUtil.parseSnowflake(id);
			return true;
		}
		catch(NumberFormatException ignored){
			return false;
		}
	}

	public static boolean isEnable(String string){
		return string.equalsIgnoreCase("enable") || string.equalsIgnoreCase("true") || string.equalsIgnoreCase("on") || string.equalsIgnoreCase("an");
	}

	public static boolean isDisable(String string){
		return string.equalsIgnoreCase("disable") || string.equalsIgnoreCase("false") || string.equalsIgnoreCase("off") || string.equalsIgnoreCase("aus");
	}

	public static boolean isHelp(String string){
		return string.equalsIgnoreCase("?") || string.equalsIgnoreCase("help") || string.equalsIgnoreCase("hilfe");
	}

}
