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

}
