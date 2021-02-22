package de.kittybot.kittybot.objects.enums;

import de.kittybot.kittybot.utils.Config;

public enum API{

	// other
	PURR_BOT("purr bot", "https://purrbot.site/api/img/%s/%s/%s"),
	HASTEBIN("hastebin", Config.HASTEBIN_URL),
	GOOGLE_TRANSLATE_API("google translate api", "https://translate.google.com/translate_a/single?client=gtx&sl=%s&tl=%s&dt=t&q=%s&ie=UTF-8&oe=UTF-8");

	private final String name;
	private final String url;

	API(final String name, final String url){
		this.name = name;
		this.url = url;
	}

	public String getName(){
		return this.name;
	}

	public String getUrl(){
		return this.url;
	}

}
