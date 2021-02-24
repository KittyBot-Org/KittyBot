package de.kittybot.kittybot.modules;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.utils.Config;
import org.apache.commons.io.IOUtils;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DatabaseModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(DatabaseModule.class);

	private Configuration configuration;

	@Override
	public void onEnable(){
		initConfiguration();
		initTable("guilds",
			"member_roles",
			"guild_tags",
			"voters",
			"guild_invites",
			"guild_invite_roles",
			"self_assignable_role_groups",
			"self_assignable_roles",
			"bot_disabled_channels",
			"snipe_disabled_channels",
			"bot_ignored_members",
			"stream_users",
			"stream_user_events",
			"reactive_messages",
			"self_assignable_role_messages",
			"notifications",
			"user_statistics",
			"requests",
			"sessions",
			"user_settings"
		);
	}

	private void initConfiguration(){
		if(Config.DB_HOST.isBlank() || Config.DB_PORT.isBlank() || Config.DB_DATABASE.isBlank() || Config.DB_USER.isBlank() || Config.DB_PASSWORD.isBlank()){
			LOG.error("Please check your db host/port/database/user/password");
			return;
		}
		var config = new HikariConfig();
		config.setDriverClassName("org.postgresql.Driver");
		config.setJdbcUrl("jdbc:postgresql://" + Config.DB_HOST + ":" + Config.DB_PORT + "/" + Config.DB_DATABASE);
		config.setUsername(Config.DB_USER);
		config.setPassword(Config.DB_PASSWORD);

		config.setMinimumIdle(2);
		config.setMaximumPoolSize(20);
		config.setConnectionTimeout(10000);
		config.setIdleTimeout(600000);
		config.setMaxLifetime(1800000);

		var defaultConfiguration = new DefaultConfiguration();
		defaultConfiguration.setSettings(new Settings().withReturnAllOnUpdatableRecord(true));
		defaultConfiguration.setDataSource(new HikariDataSource(config));
		defaultConfiguration.setSQLDialect(SQLDialect.POSTGRES);
		this.configuration = defaultConfiguration;
	}

	private void initTable(String... tables){

		for(var table : tables){
			try{
				var file = DatabaseModule.class.getClassLoader().getResourceAsStream("sql_tables/" + table + ".sql");
				if(file == null){
					throw new NullPointerException("File for table '" + table + "' not found");
				}
				getCtx().query(IOUtils.toString(file, StandardCharsets.UTF_8)).execute();
			}
			catch(IOException | NullPointerException e){
				LOG.error("Error initializing table: '{}", table, e);
			}
		}
	}

	public DSLContext getCtx(){
		return DSL.using(this.configuration);
	}

	public Configuration getConfiguration(){
		return this.configuration;
	}

}
