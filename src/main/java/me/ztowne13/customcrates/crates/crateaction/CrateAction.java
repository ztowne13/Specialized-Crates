package me.ztowne13.customcrates.crates.crateaction;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.api.CrateOpenEvent;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.players.data.events.CrateCooldownEvent;
import me.ztowne13.customcrates.players.data.events.HistoryEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class CrateAction {

    protected final SpecializedCrates instance;
    protected final Player player;
    protected final Location location;

    public CrateAction(SpecializedCrates instance, Player player, Location location) {
        this.instance = instance;
        this.player = player;
        this.location = location;
    }

    public static boolean isInventoryTooEmpty(SpecializedCrates instance, Player player) {
        return Utils.getOpenInventorySlots(player) >= ((Integer) SettingsValue.REQUIRED_SLOTS.getValue(instance));
    }

    public abstract boolean run();

    public boolean useCrate(PlayerManager playerManager, PlacedCrate placedCrate, boolean skipAnimation) {
        return useCrate(playerManager, placedCrate, skipAnimation, false);
    }

    public boolean useCrateHelper(PlayerManager playerManager, PlacedCrate placedCrate, int opened) {
        if (opened < 300 && !useCrate(playerManager, placedCrate, true, true, opened)) {
            CrateOpenEvent crateOpenEvent = new CrateOpenEvent(player, null, placedCrate.getCrate(), opened);
            Bukkit.getPluginManager().callEvent(crateOpenEvent);
            return false;
        }
        return true;
    }

    public boolean useCrate(PlayerManager playerManager, PlacedCrate placedCrate, boolean skipAnimation, boolean hasSkipped) {
        return useCrate(playerManager, placedCrate, skipAnimation, hasSkipped, 0);
    }

    public boolean useCrate(PlayerManager playerManager, PlacedCrate placedCrate, boolean skipAnimation, boolean hasSkipped, int opened) {
        Player p = playerManager.getPlayer();
        PlayerDataManager playerDataManager = playerManager.getPlayerDataManager();
        Crate crate = placedCrate.getCrate();
        CrateSettings crateSettings = crate.getSettings();
        Location placedCrateLocation = placedCrate.getLocation();

        if (crate.isNeedsReload()) {
            ChatUtils.msgInfo(p,
                    "Hey! It looks like you just created a new crate." +
                            " Whenever you edit something in the in-game config," +
                            " make sure to click the green '&asave&e' and pink '&dreload&e' button before testing it out!" +
                            " This crate won't work until saved and reloaded! Try &b/scrates edit " + crate.getName() + " &eto" +
                            " open up the menu where you can &asave &eand &dreload &ethe crate.");
            return true;
        }

        // Check permissions
        if (!p.hasPermission(crateSettings.getPermission()) && !crateSettings.getPermission().equalsIgnoreCase("no permission")) {
            Messages.NO_PERMISSION_CRATE.msgSpecified(instance, p);
            crate.getSettings().getCrateAnimation().playFailToOpen(p, false, true);
            return false;
        }

        // Check inventory spaces (defined by value in Config.YML)
        if (!isInventoryTooEmpty(instance, p)) {
            Messages.INVENTORY_TOO_FULL.msgSpecified(instance, p);
            crate.getSettings().getCrateAnimation().playFailToOpen(p, false, true);
            return false;
        }
        // Check cooldown
        CrateCooldownEvent cce = playerDataManager.getCrateCooldownEventByCrates(crate);
        if (cce != null && !cce.isCooldownOverAsBoolean()) {
            cce.playFailure(playerDataManager);
            return false;
        }

        playerManager.setLastOpenedPlacedCrate(placedCrate);

        // SHIFT-CLICK OPEN
        // If the animation needs to be skipped (shift click). Also required to be a static crate
        if (skipAnimation && crateSettings.getObtainType().equals(ObtainType.STATIC)) {
            if (!playerManager.isConfirming() && SettingsValue.SHIFT_CLICK_CONFIRM.getValue(instance).equals(Boolean.TRUE)) {
                playerManager.setConfirming(true);
                Messages.CONFIRM_OPEN_ALL.msgSpecified(instance, p, new String[]{"%timeout%"}, new String[]{
                        SettingsValue.CONFIRM_TIMEOUT.getValue(instance) + ""});
                return false;
            }

            if (!crateSettings.getCrateAnimation().canExecuteFor(p, !crate.isMultiCrate())) {
                if (!hasSkipped)
                    crate.getSettings().getCrateAnimation().playFailToOpen(p, true, true);
                return false;
            }

            if (!instance.getEconomyHandler().handleCheck(p, crate.getSettings().getCost(), true)) {
                return false;
            }

            Reward reward = crateSettings.getReward().getRandomReward();
            ArrayList<Reward> rewards = new ArrayList<>();
            rewards.add(reward);
            reward.giveRewardToPlayer(p);

            crateSettings.getKeyItemHandler().takeKeyFromPlayer(p, false);
            new HistoryEvent(Utils.currentTimeParsed(), crate, rewards, true)
                    .addTo(PlayerManager.get(instance, p).getPlayerDataManager());
            new CrateCooldownEvent(crate, System.currentTimeMillis(), true).addTo(playerDataManager);

            useCrateHelper(playerManager, placedCrate, opened + 1);

            if (!hasSkipped) {
                crate.tick(placedCrateLocation, placedCrate, CrateState.OPEN, p, new ArrayList<>());
                playerManager.setConfirming(false);
            }
            return true;
        }

        // NORMAL OPEN
        if (!playerManager.isConfirming() && SettingsValue.CONFIRM_OPEN.getValue(instance).equals(Boolean.TRUE)) {
            playerManager.setConfirming(true);
            Messages.CONFIRM_OPEN.msgSpecified(instance, p, new String[]{"%timeout%"}, new String[]{
                    SettingsValue.CONFIRM_TIMEOUT.getValue(instance) + ""});
            return false;
        }

        if (!instance.getEconomyHandler().handleCheck(p, crateSettings.getCost(), true)) {
            crateSettings.getCrateAnimation().playFailToOpen(p, false, true);
            return false;
        }

        if (!crateSettings.getCrateAnimation().startAnimation(p, placedCrateLocation, !crate.isMultiCrate(), false)) {
            instance.getEconomyHandler().failSoReturn(p, crateSettings.getCost());
            playerManager.setLastOpenedPlacedCrate(null);
            return false;
        }

        // Crate isn't static but it ALSO isn't special handling (i.e. the BLOCK_ CrateTypes)
        if (!crateSettings.getObtainType().equals(ObtainType.STATIC) && !crateSettings.getCrateType().isSpecialDynamicHandling()) {
            placedCrate.delete();
            placedCrateLocation.getBlock().setType(Material.AIR);
        }
        new CrateCooldownEvent(crate, System.currentTimeMillis(), true).addTo(playerDataManager);
        return !skipAnimation;
    }

    public boolean updateCooldown(PlayerManager pm) {

        boolean b = false;

        long ct = System.currentTimeMillis();
        long diff = ct - pm.getCmdCooldown();

        if (diff < 1000 && !pm.getLastCooldown().equalsIgnoreCase("crate")) {
            Messages.WAIT_ONE_SECOND.msgSpecified(instance, pm.getPlayer());

            b = true;
        }
        pm.setLastCooldown("crate");
        pm.setCmdCooldown(ct);
        return b;
    }

    public PlacedCrate createCrateAt(Crate crates, Location l) {
        PlacedCrate cm = PlacedCrate.get(instance, l);
        cm.setup(crates, true);

        return cm;
    }
}
