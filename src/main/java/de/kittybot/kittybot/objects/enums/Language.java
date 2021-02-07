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
	GREEK("el", "Greek"),
	GUJARATI("gu", "Gujarati"),
	HAITIAN_CREOLE("ht", "Haitian Creole"),
	HAUSA("ha", "Hausa"),
	HAWAIIAN("haw", "Hawaiian"),
	HEBREW("iw", "Hebrew"),
	HINDI("hi", "Hindi"),
	HMONG("hmn", "Hmong"),
	HUNGARIAN("hu", "Hungarian"),
	ICELANDIC("is", "Icelandic"),
	IGBO("ig", "Igbo"),
	INDONESIAN("id", "Indonesian"),
	IRISH("ga", "Irish"),
	ITALIAN("it", "Italian"),
	JAPANESE("ja", "Japanese"),
	JAVANESE("jw", "Javanese"),
	KANNADA("kn", "Kannada"),
	KAZAKH("kk", "Kazakh"),
	KHMER("km", "Khmer"),
	KOREAN("ko", "Korean"),
	KURDISH_KURMANJI("ku", "Kurdish (Kurmanji)"),
	KYRGYZ("ky", "Kyrgyz"),
	LAO("lo", "Lao"),
	LATIN("la", "Latin"),
	LATVIAN("lv", "Latvian"),
	LITHUANIAN("lt", "Lithuanian"),
	LUXEMBOURGISH("lb", "Luxembourgish"),
	MACEDONIAN("mk", "Macedonian"),
	MALAGASY("mg", "Malagasy"),
	MALAY("ms", "Malay"),
	MALAYALAM("ml", "Malayalam"),
	MALTESE("mt", "Maltese"),
	MAORI("mi", "Maori"),
	MARATHI("mr", "Marathi"),
	MONGOLIAN("mn", "Mongolian"),
	MYANMAR_BURMESE("my", "Myanmar (Burmese)"),
	NEPALI("ne", "Nepali"),
	NORWEGIAN("no", "Norwegian"),
	PASHTO("ps", "Pashto"),
	PERSIAN("fa", "Persian"),
	POLISH("pl", "Polish"),
	PORTUGUESE("pt", "Portuguese"),
	PUNJABI("ma", "Punjabi"),
	ROMANIAN("ro", "Romanian"),
	RUSSIAN("ru", "Russian"),
	SAMOAN("sm", "Samoan"),
	SCOTS_GAELIC("gd", "Scots Gaelic"),
	SERBIAN("sr", "Serbian"),
	SESOTHO("st", "Sesotho"),
	SHONA("sn", "Shona"),
	SINDHI("sd", "Sindhi"),
	SINHALA("si", "Sinhala"),
	SLOVAK("sk", "Slovak"),
	SLOVENIAN("sl", "Slovenian"),
	SOMALI("so", "Somali"),
	SPANISH("es", "Spanish"),
	SUNDANESE("su", "Sundanese"),
	SWAHILI("sw", "Swahili"),
	SWEDISH("sv", "Swedish"),
	TAJIK("tg", "Tajik"),
	TAMIL("ta", "Tamil"),
	TELUGU("te", "Telugu"),
	THAI("th", "Thai"),
	TURKISH("tr", "Turkish"),
	UKRAINIAN("uk", "Ukrainian"),
	URDU("ur", "Urdu"),
	UZBEK("uz", "Uzbek"),
	VIETNAMESE("vi", "Vietnamese"),
	WELSH("cy", "Welsh"),
	XHOSA("xh", "Xhosa"),
	YIDDISH("yi", "Yiddish"),
	YORUBA("yo", "Yoruba"),
	ZULU("zu", "Zulu"),
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
