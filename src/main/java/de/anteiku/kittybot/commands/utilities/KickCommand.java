package de.anteiku.kittybot.commands.utilities;

import de.anteiku.kittybot.command.ACommand;
import de.anteiku.kittybot.command.Category;
import de.anteiku.kittybot.command.CommandContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Icon;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

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
		var user = ctx.getUser();
		for(var member : members){
			member.kick("").reason("Kicked by command from '" + user.getAsTag() + "'(" + user.getId() + ")").queue();
		}
		sendAnswer(ctx, "Kicked members");
	}

}
