package me.ztowne13.customcrates.players.data;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.interfaces.sql.SQL;
import me.ztowne13.customcrates.interfaces.sql.SQLQueryThread;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class SQLDataHandler extends DataHandler {
    public static String table = "scPlayerStats";
    public static SQL sql;
    public static boolean loaded = false;
    static SQLQueryThread sqlQueryThread;
    SpecializedCrates sc;

    public SQLDataHandler(PlayerManager pm) {
        super(pm);
        cc.getDu().log("SQLDataHandler() - CALL", getClass());
        sc = pm.getCc();


        load();
    }

    @Override
    public boolean load() {
        if (!loaded) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(cc, new Runnable() {
                @Override
                public void run() {
                    cc.getDu().log("load() - CALL (Note: This is run synchronous not asynchronous)", getClass());
                    long curTime = System.currentTimeMillis();

                    FileHandler sqlYml = sc.getSqlFile();
                    FileConfiguration fc = sqlYml.get();

                    String dbName = fc.getString("database.name");
                    String dbIp = fc.getString("database.ip");
                    String dbPort = fc.getString("database.port");
                    String dbUsername = fc.getString("database.username");
                    String dbPassword = fc.getString("database.password");

                    cc.getDu().log("load() - Opening connection and creating query thread.", getClass());
                    sql = new SQL(sc, dbIp, dbName, dbPort, dbUsername, dbPassword);
                    sqlQueryThread = new SQLQueryThread(sql);

                    cc.getDu().log("load() - Completed creating thread and opening connection in " +
                            (System.currentTimeMillis() - curTime) + "ms.", getClass());

                    cc.getDu().log("load() - Creating table & setting unique if it doesn't exist...", getClass());
                    long createTime = System.currentTimeMillis();

                    sql.create(table,
                            "uuid varchar(36), history longtext, crateCooldowns mediumtext, virtualCrates mediumtext, rewardLimits mediumtext",
                            true);

                    cc.getDu().log("load() - Completed creating table & setting unique in " +
                                    (System.currentTimeMillis() - createTime) + "ms.",
                            getClass());
                    cc.getDu().log("load() - Completed in  " + (System.currentTimeMillis() - curTime) + "ms.", getClass());
                    loaded = true;
                }
            }, 0);
        }
        return false;
    }

    @Override
    public Object get(String value) {
        value = formatValue(value);
        return sql.get(table, "uuid", pm.getP().getUniqueId().toString(), value);
    }

    @Override
    public void write(String value, String toWrite) {
        value = formatValue(value);
        sql.write(table, "uuid", pm.getP().getUniqueId().toString(), value, toWrite);
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
