package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.modules.MessageModule;
import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.objects.settings.Tag;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionLong;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.TimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

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

	private static class CreateCommand extends GuildSubCommand{

		public CreateCommand(){
			super("create", "Used to create a tag");
			addOptions(
				new CommandOptionString("name", "Tag name").required(),
				new CommandOptionString("content", "Tag content"),
				new CommandOptionLong("message-id", "The message id to create a tag from")
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var tagName = options.getString("name");
			if(tagName.length() > 64){
				ia.error("Tag names must be 64 or less characters");
				return;
			}
			if(!options.has("content") && !options.has("message-id")){
				ia.reply("Please provide either content or message-id");
				return;
			}
			var content = "";
			if(options.has("content")){
				content = options.getString("content");
			}
			else{
				var message = ia.getChannel().retrieveMessageById(options.getLong("message-id")).complete();
				if(message == null){
					ia.error("Please provide a recent message id");
					return;
				}
				content = message.getContentRaw();
			}

			var created = ia.get(TagsModule.class).create(tagName, content, ia.getGuildId(), ia.getUserId());

			if(created){
				ia.reply("Created tag with name `" + tagName + "`");
				return;
			}
			ia.error("A tag with the name `" + tagName + "` already exists");
		}

	}

	private static class EditCommand extends GuildSubCommand{

		public EditCommand(){
			super("edit", "Used to edit a tag");
			addOptions(
				new CommandOptionString("name", "Tag name").required(),
				new CommandOptionString("content", "Tag content"),
				new CommandOptionLong("message-id", "The message id to create a tag from")
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var tagName = options.getString("name");
			if(!options.has("content") && !options.has("message-id")){
				ia.reply("Please provide either content or message-id");
				return;
			}
			var content = "";
			if(options.has("content")){
				content = options.getString("content");
			}
			else{
				var message = ia.get(MessageModule.class).getMessageById(options.getLong("message-id"));
				if(message == null){
					ia.error("Please provide a recent message id");
					return;
				}
				content = message.getContent();
			}

			var edited = ia.get(TagsModule.class).edit(tagName, content, ia.getGuildId(), ia.getUserId());

			if(edited){
				ia.reply("Edited tag with name `" + tagName + "`");
				return;
			}
			ia.error("Tag `" + tagName + "` does not exist or is not owned by you");
		}

	}

	private static class DeleteCommand extends GuildSubCommand{

		public DeleteCommand(){
			super("delete", "Used to delete a tag");
			addOptions(
				new CommandOptionString("name", "Tag name").required()
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var tagName = options.getString("name");
			var deleted = false;
			if(ia.getMember().hasPermission(Permission.ADMINISTRATOR)){
				deleted = ia.get(TagsModule.class).delete(tagName, ia.getGuildId(), ia.getUserId());
			}
			else{
				deleted = ia.get(TagsModule.class).delete(tagName, ia.getGuildId());
			}

			if(deleted){
				ia.reply("Deleted tag with name `" + tagName + "`");
				return;
			}
			ia.error("Tag `" + tagName + "` does not exist or is not owned by you");
		}

	}

	private static class SearchCommand extends GuildSubCommand{

		public SearchCommand(){
			super("search", "Used to search a tag");
			addOptions(
				new CommandOptionString("name", "Tag name").required()
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var tagName = options.getString("name");
			var tags = ia.get(TagsModule.class).search(tagName, ia.getGuildId(), ia.getUserId());

			if(tags.isEmpty()){
				ia.reply("No tags found for `" + tagName + "`");
				return;
			}
			// TODO add paginator
			ia.reply("**Following tags were found for `" + tagName + "`:**\n" +
				tags.stream().map(tag -> "• `" + tag.getName() + "` (" + MessageUtils.getUserMention(tag.getUserId()) + ")").collect(Collectors.joining("\n"))
			);
		}

	}

	private static class ListCommand extends GuildSubCommand{

		public ListCommand(){
			super("list", "Used to list tags");
			addOptions(
				new CommandOptionUser("user", "Filter by user")
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			List<Tag> tags;
			if(options.has("user")){
				tags = ia.get(TagsModule.class).get(ia.getGuildId(), options.getLong("user"));
			}
			else{
				tags = ia.get(TagsModule.class).get(ia.getGuildId());
			}

			if(tags.isEmpty()){
				ia.reply("No tags created yet");
				return;
			}
			// TODO add paginator
			ia.reply("**Following tags exist:**\n" + tags.stream().map(tag -> "• `" + tag.getName() + "` (" + MessageUtils.getUserMention(tag.getUserId()) + ")").collect(Collectors.joining("\n")));
		}

	}

	private static class InfoCommand extends GuildSubCommand{

		public InfoCommand(){
			super("info", "Used to get info about a tag");
			addOptions(
				new CommandOptionString("name", "Tag name").required()
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var tagName = options.getString("name");
			var tag = ia.get(TagsModule.class).get(tagName, ia.getGuildId());

			if(tag == null){
				ia.error("Tag with name `" + tagName + "` not found");
				return;
			}
			ia.reply(new EmbedBuilder()
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
