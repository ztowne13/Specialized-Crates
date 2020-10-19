package me.ztowne13.customcrates.players.data;

import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class IndividualFileDataHandler extends DataHandler {
    private static final List<IndividualFileDataHandler> toSave = new ArrayList<>();

    private final FileHandler fileHandler;
    private final FileConfiguration fileConfiguration;

    public IndividualFileDataHandler(PlayerManager playerManager) {
        super(playerManager);
        instance.getDebugUtils().log("Loading individual file data handler for " + playerManager.getPlayer().getName());
        this.fileHandler = new FileHandler(playerManager.getInstance(), playerManager.getPlayer().getUniqueId().toString() + ".stats", "/PlayerStats/", false, false,
                false);
        this.fileConfiguration = getFileHandler().get();
        instance.getDebugUtils().log(fileHandler.getDataFile().getAbsolutePath());
    }

    public static List<IndividualFileDataHandler> getToSave() {
        return toSave;
    }

    @Override
    public boolean load() {
        return false;
    }

    @Override
    public Object get(String value) {
        return getFileConfiguration().get(value);
    }

    @Override
    public void write(String value, String toWrite) {
        getFileConfiguration().set(value, toWrite);

        if (!toSave.contains(this))
            toSave.add(this);
    }

    @Override
    public boolean hasDataValue(String value) {
        return getFileConfiguration().contains(value);
    }

    @Override
    public boolean hasDataPath() {
        return true;
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }
}
