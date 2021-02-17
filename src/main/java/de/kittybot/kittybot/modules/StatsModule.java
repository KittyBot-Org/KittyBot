package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.data.Statistics;
import de.kittybot.kittybot.objects.data.VoiceMember;
import de.kittybot.kittybot.objects.enums.StatisticType;
import de.kittybot.kittybot.objects.module.Module;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.SortOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.USER_STATISTICS;

public class StatsModule extends Module{

	private static final Set<Class<? extends Module>> DEPENDENCIES = Set.of(DatabaseModule.class);
	private static final long MESSAGE_XP = 20L;

	private Map<Long, VoiceMember> voiceMembers;

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return DEPENDENCIES;
	}

	@Override
	public void onEnable(){
		this.voiceMembers = new HashMap<>();
	}

	@Override
	public void onDisable(){
		this.voiceMembers.forEach((userId, voiceMember) -> incrementStat(voiceMember.getGuildId(), userId, USER_STATISTICS.VOICE_TIME, voiceMember.getVoiceTime()));
	}

	public <T extends Number> void incrementStat(long guildId, long userId, Field<T> field, T value){
		this.modules.get(DatabaseModule.class).getCtx()
			.insertInto(USER_STATISTICS)
			.columns(USER_STATISTICS.GUILD_ID, USER_STATISTICS.USER_ID, field)
			.values(guildId, userId, value)
			.onConflict(USER_STATISTICS.GUILD_ID, USER_STATISTICS.USER_ID)
			.doUpdate()
			.set(field, field.add(value))
			.where(USER_STATISTICS.GUILD_ID.eq(guildId).and(USER_STATISTICS.USER_ID.eq(userId)))
			.execute();
	}

	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
		var stats = new HashMap<Field<? extends Number>, Number>();

		stats.put(USER_STATISTICS.MESSAGE_COUNT, 1);
		stats.put(USER_STATISTICS.XP, MESSAGE_XP);
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

	public <T extends Number> void incrementStat(GenericGuildEvent event, Member member, Field<T> field, T value){
		incrementStat(event.getGuild().getIdLong(), member.getIdLong(), field, value);
	}

	public void incrementStats(long guildId, long userId, Map<Field<? extends Number>, Number> values){
		var insert = new HashMap<>(values);
		insert.put(USER_STATISTICS.GUILD_ID, guildId);
		insert.put(USER_STATISTICS.USER_ID, userId);
		this.modules.get(DatabaseModule.class).getCtx()
			.insertInto(USER_STATISTICS)
			.columns(insert.keySet())
			.values(insert.values())
			.onConflict(USER_STATISTICS.GUILD_ID, USER_STATISTICS.USER_ID)
			.doUpdate()
			.set(values.entrySet().stream().map(entry -> Map.entry(entry.getKey(), entry.getKey().add(entry.getValue()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
			.where(USER_STATISTICS.GUILD_ID.eq(guildId).and(USER_STATISTICS.USER_ID.eq(userId)))
			.execute();
	}

	public <T extends Number> void incrementStat(GenericGuildEvent event, User user, Field<T> field, T value){
		incrementStat(event.getGuild().getIdLong(), user.getIdLong(), field, value);
	}

	public List<Statistics> get(long guildId, StatisticType type, SortOrder sortOrder, int limit){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(USER_STATISTICS)){
			return ctx.where(USER_STATISTICS.GUILD_ID.eq(guildId))
				.orderBy(type.getField().sort(sortOrder))
				.limit(limit)
				.fetch().map(Statistics::new);
		}
	}

	public Statistics get(long guildId, long userId){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(USER_STATISTICS)){
			var result = ctx.where(USER_STATISTICS.GUILD_ID.eq(guildId).and(USER_STATISTICS.USER_ID.eq(userId))).fetchOne();
			if(result == null){
				return null;
			}
			return new Statistics(result);
		}
	}

}
