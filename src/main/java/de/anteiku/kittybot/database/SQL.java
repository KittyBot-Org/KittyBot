package de.anteiku.kittybot.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.IOUtils;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class SQL{
	
	private static final Logger LOG = LoggerFactory.getLogger(SQL.class);
	
	private final Connection conn;
	
	public SQL(String host, String port, String user, String password, String database) throws SQLException{
		this.conn = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + database, user, password);
	}
	
	public static SQL newInstance(String host, String port, String user, String password, String database) throws SQLException{
		return new SQL(host, port, user, password, database);
	}
	
	public void use(String db){
		execute("USE " + db + ";", false);
	}
	
	public boolean setProperty(String table, String row, String value, String checkRow, String checkValue){
		return execute("UPDATE " + table + " SET " + row + "='" + value + "' WHERE " + checkRow + " = '" + checkValue + "';");
	}
	
	public boolean setProperty(String table, String row, int value, String checkRow, String checkValue){
		return execute("UPDATE " + table + " SET " + row + "='" + value + "' WHERE " + checkRow + " = '" + checkValue + "';");
	}
	
	public ResultSet getProperty(String table, String checkRow, String checkValue){
		return query("SELECT * FROM " + table + "  WHERE " + checkRow + " = '" + checkValue + "';");
	}
	
	public String getSingleProperty(String table, String checkRow, String checkValue, String property){
		try{
			ResultSet result = getProperty(table, checkRow, checkValue);
			if(! result.first()){
				return null;
			}
			return result.getString(property);
		}
		catch(SQLException e){
			LOG.error("Error while getting single property", e);
		}
		return null;
	}
	
	public void createTable(String table){
		try{
			String sql = IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("sql_tables/" + table + ".sql")));
			LOG.info("Read sql table from resources: {}", table);
			execute(sql, false);
		}
		catch(IOException e){
			LOG.error("Error while reading sql table file: " + table, e);
		}
	}
	
	public boolean execute(String query){
		return execute(query, true);
	}
	
	public boolean execute(String query, boolean retry){
		try{
			LOG.debug(query);
			return this.conn.createStatement().execute(query);
		}
		catch(SQLException e){
			if(retry)
				return execute(query, false);
			LOG.error("Error while executing sql command", e);
		}
		return false;
	}
	
	public int update(String query){
		return update(query, true);
	}
	
	public int update(String query, boolean retry){
		try{
			LOG.debug(query);
			return this.conn.createStatement().executeUpdate(query);
		}
		catch(SQLException e){
			if(retry)
				return update(query, false);
			LOG.error("Error while executing sql command", e);
		}
		return - 1;
	}
	
	public ResultSet query(String query){
		return query(query, true);
	}
	
	public ResultSet query(String query, boolean retry){
		try{
			Statement statement = this.conn.createStatement();
			LOG.debug(query);
			return statement.executeQuery(query);
		}
		catch(SQLException e){
			if(retry)
				return query(query, false);
			LOG.error("Error while querying sql command", e);
		}
		return null;
	}
	
	public boolean exists(String query){
		return exists(query, true);
	}
	
	public boolean exists(String query, boolean retry){
		try{
			return query(query).next();
		}
		catch(SQLException e){
			if(retry)
				return exists(query, false);
			LOG.error("Error while checking if sql entry exists", e);
		}
		return false;
	}
	
	public void close(){
		try{
			this.conn.close();
		}
		catch(SQLException e){
			LOG.error("Error while closing sql connection", e);
		}
	}
	
}
