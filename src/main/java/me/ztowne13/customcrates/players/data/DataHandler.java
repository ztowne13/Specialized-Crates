package me.ztowne13.customcrates.players.data;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.players.PlayerManager;

public abstract class DataHandler {
    protected final SpecializedCrates instance;

    protected final PlayerManager playerManager;
    protected final String uuid;

    public DataHandler(PlayerManager playerManager) {
        this.instance = playerManager.getInstance();
        this.playerManager = playerManager;
        this.uuid = playerManager.getPlayer().getUniqueId().toString();
    }

    public abstract boolean load();

    public abstract Object get(String value);

    public abstract void write(String value, String toWrite);

    public abstract boolean hasDataPath();

    public abstract boolean hasDataValue(String value);

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public String getUuid() {
        return uuid;
    }
}
