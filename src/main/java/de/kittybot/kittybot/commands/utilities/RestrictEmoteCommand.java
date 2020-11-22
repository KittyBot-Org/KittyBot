package de.kittybot.kittybot.commands.utilities;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

import java.util.HashSet;


public class RestrictEmoteCommand extends ACommand{

	public static final String COMMAND = "restrictemote";
	public static final String USAGE = "restrictemote <:Emote:, @Role, ...>";
	public static final String DESCRIPTION = "Restricts a given emote to one or more roles";
	protected static final String[] ALIASES = {"restricte", "remote", "re"};
	protected static final Category CATEGORY = Category.UTILITIES;

	public RestrictEmoteCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		var emotes = ctx.getMessage().getEmotes();
		var roles = new HashSet<>(ctx.getMentionedRolesBag());
		if(emotes.isEmpty() || roles.isEmpty()){
			sendUsage(ctx);
			return;
		}
		if(!ctx.getSelfMember().hasPermission(Permission.MANAGE_EMOTES)){
			sendError(ctx, "I can't manage emotes due to lack of permissions. Please give me the `MANAGE_EMOTES` permission to use this command.");
			return;
		}
		var emote = emotes.get(0);
		emote.getManager().setRoles(roles).queue(success -> sendSuccess(ctx, "Successfully set roles"), error -> sendError(ctx, "Failed to set roles.\nPlease try again or report this in our discord"));
	}

}
