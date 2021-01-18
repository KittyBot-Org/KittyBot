package de.kittybot.kittybot.commands.utility;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.utils.EmoteHelper;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class StealEmoteCommand extends Command{

	private static final int MAX_EMOTE_SIZE = 256000;

	public StealEmoteCommand(){
		super("stealemote", "Gets the avatar of a user", Category.UTILITIES);
		addOptions(
				new EmoteCommand(),
				new EmoteIdCommand(),
				new URLCommand()

		);
		addPermissions(Permission.MANAGE_EMOTES);
	}

	private static class EmoteCommand extends SubCommand{

		public EmoteCommand(){
			super("emote", "Steal an emote by emote");
			addOptions(
					new CommandOptionString("emote", "The emote to steal").required(),
					new CommandOptionString("new-name", "The new emote name")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			if(!ctx.getSelfMember().hasPermission(Permission.MANAGE_EMOTES)){
				ctx.error("To steal emotes I need the `" + Permission.MANAGE_EMOTES.getName() + "` permission");
				return;
			}
			var emoteId = options.getEmoteId("emote");
			if(emoteId == -1){
				ctx.error("Please provide a valid emote id");
				return;
			}
			var name = options.has("new-name") ? options.getString("new-name") : options.getEmoteName("emote");
			EmoteHelper.createEmote(ctx, name, emoteId, options.getIsAnimatedEmote("emote"));
		}

	}

	private static class EmoteIdCommand extends SubCommand{

		public EmoteIdCommand(){
			super("emote-id", "Steal an emote by id");
			addOptions(
					new CommandOptionString("emote-id", "The emote id to steal").required(),
					new CommandOptionString("new-name", "The new emote name"),
					new CommandOptionBoolean("animated", "If the emote is animated")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			if(!ctx.getSelfMember().hasPermission(Permission.MANAGE_EMOTES)){
				ctx.error("To steal emotes I need the `" + Permission.MANAGE_EMOTES.getName() + "` permission");
				return;
			}
			var emoteId = options.getLong("emote-id");
			if(emoteId == -1){
				ctx.error("Please provide a valid emote id");
				return;
			}
			var name = options.has("new-name") ? options.getString("new-name") : Long.toString(emoteId);
			EmoteHelper.createEmote(ctx, name, emoteId, options.has("animated") && options.getBoolean("animated"));
		}

	}

	private static class URLCommand extends SubCommand{

		public URLCommand(){
			super("url", "Steal an emote by url");
			addOptions(
					new CommandOptionString("url", "The image url to steal").required(),
					new CommandOptionString("new-name", "The new emote name").required()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			if(!ctx.getSelfMember().hasPermission(Permission.MANAGE_EMOTES)){
				ctx.error("To steal emotes I need the `" + Permission.MANAGE_EMOTES.getName() + "` permission");
				return;
			}
			EmoteHelper.createEmote(ctx, options.getString("new-name"), options.getString("url"));
		}

	}


}
