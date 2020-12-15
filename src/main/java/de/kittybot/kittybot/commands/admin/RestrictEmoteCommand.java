package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import net.dv8tion.jda.api.Permission;

import java.util.HashSet;
import java.util.List;

public class RestrictEmoteCommand extends Command{

	public RestrictEmoteCommand(KittyBot main){
		super("restrictemote", "Restricts a given emote to one or more roles", Category.UTILITIES);
		setUsage("<:Emote:> <, @Role, .../reset>");
		addAliases("restricte", "remote", "re");
		addPermissions(Permission.MANAGE_EMOTES, Permission.MANAGE_ROLES);
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		var emotes = ctx.getMessage().getEmotes();
		var roles = ctx.getMentionedRoles();
		if(emotes.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		var emote = emotes.get(0);
		if(roles.isEmpty()){
			if((args.size() > 0 && args.get(0).equalsIgnoreCase("reset")) ||
					args.size() > 1 && args.get(1).equalsIgnoreCase("reset")){
				emote.getManager().setRoles(new HashSet<>()).queue();
				ctx.sendSuccess("Roles reset");
				return;
			}
			ctx.sendUsage(this);
			return;
		}
		if(!ctx.getSelfMember().hasPermission(Permission.MANAGE_EMOTES)){
			ctx.sendError("I can't manage emotes due to lack of permissions. Please give me the `MANAGE_EMOTES` permission to use this command.");
			return;
		}
		emote.getManager().setRoles(new HashSet<>(roles)).queue(
				success -> ctx.sendSuccess("Successfully set roles"),
				error -> ctx.sendError("Failed to set roles.\nPlease try again or report this in our discord")
		);
	}

}