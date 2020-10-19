package me.ztowne13.customcrates.players;

import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom.DisplayPage;
import me.ztowne13.customcrates.crates.types.animations.AnimationDataHolder;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.players.data.*;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.ReflectionUtilities;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private static final Map<UUID, PlayerManager> playerManagerMap = new HashMap<>();
    private final SpecializedCrates instance;
    private final Player player;
    private final DataHandler dataHandler;
    private final PlayerDataManager playerDataManager;

    private long lastClickedCrateTime = 0;

    private PlacedCrate lastOpenedPlacedCrate = null;
    private Crate openCrate = null;
    private Location lastOpenCrate = null;
    private AnimationDataHolder currentAnimation;
    private boolean isInCratesClaimMenu = false;
    private boolean inRewardMenu = false;
    private DisplayPage lastPage;
    // This is to allow the anti-dupe inventory reopen/close feature but prevent it when opening the next page of an inv
    private long nextPageInventoryCloseGrace = 0;
    private boolean deleteCrate = false;
    private boolean useVirtualCrate = false;
    private boolean confirming = false;
    private BukkitTask confirmingTask = null;
    private long cmdCooldown = 0;
    private String lastCooldown = "NONE";
    private IGCMenu openMenu = null;
    private IGCMenu lastOpenMenu = null;

    public PlayerManager(SpecializedCrates instance, Player player) {
        this.instance = instance;
        this.player = player;
        this.dataHandler = getSpecifiedDataHandler();
        this.playerDataManager = new PlayerDataManager(this);

        getPlayerDataManager().setDataHandler(getDataHandler());
        getPlayerDataManager().loadAllInformation();
        getPlayerManagerMap().put(player.getUniqueId(), this);
    }

    public static PlayerManager get(SpecializedCrates instance, Player player) {
        instance.getDu().log("PlayerManager.get() - CALL (contains: " + getPlayerManagerMap().containsKey(player.getUniqueId()) + ")", PlayerManager.class);
        return getPlayerManagerMap().containsKey(player.getUniqueId()) ? getPlayerManagerMap().get(player.getUniqueId()) : new PlayerManager(instance, player);
    }

    public static void clearLoaded() {
        playerManagerMap.clear();
    }

    public static Map<UUID, PlayerManager> getPlayerManagerMap() {
        return playerManagerMap;
    }

    public void remove(int delay) {
        instance.getDu().log("PlayerManager.remove() - CALL", getClass());

        if (isInCrateAnimation()) {
            getCurrentAnimation().setFastTrack(true, true);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(getInstance(), new Runnable() {
            @Override
            public void run() {
                getPlayerManagerMap().remove(getPlayer().getUniqueId());
                instance.getDu().log("PlayerManager.remove() - Removed", getClass());
            }
        }, delay);

        ReflectionUtilities.cachedHandles.remove(getPlayer());
    }

    public DataHandler getSpecifiedDataHandler() {
        try {
            StorageType st = StorageType.valueOf(
                    ChatUtils.stripFromWhitespace(SettingsValue.STORE_DATA.getValue(getInstance()).toString().toUpperCase()));
            switch (st) {
                case MYSQL:
                    Utils.addToInfoLog(instance, "Storage Type", "MYSQL");
                    return new SQLDataHandler(this);
                case FLATFILE:
                    Utils.addToInfoLog(instance, "Storage Type", "FLATFILE");
                    return new FlatFileDataHandler(this);
                case PLAYERFILES:
                    Utils.addToInfoLog(instance, "Storage Type", "PLAYERFILES");
                    return new IndividualFileDataHandler(this);
                default:
                    ChatUtils.log(new String[]{"store-data value in the config.YML is not a valid storage type.",
                            "  It must be: MYSQL, FLATFILE, PLAYERFILES"});
                    Utils.addToInfoLog(instance, "StorageType", "FLATFILE");
                    return new FlatFileDataHandler(this);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            ChatUtils.log(new String[]{"store-data value in the config.YML is not a valid storage type.",
                    "  It must be: MYSQL, FLATFILE, PLAYERFILES"});
        }
        return null;
    }

    public boolean isConfirming() {
        return confirming;
    }

    public void setConfirming(final boolean confirming) {
        this.confirming = confirming;
        if (confirming) {
            confirmingTask = Bukkit.getScheduler().runTaskLater(instance, () -> setConfirming(false), 20L * (int) SettingsValue.CONFIRM_TIMEOUT.getValue(instance));
        } else {
            if (confirmingTask != null) {
                confirmingTask.cancel();
                confirmingTask = null;
            }
        }
    }

    public boolean isInCrate() {
        return openCrate != null;
    }

    public boolean isDeleteCrate() {
        return deleteCrate;
    }

    public void setDeleteCrate(boolean b) {
        this.deleteCrate = b;
    }

    public void openCrate(Crate crate) {
        openCrate = crate;
    }

    public void closeCrate() {
        openCrate = null;
        useVirtualCrate = false;
    }

    public Crate getOpenCrate() {
        return openCrate;
    }

    public boolean isInRewardMenu() {
        return inRewardMenu;
    }

    public void setInRewardMenu(boolean inRewardMenu) {
        this.inRewardMenu = inRewardMenu;
    }

    public Player getPlayer() {
        return player;
    }

    public SpecializedCrates getInstance() {
        return instance;
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public long getCmdCooldown() {
        return cmdCooldown;
    }

    public void setCmdCooldown(long cmdCooldown) {
        this.cmdCooldown = cmdCooldown;
    }

    public String getLastCooldown() {
        return lastCooldown;
    }

    public void setLastCooldown(String lastCooldown) {
        this.lastCooldown = lastCooldown;
    }

    public IGCMenu getOpenMenu() {
        return openMenu;
    }

    public void setOpenMenu(IGCMenu openMenu) {
        this.openMenu = openMenu;
        if (openMenu != null) {
            this.lastOpenMenu = openMenu;
        }
    }

    public boolean isInOpenMenu() {
        return this.openMenu != null;
    }

    public IGCMenu getLastOpenMenu() {
        return lastOpenMenu;
    }

    public Location getLastOpenCrate() {
        return lastOpenCrate;
    }

    public void setLastOpenCrate(Location lastOpenCrate) {
        this.lastOpenCrate = lastOpenCrate;
    }

    public boolean isUseVirtualCrate() {
        return useVirtualCrate;
    }

    public void setUseVirtualCrate(boolean useVirtualCrate) {
        this.useVirtualCrate = useVirtualCrate;
    }

    public PlacedCrate getLastOpenedPlacedCrate() {
        return lastOpenedPlacedCrate;
    }

    public void setLastOpenedPlacedCrate(PlacedCrate lastOpenedPlacedCrate) {
        this.lastOpenedPlacedCrate = lastOpenedPlacedCrate;
    }

    public DisplayPage getLastPage() {
        return lastPage;
    }

    public void setLastPage(DisplayPage lastPage) {
        this.lastPage = lastPage;
    }

    public AnimationDataHolder getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(AnimationDataHolder currentAnimation) {
        this.currentAnimation = currentAnimation;
    }

    public boolean isInCrateAnimation() {
        return currentAnimation != null;
    }

    public long getLastClickedCrateTime() {
        return lastClickedCrateTime;
    }

    public void setLastClickedCrateTime(long lastClickedCrateTime) {
        this.lastClickedCrateTime = lastClickedCrateTime;
    }

    public long getNextPageInventoryCloseGrace() {
        return nextPageInventoryCloseGrace;
    }

    public void setNextPageInventoryCloseGrace(long nextPageInventoryCloseGrace) {
        this.nextPageInventoryCloseGrace = nextPageInventoryCloseGrace;
    }

    public boolean isInCratesClaimMenu() {
        return isInCratesClaimMenu;
    }

    public void setInCratesClaimMenu(boolean inCratesClaimMenu) {
        isInCratesClaimMenu = inCratesClaimMenu;
    }
}
