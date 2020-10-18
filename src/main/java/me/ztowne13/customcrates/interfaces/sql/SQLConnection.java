package me.ztowne13.customcrates.interfaces.sql;

import me.ztowne13.customcrates.utils.ChatUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
    private static int queries = 0;
    private final String host;
    private final String database;
    private final String user;
    private final String pass;
    private final String port;
    private final SQL sql;
    private Connection connection;
    private boolean saidOnce = false;

    public SQLConnection(SQL sql, String host, String dbPort, String database, String user, String pass) {
        this.host = host;
        this.database = database;
        this.user = user;
        this.pass = pass;
        this.sql = sql;
        this.port = dbPort;
    }

    public static int getQueries() {
        return queries;
    }

    public Connection open() {
        return open(true);
    }

    public Connection open(boolean log) {
        try {
            Connection c = connection =
                    DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + port + "/" + this.database + "?autoReconnect=true&useSSL=false", this.user, this.pass);
            if (log)
                ChatUtils.log("[SpecializedCrates] Connection to the SQL database was completed successfuly.");
            return c;
        } catch (SQLException exc) {
            if (!saidOnce) {
                saidOnce = true;
                if (log)
                    ChatUtils.log("[SpecializedCrates] Error connecting to the database, are the values in the config correct?");
            }
            return null;
        }
    }

    public boolean isOpen() {
        try {
            return (connection != null) && (!connection.isClosed());
        } catch (SQLException e) {
            // IGNORED
        }

        return false;
    }

    public Connection get() {
        try {
            if (queries >= 1000) {
                if (connection != null) {
                    ChatUtils.log("[SpecializedCrates] Re-opening connection.");
                    connection.close();
                }
                queries = 0;
                open();
            }

            if (connection == null || connection.isClosed()) {
                open();
            }
        } catch (Exception exc) {
            // IGNORED
        }

        queries++;
        return connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getHost() {
        return host;
    }

    public String getDatabase() {
        return database;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public SQL getSql() {
        return sql;
    }
}
