package me.ztowne13.customcrates.players.data;

import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.configuration.file.FileConfiguration;

public class FlatFileDataHandler extends DataHandler {
    private static FileHandler fileHandler;
    private static FileConfiguration fileConfiguration;
    private static boolean toSave = false;

    public FlatFileDataHandler(PlayerManager playerManager) {
        super(playerManager);
        instance.getDebugUtils().log("Loading flat file data handler for " + playerManager.getPlayer().getName());
        if (fileHandler == null) {
            fileHandler = new FileHandler(instance, "PlayerData.db", false, false);
            fileConfiguration = getFileHandler().get();
        }
    }

    public static FileHandler getFileHandler() {
        return fileHandler;
    }

    public static FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public static boolean isToSave() {
        return toSave;
    }

    public static void resetToSave() {
        toSave = false;
    }

    @Override
    public boolean load() {
        return false;
    }

    @Override
    public Object get(String value) {
        instance.getDebugUtils().log("FlatFileDataHandler.get() - CALL", getClass());
        return getFileConfiguration().get(toPath(value));
    }

    @Override
    public void write(String value, String toWrite) {
        instance.getDebugUtils().log("FlatFileDataHandler.write() - CALL", getClass());
        getFileConfiguration().set(toPath(value), toWrite);

        toSave = true;
    }

    @Override
    public boolean hasDataPath() {
        return getFileConfiguration().contains(getUuid());
    }

    @Override
    public boolean hasDataValue(String value) {
        return getFileConfiguration().contains(toPath(value));
    }

    public String toPath(String value) {
        return getUuid() + "." + value;
    }
}
