package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.data.UserStats;
import de.kittybot.kittybot.objects.data.VoiceMember;
import de.kittybot.kittybot.objects.enums.StatisticType;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.SortOrder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.kittybot.kittybot.jooq.Tables.USER_STATISTICS;

public class StatsModule extends Module{

	private Map<Long, VoiceMember> voiceMembers;

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return Set.of(DatabaseModule.class);
	}

	@Override
	public void onEnable(){
		this.voiceMembers = new HashMap<>();
	}

	@Override
	public void onDisable(){
		this.voiceMembers.forEach((userId, voiceMember) -> incrementStat(voiceMember.getGuildId(), userId, USER_STATISTICS.VOICE_TIME, voiceMember.getVoiceTime()));
	}

	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
		var userId = event.getAuthor().getIdLong();
		if(userId == Config.BOT_ID){
			return;
		}
		var stats = new HashMap<Field<? extends Number>, Number>();

		stats.put(USER_STATISTICS.MESSAGES_SENT, 1);
		var emotes = event.getMessage().getEmotes();
		if(!emotes.isEmpty()){
			stats.put(USER_STATISTICS.EMOTES_SENT, emotes.size());
		}
		var newStats = incrementStats(event.getGuild().getIdLong(), userId, stats);
		if(!newStats.checkIfLevelUp()){
			return;
		}
		var channel = event.getChannel();
		if(!event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE)){
			return;
		}
		channel.sendMessage(event.getAuthor().getAsMention() + " leveled up to level " + newStats.getLevel() + " yay!").queue();
	}

	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event){
		for(var voiceState : event.getGuild().getVoiceStates()){
			var userId = voiceState.getMember().getIdLong();
			if(userId == Config.BOT_ID || this.voiceMembers.containsKey(userId)){
				continue;
			}
			this.voiceMembers.put(userId, new VoiceMember(voiceState));
		}
	}

	@Override
	public void onGenericGuildVoice(@NotNull GenericGuildVoiceEvent event){
		var userId = event.getMember().getIdLong();
		if(userId == Config.BOT_ID){
			return;
		}
		var voiceMember = this.voiceMembers.get(userId);
		if(voiceMember == null){
			return;
		}
		if(event instanceof GuildVoiceMuteEvent || event instanceof GuildVoiceDeafenEvent || event instanceof GuildVoiceSuppressEvent){
			var voiceState = event.getVoiceState();
			if(voiceState.isMuted() || voiceState.isDeafened() || voiceState.isSuppressed()){
				incrementStat(event.getGuild().getIdLong(), userId, USER_STATISTICS.VOICE_TIME, voiceMember.getVoiceTime());
				voiceMember.setVoice(false);
				return;
			}
			voiceMember.setVoice(true);
		}
		else if(event instanceof GuildVoiceStreamEvent){
			if(((GuildVoiceStreamEvent) event).isStream()){
				voiceMember.setStreaming(true);
				return;
			}
			incrementStat(event.getGuild().getIdLong(), userId, USER_STATISTICS.STREAM_TIME, voiceMember.getStreamTime());
			voiceMember.setStreaming(false);
		}
	}

	@Override
	public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event){
		var userId = event.getEntity().getIdLong();
		if(userId == Config.BOT_ID){
			return;
		}
		var guildId = event.getEntity().getGuild().getIdLong();
		if(event instanceof GuildVoiceJoinEvent){
			this.voiceMembers.put(userId, new VoiceMember(((GuildVoiceJoinEvent) event).getVoiceState()));
		}
		else if(event instanceof GuildVoiceLeaveEvent){
			var voiceMember = this.voiceMembers.remove(userId);
			if(voiceMember == null){
				return;
			}
			var stats = new HashMap<Field<? extends Number>, Number>();
			if(voiceMember.isVoice()){
				stats.put(USER_STATISTICS.VOICE_TIME, voiceMember.getVoiceTime());
			}
			if(voiceMember.isStreaming()){
				stats.put(USER_STATISTICS.STREAM_TIME, voiceMember.getStreamTime());
			}
			if(!stats.isEmpty()){
				incrementStats(guildId, userId, stats);
			}
		}
	}

	public long calculateXpGain(Map<Field<? extends Number>, ? extends Number> values){
		var xp = 0L;
		for(var entry : values.entrySet()){
			var field = entry.getKey();
			var value = entry.getValue();
			if(USER_STATISTICS.COMMANDS_USED.equals(field)){
				xp += value.intValue() * 20L;
			}
			else if(USER_STATISTICS.EMOTES_SENT.equals(field)){
				xp += value.intValue() * 20L;
			}
			else if(USER_STATISTICS.MESSAGES_SENT.equals(field)){
				xp += value.intValue() * 20L;
			}
			else if(USER_STATISTICS.VOICE_TIME.equals(field)){
				xp += value.longValue() / 30000;
			}
			else if(USER_STATISTICS.STREAM_TIME.equals(field)){
				xp += value.longValue() / 7000;
			}
			else if(USER_STATISTICS.XP.equals(field)){
				xp += value.longValue();
			}
		}
		return xp;
	}

	public <T extends Number> UserStats incrementStat(long guildId, long userId, Field<T> field, T value){
		return incrementStats(guildId, userId, Map.of(field, value));
	}

	public UserStats incrementStats(long guildId, long userId, Map<Field<? extends Number>, Number> values){
		var xpGain = calculateXpGain(values);
		var insert = new HashMap<>(values);
		insert.put(USER_STATISTICS.GUILD_ID, guildId);
		insert.put(USER_STATISTICS.USER_ID, userId);
		insert.put(USER_STATISTICS.XP, xpGain);

		var updateValues = new HashMap<>();
		values.forEach((field, number) -> updateValues.put(field, field.add(number)));
		updateValues.put(USER_STATISTICS.XP, USER_STATISTICS.XP.add(xpGain));
		updateValues.put(USER_STATISTICS.LAST_ACTIVE, LocalDateTime.now());

		var record = this.modules.get(DatabaseModule.class).getCtx()
			.insertInto(USER_STATISTICS)
			.columns(insert.keySet())
			.values(insert.values())
			.onConflict(USER_STATISTICS.GUILD_ID, USER_STATISTICS.USER_ID)
			.doUpdate()
			.set(updateValues)
			.where(USER_STATISTICS.GUILD_ID.eq(guildId).and(USER_STATISTICS.USER_ID.eq(userId)))
			.returning()
			.fetchOne();
		if(record == null){
			return null;
		}
		return new UserStats(record).setLastXpGain(xpGain);
	}

	public List<UserStats> get(long guildId, StatisticType type, SortOrder sortOrder, int limit){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(USER_STATISTICS)){
			return ctx.where(USER_STATISTICS.GUILD_ID.eq(guildId))
				.orderBy(type.getField().sort(sortOrder))
				.limit(limit)
				.fetch().map(UserStats::new);
		}
	}

	public UserStats get(long guildId, long userId){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(USER_STATISTICS)){
			var result = ctx.where(USER_STATISTICS.GUILD_ID.eq(guildId).and(USER_STATISTICS.USER_ID.eq(userId))).fetchOne();
			if(result == null){
				return null;
			}
			return new UserStats(result);
		}
	}

}
