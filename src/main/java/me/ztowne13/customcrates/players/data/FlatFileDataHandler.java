package me.ztowne13.customcrates.players.data;

import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.configuration.file.FileConfiguration;

public class FlatFileDataHandler extends DataHandler {
    static FileHandler fu = null;
    static FileConfiguration fc;
    static boolean toSave = false;

    public FlatFileDataHandler(PlayerManager pm) {
        super(pm);
        cc.getDu().log("Loading flat file data handler for " + pm.getP().getName());
        if (fu == null) {
            fu = new FileHandler(getCc(), "PlayerData.db", false, false);
            fc = getFileHandler().get();
        }
    }

    public static FileHandler getFileHandler() {
        return fu;
    }

    public static FileConfiguration getFc() {
        return fc;
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
        cc.getDu().log("FlatFileDataHandler.get() - CALL", getClass());
        return getFc().get(toPath(value));
    }

    @Override
    public void write(String value, String toWrite) {
        cc.getDu().log("FlatFileDataHandler.write() - CALL", getClass());
        getFc().set(toPath(value), toWrite);

        toSave = true;
    }

    @Override
    public boolean hasDataPath() {
        return getFc().contains(getUuid());
    }

    @Override
    public boolean hasDataValue(String value) {
        return getFc().contains(toPath(value));
    }

    public String toPath(String value) {
        return getUuid() + "." + value;
    }
}
