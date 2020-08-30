package de.anteiku.kittybot.commands.utilities;

import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.utils.Utils;
import net.dv8tion.jda.api.Permission;

public class BanCommand extends ACommand{

	public static final String COMMAND = "ban";
	public static final String USAGE = "ban <@user, @user, ...> <message del days> <reason>";
	public static final String DESCRIPTION = "Bans some members";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.UTILITIES;

	public BanCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(!ctx.getMember().hasPermission(Permission.BAN_MEMBERS)){
			sendError(ctx, "You don't have enough permission to ban members");
			return;
		}
		var members = ctx.getMentionedMembers();
		if(members.isEmpty()){
			sendError(ctx, "Please mention at least one user");
			return;
		}
		for(var member : members){
			member.ban(0, "").reason("Command  ran by '" + ctx.getUser().getAsTag() + "'(" + ctx.getUser().getId() + ")").queue();
		}
		sendAnswer(ctx, "Banned " + Utils.pluralize("member", members));
	}

}
