package me.ztowne13.customcrates.crates;

import me.ztowne13.customcrates.DataHandler;
import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.CrateUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Crate {
    private static Map<String, Crate> loadedCrates = new HashMap<>();

    private final SpecializedCrates instance;
    private final boolean loadedProperly;
    private String name;
    private int placedCount = 0;
    private String lastOpenedName = "Nobody";
    private String lastOpenedReward = "Nothing";
    private boolean enabled = true;
    private boolean disabledByError = false;
    private boolean canBeEnabled = true;
    private boolean isMultiCrate;
    private boolean isUsedForCratesCommand = false;
    private boolean needsReload = false;

    private CrateSettings crateSettings;

    public Crate(SpecializedCrates instance, String name, boolean newFile) {
        this(instance, name, newFile, false);
    }

    public Crate(SpecializedCrates instance, String name, boolean newFile, boolean isMultiCrate) {
        instance.getDu().log("Crate() - new", getClass());
        this.instance = instance;
        this.name = name;

        this.isMultiCrate = isMultiCrate;
        this.crateSettings = new CrateSettings(instance, this, newFile);

        setEnabled(!getSettings().getSettingsBuilder().hasValue("enabled") || crateSettings.getFileConfiguration().getBoolean("enabled"));

        getLoadedCrates().put(name, this);

        getSettings().loadAll();
        loadedProperly = true;
    }

    public static Crate getCrate(SpecializedCrates cc, String name) {
        return getCrate(cc, name, false);
    }

    public static Crate getCrate(SpecializedCrates cc, String name, boolean isMultiCrate) {
        for (String crateName : getLoadedCrates().keySet()) {
            if (crateName.equalsIgnoreCase(name)) {
                return getLoadedCrates().get(crateName);
            }
        }

        return new Crate(cc, name, false, isMultiCrate);
    }

    public static boolean existsNotCaseSensitive(String name) {
        for (String crates : getLoadedCrates().keySet()) {
            if (crates.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean exists(String name) {
        return getLoadedCrates().containsKey(name);
    }

    public static void clearLoaded() {
        getLoadedCrates().clear();
        setLoadedCrates(new HashMap<>());
    }

    public static Map<String, Crate> getLoadedCrates() {
        return loadedCrates;
    }

    public static void setLoadedCrates(Map<String, Crate> loadedCrates) {
        Crate.loadedCrates = loadedCrates;
    }

    public void tick(Location l, CrateState cstate, Player p, List<Reward> rewards) {
        tick(l, null, cstate, p, rewards);
    }

    public void tick(Location l, PlacedCrate placedCrate, CrateState cstate, Player p, List<Reward> rewards) {
        getSettings().getParticle().runAll(l, cstate, rewards);
        if (cstate.equals(CrateState.OPEN) && CrateUtils.isCrateUsable(this)) {
            getSettings().getSound().runAll(p, l, rewards);
            getSettings().getFirework().runAll(l, rewards);
            if (rewards != null && !rewards.isEmpty()) {
                getSettings().getAction().playAll(p, placedCrate, rewards, false);
            }
        }
    }

    public boolean rename(String newName) {

        if (newName.contains(".")) {
            newName = newName.split("\\.")[0];
        }

        if (FileHandler.getMap().containsKey(newName + ".crate") ||
                FileHandler.getMap().containsKey(newName + ".multicrate")) {
            return false;
        }

        FileHandler newFile =
                new FileHandler(getInstance(), newName + (isMultiCrate() ? ".multicrate" : ".crate"),
                        "/Crates", true, true, true);
        newFile.reload();

        try {
            getSettings().getFileHandler().copy(newFile);
        } catch (Exception exc) {
            exc.printStackTrace();
            return false;
        }

        HashMap<Location, PlacedCrate> placed = new HashMap<>(PlacedCrate.getPlacedCrates());

        for (Map.Entry<Location, PlacedCrate> entry : placed.entrySet()) {
            PlacedCrate cm = entry.getValue();
            if (cm.getCrate().equals(this)) {
                cm.rename(newName);
                PlacedCrate.getPlacedCrates().remove(entry.getKey());
            }
        }

        deleteCrate();
        return true;
    }

    public String deleteCrate() {
        deleteAllPlaced();

        Path path = getSettings().getFileHandler().getDataFile().toPath();

        try {
            Files.deleteIfExists(path);
            ChatUtils.log("Successfully deleted file " + path);
        } catch (Exception e) {
            return "An I/O error occurred, please try reloading or contacting the plugin author.";
        }

        for (UUID id : instance.getDataHandler().getQueuedGiveCommands().keySet()) {
            List<DataHandler.QueuedGiveCommand> cmds = instance.getDataHandler().getQueuedGiveCommands().get(id);
            for (DataHandler.QueuedGiveCommand cmd : cmds) {
                if (cmd.getCrate().equals(this)) {
                    cmds.remove(cmd);
                    instance.getDataHandler().getQueuedGiveCommands().remove(id);
                    instance.getDataHandler().getQueuedGiveCommands().put(id, cmds);
                }
            }
        }

        instance.getDataHandler().saveToFile();

        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, instance::reload, 20);
        return path.toString();
    }

    public List<PlacedCrate> deleteAllPlaced() {
        HashMap<Location, PlacedCrate> placed = new HashMap<>(PlacedCrate.getPlacedCrates());
        ArrayList<PlacedCrate> deleted = new ArrayList<>();

        for (PlacedCrate cm : placed.values()) {
            if (cm.getCrate().equals(this)) {
                deleted.add(cm);
                cm.getCrate().getSettings().getPlaceholder().remove(cm);
                cm.delete();
            }
        }

        return deleted;
    }

    public SpecializedCrates getInstance() {
        return instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CrateSettings getSettings() {
        return crateSettings;
    }

    public void setSettings(CrateSettings cs) {
        this.crateSettings = cs;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        for (PlacedCrate cm : PlacedCrate.getPlacedCrates().values()) {
            cm.setCratesEnabled(enabled);
        }

        this.enabled = enabled;
    }

    public boolean isCanBeEnabled() {
        return canBeEnabled;
    }

    public void setCanBeEnabled(boolean canBeEnabled) {
        this.canBeEnabled = canBeEnabled;
    }

    public boolean isMultiCrate() {
        return isMultiCrate;
    }

    public void setMultiCrate(boolean multiCrate) {
        isMultiCrate = multiCrate;
    }

    public int getPlacedCount() {
        return placedCount;
    }

    public void setPlacedCount(int placedCount) {
        this.placedCount = placedCount;
    }

    public boolean isUsedForCratesCommand() {
        return isUsedForCratesCommand;
    }

    public void setUsedForCratesCommand(boolean usedForCratesCommand) {
        isUsedForCratesCommand = usedForCratesCommand;
    }

    public String getLastOpenedName() {
        return lastOpenedName;
    }

    public void setLastOpenedName(String lastOpenedName) {
        this.lastOpenedName = lastOpenedName;
    }

    public String getLastOpenedReward() {
        return lastOpenedReward;
    }

    public void setLastOpenedReward(String lastOpenedReward) {
        this.lastOpenedReward = lastOpenedReward;
    }

    public String getDisplayName() {
        if (SettingsValue.USE_CRATE_NAME_FOR_DISPLAY.getValue(getInstance()).equals(Boolean.TRUE)
                && getSettings().getCrateItemHandler().getItem().hasDisplayName()) {
            return getSettings().getCrateItemHandler().getItem().getDisplayName(true);
        }
        return getName();
    }

    public boolean isLoadedProperly() {
        return loadedProperly;
    }

    public boolean isNeedsReload() {
        return needsReload;
    }

    public void setNeedsReload(boolean needsReload) {
        this.needsReload = needsReload;
    }

    public boolean isDisabledByError() {
        return disabledByError;
    }

    public void setDisabledByError(boolean disabledByError) {
        this.disabledByError = disabledByError;
    }
}
