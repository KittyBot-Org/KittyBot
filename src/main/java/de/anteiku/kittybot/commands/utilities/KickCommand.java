package de.anteiku.kittybot.commands.utilities;

import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.utils.Utils;
import net.dv8tion.jda.api.Permission;

public class KickCommand extends ACommand{

	public static final String COMMAND = "kick";
	public static final String USAGE = "kick <@user, @user, ...> <reason>";
	public static final String DESCRIPTION = "Kicks members";
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
		if(members.isEmpty()){
			sendError(ctx, "Please mention at least one member");
			return;
		}
		var selfMember = ctx.getSelfMember();
		if(!selfMember.hasPermission(Permission.BAN_MEMBERS)){
			sendError(ctx, "I have no permission to kick members");
			return;
		}
		var user  = ctx.getUser();
		var failed = 0;
		var success = 0;
		for(var member : members){
			if(!selfMember.canInteract(member)){
				failed++;
				continue;
			}
			member.kick("").reason("Command ran by '" + user.getAsTag() + "'(" + user.getId() + ")").queue();
			success++;
		}
		sendAnswer(ctx, "Successfully kicked " + success + Utils.pluralize(" member", success) + " and failed " + failed + Utils.pluralize(" member", failed));
	}

}
