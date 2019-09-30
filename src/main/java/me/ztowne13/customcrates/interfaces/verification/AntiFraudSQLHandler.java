package me.ztowne13.customcrates.interfaces.verification;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.sql.SQL;

public class AntiFraudSQLHandler extends Thread
{
    SpecializedCrates specializedCrates;

    String databaseName = "";
    String databaseIp = "";
    String databasePort = "";
    String databaseUsername = "";
    String databasePassword = "";

    SQL sql;

    public AntiFraudSQLHandler(SpecializedCrates specializedCrates)
    {
        this.specializedCrates = specializedCrates;

        start();
        setName("SpecializedCrates-SQL");
    }

    @Override
    public void run()
    {
//        sql = new SQL(specializedCrates, databaseIp, databaseName, databaseUsername, databasePassword);

    }
}
