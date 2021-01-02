package de.kittybot.kittybot.commands.streamannouncement;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;
import net.dv8tion.jda.api.Permission;

import java.util.List;
import java.util.stream.Collectors;

public class StreamAnnouncementListCommand extends Command{

	public StreamAnnouncementListCommand(Command parent){
		super(parent, "list", "Lists all stream announcements for this guild", Category.ANNOUNCEMENT);
		addAliases("ls");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	protected void run(Args args, CommandContext ctx) throws CommandException{
		var streams = ctx.getStreamAnnouncementManager().get(ctx.getGuildId());
		ctx.sendSuccess("Following stream announcements are enabled:\n" + streams.stream().map(stream -> stream.getUserName() + ": " + stream.getStreamType()).collect(Collectors.joining("\n")));
	}

}
