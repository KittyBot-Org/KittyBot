package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.data.UserSettings;
import de.kittybot.kittybot.objects.module.Module;

import java.util.Set;

import static de.kittybot.kittybot.jooq.tables.UserSettings.USER_SETTINGS;

@SuppressWarnings("unused")
public class UserSettingsModule extends Module{

	private static final Set<Class<? extends Module>> DEPENDENCIES = Set.of(DatabaseModule.class);

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return DEPENDENCIES;
	}

	public UserSettings getUserSettings(long userId){
		return new UserSettings(170939974227591168L);
		/*try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(USER_SETTINGS)){
			return ctx.where(USER_SETTINGS.USER_ID.eq(userId)).fetchOne(UserSettings::new);
		}*/
	}

}
