package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionEmote;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionRole;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class RestrictEmoteCommand extends Command{

	public RestrictEmoteCommand(){
		super("restrictemote", "Used restrict emotes to certain roles", Category.ADMIN);
		addOptions(
			new SetCommand(),
			new ResetCommand()
		);
		addPermissions(Permission.ADMINISTRATOR);
	}

	private static class SetCommand extends GuildSubCommand{

		public SetCommand(){
			super("set", "Sets the allowed roles for a specific emote");
			addOptions(
				new CommandOptionEmote("emote", "The emote to restrict").required(),
				new CommandOptionRole("role1", "Role which can use the emote").required(),
				new CommandOptionRole("role2", "Role which can use the emote"),
				new CommandOptionRole("role3", "Role which can use the emote"),
				new CommandOptionRole("role4", "Role which can use the emote"),
				new CommandOptionRole("role5", "Role which can use the emote"),
				new CommandOptionRole("role6", "Role which can use the emote"),
				new CommandOptionRole("role7", "Role which can use the emote"),
				new CommandOptionRole("role8", "Role which can use the emote"),
				new CommandOptionRole("role9", "Role which can use the emote")
			);
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			if(!ctx.getSelfMember().hasPermission(Permission.MANAGE_EMOTES)){
				ctx.error("I can't manage emotes due to lack of permissions. Please give me the `MANAGE_EMOTES` permission to use this command.");
				return;
			}
			var emoteAction = options.getEmote(ctx.getGuild(), "emote");
			if(emoteAction == null){
				ctx.error("Failed to parse emote: `" + options.getString("emote") + "`");
				return;
			}
			emoteAction.queue(emote -> emote.getManager().setRoles(getRoles(ctx.getGuild(), options, "role1", "role2", "role3", "role4", "role5", "role6", "role7", "role8", "role9")).queue(
				success -> ctx.reply("Successfully set roles"),
				error -> ctx.error("Failed to set roles")
				),
				error -> ctx.error("Emote not found in this guild")
			);
		}

		public Set<Role> getRoles(Guild guild, Options options, String... optionNames){
			return Arrays.stream(optionNames).filter(options::has).map(options::getRole).collect(Collectors.toSet());
		}

	}

	private static class ResetCommand extends GuildSubCommand{

		public ResetCommand(){
			super("reset", "Resets all allowed roles for a specific emote");
			addOptions(
				new CommandOptionEmote("emote", "The emote to restrict").required()
			);
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			if(!ctx.getSelfMember().hasPermission(Permission.MANAGE_EMOTES)){
				ctx.error("I can't manage emotes due to lack of permissions. Please give me the `MANAGE_EMOTES` permission to use this command.");
				return;
			}
			var emoteAction = options.getEmote(ctx.getGuild(), "emote");
			if(emoteAction == null){
				ctx.error("Failed to parse emote: `" + options.getString("emote") + "`");
				return;
			}
			emoteAction.queue(emote -> emote.getManager().setRoles(new HashSet<>()).queue(
				success -> ctx.reply("Successfully reset emote"),
				error -> ctx.error("Failed to reset emote")
				),
				error -> ctx.error("Emote not found in this guild")
			);
		}

	}

}
