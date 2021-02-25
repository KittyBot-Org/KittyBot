package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.data.UserStatistics;
import de.kittybot.kittybot.objects.data.VoiceMember;
import de.kittybot.kittybot.objects.enums.StatisticType;
import de.kittybot.kittybot.objects.module.Module;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SortOrder;
import org.jooq.TableField;

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
		var stats = new HashMap<Field<? extends Number>, Number>();

		stats.put(USER_STATISTICS.MESSAGE_COUNT, 1);
		var emotes = event.getMessage().getEmotes();
		if(!emotes.isEmpty()){
			stats.put(USER_STATISTICS.EMOTE_COUNT, emotes.size());
		}
		incrementStats(event.getGuild().getIdLong(), event.getAuthor().getIdLong(), stats);
	}

	@Override
	public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event){
		this.voiceMembers.put(event.getMember().getIdLong(), new VoiceMember(event.getGuild().getIdLong()));
	}

	@Override
	public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event){
		var afkChannel = event.getGuild().getAfkChannel();
		if(afkChannel == null){
			return;
		}
		if(event.getChannelJoined().getIdLong() == afkChannel.getIdLong()){
			updateVoiceStat(event, event.getMember());
			return;
		}
		this.voiceMembers.putIfAbsent(event.getMember().getIdLong(), new VoiceMember(event.getGuild().getIdLong()));
	}

	@Override
	public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event){
		updateVoiceStat(event, event.getMember());
	}

	private void updateVoiceStat(GenericGuildEvent event, Member member){
		var voiceMember = this.voiceMembers.remove(member.getIdLong());
		if(voiceMember == null){
			return;
		}
		incrementStat(event, member, USER_STATISTICS.VOICE_TIME, voiceMember.getVoiceTime());
	}

	public long calculateXpGain(Map<Field<? extends Number>, Number> values){
		var xp = 0L;
		for(var entry : values.entrySet()){
			var field = entry.getKey();
			var value = entry.getValue();
			if(USER_STATISTICS.BOT_CALLS.equals(field)){
				xp += value.intValue() * 20L;
			}
			else if(USER_STATISTICS.EMOTE_COUNT.equals(field)){
				xp += value.intValue() * 20L;
			}
			else if(USER_STATISTICS.MESSAGE_COUNT.equals(field)){
				xp += value.intValue() * 20L;
			}
			else if(USER_STATISTICS.VOICE_TIME.equals(field)){
				xp += value.longValue() / 60000;
				System.out.println("xp: " + xp);
			}
			else if(USER_STATISTICS.XP.equals(field)){
				xp += value.longValue();
			}
		}
		return xp;
	}

	public <T extends Number> UserStatistics incrementStat(GenericGuildEvent event, Member member, Field<T> field, T value){
		return incrementStat(event, member.getUser(), field, value);
	}

	public <T extends Number> UserStatistics incrementStat(GenericGuildEvent event, User user, Field<T> field, T value){
		return incrementStat(event, user.getIdLong(), field, value);
	}

	public <T extends Number> UserStatistics incrementStat(GenericGuildEvent event, long userId, Field<T> field, T value){
		return incrementStat(event.getGuild().getIdLong(), userId, field, value);
	}

	public <T extends Number> UserStatistics incrementStat(long guildId, long userId, Field<T> field, T value){
		return incrementStats(guildId, userId, Map.of(field, value));
	}

	public UserStatistics incrementStats(long guildId, long userId, Map<Field<? extends Number>, Number> values){
		var xpGain = calculateXpGain(values);
		var insert = new HashMap<>(values);
		insert.put(USER_STATISTICS.GUILD_ID, guildId);
		insert.put(USER_STATISTICS.USER_ID, userId);
		insert.put(USER_STATISTICS.XP, xpGain);

		var updateValues = new HashMap<>();
		values.forEach((field, number) -> updateValues.put(field, field.add(number)));
		updateValues.put(USER_STATISTICS.XP, xpGain);

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
		return new UserStatistics(record);
	}

	public List<UserStatistics> get(long guildId, StatisticType type, SortOrder sortOrder, int limit){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(USER_STATISTICS)){
			return ctx.where(USER_STATISTICS.GUILD_ID.eq(guildId))
				.orderBy(type.getField().sort(sortOrder))
				.limit(limit)
				.fetch().map(UserStatistics::new);
		}
	}

	public UserStatistics get(long guildId, long userId){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(USER_STATISTICS)){
			var result = ctx.where(USER_STATISTICS.GUILD_ID.eq(guildId).and(USER_STATISTICS.USER_ID.eq(userId))).fetchOne();
			if(result == null){
				return null;
			}
			return new UserStatistics(result);
		}
	}

}
