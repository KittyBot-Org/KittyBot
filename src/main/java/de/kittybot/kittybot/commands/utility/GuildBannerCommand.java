package de.kittybot.kittybot.commands.utility;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class GuildBannerCommand extends Command{

	private static final String[] SIZES = {"128", "256", "512", "1024"};

	public GuildBannerCommand(){
		super("guildbanner", "Gives some quicklinks to guild banners", Category.UTILITIES);
		setUsage("<GuildId, ...>");
		addAliases("gbanner");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var guilds = new ArrayList<Guild>();
		for(var arg : args.getList()){
			if(!Utils.isSnowflake(arg)){
				continue;
			}
			var guild = ctx.getJDA().getGuildById(arg);
			if(guild != null && !guilds.contains(guild)){
				guilds.add(guild);
			}
		}
		if(guilds.isEmpty()){
			guilds.add(ctx.getGuild());
		}
		var stringBuilder = new StringBuilder();
		var imageUrl = "";
		for(var guild : guilds){
			imageUrl = guild.getBannerUrl();
			stringBuilder.append(guild.getName()).append(": ");
			var icon = guild.getBannerUrl();
			if(icon == null){
				stringBuilder.append("No banner set");
				continue;
			}
			for(var size : SIZES){
				stringBuilder.append(MessageUtils.maskLink(size + "px", guild.getIconUrl() + "?size=" + size)).append(" ");
			}
			stringBuilder.append("\n\n");
		}
		ctx.sendSuccess(new EmbedBuilder().setTitle(MessageUtils.pluralize("Guild Banner", guilds.size())).setImage(imageUrl).setDescription(stringBuilder.toString()));
	}

}
