package de.kittybot.kittybot.modules;

import club.minnced.discord.webhook.receive.ReadonlyMessage;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.slashcommands.application.CommandOptionsHolder;
import de.kittybot.kittybot.slashcommands.application.PermissionHolder;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.RunnableGuildCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.InteractionOptionsHolder;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.slashcommands.interaction.response.FollowupMessage;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionRespondAction;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponseType;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.exporters.Metrics;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.RawGatewayEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.requests.Method;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class InteractionsModule extends Module{

	public static final Route INTERACTION_RESPONSE = Route.custom(Method.POST, "interactions/{interaction.id}/{interaction.token}/callback");
	public static final Route INTERACTION_FOLLOW_UP = Route.custom(Method.POST, "webhooks/{application.id}/{interaction.token}");
	private static final Logger LOG = LoggerFactory.getLogger(InteractionsModule.class);
	private static final String INTERACTION_CREATE = "INTERACTION_CREATE";

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return Set.of(CommandsModule.class);
	}

	@Override
	public void onRawGateway(@NotNull RawGatewayEvent event){
		if(!event.getType().equals(INTERACTION_CREATE)){
			return;
		}
		var start = System.currentTimeMillis();
		var interaction = Interaction.fromJSON(this.modules, event.getPayload(), event.getJDA());

		if(interaction instanceof GuildInteraction){
			var guildInteraction = (GuildInteraction) interaction;
			var settings = this.modules.get(SettingsModule.class).getSettings(guildInteraction.getGuild().getIdLong());

			if(settings.isBotIgnoredUser(guildInteraction.getMember().getIdLong())){
				interaction.error("I ignore u baka");
				return;
			}

			if(settings.isBotDisabledInChannel(interaction.getChannelId())){
				interaction.error("I'm disabled in this channel");
				return;
			}
		}

		var data = interaction.getData();
		var cmd = this.modules.get(CommandsModule.class).getCommands().get(data.getId());
		if(cmd != null){
			process(cmd, interaction, data);
			Metrics.COMMAND_LATENCY.labels(cmd.getName()).observe(System.currentTimeMillis() - start);
			Metrics.COMMAND_COUNTER.labels(cmd.getName()).inc();
			return;
		}
		var tag = this.modules.get(TagsModule.class).getPublishedTagById(data.getId());
		if(tag != null){
			if(!(interaction instanceof GuildInteraction)){
				LOG.error("Guild Tag command received from dms???? guild: {} command: {}", tag.getGuildId(), tag.getCommandId());
				return;
			}
			tag.process((GuildInteraction) interaction);
			return;
		}
		LOG.error("Could not process interaction: {}", event.getPayload());
		interaction.error("Nani u discovered a secret don't tell anyone(This command does not exist anymore)");
	}

	public void process(CommandOptionsHolder applicationHolder, Interaction interaction, InteractionOptionsHolder holder){
		if(applicationHolder instanceof PermissionHolder){
			var permHolder = ((PermissionHolder) applicationHolder);
			var user = interaction.getUser();
			if(permHolder.isDevOnly() && !Config.DEV_IDS.contains(user.getIdLong())){
				reply(interaction).ephemeral().content("This command is developer only").type(InteractionResponseType.ACKNOWLEDGE).queue();
				return;
			}
			if(!Config.DEV_IDS.contains(interaction.getUserId())){
				if(interaction instanceof GuildInteraction){
					var guildInteraction = (GuildInteraction) interaction;
					var missingPerms = permHolder.getPermissions().stream().dropWhile(guildInteraction.getMember().getPermissions()::contains).collect(Collectors.toSet());
					if(!missingPerms.isEmpty()){
						interaction.error("You are missing following permissions to use this command:\n" + missingPerms.stream().map(Permission::getName).collect(Collectors.joining(", ")));
						return;
					}
				}
			}
		}

		if(applicationHolder instanceof RunnableCommand || applicationHolder instanceof RunnableGuildCommand){
			try{
				var options = new Options(applicationHolder.getOptions(), holder.getOptions(), interaction.getData().getResolvedMentions());
				if(applicationHolder instanceof RunnableCommand){
					((RunnableCommand) applicationHolder).run(options, interaction);
				}
				else if(interaction instanceof GuildInteraction){
					((RunnableGuildCommand) applicationHolder).run(options, (GuildInteraction) interaction);
				}
				else if(interaction.isFromGuild()){
					interaction.reply("This slash command is not available without inviting Kitty. You can do this " + MessageUtils.maskLink("here", Config.BOT_INVITE_URL));
				}
				else{
					interaction.reply("This slash command is not available in dms. Surruwu");
				}
			}
			catch(Exception e){
				interaction.error(e.getMessage());
			}
			return;
		}

		for(var option : applicationHolder.getOptions()){
			var opt = holder.getOptions().get(0);
			if(opt.getName().equalsIgnoreCase(option.getName())){
				process(option, interaction, opt);
				return;
			}
		}
	}

	public InteractionRespondAction acknowledge(Interaction interaction, boolean withSource){
		return new InteractionRespondAction(interaction.getJDA(), INTERACTION_RESPONSE.compile(String.valueOf(interaction.getId()), interaction.getToken()), interaction, withSource).channelMessage(false);
	}

	public InteractionRespondAction reply(Interaction interaction){
		return reply(interaction, true);
	}

	public InteractionRespondAction reply(Interaction interaction, boolean withSource){
		return new InteractionRespondAction(interaction.getJDA(), INTERACTION_RESPONSE.compile(String.valueOf(interaction.getId()), interaction.getToken()), interaction, withSource);
	}

	public RestAction<ReadonlyMessage> followup(Interaction interaction, FollowupMessage message){

		Route.CompiledRoute route = INTERACTION_FOLLOW_UP.compile(interaction.getJDA().getSelfUser().getId(), interaction.getToken());

		return new RestActionImpl<>(interaction.getJDA(), route, message.toJSON());
	}

}
