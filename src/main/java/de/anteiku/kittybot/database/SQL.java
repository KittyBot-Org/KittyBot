package de.anteiku.kittybot.database;

import de.anteiku.kittybot.utils.Logger;

import java.sql.*;

public class SQL {

    private Connection conn;

    public static SQL newInstance(String host, String port, String user, String password, String database) throws SQLException {
        return new SQL(host, port, user, password, database);
    }

    public SQL(String host, String port, String user, String password, String database) throws SQLException {
        this.conn = init(host, port, user, password, database);
    }

    private Connection init(String host, String port, String user, String password, String database) throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
    }
    
    public void use(String db) {
        execute("USE `" + db + "`", false);
    }

    public boolean execute(String query) {
        return execute(query, true);
    }

    public boolean execute(String query, boolean retry) {
        try {
            Statement statement = this.conn.createStatement();
            Logger.debug(query);
            return statement.execute(query);
        }
        catch (SQLException e) {
            if(retry) return execute(query, false);
            Logger.error(e);
        }
        return false;
    }

    public ResultSet query(String query) {
        return query(query, true);
    }

    public ResultSet query(String query, boolean retry) {
        try {
            Statement statement = this.conn.createStatement();
            Logger.debug(query);
            return statement.executeQuery(query);
        } catch (SQLException e) {
            if(retry) return query(query, false);
            Logger.error(e);
        }
        return null;
    }

    public boolean exists(String query) {
        return exists(query, true);
    }

    public boolean exists(String query, boolean retry) {
        try {
            return query(query).absolute(1);
        } catch (SQLException e) {
            if(retry) return exists(query, false);
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
