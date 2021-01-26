package de.kittybot.kittybot.objects.enums;

public enum Environment{

	PRODUCTION,
	DEVELOPMENT;

	public static Environment getCurrentEnv(){
		try{
			var env = System.getenv("ENV");
			if(env != null){
				return valueOf(env);
			}
		}
		catch(IllegalArgumentException ignored){}
		return DEVELOPMENT;
	}
}
