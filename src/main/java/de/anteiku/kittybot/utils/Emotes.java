package de.anteiku.kittybot.utils;

import de.anteiku.emojiutils.EmojiUtils;

public enum Emotes{
	
	//CUSTOM EMOTES
	BLANK("blank", "702992080653516810"),
	KITTY_BLINK("KittyBlink", "666742934045196304", true),
	TWITTER("twitter", "702991502045085696"),
	CONSOLE("console", "702991438287470643"),
	INVITE("invite", "702991540171046993"),
	DISCORD("discord", "702991398840041505"),
	
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
	FOX("fox"),
	
	BACK("rewind"),
	FORWARD("fast_forward"),
	PLAY_PAUSE("black_right_pointing_triangle_with_double_vertical_bar"),
	SHUFFLE("twisted_rightwards_arrows"),
	VOLUME_DOWN("sound"),
	VOLUME_UP("loud_sound");
	
	public static final String WHITESPACE = "�";
	
	private final String emote;
	
	Emotes(String name, String id, boolean animated){
		if(animated){
			this.emote = "<a:" + name + ":" + id + "> ";
		}
		else{
			this.emote = "<:" + name + ":" + id + "> ";
		}
	}
	
	Emotes(String name, String id){
		this.emote = "<:" + name + ":" + id + "> ";
	}
	
	Emotes(String name){
		this.emote = EmojiUtils.getEmoji(name);
	}
	
	public String get(){
		return emote;
	}
	
}
