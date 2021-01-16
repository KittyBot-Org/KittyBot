package de.kittybot.kittybot.main.commands.roles;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.command.old.Context;
import de.kittybot.kittybot.command.old.ReactionContext;
import de.kittybot.kittybot.main.commands.roles.roles.RolesAddCommand;
import de.kittybot.kittybot.main.commands.roles.roles.RolesListCommand;
import de.kittybot.kittybot.main.commands.roles.roles.RolesRemoveCommand;
import de.kittybot.kittybot.modules.CommandResponseModule;
import de.kittybot.kittybot.modules.ReactiveMessageModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.Emoji;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.RestAction;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class RolesCommand extends Command{

	public RolesCommand(){
		super("roles", "Used to manage your roles", Category.ROLES);
		setUsage("<add|remove|list>");
		addAliases("r", "rollen");
		addChildren(
				new RolesAddCommand(this),
				new RolesRemoveCommand(this),
				new RolesListCommand(this)
		);
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var roles = getRoleEmoteMap(ctx);
		if(roles.isEmpty()){
			ctx.sendError("No self-assignable roles configured!\nIf you are an admin use `.roles add @role :emote: @role :emote:...` to add roles!");
			return;
		}
		var sb = new StringBuilder();
		roles.forEach((key, value) -> sb.append(value.getAsMention()).append(Emoji.BLANK.get()).append(Emoji.BLANK.get()).append(key.getAsMention()).append("\n"));

		ctx.answer(new EmbedBuilder().setTitle("Self-assignable roles")
				.setDescription("To get/remove a role click reaction emote. " + Emoji.KITTY_BLINK.get() + "\n\n")
				.setColor(Color.MAGENTA)
				.appendDescription("**Emote:**" + Emoji.BLANK.get() + "**Role:**\n" + sb))
				.queue(message -> {
					ctx.get(ReactiveMessageModule.class).add(ctx, message.getIdLong(), -1);
					ctx.get(CommandResponseModule.class).add(ctx.getMessage().getIdLong(), message.getIdLong());
					RestAction.allOf(roles.values().stream().map(message::addReaction).collect(Collectors.toSet()))
							.flatMap(ignored -> message.addReaction(Emoji.WASTEBASKET.getStripped()))
							.queue();
				});
	}

	public Map<Role, Emote> getRoleEmoteMap(Context ctx){
		var settings = ctx.get(SettingsModule.class);
		var guildId = ctx.getGuildId();
		var roles = settings.getSelfAssignableRoles(guildId);
		var map = new LinkedHashMap<Role, Emote>();
		for(var selfAssignableRole : roles){
			var role = ctx.getGuild().getRoleById(selfAssignableRole.getRoleId());
			if(role == null){
				settings.removeSelfAssignableRoles(guildId, Collections.singleton(selfAssignableRole.getRoleId()));
				continue;
			}
			var emote = ctx.getGuild().getJDA().getEmoteById(selfAssignableRole.getEmoteId());
			if(emote == null){
				settings.removeSelfAssignableRoles(guildId, Collections.singleton(selfAssignableRole.getEmoteId()));
				continue;
			}
			map.put(role, emote);
		}
		return map;
	}

	@Override
	public void process(ReactionContext ctx){
		super.process(ctx);
		var event = ctx.getEvent();
		var roles = getRoleEmoteMap(ctx);
		roles.forEach((role, emote) -> {
			if(event.getReactionEmote().isEmote() && event.getReactionEmote().getId().equals(emote.getId())){
				if(event.getMember().getRoles().contains(role)){
					event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
				}
				else{
					event.getGuild().addRoleToMember(event.getMember(), role).queue();
				}
				event.getReaction().removeReaction(event.getUser()).queue();
			}
		});
	}

}
