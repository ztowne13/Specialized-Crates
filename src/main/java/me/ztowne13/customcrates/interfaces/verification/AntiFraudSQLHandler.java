package me.ztowne13.customcrates.interfaces.verification;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.sql.SQL;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.FileHandler;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.ResultSet;
import java.util.UUID;


public class AntiFraudSQLHandler extends Thread
{
    boolean authenticated = true;

    SpecializedCrates specializedCrates;

    UUID id;

    String loggedTable = "sclogging";
    String blacklistTable = "scblacklist";
    String databaseName = "u490810685_sc";
    String databaseIp = "213.190.6.167";
    String databaseUsername = "u490810685_root";
    String databasePassword = "pJKIv5j!";

    SQL sql;

    public AntiFraudSQLHandler(SpecializedCrates specializedCrates)
    {
        this.specializedCrates = specializedCrates;

        FileHandler data = specializedCrates.getDataFile();
        FileConfiguration fc = data.get();

        if (fc.contains("server-id"))
        {
            id = UUID.fromString(fc.getString("server-id"));
        }
        else
        {
            id = UUID.randomUUID();
            fc.set("server-id", id.toString());
            data.save();
        }

        start();
        setName("SpecializedCrates-SQL");
    }

    @Override
    public void run()
    {
        try
        {
            sql = new SQL(specializedCrates, databaseIp, databaseName, databaseUsername, databasePassword);
            sql.getSqlc().open(false);

            sql.replace(loggedTable, "serverid, user, resource, nonce",
                    "'" + id + "', '" + AntiFraudPlaceholders.USER + "', '" + AntiFraudPlaceholders.RESOURCE + "', '" +
                            AntiFraudPlaceholders.NONCE + "'");

            ResultSet ids = sql.get(blacklistTable, "type", "SERVERID");
            if (ids != null)
            {
                while (!ids.isAfterLast())
                {
                    if (ids.getString("value").equalsIgnoreCase(id.toString()))
                        authenticated = false;

                    ids.next();
                }
            }

            ResultSet nonces = sql.get(blacklistTable, "type", "NONCE");
            if (nonces != null)
            {
                while (!nonces.isAfterLast())
                {
                    if (nonces.getString("value").equalsIgnoreCase(AntiFraudPlaceholders.NONCE))
                        authenticated = false;
                    nonces.next();
                }
            }

            ResultSet users = sql.get(blacklistTable, "type", "USER");
            if (users != null)
            {
                while (!users.isAfterLast())
                {
                    if (users.getString("value").equalsIgnoreCase(AntiFraudPlaceholders.USER))
                        authenticated = false;
                    users.next();
                }
            }

            ResultSet resources = sql.get(blacklistTable, "type", "RESOURCE");
            if (resources != null)
            {
                while (!resources.isAfterLast())
                {
                    if (resources.getString("value").equalsIgnoreCase(AntiFraudPlaceholders.RESOURCE))
                        authenticated = false;
                    resources.next();
                }
            }

            if (!authenticated)
            {
                ChatUtils
                        .log("&cIMPORTANT: THIS COPY OF THE SPECIALIZED CRATES HAS BEEN BLACKLISTED BECAUSE THE USER WHO PURCHASED IT" +
                                " IS NOT THE ONLY PERSON USING IT. IF YOU BELIEVE THIS IS AN ERROR, PLEASE RE-DOWNLOAD THE PLUGIN (NO" +
                                " NEED TO REGENERATE CONFIG) AND TRY AGAIN. IF IT'S STILL NOT WORKING, PLEASE CONTACT ZTOWNE13.");
            }
        }
        catch (
                Exception exc)

        {
            exc.printStackTrace();
            specializedCrates.getDu().log("run() - Failed to load to the authentication database");
        }

    }

    public boolean isAuthenticated()
    {
        return authenticated;
    }
}