package me.ztowne13.customcrates.interfaces.sql;

import me.ztowne13.customcrates.SpecializedCrates;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQL {
    private final SQLConnection connection;
    private final SpecializedCrates instance;
    private int attempts = 0;

    public SQL(SpecializedCrates instance, String databaseIP, String database, String port, String username, String password) {
        this.instance = instance;
        connection = new SQLConnection(this, databaseIP, port, database, username, password);
    }

    public SpecializedCrates getInstance() {
        return instance;
    }

    public Object get(String table, String where, String whereResult,
                      String sec) {
        instance.getDebugUtils().log("get() - CALL : Object", getClass(), false);
        if (connection.isOpen()) {
            try {
                attempts = 0;
                PreparedStatement sql = getConnection().get().prepareStatement(
                        "SELECT * FROM `" + table + "` WHERE " + where + "='"
                                + whereResult + "';");
                ResultSet result = sql.executeQuery();
                result.next();
                return result.getObject(sec).toString();
            } catch (Exception exc) {
                instance.getDebugUtils().log("get() - Exception handling request", getClass());
//                exc.printStackTrace();
//                new SQLLog("Unsuccessfuly retrieved something from database");
                return null;
            }

        } else {
            attempts++;
            if (attempts <= 10) {
                connection.open();
                return get(table, where, whereResult, sec);
            } else {
                instance.getDebugUtils().log("get() - Attempt limit reached");
//                new SQLLog("ERROR: SQL CONNECTION ATTEMPT LIMIT REACHED");
            }
        }
        return null;
    }

    public ResultSet get(String table, String where, String whereResult) {
        instance.getDebugUtils().log("get() - CALL : ResultSet", getClass());

        if (connection.isOpen()) {
            try {
                attempts = 0;
                PreparedStatement sql = getConnection().get().prepareStatement(
                        "SELECT * FROM `" + table + "` WHERE " + where + "='"
                                + whereResult + "';");
                ResultSet result = sql.executeQuery();
                result.next();
                return result;
            } catch (Exception exc) {
                instance.getDebugUtils().log("get() - Exception handling request", getClass());
//                exc.printStackTrace();
//                new SQLLog("Unsuccessfuly retrieved something from database");
                return null;
            }

        } else {
            attempts++;
            if (attempts <= 10) {
                connection.open();
                return get(table, where, whereResult);
            } else {
                instance.getDebugUtils().log("get() - Attempt limit reached");
//                new SQLLog("ERROR: SQL CONNECTION ATTEMPT LIMIT REACHED");
            }
        }
        return null;
    }

    public void write(final String table, final String set_where, final String where_value, final String set_path, final String set_value) {
        instance.getDebugUtils().log("write() - CALL", getClass(), true);

        if (connection.isOpen()) {
            String pat = "";
            if (!isInt(set_value)) {
                pat = "'" + set_value + "'";
            } else {
                pat = set_value;
            }
            String query = "UPDATE `" + table + "` SET " + set_path + "=" + pat
                    + " WHERE " + set_where + "='" + where_value + "';";
            SQLQueryThread.addQuery(query);
        } else {
            attempts++;
            if (attempts <= 10) {
                connection.open();
                write(table, set_where, where_value, set_path, set_value);
            } else {
                instance.getDebugUtils().log("get() - Attempt limit reached");
//                new SQLLog("ERROR: SQL CONNECTION ATTEMPT LIMIT REACHED");
            }
        }
    }

    public void create(String table, String format, boolean uniqueUuid) {
        instance.getDebugUtils().log("create() - CALL", getClass());

        //format (name format(len), name int)
        if (connection.isOpen()) {
            try {
                instance.getDebugUtils().log("create() - Creating table...", getClass());
                long curTime = System.currentTimeMillis();

                String createQuery = "CREATE TABLE IF NOT EXISTS " + table + " (" + format + ")";
                connection.get().prepareStatement(createQuery).executeUpdate();

                instance.getDebugUtils().log("create() - Finished creating table in " + (System.currentTimeMillis() - curTime) + "ms.", getClass());
                if (uniqueUuid) {
                    instance.getDebugUtils().log("create() - Updating unique uuid...", getClass());

                    try {
                        String uniqueQuery = "ALTER TABLE " + table + " ADD CONSTRAINT UQ_UUID UNIQUE (uuid)";
                        connection.get().prepareStatement(uniqueQuery).executeUpdate();
                    } catch (Exception exc) {
                        // IGNORED
                    }

                    instance.getDebugUtils().log("create() - Finished updating unique id in " + (System.currentTimeMillis() - curTime) +
                            "ms.", getClass());
                }
            } catch (Exception exc) {
                exc.printStackTrace();
                instance.getDebugUtils().log("create() - FAILED TO CREATE TABLE!", getClass());
            }
        } else {
            instance.getDebugUtils().log("create() - ISSUE: SQLConnection is not open.", getClass());
            attempts++;
            if (attempts <= 10) {
                connection.open();
                create(table, format, uniqueUuid);
            } else {
                instance.getDebugUtils().log("get() - Attempt limit reached");
//                new SQLLog("ERROR: SQL CONNECTION ATTEMPT LIMIT REACHED");
            }
        }
    }

    /**
     * @param table  the table to insert into
     * @param toSet  formatted value1='val1', value2='val2'
     * @param ignore whether or not to ignore duplicates
     */
    public void insert(final String table, final String toSet, final boolean ignore) {
        if (connection.isOpen()) {
            try {
                instance.getDebugUtils().log("insert() - CALL", getClass());

                String query = "INSERT " + (ignore ? "IGNORE" : "") + " INTO " + table + " SET " + toSet;
                connection.get().prepareStatement(query).executeUpdate();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        } else {
            attempts++;
            if (attempts <= 10) {
                connection.open();
                insert(table, toSet, ignore);
            } else {
                instance.getDebugUtils().log("insert() - Attempt limit reached");
//                new SQLLog("ERROR: SQL CONNECTION ATTEMPT LIMIT REACHED");
            }
        }
    }

    public void replace(final String table, final String values, final String toSetValues) {
        if (connection.isOpen()) {
            try {
                instance.getDebugUtils().log("replace() - CALL", getClass());

                String query = "REPLACE INTO " + table + "(" + values + ") VALUES(" + toSetValues + ")";
                connection.get().prepareStatement(query).executeUpdate();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        } else {
            attempts++;
            if (attempts <= 10) {
                connection.open();
                replace(table, values, toSetValues);
            } else {
                instance.getDebugUtils().log("replace() - Attempt limit reached");
//                new SQLLog("ERROR: SQL CONNECTION ATTEMPT LIMIT REACHED");
            }
        }
    }

    public boolean isInt(String s) {
        try {
            int i = Integer.parseInt(s);
            return true;
        } catch (Exception exc) {
        }
        return false;
    }

    public SQLConnection getConnection() {
        return connection;
    }

    public int getAttempts() {
        return attempts;
    }
}
