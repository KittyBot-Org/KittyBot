package de.anteiku.kittybot.commands.utilities;

import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.utils.MessageUtils;
import de.anteiku.kittybot.utils.Utils;
import net.dv8tion.jda.api.Permission;

public class BanCommand extends ACommand{

	public static final String COMMAND = "ban";
	public static final String USAGE = "ban <@user, @user, ...> <message del days> <reason>";
	public static final String DESCRIPTION = "Bans members";
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
			sendError(ctx, "Please mention at least one member");
			return;
		}
		var selfMember = ctx.getSelfMember();
		if(!selfMember.hasPermission(Permission.BAN_MEMBERS)){
			sendError(ctx, "I have no permission to ban members");
			return;
		}
		int delDays;
		try{
			System.out.println(ctx.getArgs()[members.size()]);
			delDays = Integer.parseInt(ctx.getArgs()[members.size()]);
		}
		catch(NumberFormatException e){
			sendError(ctx, "Please provide a valid number for message deletion days");
			return;
		}
		var user = ctx.getUser();
		var failed = 0;
		var success = 0;
		for(var member : members){
			sendAnswer(ctx, "Reason: " + MessageUtils.rebuildMessage(ctx.getMessage(), members.get(members.size() - 1).getId() + ">", String.valueOf(delDays)));
			if(!selfMember.canInteract(member)){
				failed++;
				continue;
			}
			//member.ban(delDays, MessageUtils.rebuildMessage(ctx.getMessage(), members.get(members.size() - 1).getId() + ">")).reason("Command ran by '" + user.getAsTag() + "'(" + user.getId() + ")").queue();
			success++;
		}
		sendAnswer(ctx, "Successfully banned " + success + Utils.pluralize(" member", success) + " and failed " + failed + Utils.pluralize(" member", failed));
	}

}
