package de.kittybot.kittybot.commands.streamannouncement;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.streams.StreamType;
import net.dv8tion.jda.api.Permission;

import java.util.List;

public class StreamAnnouncementDeleteCommand extends Command{

	public StreamAnnouncementDeleteCommand(Command parent){
		super(parent, "delete", "Deletes a stream announcement from this guild", Category.ANNOUNCEMENT);
		addAliases("remove", "del");
		setUsage("<twitch/youtube> <username>");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	protected void run(List<String> args, CommandContext ctx) throws CommandException{
		if(args.size() < 2){
			ctx.sendUsage(this);
			return;
		}
		var type = StreamType.byName(args.get(0));
		if(type == null){
			ctx.sendError("'"  + args.get(0) + "' is not a valid stream type. Use twitch or youtube");
			return;
		}
		ctx.getStreamAnnouncementManager().delete(args.get(1), ctx.getGuild().getIdLong(), type);
		ctx.sendSuccess("Stream announcement deleted");
	}

}
