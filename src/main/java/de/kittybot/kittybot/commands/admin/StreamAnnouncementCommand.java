package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.Permission;

import java.util.List;

public class StreamAnnouncementCommand extends Command{

	public StreamAnnouncementCommand(){
		super("streamannouncement", "Enables or sets join messages for the current channel", Category.ADMIN);
		addAliases("streamannounce", "streamannouncement");
		setUsage("<twitch/youtube> <username>");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	protected void run(List<String> args, CommandContext ctx){
		var guild = ctx.getGuild().getIdLong();
		var settingsManager = ctx.getGuildSettingsManager();
		if(args.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		else if(Utils.isEnable(args.get(0))){
			settingsManager.setJoinMessagesEnabled(guild, false);
			ctx.sendSuccess("Disabled join messages here");
			return;
		}
		else if(Utils.isDisable(args.get(0))){
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
