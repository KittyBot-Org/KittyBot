package de.anteiku.kittybot.database;

import com.zaxxer.hikari.HikariDataSource;
import de.anteiku.kittybot.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class SQL{

	private static final Logger LOG = LoggerFactory.getLogger(SQL.class);

	private static final HikariDataSource dataSource;

	static{
		dataSource = new HikariDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");

		dataSource.setJdbcUrl("jdbc:postgresql://" + Config.DB_HOST + ":" + Config.DB_PORT + "/" + Config.DB_DB);
		dataSource.setUsername(Config.DB_USER);
		dataSource.setPassword(Config.DB_PASSWORD);

		dataSource.setMaximumPoolSize(16);
		dataSource.setConnectionTimeout(3600000);
		dataSource.setIdleTimeout(600000);
		dataSource.setMinimumIdle(4);
		dataSource.setAutoCommit(true);
	}

	public static Connection getConnection() throws NullPointerException{
		try{
			return dataSource.getConnection();
		}
		catch(SQLException e){
			LOG.error("Error while fetching connection from datasource", e);
		}
		throw new NullPointerException("Datasource returned empty connection");
	}

	public static void close(){
		dataSource.close();
	}

	public static void createTable(String table){
		try{
			Scanner scanner = new Scanner(SQL.class.getClassLoader().getResourceAsStream("sql_tables/" + table + ".sql")).useDelimiter("\\A");
			String sql = scanner.hasNext() ? scanner.next() : "";
			LOG.info("Read sql table from resources: {}", table);
			var stmt = getConnection().createStatement();
			stmt.execute(sql);
			stmt.close();
		}
		catch(SQLException e){
			LOG.error("Error while reading sql table file: " + table, e);
		}
	}

	public static PreparedStatement prepStatement(String sql) throws NullPointerException{
		try{
			LOG.debug("prepareStatement sql: {}", sql);
			return getConnection().prepareStatement(sql);
		}
		catch(SQLException e){
			LOG.error("Error preparing statement", e);
		}
		catch(NullPointerException e){
			LOG.error("Error connection is null", e);
		}
		throw new NullPointerException("Error getting preparedStatement");
	}

	public static PreparedStatement prepStatement(String sql, int resultSetType) throws NullPointerException{
		try{
			LOG.debug("prepareStatement sql: {}", sql);
			return getConnection().prepareStatement(sql, resultSetType);
		}
		catch(SQLException e){
			LOG.error("Error preparing statement", e);
		}
		catch(NullPointerException e){
			LOG.error("Error connection is null", e);
		}
		throw new NullPointerException("Error getting preparedStatement");
	}

	public static boolean execute(PreparedStatement preparedStatement) throws SQLException{
		try{
			return preparedStatement.execute();
		}
		catch(SQLException e){
			LOG.error("Error executing prepared statement", e);
		}
		finally{
			preparedStatement.close();
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

	public static ResultSet query(PreparedStatement preparedStatement){
		try{
			return preparedStatement.executeQuery();
		}
		catch(SQLException e){
			LOG.error("Error query prepared statement", e);
		}
		return null;
	}

	public static int update(PreparedStatement preparedStatement) throws SQLException{
		try{
			return preparedStatement.executeUpdate();
		}
		catch(SQLException e){
			LOG.error("Error update prepared statement", e);
		}
		finally{
			preparedStatement.close();
		}
		return -1;
	}

	public static boolean exists(PreparedStatement preparedStatement) throws SQLException{
		try{
			var result = query(preparedStatement);
			return result != null && result.next();
		}
		catch(SQLException e){
			LOG.error("Error exists prepared statement", e);
		}
		finally{
			preparedStatement.close();
		}
		return false;
	}

}
