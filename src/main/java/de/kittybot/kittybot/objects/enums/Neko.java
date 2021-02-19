package de.kittybot.kittybot.objects.enums;

public enum Neko{

	BITE("bite", false, true),
	BLUSH("blush", false, true),
	CRY("cry", false, true),
	TICKLE("tickle", false, true),
	CUDDLE("cuddle", false, true),
	FEED("feed", false, true),
	DANCE("dance", false, true),
	EEVEE("eevee", false, true),
	EEVEE_GIF("eevee", false, false),
	HOLO("holo", false, false),
	HUG("hug", false, true),
	KISS("kiss", false, true),
	KITSUNE("kitsune", false, false),
	LICK("lick", false, true),
	NEKO_SFW("sfw neko", false, false),
	NEKO_SFW_GIF("sfw neko gif", false, true),
	PAT("pat", false, true),
	POKE("poke", false, true),
	SENKO("senko", false, false),
	SLAP("slap", false, true),
	SMILE( "smile", false, true),
	TAIL("tail", false, true),
	FLUFF("fluff", false, true),

	ANAL("anal", true, true),
	BLOWJOB("blowjob", true, true),
	CUM("cum", true, true),
	FUCK("fuck", true, true),
	NEKO_NSFW("neko", true, false),
	NEKO_NSFW_GIF("neko", true, true),
	PUSSY_LICK("pussylick", true, true),
	SOLO("solo", true, true),
	THREESOME_FFF("threesome_fff", true, true),
	THREESOME_FFM("threesome_ffm", true, true),
	THREESOME_MMF("threesome_fmm", true, true),
	YAOI("yaoi", true, true),
	YURI("yuri", true, true);

	private final String name;
	private final boolean nsfw, gif;

	Neko(String name, boolean nsfw, boolean gif){
		this.name = name;
		this.nsfw = nsfw;
		this.gif = gif;
	}

	public String getName(){
		return this.name;
	}

	public boolean isNsfw(){
		return this.nsfw;
	}

	public boolean isGIF(){
		return this.gif;
	}
}
