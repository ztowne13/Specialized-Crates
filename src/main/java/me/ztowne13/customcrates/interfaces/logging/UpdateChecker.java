package me.ztowne13.customcrates.interfaces.logging;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by ztowne13 on 7/7/16.
 */
public class UpdateChecker {
    private final SpecializedCrates instance;
    private String latestVersion;
    private boolean needsUpdate = false;

    public UpdateChecker(SpecializedCrates instance) {
        this.instance = instance;

        if (Boolean.parseBoolean(instance.getSettings().getConfigValues().get("notify-updates").toString())) {
            updateMostRecentVersion();
        }
    }

    public void updateMostRecentVersion() {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            try {
                HttpURLConnection con =
                        (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.getOutputStream()
                        .write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + 9047)
                                .getBytes(StandardCharsets.UTF_8));
                String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
                if (version.length() <= 7) {
                    latestVersion = version;
                }
                updateNeedsUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
                ChatUtils.log("Failed to check for a update on spigot.");
            }
        });
    }

    public void updateNeedsUpdate() {
        String thisVersion = instance.getDescription().getVersion();

        String[] splitCurrent = latestVersion.split("\\.");
        String[] splitThis = thisVersion.split("\\.");
        for (int i = 0; i < 4; i++) {
            int currentI = splitCurrent.length <= i ? 0 : Integer.parseInt(splitCurrent[i]);
            int thisI = splitThis.length <= i ? 0 : Integer.parseInt(splitThis[i]);

            if (currentI == thisI) {
                continue;
            } else if (currentI > thisI) {
                ChatUtils.log(new String[]{
                        "There is an update for Specialized Crates! You are currently on version " + thisVersion +
                                " but an update for version " + latestVersion + " is available."});
                //notifyPlayers();
                needsUpdate = true;
            }
            return; // If the plugin is a newer version
        }
    }

//    public void notifyPlayers()
//    {
//        for (final Player p : Bukkit.getOnlinePlayers())
//        {
//            if (p.hasPermission(Bukkit.getPluginCommand("scrates").getPermission()))
//            {
//                Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        Messages.NEEDS_UPDATE.msgSpecified(cc, p, new String[]{"%version%"},
//                                new String[]{cc.getUpdateChecker().getLatestVersion()});
//                    }
//                }, 1);
//            }
//        }
//    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public boolean needsUpdate() {
        return needsUpdate;
    }

    public void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }
}
