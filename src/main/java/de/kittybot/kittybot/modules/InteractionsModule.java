package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.exceptions.MissingOptionException;
import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.slashcommands.CommandContext;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.CommandOptionsHolder;
import de.kittybot.kittybot.slashcommands.application.PermissionHolder;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.RunnableGuildCommand;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.exporters.Metrics;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.internal.requests.Method;
import net.dv8tion.jda.internal.requests.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class InteractionsModule extends Module{

	public static final Route INTERACTION_RESPONSE = Route.custom(Method.POST, "interactions/{interaction.id}/{interaction.token}/callback");
	public static final Route INTERACTION_FOLLOW_UP = Route.custom(Method.POST, "webhooks/{application.id}/{interaction.token}");
	private static final Logger LOG = LoggerFactory.getLogger(InteractionsModule.class);
	private static final Set<Class<? extends Module>> DEPENDENCIES = Set.of(CommandsModule.class);
	private static final String INTERACTION_CREATE = "INTERACTION_CREATE";

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return DEPENDENCIES;
	}

	@Override
	public void onSlashCommand(SlashCommandEvent event) {
		var start = System.currentTimeMillis();
		var guild = event.getGuild();
		if(guild != null && event.getMember() != null){
			var settings = this.modules.get(SettingsModule.class).getSettings(guild.getIdLong());

			if(settings.isBotIgnoredUser(event.getMember().getIdLong())){
				event.reply("I ignore u baka").setEphemeral(true).queue();
				return;
			}

			if(settings.isBotDisabledInChannel(event.getChannel().getIdLong())){
				event.reply("I'm disabled in this channel").setEphemeral(true).queue();
				return;
			}
		}
		var cmd = this.modules.get(CommandsModule.class).getCommands().get(event.getName());
		if(cmd != null){
			process(cmd, event, event.getOptions());
			Metrics.COMMAND_LATENCY.labels(event.getName()).observe(System.currentTimeMillis() - start);
			Metrics.COMMAND_COUNTER.labels(event.getName()).inc();
			return;
		}
		var tag = this.modules.get(TagsModule.class).getPublishedTagById(event.getCommandIdLong());
		if(tag != null){
			if(!(event.getGuild() == null)){
				LOG.error("Guild Tag command received from dms???? guild: {} command: {}", tag.getGuildId(), tag.getCommandId());
				return;
			}
			tag.process(event);
			return;
		}
		event.reply("Nani u discovered a secret don't tell anyone(This command does not exist anymore)").setEphemeral(true).queue();
	}

	public void process(CommandOptionsHolder applicationHolder, SlashCommandEvent event, List<SlashCommandEvent.OptionData> options){
		if(applicationHolder instanceof PermissionHolder){
			var permHolder = ((PermissionHolder) applicationHolder);
			var user = event.getUser();
			if(permHolder.isDevOnly() && !Config.DEV_IDS.contains(user.getIdLong())){
				event.reply("This command is developer only").setEphemeral(true).queue();
				return;
			}
			if(!Config.DEV_IDS.contains(event.getUser().getIdLong())){
				if(event.getGuild() != null && event.getMember() != null){
					var missingPerms = permHolder.getPermissions().stream().dropWhile(event.getMember().getPermissions()::contains).collect(Collectors.toSet());
					if(!missingPerms.isEmpty()){
						event.reply("You are missing following permissions to use this command:\n" + missingPerms.stream().map(Permission::getName).collect(Collectors.joining(", "))).setEphemeral(true).queue();
						return;
					}
				}
			}
		}

		if(applicationHolder instanceof RunnableCommand || applicationHolder instanceof RunnableGuildCommand){
			try{
				var commandOptions = new Options(event.getOptions());
				if(applicationHolder instanceof RunnableCommand){
					((RunnableCommand) applicationHolder).run(commandOptions, new CommandContext(this.modules, commandOptions, event));
				}
				else if(event.getGuild() != null){
					((RunnableGuildCommand) applicationHolder).run(commandOptions, new GuildCommandContext(this.modules, commandOptions, event));
				}
				/*else if(interaction.isFromGuild()){
					interaction.reply("This slash command is not available without inviting Kitty. You can do this " + MessageUtils.maskLink("here", Config.BOT_INVITE_URL));
				}*/
				else{
					event.reply("This slash command is not available in dms. Surruwu").queue();
				}
			}
			catch(MissingOptionException | OptionParseException e){
				event.reply(e.getMessage()).queue();
			}
			catch(Exception e){
				LOG.error("Unexpected error while handling command", e);
				event.reply("Error while handling event please redirect to following to my dev " + MessageUtils.maskLink("here", Config.SUPPORT_GUILD_INVITE_URL) + "\nError: `" + e.getMessage() + "`").queue();
			}
			return;
		}

		for(var option : applicationHolder.getOptions()){
			var opt = options.get(0);
			if(opt.getName().equalsIgnoreCase(option.getName())){
				process(option, event, opt.getOptions());
				return;
			}
		}
	}

}
