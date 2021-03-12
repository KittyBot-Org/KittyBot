package de.kittybot.kittybot.commands.tags;

import de.kittybot.kittybot.commands.tags.tags.*;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.GenericHelpCommand;

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
			new InfoCommand(),
			new PublishCommand(),
			new RemoveCommand(),
			new GenericHelpCommand("Tags lets anyone create shortcuts to certain information.\n" +
				"You can create them vctx `/tags create <name> <content>` or  `/tags create <name> <message-id>` to mirror the content of an already sent message.\n" +
				"Members with the `MANAGE_SERVER` permission can also add up to 50 tags as server commands with `/tags publish <tag-name> <command description>`\n" +
				"You can remove them again vctx `/tags remove <tag-name>`"
			)
		);
	}

}
