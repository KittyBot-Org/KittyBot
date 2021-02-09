package de.kittybot.kittybot.modules;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import de.kittybot.kittybot.objects.module.Module;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static de.kittybot.kittybot.jooq.Tables.SELF_ASSIGNABLE_ROLE_MESSAGES;

@SuppressWarnings("unused")
public class ReactionRoleModule extends Module{

	private static final Set<Class<? extends Module>> DEPENDENCIES = Set.of(SettingsModule.class);

	private LoadingCache<Long, Set<Long>> reactionMessages;

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return DEPENDENCIES;
	}

	@Override
	public void onEnable(){
		this.reactionMessages = Caffeine.newBuilder()
			.expireAfterAccess(5, TimeUnit.MINUTES)
			.recordStats()
			.build(key -> new HashSet<>());
	}

	@Override
	public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event){
		deleteReactionMessage(event.getGuild().getIdLong(), event.getMessageIdLong());
	}

	@Override
	public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event){
		if(event.getUser().isBot()){
			return;
		}
		var guild = event.getGuild();
		var guildId = guild.getIdLong();
		if(is(guildId, event.getMessageIdLong())){
			if(event.getReactionEmote().isEmoji()){
				event.getReaction().removeReaction(event.getUser()).queue();
				return;
			}
			var settings = this.modules.get(SettingsModule.class).getSettings(guildId);
			var roles = settings.getSelfAssignableRoles();
			var emoteId = event.getReactionEmote().getEmote().getIdLong();
			var selfAssignableRole = roles.stream().filter(r -> r.getEmoteId() == emoteId).findFirst().orElse(null);
			if(selfAssignableRole == null){
				event.getReaction().removeReaction(event.getUser()).queue();
				return;
			}
			var member = event.getMember();
			var role = guild.getRoleById(selfAssignableRole.getRoleId());
			if(role != null && guild.getSelfMember().canInteract(role)){
				if(member.getRoles().stream().anyMatch(r -> r.getIdLong() == selfAssignableRole.getRoleId())){
					guild.removeRoleFromMember(member, role)
						.reason("self unassigned with kittybot")
						.queue();
				}
				else{
					var group = settings.getSelfAssignableRoleGroups().stream().filter(g -> g.getId() == selfAssignableRole.getGroupId()).findFirst().orElse(null);
					if(group != null){
						if(group.getMaxRoles() == -1 || roles.stream().filter(r -> r.getGroupId() == group.getId() && member.getRoles().stream().anyMatch(mr -> mr.getIdLong() == r.getRoleId())).count() < group.getMaxRoles()){
							guild.addRoleToMember(member, role)
								.reason("self assigned with kittybot")
								.queue();
						}
					}
				}
			}
			event.getReaction().removeReaction(event.getUser()).queue();
		}
	}

	public boolean is(long guildId, long messageId){
		var messages = this.reactionMessages.get(guildId);
		if(messages == null){
			return false;
		}
		var is = messages.contains(messageId);
		if(!is){
			is = isReactionMessage(guildId, messageId);
			if(is){
				messages.add(messageId);
			}
		}
		return is;
	}

	private boolean isReactionMessage(long guildId, long messageId){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(SELF_ASSIGNABLE_ROLE_MESSAGES)){
			var res = ctx.where(SELF_ASSIGNABLE_ROLE_MESSAGES.GUILD_ID.eq(guildId).and(SELF_ASSIGNABLE_ROLE_MESSAGES.MESSAGE_ID.eq(messageId))).fetchOne();
			return res != null;
		}
	}

	private void deleteReactionMessage(long guildId, long messageId){
		this.modules.get(DatabaseModule.class).getCtx().deleteFrom(SELF_ASSIGNABLE_ROLE_MESSAGES)
			.where(SELF_ASSIGNABLE_ROLE_MESSAGES.GUILD_ID.eq(guildId).and(SELF_ASSIGNABLE_ROLE_MESSAGES.MESSAGE_ID.eq(messageId))).executeAsync();
	}

	public void add(long guildId, long messageId){
		var messages = this.reactionMessages.get(guildId);
		if(messages == null){
			return;//WTF?!?!?!
		}
		messages.add(messageId);
		insertReactionMessage(guildId, messageId);
	}

	private void insertReactionMessage(long guildId, long messageId){
		this.modules.get(DatabaseModule.class).getCtx().insertInto(SELF_ASSIGNABLE_ROLE_MESSAGES)
			.columns(SELF_ASSIGNABLE_ROLE_MESSAGES.GUILD_ID, SELF_ASSIGNABLE_ROLE_MESSAGES.MESSAGE_ID)
			.values(guildId, messageId)
			.executeAsync();
	}

}
