package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.utils.Utils;

import java.util.List;

public class JoinMessagesCommand extends Command{

	public JoinMessagesCommand(){
		super("joinmessages", "Enables or sets join messages for the current channel", Category.ADMIN);
		addAliases("join", "joinm", "jmessages");
		setUsage(".../<false>/<message>");
	}

	@Override
	protected void run(List<String> args, CommandContext ctx){
		var guild = ctx.getGuild().getIdLong();
		var settingsManager = ctx.getGuildSettingsManager();
		if(!args.isEmpty() && Utils.isDisable(args.get(0))){
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
		settingsManager.setAnnouncementChannelId(guild, ctx.getChannel().getIdLong());
		settingsManager.setJoinMessagesEnabled(guild, true);
		ctx.sendSuccess("Enabled join messages here");
	}

}
