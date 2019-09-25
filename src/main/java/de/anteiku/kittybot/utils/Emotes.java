package de.anteiku.kittybot.utils;

import de.anteiku.emojiutils.EmojiUtils;

public enum Emotes{
	
	//CUSTOM EMOTES
	BLANK("594914315463557121"),
	TEAM_KITTY("593061412146774016"),
	TEAM_DOGGO("593061487858155540"),
	TEAM_AUSSENSEITER("594902581873082397"),
	NSFW("594884311249846288"),
	LOL("592801570027077652"),
	TFT("594880799640125451"),
	TWITTER("595291425990639658"),
	CONSOLE("595296169693806803"),
	INVITE("595297943192600577"),
	DISCORD("595302847562907649"),
	
	//UNICODE EMOTES
	ARROW_RIGHT("arrow_right"),
	ARROW_LEFT("arrow_left"),
	WASTEBASKET("wastebasket"),
	NEW("new"),
	QUESTION("question"),
	REFRESH("arrows_counterclockwise"),
	CHECK("white_check_mark"),
	EMAIL("email"),
	X("x"),
	ZERO("zero"),
	ONE("one"),
	TWO("two"),
	THREE("three"),
	FOUR("four"),
	FIVE("five"),
	SIX("six"),
	SEVEN("seven"),
	EIGHT("eight"),
	NINE("nine"),
	CAT("cat"),
	DOG("dog"),
	TURTLE("turtle"),
	WOLF("wolf"),
	FOX("fox");
	
	public static final String WHITESPACE = "�";
	
	private String emote;
	
	Emotes(String name){
		if(name.matches("[0-9]+")){
			this.emote = "<:emote:" + name + "> ";
		}
		else{
			this.emote = EmojiUtils.getEmoji(name);
		}
	}
	
	public String get(){
		return emote;
	}

}
