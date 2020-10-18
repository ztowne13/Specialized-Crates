package me.ztowne13.customcrates.interfaces.sql;

import me.ztowne13.customcrates.players.data.SQLDataHandler;

import java.util.concurrent.CopyOnWriteArrayList;

public class SQLQueryThread extends Thread {
    public static volatile CopyOnWriteArrayList<String> sql_query = new CopyOnWriteArrayList<>();
    public static volatile CopyOnWriteArrayList<Runnable> task_query = new CopyOnWriteArrayList<>();

    SQL sql;

    public SQLQueryThread(SQL sql) {
        this.sql = sql;
        start();
        setName("SpecializedCrates-SQL");

        sql.getInstance().getDu().log("SQLQueryThread() - Opening connection...", getClass());
        long curTime = System.currentTimeMillis();

        sql.getConnection().open();

        sql.getInstance().getDu().log("SQLQueryThread() - Completed opening connection in " + (System.currentTimeMillis() - curTime) + "ms.", getClass());
    }

    public static void addQuery(String query) {
        sql_query.add(query);
    }

    public static void addQuery(Runnable runnable) {
        task_query.add(runnable);
    }

    @Override
    public void run() {
        boolean tryReconnect = false;
        while (true) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException exc) {

            }

            if (!SQLDataHandler.loaded) {
                continue;
            }

            for (String query : sql_query) {
                sql.getInstance().getDu().log("run() - query: " + query, getClass());
                try {
                    sql.getConnection().get().prepareStatement(query).executeUpdate();
                    tryReconnect = false;
                } catch (Exception exc) {
                    //new SQLLog("Failed query: " + query);
                    //exc.printStackTrace();
                    if (!tryReconnect) {
                        tryReconnect = true;
                        sql.getConnection().open();
                        sql.getInstance().getDu().log("Trying to reconnect to SQL servers.");
                    } else {
                        sql.getInstance().getDu().log("Failed to reconnect to SQL servers.");
                        exc.printStackTrace();
                    }
                }

                sql_query.remove(query);
            }

            for (Runnable query : task_query) {
                sql.getInstance().getDu().log("run() - query: " + query.toString(), getClass());

                query.run();
                task_query.remove(query);
            }
        }
    }
}
