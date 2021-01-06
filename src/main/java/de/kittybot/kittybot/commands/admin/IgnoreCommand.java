package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.util.stream.Collectors;

public class IgnoreCommand extends Command{

	public IgnoreCommand(){
		super("ignore", "Makes Kitty ignore users", Category.ADMIN);
		addAliases("shutup");
		setUsage("<@User/user_id ...>");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	protected void run(Args args, CommandContext ctx){
		var users = ctx.getMentionedUsers();
		for(var arg : args.getList()){
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
		ctx.getGuildSettingsModule().addBotIgnoredUsers(ctx.getGuildId(), users.stream().map(User::getIdLong).collect(Collectors.toSet()));
		ctx.sendSuccess(new EmbedBuilder().setDescription("Ignoring following users: " + users.stream().map(User::getAsMention).collect(Collectors.joining(", "))));
	}

}
