package de.kittybot.kittybot.objects;

public enum Emoji{

	BLANK(702992080653516810L),
	KITTY_BLINK(666742934045196304L, true),
	TWITTER(702991502045085696L),
	CONSOLE(702991438287470643L),
	INVITE(702991540171046993L),
	DISCORD(702991398840041505L),
	GITHUB(766203064486068234L),
	INFORMATION(745709333127626762L),
	UTILITIES(745709801433989120L),
	SNIPE(787807190090121236L),
	ROLES(745709332519321620L),
	MUSIC(745710566571638885L),
	ARROW_LEFT("\u2B05\uFE0F"),
	ADMIN(745715466349314049L),
	BOT_OWNER(788049369109889065L),
	SETTINGS(787994539105320970L),
	NEKO(760947689511714836L, true),
	ARROW_RIGHT("\u27A1\uFE0F"),
	BACK("\u25C0"),
	FORWARD("\u25B6"),
	WASTEBASKET("\uD83D\uDDD1\uFE0F"),
	SHUFFLE("\uD83D\uDD00"),
	VOLUME_DOWN("\uD83D\uDD09"),
	VOLUME_UP("\uD83D\uDD0A"),
	X("\u274C"),
	QUESTION("\u2753"),
	CHECK("\u2705"),
	CAT("\uD83D\uDC31"),
	DOG("\uD83D\uDC36");

	private final long emoteId;
	private final boolean isAnimated;
	private final String unicode;

	Emoji(long emoteId, boolean isAnimated){
		this.emoteId = emoteId;
		this.isAnimated = isAnimated;
		this.unicode = "";
	}

	Emoji(long emoteId){
		this.emoteId = emoteId;
		this.isAnimated = false;
		this.unicode = "";
	}

	Emoji(String unicode){
		this.emoteId = 0;
		this.isAnimated = false;
		this.unicode = unicode;
	}

	public long getId(){
		return this.emoteId;
	}

	public String getUnicode(){
		return this.unicode;
	}

	public String getAsMention(){
		if(this.unicode.equals("")){
			if(this.isAnimated){
				return "<a:emote:" + this.emoteId + ">";
			}
			return "<:emote:" + this.emoteId + ">";
		}
		return this.unicode;
	}
}
