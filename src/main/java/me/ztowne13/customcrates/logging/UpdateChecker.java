package me.ztowne13.customcrates.logging;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ztowne13 on 7/7/16.
 */
public class UpdateChecker
{
    CustomCrates cc;
    String latestVersion;
    boolean needsUpdate = false;

    public UpdateChecker(CustomCrates cc)
    {
        this.cc = cc;

        if (Boolean.valueOf(cc.getSettings().getConfigValues().get("notify-updates").toString()))
        {
            updateMostRecentVersion();
        }
    }

    public void updateMostRecentVersion()
    {
        Bukkit.getScheduler().runTaskAsynchronously(cc, new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    HttpURLConnection con =
                            (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.getOutputStream()
                            .write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + 9047)
                                    .getBytes("UTF-8"));
                    String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
                    if (version.length() <= 7)
                    {
                        latestVersion = version;
                    }
                    updateNeedsUpdate();
                }
                catch (Exception ex)
                {
                    ChatUtils.log("Failed to check for a update on spigot.");
                }
            }
        });
    }

    public void updateNeedsUpdate()
    {
        String thisVersion = cc.getDescription().getVersion();

        String[] splitCurrent = latestVersion.split("\\.");
        String[] splitThis = thisVersion.split("\\.");
        for (int i = 0; i < 4; i++)
        {
            int currentI = splitCurrent.length <= i ? 0 : Integer.parseInt(splitCurrent[i]);
            int thisI = splitThis.length <= i ? 0 : Integer.parseInt(splitThis[i]);

            if (currentI == thisI)
            {
                continue;
            }
            else if (currentI > thisI)
            {
                ChatUtils.log(new String[]{
                        "There is an update for Specialized Crates! You are currently on version " + thisVersion +
                                " but an update for version " + latestVersion + " is available."});
                notifyPlayers();
                needsUpdate = true;
                return;
            }
            return; // If the plugin is a newer version
        }
    }

    public void notifyPlayers()
    {
        for (final Player p : Bukkit.getOnlinePlayers())
        {
            if (p.hasPermission(Bukkit.getPluginCommand("scrates").getPermission()))
            {
                Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Messages.NEEDS_UPDATE.msgSpecified(cc, p, new String[]{"%version%"},
                                new String[]{cc.getUpdateChecker().getLatestVersion()});
                    }
                }, 1);
            }
        }
    }

    public String getLatestVersion()
    {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion)
    {
        this.latestVersion = latestVersion;
    }

    public boolean needsUpdate()
    {
        return needsUpdate;
    }

    public void setNeedsUpdate(boolean needsUpdate)
    {
        this.needsUpdate = needsUpdate;
    }
}
