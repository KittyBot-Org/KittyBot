package de.anteiku.kittybot.database;

import de.anteiku.kittybot.utils.Logger;

import java.sql.*;

public class SQL {

    private Connection conn;

    public static SQL newInstance(String host, String port, String user, String password) {
        return new SQL(host, port, user, password);
    }

    public SQL(String host, String port, String user, String password) {
        this.conn = init(host, port, user, password);
    }

    private Connection init(String host, String port, String user, String password) {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "?autoReconnect=true", user, password);
        }
        catch (Exception e) {
            Logger.error(e);
        }
        return null;
    }
    
    public void use(String db) {
        execute("CREATE DATABASE IF NOT EXISTS " + db + "");
        execute("USE `" + db + "`");
    }

    public boolean execute(String query) {
        try {
            Statement statement = this.conn.createStatement();
            Logger.debug(query);
            return statement.execute(query);
        } catch (SQLException e) {
            Logger.error(e);
        }
        return false;
    }

    public ResultSet query(String query) {
        try {
            Statement statement = this.conn.createStatement();
            Logger.debug(query);
            return statement.executeQuery(query);
        } catch (SQLException e) {
            Logger.error(e);
        }
        return null;
    }

    public boolean exists(String query) {
        try {
            return query(query).absolute(1);
        } catch (SQLException e) {
            Logger.error(e);
        }
        return false;
    }

    public void close() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }
}
