package de.kittybot.kittybot.objects.enums;

import java.util.Arrays;

public enum Neko{

	BITE(0, "bite", false),
	BLUSH(1, "blush", false),
	CRY(2, "cry", false),
	TICKLE(20, "tickle", false),
	CUDDLE(3, "cuddle", false),
	FEED(6, "feed", false),
	DANCE(4, "dance", false),
	EEVEE(5, "eevee", false),
	HOLO(8, "holo", false),
	HUG(9, "hug", false),
	KISS(10, "kiss", false),
	KITSUNE(11, "kitsune", false),
	LICK(12, "lick", false),
	NEKO_SFW(13, "neko", false),
	PAT(14, "pat", false),
	POKE(15, "poke", false),
	SENKO(16, "senko", false),
	SLAP(17, "slap", false),
	SMILE(18, "smile", false),
	TAIL(19, "tail", false),
	FLUFF(7, "fluff", false),

	ANAL(21, "anal", true),
	BLOWJOB(22, "blowjob", true),
	CUM(23, "cum", true),
	FUCK(24, "fuck", true),
	NEKO_NSFW(25, "neko", true),
	PUSSY_LICK(26, "pussylick", true),
	SOLO(27, "solo", true),
	THREESOME_FFF(28, "threesome_fff", true),
	THREESOME_FFM(29, "threesome_ffm", true),
	THREESOME_MMF(30, "threesome_fmm", true),
	YAOI(31, "yaoi", true),
	YURI(32, "yuri", true);

	private final int id;
	private final String name;
	private final boolean nsfw;

	Neko(int id, String name, boolean nsfw){
		this.id = id;
		this.name = name;
		this.nsfw = nsfw;
	}

	public static Neko byId(int id){
		return Arrays.stream(values()).filter(neko -> neko.getId() == id).findFirst().orElse(null);
	}

	public int getId(){
		return this.id;
	}

	public String getName(){
		return this.name;
	}

	public boolean isNsfw(){
		return this.nsfw;
	}
}
