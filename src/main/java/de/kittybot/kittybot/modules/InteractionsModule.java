package de.kittybot.kittybot.modules;

import club.minnced.discord.webhook.receive.ReadonlyMessage;
import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.slashcommands.application.CommandOptionsHolder;
import de.kittybot.kittybot.slashcommands.application.PermissionHolder;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.InteractionOptionsHolder;
import de.kittybot.kittybot.slashcommands.interaction.response.FollowupMessage;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionRespondAction;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponseType;
import de.kittybot.kittybot.utils.Config;
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
	private static final Set<Class<? extends Module>> DEPENDENCIES = Set.of(CommandsModule.class);
	private static final String INTERACTION_CREATE = "INTERACTION_CREATE";

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return DEPENDENCIES;
	}

	@Override
	public void onRawGateway(@NotNull RawGatewayEvent event){
		if(event.getType().equals(INTERACTION_CREATE)){
			var start = System.currentTimeMillis();

			var interaction = Interaction.fromJSON(this.modules, event.getPayload(), event.getJDA());
			var settings = this.modules.get(SettingsModule.class).getSettings(interaction.getGuild().getIdLong());

			if(settings.isBotIgnoredUser(interaction.getMember().getIdLong())){
				reply(interaction, false).content("I ignore u baka").ephemeral().queue();
				return;
			}

			if(settings.isBotDisabledInChannel(interaction.getChannelId())){
				reply(interaction, false).content("I'm disabled in this channel").ephemeral().queue();
				return;
			}

			var data = interaction.getData();
			var cmd = this.modules.get(CommandsModule.class).getCommands().get(data.getName());
			if(cmd == null){
				LOG.error("Could not process interaction: {}", event.getPayload());
				reply(interaction).ephemeral().content("Nani u discovered a secret don't tell anyone(This is weired af)").queue();
				return;
			}
			process(cmd, interaction, data);

			Metrics.COMMAND_LATENCY.labels(cmd.getName()).set(System.currentTimeMillis() - start);
			Metrics.COMMAND_COUNTER.labels(cmd.getName()).inc();
		}
	}

	public void process(CommandOptionsHolder applicationHolder, Interaction interaction, InteractionOptionsHolder holder){
		if(applicationHolder instanceof PermissionHolder){
			var permHolder = ((PermissionHolder) applicationHolder);
			var member = interaction.getMember();
			if(permHolder.isDevOnly() && !Config.DEV_IDS.contains(member.getIdLong())){
				reply(interaction).ephemeral().content("This command is developer only").withSource(false).channelMessage(false).queue();
				return;
			}

			var missingPerms = permHolder.getPermissions().stream().dropWhile(member.getPermissions()::contains).collect(Collectors.toSet());
			if(!missingPerms.isEmpty()){
				reply(interaction).ephemeral().content("You are missing following permissions to use this command:\n" + missingPerms.stream().map(Permission::getName).collect(Collectors.joining(", "))).withSource(false).queue();
				return;
			}
		}

		if(applicationHolder instanceof RunnableCommand){
			try{
				((RunnableCommand) applicationHolder).run(new Options(applicationHolder.getOptions(), holder.getOptions()), new CommandContext(interaction, this.modules));
			}
			catch(Exception e){
				reply(interaction).ephemeral().content(e.getMessage()).type(InteractionResponseType.ACKNOWLEDGE).queue();
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
