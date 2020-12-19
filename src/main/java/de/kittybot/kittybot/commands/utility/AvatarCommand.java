package de.kittybot.kittybot.commands.utility;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.util.List;

public class AvatarCommand extends Command{

	private static final String[] SIZES = {"128", "256", "512", "1024"};

	public AvatarCommand(){
		super("avatar", "Gives some quicklinks to the users avatars", Category.UTILITIES);
		setUsage("<@User, UserId, ...>");
		addAliases("ava");
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		var users = ctx.getMentionedUsers();
		for(var arg : ctx.getArgs()){
			if(!Utils.isSnowflake(arg)){
				continue;
			}
			try{
				var user = ctx.getJDA().retrieveUserById(arg).complete();
				if(!users.contains(user)){
					users.add(user);
				}
			}
			catch(ErrorResponseException ignored){
			}
		}
		if(users.isEmpty()){
			users.add(ctx.getUser());
		}
		var stringBuilder = new StringBuilder();
		var imageUrl = "";
		for(var user : users){
			imageUrl = user.getEffectiveAvatarUrl();
			stringBuilder.append(user.getAsTag()).append(": ");
			for(var size : SIZES){
				stringBuilder.append(MessageUtils.maskLink(size + "px", user.getEffectiveAvatarUrl() + "?size=" + size)).append(" ");
			}
			stringBuilder.append("\n\n");
		}
		ctx.sendSuccess(new EmbedBuilder().setTitle(MessageUtils.pluralize("User Avatar", users)).setImage(imageUrl).setDescription(stringBuilder.toString()));
	}

}
