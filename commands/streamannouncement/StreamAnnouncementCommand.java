package de.kittybot.kittybot.main.commands.streamannouncement;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.old.Args;
import de.kittybot.kittybot.slashcommands.old.Command;
import de.kittybot.kittybot.slashcommands.old.CommandContext;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
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
	protected void run(Args args, CommandContext ctx){
		ctx.sendUsage(this.getUsage() + this.getRawUsage());
	}

}
