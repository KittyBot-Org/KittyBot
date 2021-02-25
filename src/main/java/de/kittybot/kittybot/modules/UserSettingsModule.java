package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.data.UserSettings;
import de.kittybot.kittybot.objects.module.Module;
import org.jooq.Field;
import org.jooq.Record;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static de.kittybot.kittybot.jooq.Tables.USER_STATISTICS;
import static de.kittybot.kittybot.jooq.tables.UserSettings.USER_SETTINGS;

@SuppressWarnings("unused")
public class UserSettingsModule extends Module{

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return Set.of(DatabaseModule.class);
	}

	public UserSettings getUserSettings(long userId){
		var record = this.modules.get(DatabaseModule.class).getCtx()
			.insertInto(USER_SETTINGS)
			.set(USER_SETTINGS.USER_ID, userId)
			.onDuplicateKeyUpdate()
			.set(USER_SETTINGS.USER_ID, userId)
			.returning()
			.fetchOne();
		if(record == null){
			return null;
		}
		return new UserSettings(record);
	}

	public void setUserSettings(long userId, Map<Field<?>, Object> values){
		values.put(USER_SETTINGS.USER_ID, userId);
		this.modules.get(DatabaseModule.class).getCtx()
			.insertInto(USER_SETTINGS)
			.columns(values.keySet())
			.values(values.values())
			.onConflict(USER_SETTINGS.USER_ID)
			.doUpdate()
			.set(values)
			.where(USER_SETTINGS.USER_ID.eq(userId))
			.execute();
	}

	public <T> void setUserSetting(long userId, Field<T> field, T value){
		this.modules.get(DatabaseModule.class).getCtx()
			.insertInto(USER_SETTINGS)
			.columns(USER_SETTINGS.USER_ID, field)
			.values(userId, value)
			.onConflict(USER_SETTINGS.USER_ID)
			.doUpdate()
			.set(field, value)
			.where(USER_SETTINGS.USER_ID.eq(userId))
			.execute();
	}

}
