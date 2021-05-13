package de.kittybot.kittybot.utils;

import de.kittybot.kittybot.modules.StatsModule;
import de.kittybot.kittybot.objects.data.UserSettings;
import de.kittybot.kittybot.objects.data.UserStats;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class ImageUtils{

	private static final Logger LOG = LoggerFactory.getLogger(ImageUtils.class);

	private static Font FONT;

	private static final int CARD_WIDTH = 1200;
	private static final int CARD_HEIGHT = CARD_WIDTH / 4;
	private static final int CARD_RATIO = CARD_WIDTH / CARD_HEIGHT;

	private static final float FONT_SIZE = 60f;
	private static final float DISCRIMINATOR_FONT_SIZE = (float) (FONT_SIZE / 1.5);

	private static final int RAW_AVATAR_SIZE = 512;
	private static final int RAW_AVATAR_BORDER_SIZE = RAW_AVATAR_SIZE / 64;

	private static final int BORDER_SIZE = CARD_HEIGHT / 13;

	private static final int AVATAR_SIZE = CARD_HEIGHT - BORDER_SIZE * 2;

	private static final int XP_BAR_WIDTH = CARD_WIDTH - AVATAR_SIZE - BORDER_SIZE * 3;
	private static final int XP_BAR_HEIGHT = CARD_HEIGHT / 5;

	static{
		try{
			FONT = Font.createFont(Font.TRUETYPE_FONT, StatsModule.class.getResourceAsStream("/NotoSans.ttf"));
		}
		catch(FontFormatException | IOException e){
			LOG.error("Failed to load font from resources");
		}
	}

	public static byte[] generateLevelCard(UserStats stats, UserSettings settings, User user){
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

			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

			g.drawImage(downscaledAvatar, BORDER_SIZE, BORDER_SIZE, AVATAR_SIZE, AVATAR_SIZE, null);

			// draw username
			g.setFont(FONT.deriveFont(FONT_SIZE).deriveFont(Font.BOLD));
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
			var currentXp = stats.getRestXp();
			var neededXp = stats.getNeededXp();
			g.setFont(g.getFont().deriveFont(FONT_SIZE).deriveFont(Font.BOLD));
			g.setColor(new Color(settings.getLevelCardFontColor()));
			var xpString = currentXp + " / " + neededXp + " XP";
			g.drawString(xpString, CARD_WIDTH - BORDER_SIZE - g.getFontMetrics().stringWidth(xpString), CARD_HEIGHT - BORDER_SIZE * 2 - XP_BAR_HEIGHT);

			// draw level
			g.setColor(new Color(settings.getLevelCardPrimaryColor()));
			var levelString = "Level: " + stats.getLevel();
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

	public static byte[] generateColorImage(Color color){
		try{
			var image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
			var g = image.createGraphics();

			g.setColor(color);
			g.fillRect(0, 0, 100, 100);

			var baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			return baos.toByteArray();
		}
		catch(IOException e){
			LOG.error("Error while creating color image", e);
		}
		return new byte[]{};
	}

}
