package me.ztowne13.customcrates.crates.crateaction;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValues;
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
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class CrateAction
{

    SpecializedCrates cc;
    Player player;
    Location location;

    public CrateAction(SpecializedCrates cc, Player player, Location location)
    {
        this.cc = cc;
        this.player = player;
        this.location = location;
    }

    public abstract boolean run();

    public boolean useCrate(PlayerManager pm, PlacedCrate cm, boolean skipAnimation)
    {
        return useCrate(pm, cm, skipAnimation, false);
    }

    public boolean useCrateHelper(PlayerManager pm, PlacedCrate cm, boolean skipAnimation, boolean hasSkipped, int opened)
    {
        if(!useCrate(pm, cm, true, true, opened)) {
            CrateOpenEvent crateOpenEvent = new CrateOpenEvent(player, null, cm.getCrate(), opened);
            Bukkit.getPluginManager().callEvent(crateOpenEvent);
            return false;
        }
        return true;
    }

    public boolean useCrate(PlayerManager pm, PlacedCrate cm, boolean skipAnimation, boolean hasSkipped)
    {
        return useCrate(pm, cm, skipAnimation, hasSkipped, 0);
    }

    public boolean useCrate(PlayerManager pm, PlacedCrate cm, boolean skipAnimation, boolean hasSkipped, int opened)
    {
        Player player = pm.getP();
        PlayerDataManager pdm = pm.getPdm();
        Crate crates = cm.getCrate();
        CrateSettings cs = crates.getSettings();
        Location location = cm.getL();

        // Player has correct permissions
        if (player.hasPermission(cs.getPermission()) || cs.getPermission().equalsIgnoreCase("no permission"))
        {
            // Player has enough inventory spaces (as defined by value in Config.YML)
            if (isInventoryTooEmpty(cc, player))
            {
                // There is no cooldown or the previous cooldown is over
                CrateCooldownEvent cce = pdm.getCrateCooldownEventByCrates(crates);
                if (cce == null || cce.isCooldownOverAsBoolean())
                {
                    pm.setLastOpenedPlacedCrate(cm);

                    // SHIFT-CLICK OPEN
                    // If the animation needs to be skipped (shift click). Also required to be a static crate
                    if (skipAnimation && cs.getObtainType().equals(ObtainType.STATIC))
                    {
                        if(pm.isConfirming() || !((Boolean) SettingsValues.SHIFT_CLICK_CONFIRM.getValue(cc)))
                        {
                            if (cs.getAnimation().canExecuteFor(player, !crates.isMultiCrate()))
                            {
                                if(cc.getEconomyHandler().handleCheck(player, crates.getSettings().getCost(), true))
                                {
                                    Reward reward = cs.getRewards().getRandomReward();
                                    ArrayList<Reward> rewards = new ArrayList<>();
                                    rewards.add(reward);
                                    reward.giveRewardToPlayer(player);

                                    cs.getKeyItemHandler().takeKeyFromPlayer(player, false);
                                    new HistoryEvent(Utils.currentTimeParsed(), crates, rewards, true)
                                            .addTo(PlayerManager.get(cc, player).getPdm());
                                    new CrateCooldownEvent(crates, System.currentTimeMillis(), true).addTo(pdm);

                                    useCrateHelper(pm, cm, true, true, opened + 1);

                                    if (!hasSkipped)
                                    {
                                        crates.tick(location, cm, CrateState.OPEN, player, new ArrayList<Reward>());
                                        pm.setConfirming(false);
                                    }

                                    return true;
                                }
                                else
                                    return false;
                            }

                            if (!hasSkipped)
                                crates.getSettings().getAnimation().playFailToOpen(player, true, true);

                            return false;
                        }
                        else
                        {
                            pm.setConfirming(true);
                            Messages.CONFIRM_OPEN_ALL.msgSpecified(cc, player, new String[]{"%timeout%"}, new String[]{SettingsValues.CONFIRM_TIMEOUT.getValue(cc) + ""});
                        }
                        return false;
                    }
                    // NORMAL OPEN
                    else
                    {
                        if(pm.isConfirming() || !((Boolean) SettingsValues.CONFIRM_OPEN.getValue(cc)))
                        {
                            if(cc.getEconomyHandler().handleCheck(player, cs.getCost(), true))
                            {
                                if (cs.getAnimation().startAnimation(player, location, !crates.isMultiCrate(), false))
                                {
                                    // Crate isn't static but it ALSO isn't special handling (i.e. the BLOCK_ CrateTypes)
                                    if (!cs.getObtainType().equals(ObtainType.STATIC) && !cs.getCrateType().isSpecialDynamicHandling())
                                    {
                                        cm.delete();
                                        location.getBlock().setType(Material.AIR);
                                    }
                                    new CrateCooldownEvent(crates, System.currentTimeMillis(), true).addTo(pdm);
                                    Bukkit.broadcastMessage("Here");
                                    return !skipAnimation;
                                }
                                cc.getEconomyHandler().failSoReturn(player, cs.getCost());
                                pm.setLastOpenedPlacedCrate(null);
                                return false;
                            }
                            else
                            {
                                cs.getAnimation().playFailToOpen(player, false, true);
                                return false;
                            }
                        }
                        else
                        {
                            pm.setConfirming(true);
                            Messages.CONFIRM_OPEN.msgSpecified(cc, player, new String[]{"%timeout%"}, new String[]{SettingsValues.CONFIRM_TIMEOUT.getValue(cc) + ""});
                        }
                        return false;
                    }
                }
                cce.playFailure(pdm);
                return false;
            }
            Messages.INVENTORY_TOO_FULL.msgSpecified(cc, player);
            crates.getSettings().getAnimation().playFailToOpen(player, false, true);
            return false;
        }
        else
        {
            Messages.NO_PERMISSION_CRATE.msgSpecified(cc, player);
            crates.getSettings().getAnimation().playFailToOpen(player, false, true);
        }
        return false;
    }

    public boolean updateCooldown(PlayerManager pm)
    {

        boolean b = false;

        long ct = System.currentTimeMillis();
        long diff = ct - pm.getCmdCooldown();

        if (!(diff >= 1000) && !pm.getLastCooldown().equalsIgnoreCase("crate"))
        {
            Messages.WAIT_ONE_SECOND.msgSpecified(cc, pm.getP());

            b = true;
        }
        pm.setLastCooldown("crate");
        pm.setCmdCooldown(ct);
        return b;
    }

    public PlacedCrate createCrateAt(Crate crates, Location l)
    {
        PlacedCrate cm = PlacedCrate.get(cc, l);
        cm.setup(crates, true);

        return cm;
    }

    public static boolean isInventoryTooEmpty(SpecializedCrates cc, Player p)
    {
        return Utils.getOpenInventorySlots(p) >= ((Integer) SettingsValues.REQUIRED_SLOTS.getValue(cc));
    }
}
