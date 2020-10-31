package de.kittybot.kittybot.commands.utilities;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashSet;


public class GuildIconCommand extends ACommand{

	public static final String COMMAND = "guildicon";
	public static final String USAGE = "guildicon <GuildId, ...>";
	public static final String DESCRIPTION = "Gives some quicklinks to guild icons";
	protected static final String[] ALIASES = {"gicon"};
	protected static final Category CATEGORY = Category.UTILITIES;
	private static final String[] SIZES = {"128", "256", "512", "1024"};

	public GuildIconCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		var guilds = new HashSet<Guild>();
		for(var arg : ctx.getArgs()){
			if(!Utils.isSnowflake(arg)){
				continue;
			}
			var guild = ctx.getJDA().getGuildById(arg);
			if(guild != null){
				guilds.add(guild);
			}
		}
		if(guilds.isEmpty()){
			guilds.add(ctx.getGuild());
		}
		var stringBuilder = new StringBuilder();
		for(var guild : guilds){
			stringBuilder.append(guild.getName()).append(": ");
			var icon = guild.getIconUrl();
			if(icon == null){
				stringBuilder.append("No icon set");
				continue;
			}
			for(var size : SIZES){
				stringBuilder.append(MessageUtils.maskLink(size + "px", guild.getIconUrl() + "?size=" + size)).append(" ");
			}
			stringBuilder.append("\n\n");
		}
		sendSuccess(ctx, new EmbedBuilder().setTitle(Utils.pluralize("Guild Icon", guilds.size())).setDescription(stringBuilder.toString()));
	}

}
