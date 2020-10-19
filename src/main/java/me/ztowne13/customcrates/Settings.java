package me.ztowne13.customcrates;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.utils.*;
import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings {
    private final SpecializedCrates instance;

    private final HashMap<String, String> infoToLog = new HashMap<>();
    private final HashMap<String, Object> configValues = new HashMap<>();

    private final ArrayList<String> failedPlacedCrate = new ArrayList<>();

    public Settings(SpecializedCrates instance) {
        this.instance = instance;
    }


    public void load() {
        loadSettings();
        ChatUtils.log("");
        loadCrates(false);
        loadCrates(true);
        loadPlacedCrates();
        loadInfo();
        ChatUtils.log("");
        ChatUtils.log("");
    }

    public void loadCrates(boolean multiCrate) {
        instance.getDebugUtils().log("loadCrates - CALL", getClass());
        File folder = new File(instance.getDataFolder().getPath() + "/Crates");
        File[] listOfFiles = folder.listFiles();

        instance.getDebugUtils().log("loadCrates - Beginning loop", getClass());
        for (int i = 0; i < listOfFiles.length; i++) {
            instance.getDebugUtils().log("loadCrates - Looping " + i, getClass());
            if (listOfFiles[i].isFile()) {
                instance.getDebugUtils().log("loadCrates - Is file " + i, getClass());
                File f = listOfFiles[i];

                if (f.getName().contains(";") || f.getName().contains(",")) {
                    ChatUtils.log(new String[]{"FAILED TO LOAD THE " + f.getName() + " CRATE.",
                            "  CAUSE: Crate's Name contains ',' or ';' characters."});
                    continue;
                }

                instance.getDebugUtils().log("loadCrates - Attempting load", getClass());
                if (f.getName().endsWith(".crate") && !multiCrate) {
                    instance.getDebugUtils().log("loadCrates - Loading " + f.getName() + " is .crate", getClass());
                    String crateName = f.getName().replace(".crate", "");
                    Crate.getCrate(instance, crateName);
                    instance.getDebugUtils().log("loadCrates - Done", getClass());
                } else if (f.getName().endsWith(".multicrate") && multiCrate) {
                    instance.getDebugUtils().log("loadCrates - Loading " + f.getName() + " is .multicrate", getClass());
                    String crateName = f.getName().replace(".multicrate", "");
                    Crate.getCrate(instance, crateName, true);
                    instance.getDebugUtils().log("loadCrates - Done", getClass());
                }
            }
        }
    }

    public void loadPlacedCrates() {
        for (String s : instance.getActiveCratesFile().get().getKeys(false))
            if (!loadCrateFromFile(s))
                failedPlacedCrate.add(s);
    }

    public boolean loadCrateFromFile(String s) {
        FileHandler activeCrates = instance.getActiveCratesFile();
        String crateName = activeCrates.get().getString(s + ".crate");
        Location l = LocationUtils.stringToLoc(s);

        if (Crate.exists(crateName)) {
            Crate crates = Crate.getCrate(instance, crateName);

            PlacedCrate cm = PlacedCrate.get(instance, l);
            if (cm == null) {
                ChatUtils.log("location: " + s);
                return false;
            }

            if (crates.isEnabled()) {
                cm.setup(crates, false);
            } else {
                cm.setCratesEnabled(false);
            }

            if (activeCrates.get().contains(s + ".placedTime")) {
                cm.setPlacedTime(activeCrates.get().getLong(s + ".placedTime"));
            } else {
                cm.setPlacedTime(0L);
            }
        } else {
            ChatUtils.log(new String[]{"ERROR: " + crateName + " DOES NOT EXIST TO BE USED AT LOCATION: " + s});
        }

        return true;
    }

    public void loadSettings() {
        for (SettingsValue sv : SettingsValue.values()) {
            getConfigValues().put(sv.getPath(), instance.getConfig().get(sv.getPath()));
        }
    }

    public void loadInfo() {
        Utils.addToInfoLog(instance, "Server version", VersionUtils.getServerVersion());
        Utils.addToInfoLog(instance, "Citizens Installed",
                (NPCUtils.isCitizensInstalled() ? "" : "&c") + Utils.isPLInstalled("Citizens") + "");

        int enabled = 0;
        int disabled = 0;
        for (Crate crates : Crate.getLoadedCrates().values()) {
            if (crates.isEnabled()) {
                enabled++;
            } else {
                disabled++;
            }
        }

        Utils.addToInfoLog(instance, "Crates loaded", enabled + " enabled &4/ &c" + disabled + " disabled");
        Utils.addToInfoLog(instance, "Crates placed", PlacedCrate.getPlacedCrates().keySet().size() + "");

        Utils.addToInfoLog(instance, "Plugin version", instance.getDescription().getVersion());

    }

    public void writeSettingsValues() {
        FileHandler fu = new FileHandler(instance, "config.yml", true, true);
        for (String s : getConfigValues().keySet()) {
            fu.get().set(s, getConfigValues().get(s));
        }

        fu.save();
    }

    public Boolean getConfigValAsBoolean(String path) {
        return Boolean.valueOf(getConfigValues().get(path).toString());
    }

    public Map<String, Object> getConfigValues() {
        return configValues;
    }

    public Map<String, String> getInfoToLog() {
        return infoToLog;
    }

    public List<String> getFailedPlacedCrate() {
        return failedPlacedCrate;
    }
}
