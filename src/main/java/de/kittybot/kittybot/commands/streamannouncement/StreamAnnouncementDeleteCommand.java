package de.kittybot.kittybot.commands.streamannouncement;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.modules.StreamAnnouncementModule;
import de.kittybot.kittybot.streams.StreamType;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class StreamAnnouncementDeleteCommand extends Command{

	public StreamAnnouncementDeleteCommand(Command parent){
		super(parent, "delete", "Deletes a stream announcement from this guild", Category.ANNOUNCEMENT);
		addAliases("remove", "del");
		setUsage("<twitch/youtube> <username>");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	protected void run(Args args, CommandContext ctx) throws CommandException{
		if(args.size() < 2){
			ctx.sendUsage(this);
			return;
		}
		var type = StreamType.byName(args.get(0));
		if(type == null){
			ctx.sendError("'" + args.get(0) + "' is not a valid stream type. Use twitch or youtube");
			return;
		}
		ctx.get(StreamAnnouncementModule.class).delete(args.get(1), ctx.getGuildId(), type);
		ctx.sendSuccess("Stream announcement deleted");
	}

}
