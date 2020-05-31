package me.ztowne13.customcrates.interfaces.sql;

import me.ztowne13.customcrates.utils.ChatUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection
{
    Connection connection;
    String dbIP, db, user, pass, port;
    SQL sql;

    static int queries = 0;

    boolean saidOnce = false;

    public SQLConnection(SQL sql, String dbIP, String dbPort, String db, String user, String pass)
    {
        this.dbIP = dbIP;
        this.db = db;
        this.user = user;
        this.pass = pass;
        this.sql = sql;
        this.port = dbPort;
    }

    public Connection open()
    {
        return open(true);
    }

    @SuppressWarnings("deprecation")
    public Connection open(boolean log)
    {
        try
        {
            Connection c = connection =
                    DriverManager.getConnection("jdbc:mysql://" + this.dbIP + ":" + port + "/" + this.db + "?autoReconnect=true&useSSL=false", this.user, this.pass);
            if(log)
                ChatUtils.log("[SpecializedCrates] Connection to the SQL database was completed successfuly.");
            return c;
        }
        catch (SQLException exc)
        {
            if(!saidOnce)
            {
                saidOnce = true;
                if(log)
                    ChatUtils.log("[SpecializedCrates] Error connecting to the database, are the values in the config correct?");
            }
            return null;
        }
    }

    public boolean isOpen()
    {
        try
        {
            return (connection != null) && (!connection.isClosed());
        }
        catch (SQLException e) {}

        return false;
    }

    public Connection get()
    {
        try
        {
            if (queries >= 1000)
            {
                if (connection != null)
                {
                    ChatUtils.log("[SpecializedCrates] Re-opening connection.");
                    connection.close();
                }
                queries = 0;
                open();
            }

            if (connection == null || connection.isClosed())
            {
                open();
            }
        }
        catch (Exception exc)
        {

        }

        queries++;
        return connection;
    }

    public Connection getConnection()
    {
        return connection;
    }

    public String getDbIP()
    {
        return dbIP;
    }

    public String getDb()
    {
        return db;
    }

    public String getUser()
    {
        return user;
    }

    public String getPass()
    {
        return pass;
    }

    public SQL getSql()
    {
        return sql;
    }

    public static int getQueries()
    {
        return queries;
    }
}
