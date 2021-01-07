package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.objects.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

public class TagModule{

	private final Modules modules;

	public TagModule(Modules modules){
		this.modules = modules;
	}

	public void create(String name, String content, long guildId, long userId) throws CommandException{
		var dbManager = this.modules.getDatabaseModule();
		var member = dbManager.getCtx().insertInto(MEMBERS).columns(MEMBERS.GUILD_ID, MEMBERS.USER_ID).values(guildId, userId).onConflictDoNothing().returningResult(MEMBERS.ID).fetchOne();
		if(member == null){
			throw new CommandException("WTF is going on welp");
		}
		var rows = dbManager.getCtx().insertInto(GUILD_TAGS)
				.columns(GUILD_TAGS.NAME, GUILD_TAGS.MEMBER_ID, GUILD_TAGS.CONTENT, GUILD_TAGS.CREATED_AT)
				.values(name.toLowerCase(), member.get(MEMBERS.ID), content, LocalDateTime.now())
				.onConflictDoNothing()
				.execute();
		if(rows != 1){
			throw new CommandException("This name is already in use");
		}
	}

	public Tag get(String name, long guildId) throws CommandException{
		try(var ctx = this.modules.getDatabaseModule().getCtx().select()){
			var res = ctx.from(GUILD_TAGS).join(MEMBERS).onKey().where(GUILD_TAGS.NAME.eq(name.toLowerCase()).and(MEMBERS.GUILD_ID.eq(guildId))).fetchOne();
			if(res == null){
				throw new CommandException("Tag not found");
			}
			return new Tag(res);
		}
	}

	public List<Tag> get(long userId, long guildId) throws CommandException{
		try(var ctx = this.modules.getDatabaseModule().getCtx().select()){
			var res = ctx.from(GUILD_TAGS).join(MEMBERS).onKey()
					.where(MEMBERS.USER_ID.eq(userId).and(MEMBERS.GUILD_ID.eq(guildId))).fetch();
			if(res.isEmpty()){
				throw new CommandException("No Tags found");
			}
			return res.stream().map(Tag::new).collect(Collectors.toList());
		}
	}

	public List<Tag> get(long guildId) throws CommandException{
		try(var ctx = this.modules.getDatabaseModule().getCtx().select()){
			var res = ctx.from().join(MEMBERS).onKey()
					.where(MEMBERS.GUILD_ID.eq(guildId)).fetch();
			if(res.isEmpty()){
				throw new CommandException("No Tags found");
			}
			return res.stream().map(Tag::new).collect(Collectors.toList());
		}
	}

	public List<Tag> getAll(long guildId){
		try(var ctx = this.modules.getDatabaseModule().getCtx().select()){
			var res = ctx.from().join(MEMBERS).onKey()
					.where(MEMBERS.GUILD_ID.eq(guildId))
					.fetch();
			return res.stream().map(Tag::new).collect(Collectors.toList());
		}
	}

	public List<Tag> search(String name, long guildId) throws CommandException{
		try(var ctx = this.modules.getDatabaseModule().getCtx().select()){
			var res = ctx.from().join(MEMBERS).onKey()
					.where(MEMBERS.GUILD_ID.eq(guildId).and(GUILD_TAGS.NAME.likeRegex(name.toLowerCase()))).fetch();
			if(res.isEmpty()){
				throw new CommandException("No Tags found");
			}
			return res.stream().map(Tag::new).collect(Collectors.toList());
		}
	}

	public void delete(String name, long userId, long guildId) throws CommandException{
		var rows = this.modules.getDatabaseModule().getCtx().deleteFrom(GUILD_TAGS)
				.where(GUILD_TAGS.MEMBER_ID.eq(select(MEMBERS.ID).where(MEMBERS.GUILD_ID.eq(guildId).and(MEMBERS.USER_ID.eq(userId)))).and(GUILD_TAGS.NAME.eq(name.toLowerCase())))
				.execute();
		if(rows != 1){
			throw new CommandException("Tag not found or not owned by you");
		}
	}

	public boolean delete(long tagId){
		var dbModule = this.modules.getDatabaseModule();
		var rows = dbModule.getCtx().deleteFrom(GUILD_TAGS)
				.where(GUILD_TAGS.ID.eq(tagId))
				.execute();
		return rows == 1;
	}

	public void edit(String name, String content, long userId, long guildId) throws CommandException{
		var rows = this.modules.getDatabaseModule().getCtx().update(GUILD_TAGS)
				.set(GUILD_TAGS.CONTENT, content)
				.where(GUILD_TAGS.MEMBER_ID.eq(select(MEMBERS.ID).where(MEMBERS.GUILD_ID.eq(guildId).and(MEMBERS.USER_ID.eq(userId)))).and(GUILD_TAGS.NAME.eq(name.toLowerCase())))
				.execute();
		if(rows != 1){
			throw new CommandException("Tag not found or not owned by you");
		}
	}

	public boolean edit(long id, String name, String content, long userId){
		var rows = this.modules.getDatabaseModule().getCtx().update(GUILD_TAGS)
				.set(GUILD_TAGS.NAME, name)
				.set(GUILD_TAGS.CONTENT, content)
				.set(GUILD_TAGS.MEMBER_ID, select(MEMBERS.ID).where(MEMBERS.USER_ID.eq(userId)))
				.where(GUILD_TAGS.ID.eq(id))
				.execute();
		return rows == 1;
	}

}
