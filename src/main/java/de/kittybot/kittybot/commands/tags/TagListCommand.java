package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.modules.TagModule;
import de.kittybot.kittybot.objects.Tag;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class TagListCommand extends Command{

	public TagListCommand(Command parent){
		super(parent, "list", "Lists all tags", Category.TAGS);
		addAliases("ls");
		setUsage("<@user/userId>");
	}

	@Override
	protected void run(Args args, CommandContext ctx) throws CommandException{
		var guildId = ctx.getGuildId();
		if(args.isEmpty()){
			var tags = ctx.get(TagModule.class).get(guildId);
			ctx.sendSuccess(new EmbedBuilder()
					.setAuthor("Tags", Category.TAGS.getUrl(), Category.TAGS.getEmoteUrl())
					.setDescription("This guild has " + tags.size() + " tags:\n" + tags.stream().map(Tag::getName).collect(Collectors.joining("\n")))
			);
			return;
		}
		var users = ctx.getMentionedUsers();
		if(users.isEmpty() && Utils.isSnowflake(args.get(0))){
			users.add(User.fromId(args.get(0)));
		}
		if(users.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		var tags = ctx.get(TagModule.class).get(guildId, users.get(0).getIdLong());
		ctx.sendSuccess(users.get(0).getAsMention() + " owns following tags:\n" + tags.stream().map(Tag::getName).collect(Collectors.joining("\n")));
	}

}
