package de.kittybot.kittybot.commands.utilities;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

import java.util.HashSet;


public class RestrictEmoteCommand extends ACommand{

	public static final String COMMAND = "restrictemote";
	public static final String USAGE = "restrictemote <:Emote:> <@Role, .../reset>";
	public static final String DESCRIPTION = "Restricts a given emote to one or more roles";
	protected static final String[] ALIASES = {"restricte", "remote", "re"};
	protected static final Category CATEGORY = Category.UTILITIES;

	public RestrictEmoteCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(!ctx.getMember().hasPermission(Permission.MANAGE_EMOTES, Permission.MANAGE_ROLES)){
			sendNoPerms(ctx);
			return;
		}
		var emotes = ctx.getMessage().getEmotes();
		var roles = ctx.getMentionedRoles();
		if(emotes.isEmpty()){
			sendUsage(ctx);
			return;
		}
		var emote = emotes.get(0);
		if(roles.isEmpty()){
			if((ctx.getArgs().length > 0 && ctx.getArgs()[0].equalsIgnoreCase("reset")) ||
					ctx.getArgs().length > 1 && ctx.getArgs()[1].equalsIgnoreCase("reset")){
				emote.getManager().setRoles(new HashSet<>()).queue();
				sendSuccess(ctx, "Roles reset");
				return;
			}
			sendUsage(ctx);
			return;
		}
		if(!ctx.getSelfMember().hasPermission(Permission.MANAGE_EMOTES)){
			sendError(ctx, "I can't manage emotes due to lack of permissions. Please give me the `MANAGE_EMOTES` permission to use this command.");
			return;
		}
		emote.getManager().setRoles(new HashSet<>(roles)).queue(
				success -> sendSuccess(ctx, "Successfully set roles"),
				error -> sendError(ctx, "Failed to set roles.\nPlease try again or report this in our discord")
		);
	}

}