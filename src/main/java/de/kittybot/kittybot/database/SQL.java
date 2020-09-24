package de.kittybot.kittybot.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.kittybot.kittybot.objects.Config;
import org.apache.commons.io.IOUtils;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static de.kittybot.kittybot.database.jooq.Tables.GUILDS;

public class SQL{

	private static final Logger LOG = LoggerFactory.getLogger(SQL.class);

	private static final HikariDataSource dataSource;

	static{
		var config = new HikariConfig();
		config.setDriverClassName("org.postgresql.Driver");
		config.setJdbcUrl("jdbc:postgresql://" + Config.DB_HOST + ":" + Config.DB_PORT + "/" + Config.DB_DB);
		config.setUsername(Config.DB_USER);
		config.setPassword(Config.DB_PASSWORD);

		config.setMinimumIdle(80);
		config.setMaximumPoolSize(90);
		config.setConnectionTimeout(10000);
		config.setIdleTimeout(600000);
		config.setMaxLifetime(1800000);

		dataSource = new HikariDataSource(config);
	}

	public static void close(){
		dataSource.close();
	}

	public static void createTable(String table){
		try{
			String sql = IOUtils.toString(SQL.class.getClassLoader().getResourceAsStream("sql_tables/" + table + ".sql"), StandardCharsets.UTF_8.name());
			LOG.info("Read sql table from resources: {}", table);
			dataSource.getConnection().createStatement().execute(sql);
		}
		catch(IOException e){
			LOG.error("Error while reading sql table file: " + table, e);
		}
		catch(SQLException e){
			LOG.error("Error while creating new table: " + table, e);
		}
	}

	public static <T> T getProperty(String guildId, Field<T> field){
		try{
			var ctx = getCtx();
			var res = ctx.select(field).from(GUILDS).where(GUILDS.GUILD_ID.eq(guildId)).fetchOne();
			if(res != null){
				return res.getValue(field);
			}
		}
		catch(SQLException e){
			LOG.error("Error while getting key " + field.getName() + " from guild " + guildId, e);
		}
		return null;
	}

	public static DSLContext getCtx() throws SQLException{
		try(var con = dataSource.getConnection()){
			return DSL.using(con, SQLDialect.POSTGRES);
		}
	}

	public static <T> void setProperty(String guildId, Field<T> field, T value){
		try{
			var ctx = getCtx();
			ctx.update(GUILDS).set(field, value).where(GUILDS.GUILD_ID.eq(guildId)).executeAsync();
		}
		catch(SQLException e){
			LOG.error("Error while getting key " + field.getName() + " from guild " + guildId, e);
		}
	}

}
