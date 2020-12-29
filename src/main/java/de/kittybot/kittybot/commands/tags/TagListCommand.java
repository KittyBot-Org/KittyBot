package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.objects.Tag;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class TagListCommand extends Command{

	public TagListCommand(Command parent){
		super(parent, "list", "Lists all tags", Category.TAGS);
		addAliases("ls");
		setUsage("<@user/userId>");
	}

	@Override
	protected void run(List<String> args, CommandContext ctx) throws CommandException{
		var guildId = ctx.getGuild().getIdLong();
		if(args.isEmpty()){
			ctx.sendSuccess("This guild has " + ctx.getTagManager().get(guildId).size() + "tags");
			return;
		}
		var users = ctx.getMentionedUsers();
		if(users.isEmpty()){
			if(Utils.isSnowflake(args.get(0))){
				users.add(User.fromId(args.get(0)));
			}
		}
		if(users.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		var tags = ctx.getTagManager().get(users.get(0).getIdLong(), guildId);
		ctx.sendSuccess(users.get(0).getAsMention() + " owns following tags:\n" + tags.parallelStream().map(Tag::getName).collect(Collectors.joining("\n")));
	}

}
