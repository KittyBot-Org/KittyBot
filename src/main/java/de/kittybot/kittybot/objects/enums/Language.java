package de.kittybot.kittybot.objects.enums;

import java.util.Arrays;

public enum Language{

	AFRIKAANS("af", "Afrikaans"),
	ALBANIAN("sq", "Albanian"),
	AMHARIC("am", "Amharic"),
	ARABIC("ar", "Arabic"),
	ARMENIAN("hy", "Armenian"),
	AZERBAIJANI("az", "Azerbaijani"),
	BASQUE("eu", "Basque"),
	BELARUSIAN("be", "Belarusian"),
	BENGALI("bn", "Bengali"),
	BOSNIAN("bs", "Bosnian"),
	BULGARIAN("bg", "Bulgarian"),
	CATALAN("ca", "Catalan"),
	CEBUANO("ceb", "Cebuano"),
	CHICHEWA("ny", "Chichewa"),
	CHINESE_SIMPLIFIED("zh-cn", "Chinese Simplified"),
	CHINESE_TRADITIONAL("zh-tw", "Chinese Traditional"),
	CORSICAN("co", "Corsican"),
	CROATIAN("hr", "Croatian"),
	CZECH("cs", "Czech"),
	DANISH("da", "Danish"),
	DUTCH("nl", "Dutch"),
	ENGLISH("en", "English"),
	ESPERANTO("eo", "Esperanto"),
	ESTONIAN("et", "Estonian"),
	FILIPINO("tl", "Filipino"),
	FINNISH("fi", "Finnish"),
	FRENCH("fr", "French"),
	FRISIAN("fy", "Frisian"),
	GALICIAN("gl", "Galician"),
	GEORGIAN("ka", "Georgian"),
	GERMAN("de", "German"),
	Greek("el", "Greek"),
	Gujarati("gu", "Gujarati"),
	Haitian_Creole("ht", "Haitian Creole"),
	Hausa("ha", "Hausa"),
	Hawaiian("haw", "Hawaiian"),
	Hebrew("iw", "Hebrew"),
	Hindi("hi", "Hindi"),
	Hmong("hmn", "Hmong"),
	Hungarian("hu", "Hungarian"),
	Icelandic("is", "Icelandic"),
	Igbo("ig", "Igbo"),
	Indonesian("id", "Indonesian"),
	Irish("ga", "Irish"),
	Italian("it", "Italian"),
	Japanese("ja", "Japanese"),
	Javanese("jw", "Javanese"),
	Kannada("kn", "Kannada"),
	Kazakh("kk", "Kazakh"),
	Khmer("km", "Khmer"),
	Korean("ko", "Korean"),
	Kurdish_Kurmanji("ku", "Kurdish (Kurmanji)"),
	Kyrgyz("ky", "Kyrgyz"),
	Lao("lo", "Lao"),
	Latin("la", "Latin"),
	Latvian("lv", "Latvian"),
	Lithuanian("lt", "Lithuanian"),
	Luxembourgish("lb", "Luxembourgish"),
	Macedonian("mk", "Macedonian"),
	Malagasy("mg", "Malagasy"),
	Malay("ms", "Malay"),
	Malayalam("ml", "Malayalam"),
	Maltese("mt", "Maltese"),
	Maori("mi", "Maori"),
	Marathi("mr", "Marathi"),
	Mongolian("mn", "Mongolian"),
	Myanmar_Burmese("my", "Myanmar (Burmese)"),
	Nepali("ne", "Nepali"),
	Norwegian("no", "Norwegian"),
	Pashto("ps", "Pashto"),
	Persian("fa", "Persian"),
	Polish("pl", "Polish"),
	Portuguese("pt", "Portuguese"),
	Punjabi("ma", "Punjabi"),
	Romanian("ro", "Romanian"),
	Russian("ru", "Russian"),
	Samoan("sm", "Samoan"),
	Scots_Gaelic("gd", "Scots Gaelic"),
	Serbian("sr", "Serbian"),
	Sesotho("st", "Sesotho"),
	Shona("sn", "Shona"),
	Sindhi("sd", "Sindhi"),
	Sinhala("si", "Sinhala"),
	Slovak("sk", "Slovak"),
	Slovenian("sl", "Slovenian"),
	Somali("so", "Somali"),
	Spanish("es", "Spanish"),
	Sundanese("su", "Sundanese"),
	Swahili("sw", "Swahili"),
	Swedish("sv", "Swedish"),
	Tajik("tg", "Tajik"),
	Tamil("ta", "Tamil"),
	Telugu("te", "Telugu"),
	Thai("th", "Thai"),
	Turkish("tr", "Turkish"),
	Ukrainian("uk", "Ukrainian"),
	Urdu("ur", "Urdu"),
	Uzbek("uz", "Uzbek"),
	Vietnamese("vi", "Vietnamese"),
	Welsh("cy", "Welsh"),
	Xhosa("xh", "Xhosa"),
	Yiddish("yi", "Yiddish"),
	Yoruba("yo", "Yoruba"),
	Zulu("zu", "Zulu"),
	AUTO("auto", "Auto"),
	UNKNOWN("??", "Unknown");

	private final String shortname, name;

	Language(String shortName, String name){
		this.shortname = shortName;
		this.name = name;
	}

	public static Language getFromName(String name){
		return Arrays.stream(values()).filter(lang -> lang.name.equalsIgnoreCase(name) || lang.shortname.equalsIgnoreCase(name)).findFirst().orElse(UNKNOWN);
	}

	public String getShortname(){
		return this.shortname;
	}

	public String getName(){
		return this.name;
	}

}
