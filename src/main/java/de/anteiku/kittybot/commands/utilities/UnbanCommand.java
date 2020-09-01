package de.anteiku.kittybot.commands.utilities;

import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.utils.Utils;
import net.dv8tion.jda.api.Permission;

public class UnbanCommand extends ACommand{

	public static final String COMMAND = "unban";
	public static final String USAGE = "unban <@user, @user, ...>";
	public static final String DESCRIPTION = "Unbans users";
	protected static final String[] ALIASES = {"pardon"};
	protected static final Category CATEGORY = Category.UTILITIES;

	public UnbanCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(!ctx.getMember().hasPermission(Permission.BAN_MEMBERS)){
			sendError(ctx, "You don't have enough permission to unban users");
			return;
		}
		var users = ctx.getMentionedUsers();
		if(users.isEmpty()){
			sendError(ctx, "Please mention at least one user");
			return;
		}
		if(!ctx.getSelfMember().hasPermission(Permission.BAN_MEMBERS)){
			sendError(ctx, "I have no permission to unban users");
			return;
		}
		var user = ctx.getUser();
		for(var u : users){
			ctx.getGuild().unban(u.getId()).reason("Command ran by '" + user.getAsTag() + "'(" + user.getId() + ")").queue();
		}
		sendAnswer(ctx, "Unbanned " + Utils.pluralize("user", users));
	}

}
