package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

public class PluginEnableListener implements Listener {
    SpecializedCrates sc;

    String[] toListenHolograms = new String[]{
            "CMI",
            "Holographic Displays",
            "Holograms"
    };

    public PluginEnableListener(SpecializedCrates sc) {
        this.sc = sc;
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent pluginEnableEvent) {
        for (String hologramName : toListenHolograms) {
            if (hologramName.equalsIgnoreCase(pluginEnableEvent.getPlugin().getName())) {
                for (PlacedCrate crate : PlacedCrate.getPlacedCrates().values()) {
                    crate.setupHolo(crate.getCrate(), false);
                }
            }
        }

        if (pluginEnableEvent.getPlugin().getName().equalsIgnoreCase("Multiverse-Core")) {
            for (String s : sc.getSettings().getFailedPlacedCrate())
                sc.getSettings().loadCrateFromFile(s);
        }
    }
}
