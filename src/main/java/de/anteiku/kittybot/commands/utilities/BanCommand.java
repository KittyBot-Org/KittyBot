package de.anteiku.kittybot.commands.utilities;

import de.anteiku.kittybot.Utils;
import de.anteiku.kittybot.command.ACommand;
import de.anteiku.kittybot.command.Category;
import de.anteiku.kittybot.command.CommandContext;
import net.dv8tion.jda.api.MessageBuilder;
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
		if(members.size() == 0){
			sendError(ctx, "Please mention a user");
			return;
		}
		for(var member : members){
			member.ban(0, "").reason("Command  ran by '" + ctx.getUser().getAsTag() + "'(" + ctx.getUser().getId() + ")").queue();
		}
		sendAnswer(ctx, "Banned " + Utils.pluralize("member", members));
	}

}
