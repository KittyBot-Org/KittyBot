package de.kittybot.kittybot.objects.enums;

public enum Environment{

	PRODUCTION,
	DEVELOPMENT;

	public static Environment getCurrent(){
		try{
			var env = System.getenv("ENV");
			if(env != null){
				return valueOf(env);
			}
		}
		catch(IllegalArgumentException ignored){
		}
		return DEVELOPMENT;
	}

	public static boolean is(Environment environment){
		return getCurrent() == environment;
	}
}
