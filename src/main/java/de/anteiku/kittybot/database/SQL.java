package de.anteiku.kittybot.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.anteiku.kittybot.objects.Config;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
			getConnection().createStatement().execute(sql);
		}
		catch(IOException e){
			LOG.error("Error while reading sql table file: " + table, e);
		}
		catch(SQLException e){
			LOG.error("Error while creating new table: " + table, e);
		}
	}

	public static Connection getConnection() throws SQLException{
		return dataSource.getConnection();
	}

	public static boolean execute(PreparedStatement preparedStatement){
		try{
			return preparedStatement.execute();
		}
		catch(SQLException e){
			LOG.error("Error executing prepared statement", e);
		}
		return false;
	}

	public static ResultSet executeWithResult(PreparedStatement preparedStatement){
		try{
			preparedStatement.executeUpdate();
			return preparedStatement.getGeneratedKeys();
		}
		catch(SQLException e){
			LOG.error("Error while executeWithResult prepared statement", e);
		}
		return null;
	}

	public static int update(PreparedStatement preparedStatement){
		try{
			return preparedStatement.executeUpdate();
		}
		catch(SQLException e){
			LOG.error("Error update prepared statement", e);
		}
		return -1;
	}

	public static boolean exists(PreparedStatement preparedStatement){
		try{
			var result = query(preparedStatement);
			return result != null && result.next();
		}
		catch(SQLException e){
			LOG.error("Error exists prepared statement", e);
		}
		return false;
	}

	public static ResultSet query(PreparedStatement preparedStatement){
		try{
			return preparedStatement.executeQuery();
		}
		catch(SQLException e){
			LOG.error("Error query prepared statement", e);
		}
		return null;
	}

}
