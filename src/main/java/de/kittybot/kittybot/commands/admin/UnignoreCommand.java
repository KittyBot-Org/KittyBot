package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.util.List;
import java.util.stream.Collectors;

public class UnignoreCommand extends Command{

	public UnignoreCommand(){
		super("unignore", "Makes Kitty unignore users", Category.ADMIN);
		setUsage("<@User/user_id ...>");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	protected void run(List<String> args, CommandContext ctx){
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
			ctx.sendError("Please provide a user");
		}
		ctx.getGuildSettingsManager().deleteBotIgnoredUsers(ctx.getGuild().getIdLong(), users.stream().map(User::getIdLong).collect(Collectors.toSet()));
		ctx.sendSuccess(new EmbedBuilder().setDescription("Unignoring following users: " + users.stream().map(User::getAsMention).collect(Collectors.joining(", "))));
	}

}
