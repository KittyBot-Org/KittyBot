package de.kittybot.kittybot.commands.streamannouncement;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import net.dv8tion.jda.api.Permission;

import java.util.List;

public class StreamAnnouncementCommand extends Command{

	public StreamAnnouncementCommand(){
		super("streamannouncement", "Enables or sets join messages for the current channel", Category.ANNOUNCEMENT);
		addChildren(new StreamAnnouncementCreateCommand(this));
		addChildren(new StreamAnnouncementDeleteCommand(this));
		addChildren(new StreamAnnouncementListCommand(this));
		addAliases("streamannounce", "streamannouncement");
		setUsage("<create/delete/list> <twitch/youtube> <username>");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	protected void run(List<String> args, CommandContext ctx){
		ctx.sendUsage(this.getUsage() + this.getRawUsage());
	}

}
