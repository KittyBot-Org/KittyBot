package de.anteiku.kittybot.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;

public class SQL {

    private static final Logger LOG = LoggerFactory.getLogger(Database.class);

    private final Connection conn;

    public static SQL newInstance(String host, String port, String user, String password, String database) throws SQLException {
        return new SQL(host, port, user, password, database);
    }

    public SQL(String host, String port, String user, String password, String database) throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
    }
    
    public void use(String db) {
        execute("USE `" + db + "`", false);
    }

    public void createTable(String table){
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        File file = new File(classLoader.getResource("sql_tables/" + table + ".sql").getFile());
        if(file.exists()){
            try {
                execute(new String(Files.readAllBytes(file.toPath())), false);
            }
            catch (IOException e) {
                LOG.error("Error while reading sql table file: " + table, e);
            }
        }
        else{
            LOG.error("Unable to find given sql table file: {}", table);
        }
    }

    public boolean execute(String query) {
        return execute(query, true);
    }

    public boolean execute(String query, boolean retry) {
        try {
            Statement statement = this.conn.createStatement();
            LOG.debug(query);
            return statement.execute(query);
        }
        catch (SQLException e) {
            if(retry) return execute(query, false);
            LOG.error("Error while executing sql command", e);
        }
        return false;
    }

    public ResultSet query(String query) {
        return query(query, true);
    }

    public ResultSet query(String query, boolean retry) {
        try {
            Statement statement = this.conn.createStatement();
            LOG.debug(query);
            return statement.executeQuery(query);
        }
        catch (SQLException e) {
            if(retry) return query(query, false);
            LOG.error("Error while querying sql command", e);
        }
        return null;
    }

    public boolean exists(String query) {
        return exists(query, true);
    }

    public boolean exists(String query, boolean retry) {
        try {
            return query(query).absolute(1);
        }
        catch (SQLException e) {
            if(retry) return exists(query, false);
            LOG.error("Error while checking if sql entry exists", e);
        }
        return false;
    }

    public void close() {
        try {
            this.conn.close();
        }
        catch (SQLException e) {
            LOG.error("Error while closing sql connection", e);
        }
    }
}
