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

import static de.kittybot.kittybot.jooq.Tables.GUILD_TAGS;

public class TagModule{

	private static final Logger LOG = LoggerFactory.getLogger(TagModule.class);

	private final Modules modules;

	public TagModule(Modules modules){
		this.modules = modules;
	}

	public void create(String name, String content, long guildId, long userId) throws CommandException{
		name = name.toLowerCase();
		var dbModule = this.modules.getDatabaseModule();
		try(var con = dbModule.getCon()){
			var rows = dbModule.getCtx(con).insertInto(GUILD_TAGS)
					.columns(GUILD_TAGS.NAME, GUILD_TAGS.GUILD_ID, GUILD_TAGS.USER_ID, GUILD_TAGS.CONTENT, GUILD_TAGS.CREATED_AT)
					.values(name, guildId, userId, content, LocalDateTime.now())
					.onConflictDoNothing()
					.execute();
			if(rows != 1){
				throw new CommandException("This name is already in use");
			}
		}
		catch(SQLException e){
			LOG.error("Error creating tag", e);
			throw new CommandException(e);
		}
	}

	public Tag get(String name, long guildId) throws CommandException{
		name = name.toLowerCase();
		var dbModule = this.modules.getDatabaseModule();
		try(var con = dbModule.getCon(); var ctx = dbModule.getCtx(con).selectFrom(GUILD_TAGS)){
			var res = ctx
					.where(GUILD_TAGS.NAME.eq(name).and(GUILD_TAGS.GUILD_ID.eq(guildId)))
					.fetchOne();
			if(res == null){
				throw new CommandException("Tag not found");
			}
			return new Tag(res);
		}
		catch(SQLException e){
			LOG.error("Error getting tag", e);
			throw new CommandException(e);
		}
	}

	public List<Tag> get(long userId, long guildId) throws CommandException{
		var dbModule = this.modules.getDatabaseModule();
		try(var con = dbModule.getCon(); var ctx = dbModule.getCtx(con).selectFrom(GUILD_TAGS)){
			var res = ctx
					.where(GUILD_TAGS.USER_ID.eq(userId).and(GUILD_TAGS.GUILD_ID.eq(guildId)))
					.fetch();
			if(res.isEmpty()){
				throw new CommandException("No Tags found");
			}
			return res.stream().map(Tag::new).collect(Collectors.toList());
		}
		catch(SQLException e){
			LOG.error("Error getting tags", e);
			throw new CommandException(e);
		}
	}

	public List<Tag> get(long guildId) throws CommandException{
		var dbModule = this.modules.getDatabaseModule();
		try(var con = dbModule.getCon(); var ctx = dbModule.getCtx(con).selectFrom(GUILD_TAGS)){
			var res = ctx
					.where(GUILD_TAGS.GUILD_ID.eq(guildId))
					.fetch();
			if(res.isEmpty()){
				throw new CommandException("No Tags found");
			}
			return res.stream().map(Tag::new).collect(Collectors.toList());
		}
		catch(SQLException e){
			LOG.error("Error getting tags", e);
			throw new CommandException(e);
		}
	}

	public List<Tag> getAll(long guildId){
		var dbModule = this.modules.getDatabaseModule();
		try(var con = dbModule.getCon(); var ctx = dbModule.getCtx(con).selectFrom(GUILD_TAGS)){
			var res = ctx
					.where(GUILD_TAGS.GUILD_ID.eq(guildId))
					.fetch();
			return res.stream().map(Tag::new).collect(Collectors.toList());
		}
		catch(SQLException e){
			LOG.error("Error getting tags", e);
		}
		return null;
	}

	public List<Tag> search(String name, long guildId) throws CommandException{
		name = name.toLowerCase();
		var dbModule = this.modules.getDatabaseModule();
		try(var con = dbModule.getCon(); var ctx = dbModule.getCtx(con).selectFrom(GUILD_TAGS)){
			var res = ctx
					.where(GUILD_TAGS.GUILD_ID.eq(guildId).and(GUILD_TAGS.NAME.likeRegex(name)))
					.fetch();
			if(res.isEmpty()){
				throw new CommandException("No Tags found");
			}
			return res.stream().map(Tag::new).collect(Collectors.toList());
		}
		catch(SQLException e){
			LOG.error("Error searching tags", e);
			throw new CommandException(e);
		}
	}

	public void delete(String name, long userId, long guildId) throws CommandException{
		name = name.toLowerCase();
		var dbModule = this.modules.getDatabaseModule();
		try(var con = dbModule.getCon()){
			var rows = dbModule.getCtx(con).deleteFrom(GUILD_TAGS)
					.where(GUILD_TAGS.USER_ID.eq(userId).and(GUILD_TAGS.NAME.eq(name).and(GUILD_TAGS.GUILD_ID.eq(guildId))))
					.execute();
			if(rows != 1){
				throw new CommandException("Tag not found or not owned by you");
			}
		}
		catch(SQLException e){
			LOG.error("Error deleting tag", e);
			throw new CommandException(e);
		}
	}

	public boolean delete(long tagId){
		var dbModule = this.modules.getDatabaseModule();
		try(var con = dbModule.getCon()){
			var rows = dbModule.getCtx(con).deleteFrom(GUILD_TAGS)
					.where(GUILD_TAGS.TAG_ID.eq(tagId))
					.execute();
			return rows == 1;
		}
		catch(SQLException e){
			LOG.error("Error deleting tag", e);
		}
		return false;
	}

	public void edit(String name, String content, long userId, long guildId) throws CommandException{
		name = name.toLowerCase();
		var dbModule = this.modules.getDatabaseModule();
		try(var con = dbModule.getCon()){
			var rows = dbModule.getCtx(con).update(GUILD_TAGS)
					.set(GUILD_TAGS.CONTENT, content)
					.where(GUILD_TAGS.USER_ID.eq(userId).and(GUILD_TAGS.NAME.eq(name).and(GUILD_TAGS.GUILD_ID.eq(guildId))))
					.execute();
			if(rows != 1){
				throw new CommandException("Tag not found or not owned by you");
			}
		}
		catch(SQLException e){
			LOG.error("Error editing tag", e);
			throw new CommandException(e);
		}
	}

	public boolean edit(long id, String name, String content, long userId){
		var dbModule = this.modules.getDatabaseModule();
		try(var con = dbModule.getCon()){
			var rows = dbModule.getCtx(con).update(GUILD_TAGS)
					.set(GUILD_TAGS.NAME, name)
					.set(GUILD_TAGS.CONTENT, content)
					.set(GUILD_TAGS.USER_ID, userId)
					.where(GUILD_TAGS.TAG_ID.eq(id))
					.execute();
			return rows == 1;
		}
		catch(SQLException e){
			LOG.error("Error editing tag", e);
		}
		return false;
	}

}
