package de.anteiku.kittybot;

import de.anteiku.emojiutils.EmojiUtils;
import net.dv8tion.jda.core.entities.Emote;

public class Emotes{
	
	public static final String WHITESPACE = "�";
	
	public static final String ARROWRIGHT = EmojiUtils.getUnicode("arrow_right");
	public static final String ARROWLEFT = EmojiUtils.getUnicode("arrow_left");
	public static final String WASTEBASKET = EmojiUtils.getUnicode("wastebasket");
	public static final String NEW = EmojiUtils.getUnicode("new");
	public static final String QUESTIONMARK = EmojiUtils.getUnicode("question");
	public static final String REFRESH = EmojiUtils.getUnicode("arrows_counterclockwise");
	public static final String CHECK = EmojiUtils.getUnicode("white_check_mark");
	public static final String EMAIL = EmojiUtils.getUnicode("email");
	public static final String X = EmojiUtils.getUnicode("x");
	
	public static final String ZERO = EmojiUtils.getUnicode("zero");
	public static final String ONE = EmojiUtils.getUnicode("one");
	public static final String TWO = EmojiUtils.getUnicode("two");
	public static final String THREE = EmojiUtils.getUnicode("three");
	public static final String FOUR = EmojiUtils.getUnicode("four");
	public static final String FIVE = EmojiUtils.getUnicode("five");
	public static final String SIX = EmojiUtils.getUnicode("six");
	public static final String SEVEN = EmojiUtils.getUnicode("seven");
	public static final String EIGHT = EmojiUtils.getUnicode("eight");
	public static final String NINE = EmojiUtils.getUnicode("nine");
	
	public static final String CAT = EmojiUtils.getUnicode("cat");
	public static final String DOG = EmojiUtils.getUnicode("dog");
	public static final String TURTLE = EmojiUtils.getUnicode("turtle");
	public static final String WOLF = EmojiUtils.getUnicode("wolf");
	public static final String FOX = EmojiUtils.getUnicode("fox");
	public static final String BLANK = "594914315463557121";
	public static final String TEAMKITTY = "593061412146774016";
	public static final String TEAMDOGGO = "593061487858155540";
	public static final String TEAMAUSSENSEITER = "594902581873082397";
	public static final String NSFW = "594884311249846288";
	public static final String LOL = "592801570027077652";
	public static final String TFT = "594880799640125451";
	public static final String TWITTER = "595291425990639658";
	public static final String CONSOLE = "595296169693806803";
	public static final String INVITE = "595297943192600577";
	public static final String DISCORD = "595302847562907649";
	public static Emote blank;
	public static Emote teamkitty;
	public static Emote teamdoggo;
	public static Emote teamaussenseiter;
	public static Emote nsfw;
	public static Emote lol;
	public static Emote tft;
	public static Emote twitter;
	public static Emote console;
	public static Emote invite;
	public static Emote discord;
	
	public Emotes(KittyBot main){
		blank = main.jda.getEmoteById("594914315463557121");
		teamkitty = main.jda.getEmoteById("593061412146774016");
		teamdoggo = main.jda.getEmoteById("593061487858155540");
		teamaussenseiter = main.jda.getEmoteById("594902581873082397");
		nsfw = main.jda.getEmoteById("594884311249846288");
		lol = main.jda.getEmoteById("592801570027077652");
		tft = main.jda.getEmoteById("594880799640125451");
		twitter = main.jda.getEmoteById("595291425990639658");
		console = main.jda.getEmoteById("595296169693806803");
		invite = main.jda.getEmoteById("595297943192600577");
		discord = main.jda.getEmoteById("595302847562907649");
	}
	
}
