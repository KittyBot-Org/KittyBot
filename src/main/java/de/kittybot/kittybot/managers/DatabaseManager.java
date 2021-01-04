package de.kittybot.kittybot.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.kittybot.kittybot.utils.Config;
import org.apache.commons.io.IOUtils;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager{

	private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);

	private final HikariDataSource dataSource;

	public DatabaseManager(){
		this.dataSource = initDataSource();
		initTable("guilds");

		initTable("guild_invites");
		initTable("guild_invite_roles");

		initTable("self_assignable_role_groups");
		initTable("self_assignable_roles");

		initTable("bot_disabled_channels");
		initTable("snipe_disabled_channels");
		initTable("bot_ignored_users");

		initTable("stream_users");
		initTable("stream_user_events");

		initTable("reactive_messages");

		initTable("notifications");
		initTable("user_statistics");

		initTable("requests");

		initTable("sessions");
	}

	private HikariDataSource initDataSource(){
		if(Config.DB_HOST.isBlank() || Config.DB_PORT.isBlank() || Config.DB_DATABASE.isBlank() || Config.DB_USER.isBlank() || Config.DB_PASSWORD.isBlank()){
			LOG.error("Please check your db host/port/database/user/password");
			return null;
		}
		var config = new HikariConfig();
		config.setDriverClassName("org.postgresql.Driver");
		config.setJdbcUrl("jdbc:postgresql://" + Config.DB_HOST + ":" + Config.DB_PORT + "/" + Config.DB_DATABASE);
		config.setUsername(Config.DB_USER);
		config.setPassword(Config.DB_PASSWORD);

		config.setMinimumIdle(80);
		config.setMaximumPoolSize(90);
		config.setConnectionTimeout(10000);
		config.setIdleTimeout(600000);
		config.setMaxLifetime(1800000);

		return new HikariDataSource(config);
	}

	private void initTable(String table){
		try(var con = getCon()){
			var file = DatabaseManager.class.getClassLoader().getResourceAsStream("sql_tables/" + table + ".sql");
			if(file == null){
				throw new NullPointerException("File for table '" + table + "' not found");
			}
			getCtx(con).query(IOUtils.toString(file, StandardCharsets.UTF_8.name())).execute();
		}
		catch(SQLException | IOException | NullPointerException e){
			LOG.error("Error initializing table: '" + table + "'", e);
		}
	}

	public Connection getCon() throws SQLException{
		return this.dataSource.getConnection();
	}

	public DSLContext getCtx(Connection con){
		return DSL.using(con, SQLDialect.POSTGRES);
	}

}
