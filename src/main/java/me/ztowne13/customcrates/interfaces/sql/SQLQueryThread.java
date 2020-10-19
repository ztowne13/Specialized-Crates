package me.ztowne13.customcrates.interfaces.sql;

import me.ztowne13.customcrates.players.data.SQLDataHandler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SQLQueryThread extends Thread {
    private static final Queue<String> sql_query = new ConcurrentLinkedQueue<>();
    private static final Queue<Runnable> task_query = new ConcurrentLinkedQueue<>();
    private static boolean stopped = false;

    private final SQL sql;

    public SQLQueryThread(SQL sql) {
        this.sql = sql;
        start();
        setName("SpecializedCrates-SQL");

        sql.getInstance().getDebugUtils().log("SQLQueryThread() - Opening connection...", getClass());
        long curTime = System.currentTimeMillis();

        sql.getConnection().open();

        sql.getInstance().getDebugUtils().log("SQLQueryThread() - Completed opening connection in " + (System.currentTimeMillis() - curTime) + "ms.", getClass());
    }

    public static void addQuery(String query) {
        sql_query.add(query);
    }

    public static void stopRun() {
        stopped = true;
    }

    public static void clearQuery() {
        sql_query.clear();
        task_query.clear();
    }

    public static void addQuery(Runnable runnable) {
        task_query.add(runnable);
    }

    @Override
    public void run() {
        boolean tryReconnect = false;
        while (!stopped) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException exc) {
                Thread.currentThread().interrupt();
            }

            if (!SQLDataHandler.isLoaded()) {
                continue;
            }

            while (!sql_query.isEmpty()) {
                String query = sql_query.remove();
                sql.getInstance().getDebugUtils().log("run() - query: " + query, getClass());
                try {
                    sql.getConnection().get().prepareStatement(query).executeUpdate();
                    tryReconnect = false;
                } catch (Exception exc) {
                    if (!tryReconnect) {
                        tryReconnect = true;
                        sql.getConnection().open();
                        sql.getInstance().getDebugUtils().log("Trying to reconnect to SQL servers.");
                    } else {
                        sql.getInstance().getDebugUtils().log("Failed to reconnect to SQL servers.");
                        exc.printStackTrace();
                    }
                }
            }

            while (!task_query.isEmpty()) {
                Runnable query = task_query.remove();
                sql.getInstance().getDebugUtils().log("run() - query: " + query.toString(), getClass());
                query.run();
            }
        }
    }
}
