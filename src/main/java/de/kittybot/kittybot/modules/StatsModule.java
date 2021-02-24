package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.main.Main;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.USER_STATISTICS;

public class StatsModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	private static final Set<Class<? extends Module>> DEPENDENCIES = Set.of(DatabaseModule.class);
	private static final long MESSAGE_XP = 20L;

	private static final int CARD_WIDTH = 1200;
	private static final int CARD_HEIGHT = 300;
	private static final int CARD_RATIO = CARD_WIDTH / CARD_HEIGHT;

	private static final float FONT_SIZE = 60f;
	private static final float DISCRIMINATOR_FONT_SIZE = (float) (FONT_SIZE / 1.5);

	private static final int RAW_AVATAR_SIZE = 512;
	private static final int RAW_AVATAR_BORDER_SIZE = RAW_AVATAR_SIZE / 64;

	private static final int BORDER_SIZE = CARD_HEIGHT / 13;

	private static final int AVATAR_SIZE = CARD_HEIGHT - BORDER_SIZE * 2;

	private static final int XP_BAR_WIDTH = CARD_WIDTH - AVATAR_SIZE - BORDER_SIZE * 3;
	private static final int XP_BAR_HEIGHT = CARD_HEIGHT / 5;

	private Font font;

	private Map<Long, VoiceMember> voiceMembers;

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return DEPENDENCIES;
	}

	@Override
	public void onEnable(){
		this.voiceMembers = new HashMap<>();
		try{
			this.font = Font.createFont(Font.TRUETYPE_FONT, StatsModule.class.getResourceAsStream("/NotoSans.ttf"));
		}
		catch(FontFormatException | IOException e){
			LOG.error("Failed to load font from resources");
		}
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
			var avatar = ImageIO.read(new URL(user.getEffectiveAvatarUrl() + "?size=" + RAW_AVATAR_SIZE));

			// make the avatar round
			var roundAvatar = new BufferedImage(RAW_AVATAR_SIZE, RAW_AVATAR_SIZE, BufferedImage.TYPE_INT_ARGB);
			var roundAvatarG= roundAvatar.createGraphics();
			roundAvatarG.setColor(new Color(settings.getLevelCardBorderColor()));
			roundAvatarG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			roundAvatarG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			roundAvatarG.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			roundAvatarG.fillArc(0, 0, RAW_AVATAR_SIZE, RAW_AVATAR_SIZE, 0, 360);
			var rawAvatarSize = RAW_AVATAR_SIZE - RAW_AVATAR_BORDER_SIZE * 2;
			roundAvatarG.setClip(new Ellipse2D.Float(RAW_AVATAR_BORDER_SIZE, RAW_AVATAR_BORDER_SIZE, rawAvatarSize, rawAvatarSize));
			roundAvatarG.drawImage(avatar, RAW_AVATAR_BORDER_SIZE, RAW_AVATAR_BORDER_SIZE, rawAvatarSize, rawAvatarSize, null);
			roundAvatarG.dispose();

			// downscale the avatar to get rid of sharp edges
			var downscaledAvatar = new BufferedImage(AVATAR_SIZE, AVATAR_SIZE, BufferedImage.TYPE_INT_ARGB);
			var downscaledAvatarG = downscaledAvatar.createGraphics();
			downscaledAvatarG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			downscaledAvatarG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			downscaledAvatarG.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			downscaledAvatarG.drawImage(roundAvatar, 0, 0, AVATAR_SIZE, AVATAR_SIZE, null);
			downscaledAvatarG.dispose();

			// prepare level card
			var rankCard = new BufferedImage(CARD_WIDTH, CARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			var g = rankCard.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

			g.setColor(new Color(settings.getLevelCardBackgroundColor()));
			g.fillRect(0, 0, CARD_WIDTH, CARD_HEIGHT);

			var backgroundUrl = settings.getLevelCardBackgroundUrl();
			if(backgroundUrl != null){
				var image = ImageIO.read(new URL(backgroundUrl));
				var width = image.getWidth();
				var height = image.getHeight();
				var drawWidth = 0;
				var drawHeight = 0;
				if(width > height){
					drawWidth = width;
					drawHeight = width / CARD_RATIO;
				}
				else{
					drawHeight = height;
					drawWidth = height * CARD_RATIO;
				}
				g.drawImage(image.getSubimage(0, 0, drawWidth, drawHeight), 0, 0, CARD_WIDTH, CARD_HEIGHT, null);
			}

			g.drawImage(downscaledAvatar, BORDER_SIZE, BORDER_SIZE, AVATAR_SIZE, AVATAR_SIZE, null);

			// draw username
			g.setFont(this.font.deriveFont(FONT_SIZE).deriveFont(Font.BOLD));
			g.setColor(new Color(settings.getLevelCardFontColor()));
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			var userString = user.getName();
			var leftAvatarAlign = AVATAR_SIZE + BORDER_SIZE * 2;
			g.drawString(userString, leftAvatarAlign, CARD_HEIGHT - BORDER_SIZE * 2 - XP_BAR_HEIGHT);

			// draw discriminator
			var nameWidth = g.getFontMetrics().stringWidth(userString);
			g.setFont(g.getFont().deriveFont(DISCRIMINATOR_FONT_SIZE).deriveFont(Font.BOLD));
			g.setColor(g.getColor().darker().darker());
			g.drawString("#" + user.getDiscriminator(), AVATAR_SIZE + BORDER_SIZE * 2 + nameWidth, CARD_HEIGHT - BORDER_SIZE * 2 - XP_BAR_HEIGHT);

			// draw xp
			var currentXp = statistics.getRestXp();
			var neededXp = statistics.getNextLevelXp();
			g.setFont(g.getFont().deriveFont(FONT_SIZE).deriveFont(Font.BOLD));
			g.setColor(new Color(settings.getLevelCardFontColor()));
			var xpString = currentXp + " / " + neededXp + " XP";
			g.drawString(xpString, CARD_WIDTH - BORDER_SIZE - g.getFontMetrics().stringWidth(xpString), CARD_HEIGHT - BORDER_SIZE * 2 - XP_BAR_HEIGHT);

			// draw level
			g.setColor(new Color(settings.getLevelCardPrimaryColor()));
			var levelString = "Level: " + statistics.getLevel();
			g.drawString(levelString, CARD_WIDTH - BORDER_SIZE - g.getFontMetrics().stringWidth(levelString), BORDER_SIZE + FONT_SIZE);

			// draw empty xp bar
			g.setColor(new Color(settings.getLevelCardPrimaryColor()).darker().darker());
			g.fillRoundRect(leftAvatarAlign, CARD_HEIGHT - XP_BAR_HEIGHT - BORDER_SIZE, XP_BAR_WIDTH, XP_BAR_HEIGHT, XP_BAR_HEIGHT, XP_BAR_HEIGHT);

			// draw current xp bar
			g.setColor(new Color(settings.getLevelCardPrimaryColor()));
			g.fillRoundRect(leftAvatarAlign, CARD_HEIGHT - XP_BAR_HEIGHT - BORDER_SIZE, (int) (((double) currentXp) / neededXp * XP_BAR_WIDTH), XP_BAR_HEIGHT, XP_BAR_HEIGHT, XP_BAR_HEIGHT);

			g.dispose();

			var baos = new ByteArrayOutputStream();
			ImageIO.write(rankCard, "png", baos);
			return baos.toByteArray();
		}
		catch(IOException e){
			LOG.error("Error while generating level cards", e);
		}
		return null;
	}

}
