package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.objects.settings.Tag;
import net.dv8tion.jda.api.utils.data.DataObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.GUILD_TAGS;

public class TagsModule extends Module{

	public boolean create(String name, String content, long guildId, long userId){
		var dbManager = this.modules.get(DatabaseModule.class);
		var rows = dbManager.getCtx().insertInto(GUILD_TAGS)
			.columns(GUILD_TAGS.NAME, GUILD_TAGS.GUILD_ID, GUILD_TAGS.USER_ID, GUILD_TAGS.CONTENT, GUILD_TAGS.CREATED_AT)
			.values(name.toLowerCase(), guildId, userId, content, LocalDateTime.now())
			.onConflictDoNothing()
			.execute();
		return rows == 1;
	}

	public boolean canPublishTag(long guildId){
		return this.modules.get(CommandsModule.class).getGuildCommands(guildId).length() < 100;
	}

	@Nullable
	public Tag getPublishedTagById(long commandId){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(GUILD_TAGS)){
			var res = ctx.where(GUILD_TAGS.COMMAND_ID.eq(commandId)).fetchOne();
			if(res == null){
				return null;
			}
			return new Tag(res);
		}
	}

	public boolean publishTag(String name, String description, long guildId){
		var commandId = this.modules.get(CommandsModule.class).registerGuildCommand(guildId, DataObject.empty()
			.put("name", name)
			.put("description", description)
		);
		if(commandId == -1L){
			return false;
		}
		return setCommandIdByName(name, commandId, guildId);
	}

	public boolean setCommandIdByName(String name, long commandId, long guildId){
		var rows = this.modules.get(DatabaseModule.class).getCtx().update(GUILD_TAGS)
			.set(GUILD_TAGS.COMMAND_ID, commandId)
			.where(GUILD_TAGS.GUILD_ID.eq(guildId).and(GUILD_TAGS.NAME.equalIgnoreCase(name)))
			.execute();
		return rows == 1;
	}

	public boolean removePublishedTag(long guildId, long commandId){
		var res = this.modules.get(CommandsModule.class).deleteGuildCommand(guildId, commandId);
		return setCommandIdByCommandId(commandId, -1L) && res;
	}

	public boolean setCommandIdByCommandId(long commandId, long newCommandId){
		var rows = this.modules.get(DatabaseModule.class).getCtx().update(GUILD_TAGS)
			.set(GUILD_TAGS.COMMAND_ID, newCommandId)
			.where(GUILD_TAGS.COMMAND_ID.eq(commandId))
			.execute();
		return rows == 1;
	}

	@Nullable
	public Tag get(String name, long guildId){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(GUILD_TAGS)){
			var res = ctx.where(GUILD_TAGS.NAME.equalIgnoreCase(name).and(GUILD_TAGS.GUILD_ID.eq(guildId))).fetchOne();
			if(res == null){
				return null;
			}
			return new Tag(res);
		}
	}

	public List<Tag> get(long guildId, long userId){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(GUILD_TAGS)){
			var res = ctx.where(GUILD_TAGS.GUILD_ID.eq(guildId).and(GUILD_TAGS.USER_ID.eq(userId))).fetch();
			return res.stream().map(Tag::new).collect(Collectors.toList());
		}
	}

	public List<Tag> get(long guildId){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(GUILD_TAGS)){
			var res = ctx.where(GUILD_TAGS.GUILD_ID.eq(guildId)).fetch();
			return res.stream().map(Tag::new).collect(Collectors.toList());
		}
	}

	@Nonnull
	public List<Tag> search(String name, long guildId){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(GUILD_TAGS)){
			var res = ctx.where(GUILD_TAGS.GUILD_ID.eq(guildId).and(GUILD_TAGS.NAME.like("%" + name.toLowerCase() + "%"))).fetch();
			return res.stream().map(Tag::new).collect(Collectors.toList());
		}
	}

	@Nonnull
	public List<Tag> search(String name, long guildId, long userId){
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(GUILD_TAGS)){
			var res = ctx.where(GUILD_TAGS.GUILD_ID.eq(guildId).and(GUILD_TAGS.USER_ID.eq(userId).and(GUILD_TAGS.NAME.likeIgnoreCase("%" + name + "%")))).fetch();
			return res.stream().map(Tag::new).collect(Collectors.toList());
		}
	}

	public boolean delete(String name, long guildId, long userId){
		var rows = this.modules.get(DatabaseModule.class).getCtx().deleteFrom(GUILD_TAGS)
			.where(GUILD_TAGS.NAME.equalIgnoreCase(name).and(GUILD_TAGS.GUILD_ID.eq(guildId).and(GUILD_TAGS.USER_ID.eq(userId))))
			.execute();
		return rows == 1;
	}

	public boolean delete(String name, long guildId){
		var rows = this.modules.get(DatabaseModule.class).getCtx().deleteFrom(GUILD_TAGS)
			.where(GUILD_TAGS.NAME.equalIgnoreCase(name).and(GUILD_TAGS.GUILD_ID.eq(guildId)))
			.execute();
		return rows == 1;
	}

	public boolean delete(long tagId){
		var rows = this.modules.get(DatabaseModule.class).getCtx().deleteFrom(GUILD_TAGS)
			.where(GUILD_TAGS.ID.eq(tagId))
			.execute();
		return rows == 1;
	}

	public boolean edit(String name, String content, long guildId, long userId, String newName){
		var values = new HashMap<>();
		if(content != null){
			values.put(GUILD_TAGS.CONTENT, content);
		}
		if(newName != null){
			values.put(GUILD_TAGS.NAME, newName);
		}
		var record = this.modules.get(DatabaseModule.class).getCtx().update(GUILD_TAGS)
			.set(values)
			.where(GUILD_TAGS.NAME.equalIgnoreCase(name).and(GUILD_TAGS.GUILD_ID.eq(guildId).and(GUILD_TAGS.USER_ID.eq(userId))))
			.returning()
			.fetchOne();
		if(record == null){
			return false;
		}
		if(record.getCommandId() != -1 && newName != null){
			this.modules.get(CommandsModule.class).editGuildCommand(guildId, record.getCommandId(), DataObject.empty().put("name", newName));
		}
		return true;
	}

	public boolean edit(long id, String name, String content, long userId){
		var rows = this.modules.get(DatabaseModule.class).getCtx().update(GUILD_TAGS)
			.set(GUILD_TAGS.NAME, name.toLowerCase())
			.set(GUILD_TAGS.CONTENT, content)
			.set(GUILD_TAGS.USER_ID, userId)
			.where(GUILD_TAGS.ID.eq(id))
			.execute();
		return rows == 1;
	}

}
