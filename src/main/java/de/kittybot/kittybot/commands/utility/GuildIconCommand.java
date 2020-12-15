package de.kittybot.kittybot.commands.utility;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashSet;
import java.util.List;

public class GuildIconCommand extends Command{

	private static final String[] SIZES = {"128", "256", "512", "1024"};

	public GuildIconCommand(KittyBot main){
		super("guildicon", "Gives some quicklinks to guild icons", Category.UTILITIES);
		setUsage("<GuildId, ...>");
		addAliases("gicon");
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
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
		var imageUrl = "";
		for(var guild : guilds){
			imageUrl = guild.getIconUrl();
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
		ctx.sendSuccess(new EmbedBuilder().setTitle(MessageUtils.pluralize("Guild Icon", guilds.size())).setImage(imageUrl).setDescription(stringBuilder.toString()));
	}

}
