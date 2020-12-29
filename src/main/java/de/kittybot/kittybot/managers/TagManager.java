package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.GUILD_TAGS;

public class TagManager{

	private static final Logger LOG = LoggerFactory.getLogger(TagManager.class);

	private final KittyBot main;

	public TagManager(KittyBot main){
		this.main = main;
	}

	public void create(String name, String content, long guildId, long userId) throws CommandException{
		name = name.toLowerCase();
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			var rows = dbManager.getCtx(con).insertInto(GUILD_TAGS)
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
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon(); var ctx = dbManager.getCtx(con).selectFrom(GUILD_TAGS)){
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
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon(); var ctx = dbManager.getCtx(con).selectFrom(GUILD_TAGS)){
			var res = ctx
					.where(GUILD_TAGS.USER_ID.eq(userId).and(GUILD_TAGS.GUILD_ID.eq(guildId)))
					.fetch();
			if(res.isEmpty()){
				throw new CommandException("No Tags found");
			}
			return res.parallelStream().map(Tag::new).collect(Collectors.toList());
		}
		catch(SQLException e){
			LOG.error("Error getting tags", e);
			throw new CommandException(e);
		}
	}

	public List<Tag> get(long guildId) throws CommandException{
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon(); var ctx = dbManager.getCtx(con).selectFrom(GUILD_TAGS)){
			var res = ctx
					.where(GUILD_TAGS.GUILD_ID.eq(guildId))
					.fetch();
			if(res.isEmpty()){
				throw new CommandException("No Tags found");
			}
			return res.parallelStream().map(Tag::new).collect(Collectors.toList());
		}
		catch(SQLException e){
			LOG.error("Error getting tags", e);
			throw new CommandException(e);
		}
	}

	public List<Tag> search(String name, long guildId) throws CommandException{
		name = name.toLowerCase();
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon(); var ctx = dbManager.getCtx(con).selectFrom(GUILD_TAGS)){
			var res = ctx
					.where(GUILD_TAGS.GUILD_ID.eq(guildId).and(GUILD_TAGS.NAME.likeRegex(name)))
					.fetch();
			if(res.isEmpty()){
				throw new CommandException("No Tags found");
			}
			return res.parallelStream().map(Tag::new).collect(Collectors.toList());
		}
		catch(SQLException e){
			LOG.error("Error searching tags", e);
			throw new CommandException(e);
		}
	}

	public void delete(String name, long userId, long guildId) throws CommandException{
		name = name.toLowerCase();
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			var rows = dbManager.getCtx(con).deleteFrom(GUILD_TAGS)
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

	public void edit(String name, String content, long userId, long guildId) throws CommandException{
		name = name.toLowerCase();
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			var rows = dbManager.getCtx(con).update(GUILD_TAGS)
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

}
