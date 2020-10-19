package me.ztowne13.customcrates.players.data.events;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.players.PlayerDataManager;

/**
 * Created by ztowne13 on 8/5/15.
 */
public abstract class DataEvent {
    protected final SpecializedCrates instance;

    public DataEvent(SpecializedCrates instance) {
        this.instance = instance;
    }

    public abstract void addTo(PlayerDataManager playerDataManager);

    public abstract String getFormatted();

    public SpecializedCrates getInstance() {
        return instance;
    }
}
