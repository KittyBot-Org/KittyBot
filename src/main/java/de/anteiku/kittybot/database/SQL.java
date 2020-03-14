package de.anteiku.kittybot.database;

import de.anteiku.kittybot.utils.Logger;

import java.sql.*;

public class SQL {

    public static final String VARCHAR = "VARCHAR";
    public static final String BOOLEAN = "TINYINT";

    public static final String NOT_NULL = "NO NULL";
    public static final String PRIMARY_KEY = "PRIMARY KEY";

    private Connection conn;

    public static SQL newInstance(String host, String port, String user, String password,String db) {
        return new SQL(host, port, user, password, db);
    }

    public SQL(String host, String port, String user, String password,String db) {
        this.conn = init(host, port, user, password, db);
    }

    private Connection init(String host, String port, String user, String password,String db) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db + "", user, password);
        }
        catch (Exception e) {
            Logger.error(e);
        }
        return null;
    }

    public boolean execute(String query) {
        try {
            Statement statement = this.conn.createStatement();
            return statement.execute(query);
        } catch (SQLException e) {
            Logger.error(e);
        }
        return false;
    }

    public ResultSet query(String query) {
        try {
            Statement statement = this.conn.createStatement();
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
