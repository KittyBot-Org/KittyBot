package de.anteiku.kittybot.commands.utilities;

import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.utils.Utils;
import net.dv8tion.jda.api.Permission;

public class KickCommand extends ACommand{

	public static final String COMMAND = "kick";
	public static final String USAGE = "kick <@user, @user, ...> <reason>";
	public static final String DESCRIPTION = "Kicks some members";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.UTILITIES;

	public KickCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(!ctx.getMember().hasPermission(Permission.KICK_MEMBERS)){
			sendError(ctx, "You don't have enough permission to kick members");
			return;
		}
		var members = ctx.getMentionedMembers();
		if(members.size() == 0){
			sendError(ctx, "Please mention a user");
			return;
		}
		for(var member : members){
			member.kick("").reason("Command ran by '" + ctx.getUser().getAsTag() + "'(" + ctx.getUser().getId() + ")").queue();
		}
		sendAnswer(ctx, "Kicked " + Utils.pluralize("member", members));
	}

}
