package me.ztowne13.customcrates.interfaces.sql;

import me.ztowne13.customcrates.SpecializedCrates;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQL
{
    SpecializedCrates sc;

    public SQLConnection sqlc;

    int attempts = 0;

    public SQL(SpecializedCrates sc, String databaseIP, String database, String username, String password)
    {
        this.sc = sc;
        sqlc = new SQLConnection(this, databaseIP, database, username, password);
    }

    public Object get(String table, String where, String whereResult,
                      String sec)
    {
        sc.getDu().log("get() - CALL : Object", getClass(), false);
        if (sqlc.isOpen())
        {
            try
            {
                attempts = 0;
                PreparedStatement sql = getSqlc().get().prepareStatement(
                        "SELECT * FROM `" + table + "` WHERE " + where + "='"
                                + whereResult + "';");
                ResultSet result = sql.executeQuery();
                result.next();
                ResultSet set = result;
                return set.getObject(sec).toString();
            }
            catch (Exception exc)
            {
                sc.getDu().log("get() - Exception handling request", getClass());
//                exc.printStackTrace();
//                new SQLLog("Unsuccessfuly retrieved something from database");
                return null;
            }

        }
        else
        {
            attempts++;
            if (attempts <= 10)
            {
                Bukkit.getScheduler().scheduleSyncDelayedTask(sc, new Runnable()
                {

                    @Override
                    public void run()
                    {
                        sqlc.open();
                    }

                }, 40);
            }
            else
            {
                sc.getDu().log("get() - Attempt limit reached");
//                new SQLLog("ERROR: SQL CONNECTION ATTEMPT LIMIT REACHED");
            }
        }
        return get(table, where, whereResult, sec);
    }

    public ResultSet get(String table, String where, String whereResult)
    {
        sc.getDu().log("get() - CALL : ResultSet", getClass());

        if (sqlc.isOpen())
        {
            try
            {
                attempts = 0;
                PreparedStatement sql = getSqlc().get().prepareStatement(
                        "SELECT * FROM `" + table + "` WHERE " + where + "='"
                                + whereResult + "';");
                ResultSet result = sql.executeQuery();
                result.next();
                return result;
            }
            catch (Exception exc)
            {
                sc.getDu().log("get() - Exception handling request", getClass());
//                exc.printStackTrace();
//                new SQLLog("Unsuccessfuly retrieved something from database");
                return null;
            }

        }
        else
        {
            attempts++;
            if (attempts <= 10)
            {
                Bukkit.getScheduler().scheduleSyncDelayedTask(sc, new Runnable()
                {

                    @Override
                    public void run()
                    {
                        sqlc.open();
                    }

                }, 40);
            }
            else
            {
                sc.getDu().log("get() - Attempt limit reached");
//                new SQLLog("ERROR: SQL CONNECTION ATTEMPT LIMIT REACHED");
            }
        }
        return get(table, where, whereResult);
    }

    public void write(final String table, final String set_where, final String where_value, final String set_path, final String set_value)
    {
        sc.getDu().log("write() - CALL", getClass(), true);

        if (sqlc.isOpen())
        {
            String pat = "";
            if (!isInt(set_value))
            {
                pat = "'" + set_value + "'";
            }
            else
            {
                pat = set_value;
            }
            String query = "UPDATE `" + table + "` SET " + set_path + "=" + pat
                    + " WHERE " + set_where + "='" + where_value + "';";
            SQLQueryThread.addQuery(query);
        }
        else
        {
            attempts++;
            if (attempts <= 10)
            {
                Bukkit.getScheduler().scheduleSyncDelayedTask(sc, new Runnable()
                {

                    @Override
                    public void run()
                    {
                        sqlc.open();
                        write(table, set_where, where_value, set_path, set_value);
                    }

                }, 40);
            }
            else
            {
                sc.getDu().log("get() - Attempt limit reached");
//                new SQLLog("ERROR: SQL CONNECTION ATTEMPT LIMIT REACHED");
            }
        }
    }

    public void create(String table, String format, boolean uniqueUuid)
    {
        sc.getDu().log("create() - CALL", getClass());

        //format (name format(len), name int)
        if (sqlc.isOpen())
        {
            try
            {
                sc.getDu().log("create() - Creating table...", getClass());
                long curTime = System.currentTimeMillis();

                String createQuery = "CREATE TABLE IF NOT EXISTS " + table + " (" + format + ")";
                sqlc.get().prepareStatement(createQuery).executeUpdate();

                sc.getDu().log("create() - Finished creating table in " + (System.currentTimeMillis() - curTime) + "ms.", getClass());
                if (uniqueUuid)
                {
                    sc.getDu().log("create() - Updating unique uuid...", getClass());
                    long curTimeUuid = System.currentTimeMillis();

                    try
                    {
                        String uniqueQuery = "ALTER TABLE " + table + " ADD CONSTRAINT UQ_UUID UNIQUE (uuid)";
                        sqlc.get().prepareStatement(uniqueQuery).executeUpdate();
                    }
                    catch(Exception exc)
                    {

                    }

                    sc.getDu().log("create() - Finished updating unique id in " + (System.currentTimeMillis() - curTime) +
                            "ms.", getClass());
                }
            }
            catch (Exception exc)
            {
                exc.printStackTrace();
                sc.getDu().log("create() - FAILED TO CREATE TABLE!", getClass());
            }
        }
        else
        {
            sc.getDu().log("create() - ISSUE: SQLConnection is not open.", getClass());
        }
    }

    /**
     * @param table  the table to insert into
     * @param toSet  formatted value1='val1', value2='val2'
     * @param ignore whether or not to ignore duplicates
     */
    public void insert(final String table, final String toSet, final boolean ignore)
    {
        if (sqlc.isOpen())
        {
            try
            {
                sc.getDu().log("insert() - CALL", getClass());

                String query = "INSERT " + (ignore ? "IGNORE" : "") + " INTO " + table + " SET " + toSet;
                sqlc.get().prepareStatement(query).executeUpdate();
            }
            catch (Exception exc)
            {
                exc.printStackTrace();
            }
        }
        else
        {
            attempts++;
            if (attempts <= 10)
            {
                Bukkit.getScheduler().scheduleSyncDelayedTask(sc, new Runnable()
                {

                    @Override
                    public void run()
                    {
                        sqlc.open();
                        insert(table, toSet, ignore);
                    }

                }, 40);
            }
            else
            {
                sc.getDu().log("get() - Attempt limit reached");
//                new SQLLog("ERROR: SQL CONNECTION ATTEMPT LIMIT REACHED");
            }
        }
    }

    public boolean isInt(String s)
    {
        try
        {
            int i = Integer.parseInt(s);
            return true;
        }
        catch (Exception exc)
        {
        }
        return false;
    }

    public SQLConnection getSqlc()
    {
        return sqlc;
    }

    public int getAttempts()
    {
        return attempts;
    }
}
