package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.objects.Tag;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class TagsCommand extends Command{

	public TagsCommand(){
		super("tags", "Used to create/edit/delete/search tags", Category.TAGS);
		addOptions(
			new CreateCommand(),
			new EditCommand(),
			new DeleteCommand(),
			new SearchCommand(),
			new ListCommand(),
			new InfoCommand()
		);
	}

	private static class CreateCommand extends SubCommand{

		public CreateCommand(){
			super("create", "Used to create a tag");
			addOptions(
				new CommandOptionString("name", "Tag name").required(),
				new CommandOptionString("content", "Tag content").required()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var tagName = options.getString("name");
			var created = ctx.get(TagsModule.class).create(tagName, options.getString("content"), ctx.getGuildId(), ctx.getUserId());

			if(created){
				ctx.reply("Created tag with name `" + tagName + "`");
				return;
			}
			ctx.error("A tag with the name `" + tagName + "` already exists");
		}

	}

	private static class EditCommand extends SubCommand{

		public EditCommand(){
			super("edit", "Used to edit a tag");
			addOptions(
				new CommandOptionString("name", "Tag name").required(),
				new CommandOptionString("content", "Tag content").required()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var tagName = options.getString("name");
			var edited = ctx.get(TagsModule.class).edit(tagName, options.getString("content"), ctx.getGuildId(), ctx.getUserId());

			if(edited){
				ctx.reply("Edited tag with name `" + tagName + "`");
				return;
			}
			ctx.error("Tag `" + tagName + "` does not exist or is not owned by you");
		}

	}

	private static class DeleteCommand extends SubCommand{

		public DeleteCommand(){
			super("delete", "Used to delete a tag");
			addOptions(
				new CommandOptionString("name", "Tag name").required()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var tagName = options.getString("name");
			var deleted = ctx.get(TagsModule.class).delete(tagName, ctx.getGuildId(), ctx.getUserId());

			if(deleted){
				ctx.reply("Deleted tag with name `" + tagName + "`");
				return;
			}
			ctx.error("Tag `" + tagName + "` does not exist or is not owned by you");
		}

	}

	private static class SearchCommand extends SubCommand{

		public SearchCommand(){
			super("search", "Used to search a tag");
			addOptions(
				new CommandOptionString("name", "Tag name").required()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var tagName = options.getString("name");
			var tags = ctx.get(TagsModule.class).search(tagName, ctx.getGuildId(), ctx.getUserId());

			if(tags.isEmpty()){
				ctx.reply("No tags found for `" + tagName + "`");
				return;
			}
			// TODO add paginator
			ctx.reply("**Following tags were found for `" + tagName + "`:**\n" +
				tags.stream().map(tag -> "• `" + tag.getName() + "` (" + MessageUtils.getUserMention(tag.getUserId()) + ")").collect(Collectors.joining("\n"))
			);
		}

	}

	private static class ListCommand extends SubCommand{

		public ListCommand(){
			super("list", "Used to list tags");
			addOptions(
				new CommandOptionString("user", "Filter by user")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			List<Tag> tags;
			if(options.has("user")){
				tags = ctx.get(TagsModule.class).get(ctx.getGuildId(), options.getLong("user"));
			}
			else{
				tags = ctx.get(TagsModule.class).get(ctx.getGuildId());
			}

			if(tags.isEmpty()){
				ctx.reply("No tags created yet");
				return;
			}
			// TODO add paginator
			ctx.reply("**Following tags exist:**\n" + tags.stream().map(tag -> "• `" + tag.getName() + "` (" + MessageUtils.getUserMention(tag.getUserId()) + ")").collect(Collectors.joining("\n")));
		}

	}

	private static class InfoCommand extends SubCommand{

		public InfoCommand(){
			super("info", "Used to get info about a tag");
			addOptions(
				new CommandOptionString("name", "Tag name").required()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var tagName = options.getString("name");
			var tag = ctx.get(TagsModule.class).get(tagName, ctx.getGuildId());

			if(tag == null){
				ctx.error("Tag with name `" + tagName + "` not found");
				return;
			}
			ctx.reply(new EmbedBuilder()
				.setColor(Colors.KITTYBOT_BLUE)
				.setTitle("Tag `" + tagName + "`")
				.addField("Owner", MessageUtils.getUserMention(tag.getUserId()), false)
				.addField("ID", Long.toString(tag.getId()), false)
				.addField("Created at", TimeUtils.format(tag.getCreatedAt()), false)
				.addField("Updated at", (tag.getUpdatedAt() == null ? "not edited" : TimeUtils.format(tag.getUpdatedAt())), false)
			);
		}

	}

}
