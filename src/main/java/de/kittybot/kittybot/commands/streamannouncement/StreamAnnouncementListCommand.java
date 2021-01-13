package de.kittybot.kittybot.commands.streamannouncement;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.modules.StreamAnnouncementModule;
import net.dv8tion.jda.api.Permission;

import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class StreamAnnouncementListCommand extends Command{

	public StreamAnnouncementListCommand(Command parent){
		super(parent, "list", "Lists all stream announcements for this guild", Category.ANNOUNCEMENT);
		addAliases("ls");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	protected void run(Args args, CommandContext ctx) throws CommandException{
		var streams = ctx.get(StreamAnnouncementModule.class).get(ctx.getGuildId());
		ctx.sendSuccess("Following stream announcements are enabled:\n" + streams.stream().map(stream -> stream.getUserName() + ": " + stream.getStreamType()).collect(Collectors.joining("\n")));
	}

}
