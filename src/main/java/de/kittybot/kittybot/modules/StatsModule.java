package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.data.UserSettings;
import de.kittybot.kittybot.objects.data.UserStatistics;
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

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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

	public byte[] generateLevelCard(UserStatistics statistics, UserSettings settings, User user){
		try{
			var avatar = ImageIO.read(new URL(user.getEffectiveAvatarUrl() + "?size=512"));

			// make the avatar round
			var roundAvatar = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
			var roundAvatarG= roundAvatar.createGraphics();
			roundAvatarG.setColor(new Color(settings.getLevelCardBorderColor()));
			roundAvatarG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			roundAvatarG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			roundAvatarG.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			roundAvatarG.fillArc(0, 0, 512, 512, 0, 360);
			roundAvatarG.setClip(new Ellipse2D.Float(8, 8, 496, 496));
			roundAvatarG.drawImage(avatar, 8, 8, 496, 496, null);
			roundAvatarG.dispose();

			// downscale the avatar to get rid of sharp edges
			var downscaledAvatar = new BufferedImage(170, 170, BufferedImage.TYPE_INT_ARGB);
			var downscaledAvatarG = downscaledAvatar.createGraphics();
			downscaledAvatarG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			downscaledAvatarG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			downscaledAvatarG.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			downscaledAvatarG.drawImage(roundAvatar, 0, 0, 170, 170, null);
			downscaledAvatarG.dispose();

			// compute the rank card
			var rankCard = new BufferedImage(800, 200, BufferedImage.TYPE_INT_ARGB);//ImageIO.read(getClass().getResource("/ranktemplate.png")); // load the template
			var g = rankCard.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g.setColor(new Color(settings.getLevelCardColor()));
			g.fillRect(0, 0, 800, 200);
			g.drawImage(downscaledAvatar, 15, 15, 170, 170, null);

			// draw username
			g.setFont(g.getFont().deriveFont(30f).deriveFont(Font.BOLD));
			g.setColor(new Color(settings.getLevelCardFontColor()));
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			var userString = user.getName();
			g.drawString(userString, 200, 130);

			// draw discriminator
			var nameWidth = g.getFontMetrics().stringWidth(userString);
			g.setFont(g.getFont().deriveFont(25f).deriveFont(Font.BOLD));
			g.setColor(g.getColor().darker().darker());
			g.drawString("#" + user.getDiscriminator(), 205 + nameWidth, 130);

			// draw xp
			var currentXp = statistics.getRestXp();
			var neededXp = statistics.getNextLevelXp();
			g.setFont(g.getFont().deriveFont(30f).deriveFont(Font.BOLD));
			g.setColor(new Color(settings.getLevelCardFontColor()));
			var xpString = currentXp + " / " + neededXp + " XP";
			g.drawString(xpString, 785 - g.getFontMetrics().stringWidth(xpString), 130);

			// draw level
			g.setColor(new Color(settings.getLevelCardPrimaryColor()));
			var levelString = "Level: " + statistics.getLevel();
			g.drawString(levelString, 785 - g.getFontMetrics().stringWidth(levelString), 45);

			// draw empty xp bar
			g.setColor(new Color(settings.getLevelCardPrimaryColor()).darker().darker());
			g.fillRoundRect(200, 145, 585, 40, 40, 40);

			// draw current xp bar
			g.setColor(new Color(settings.getLevelCardPrimaryColor()));
			g.fillRoundRect(200, 145, (int) (((double) currentXp) / neededXp * 585), 40, 40, 40);

			g.dispose();

			var baos = new ByteArrayOutputStream();
			ImageIO.write(rankCard, "png", baos);
			return baos.toByteArray();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}

}
