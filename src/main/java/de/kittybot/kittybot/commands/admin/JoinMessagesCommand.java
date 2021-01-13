package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.modules.SettingsModule;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class JoinMessagesCommand extends Command{

	public JoinMessagesCommand(){
		super("joinmessages", "Enables or sets join messages for the current channel", Category.ADMIN);
		addAliases("join", "joinm", "jmessages");
		setUsage(".../<false>/<message>");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	protected void run(Args args, CommandContext ctx){
		var guild = ctx.getGuildId();
		var settingsManager = ctx.get(SettingsModule.class);
		if(!args.isEmpty() && args.isDisable(0)){
			settingsManager.setJoinMessagesEnabled(guild, false);
			ctx.sendSuccess("Disabled join messages here");
			return;
		}
		else if(!args.isEmpty()){
			settingsManager.setJoinMessage(guild, ctx.getRawMessage());
			settingsManager.setJoinMessagesEnabled(guild, true);
			ctx.sendSuccess("Join message enabled & set to:\n" + ctx.getRawMessage());
			return;
		}
		settingsManager.setAnnouncementChannelId(guild, ctx.getChannelId());
		settingsManager.setJoinMessagesEnabled(guild, true);
		ctx.sendSuccess("Enabled join messages here");
	}

}
