package me.ztowne13.customcrates.players.data;

import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.interfaces.sql.SQL;
import me.ztowne13.customcrates.interfaces.sql.SQLQueryThread;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class SQLDataHandler extends DataHandler {
    public static final String TABLE = "scPlayerStats";
    private static SQL sql;
    private static boolean loaded = false;

    public SQLDataHandler(PlayerManager playerManager) {
        super(playerManager);
        instance.getDebugUtils().log("SQLDataHandler() - CALL", getClass());
        load();
    }

    public static SQL getSql() {
        return sql;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    @Override
    public boolean load() {
        if (!loaded) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(instance, new Runnable() {
                @Override
                public void run() {
                    instance.getDebugUtils().log("load() - CALL (Note: This is run synchronous not asynchronous)", getClass());
                    long curTime = System.currentTimeMillis();

                    FileHandler sqlYml = instance.getSqlFile();
                    FileConfiguration fc = sqlYml.get();

                    String dbName = fc.getString("database.name");
                    String dbIp = fc.getString("database.ip");
                    String dbPort = fc.getString("database.port");
                    String dbUsername = fc.getString("database.username");
                    String dbPassword = fc.getString("database.password");

                    instance.getDebugUtils().log("load() - Opening connection and creating query thread.", getClass());
                    sql = new SQL(instance, dbIp, dbName, dbPort, dbUsername, dbPassword);
                    new SQLQueryThread(sql);

                    instance.getDebugUtils().log("load() - Completed creating thread and opening connection in " +
                            (System.currentTimeMillis() - curTime) + "ms.", getClass());

                    instance.getDebugUtils().log("load() - Creating table & setting unique if it doesn't exist...", getClass());
                    long createTime = System.currentTimeMillis();

                    sql.create(TABLE,
                            "uuid varchar(36), history longtext, crateCooldowns mediumtext, virtualCrates mediumtext, rewardLimits mediumtext",
                            true);

                    instance.getDebugUtils().log("load() - Completed creating table & setting unique in " +
                                    (System.currentTimeMillis() - createTime) + "ms.",
                            getClass());
                    instance.getDebugUtils().log("load() - Completed in  " + (System.currentTimeMillis() - curTime) + "ms.", getClass());
                    loaded = true;
                }
            }, 0);
        }
        return false;
    }

    @Override
    public Object get(String value) {
        value = formatValue(value);
        return sql.get(TABLE, "uuid", playerManager.getPlayer().getUniqueId().toString(), value);
    }

    @Override
    public void write(String value, String toWrite) {
        value = formatValue(value);
        sql.write(TABLE, "uuid", playerManager.getPlayer().getUniqueId().toString(), value, toWrite);
    }

    public String formatValue(String value) {
        if (value.equalsIgnoreCase("crate-cooldowns"))
            return "crateCooldowns";
        else if (value.equalsIgnoreCase("virtual-crates"))
            return "virtualCrates";
        return value;
    }

    @Override
    public boolean hasDataValue(String value) {
        return true;
    }

    @Override
    public boolean hasDataPath() {
        return true;
    }

}
