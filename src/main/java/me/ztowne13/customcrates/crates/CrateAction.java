package me.ztowne13.customcrates.crates;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.crates.options.rewards.RewardDisplayer;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.players.data.events.CrateCooldownEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.CrateUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class CrateAction
{
    public enum Types
    {
        USE_KEY,

        USE_CRATE,

        LEFT_CLICK,
    }

    CustomCrates cc;
    CrateAction.Types ca;

    public CrateAction(CustomCrates cc, CrateAction.Types ca)
    {
        this.ca = ca;
        this.cc = cc;
    }

    public boolean completeAction(Player p, Location l)
    {
        if (ca.equals(Types.USE_CRATE)) // Place crate
        {
            return attempt_use_crate(p, l);
        }
        else if (ca.equals(Types.USE_KEY))
        {
            return attempt_use_key(p, l);
        }
        else if (ca.equals(Types.LEFT_CLICK))
        {
            return left_click(p, l);
        }
        return false;
    }

    public boolean attempt_use_key(Player p, Location l)
    {
        PlayerManager pm = PlayerManager.get(cc, p);

        if (PlacedCrate.crateExistsAt(cc, l))
        {
            PlacedCrate cm = PlacedCrate.get(cc, l);
            Crate crates = cm.getCrates();
            if (crates.isMultiCrate())
            {
                crates.getCs().getCmci().getInventory(p, crates.getCs().getCrateInventoryName() == null ? crates.getName() :
                        crates.getCs().getCrateInventoryName(), true).open();
                pm.setLastOpenCrate(l);
                pm.setLastOpenedPlacedCrate(cm);
                pm.openCrate(crates);
                return true;
            }
            else if (!p.getGameMode().equals(GameMode.CREATIVE) ||
                    (Boolean) cc.getSettings().getConfigValues().get("open-creative"))
            {
                if (CrateUtils.isCrateUsable(cm))
                {
                    useCrate(pm, cm);
                    return true;
                }
                else
                {
                    Messages.CRATE_DISABLED.msgSpecified(cc, p);
                    if (p.hasPermission("customcrates.admin") || p.isOp())
                    {
                        Messages.CRATE_DISABLED_ADMIN.msgSpecified(cc, p);
                    }
                    return true;
                }
            }
            else
            {
                crates.getCs().getCh().playFailToOpen(p, false);
                Messages.DENY_CREATIVE_MODE.msgSpecified(cc, p);
                return true;
            }
        }
        return false;
    }

    public boolean attempt_use_crate(final Player p, final Location l)
    {
        PlayerManager pm = PlayerManager.get(cc, p);
        final PlayerDataManager pdm = pm.getPdm();

        if (Utils.hasItemInHand(p)) // Has an item in hand
        {
            final Crate crates = CrateUtils.searchByCrate(p.getItemInHand());
            if (!(crates == null)) // Are they holding a crate
            {
                if (l.getBlock().getRelative(BlockFace.UP).getType().equals(Material.AIR))
                {
                    if (CrateUtils.isCrateUsable(crates))
                    { // is crate enabled
                        ObtainType ot = crates.getCs().getOt();

                        boolean b = true;

                        if (ot.equals(ObtainType.STATIC)) // The crate is a static crate
                        {
                            if (p.hasPermission("customcrates.place.bypass"))
                            {
                                Messages.BYPASS_BREAK_RESTRICTIONS.msgSpecified(cc, p);
                                b = false;
                            }
                            else
                            {
                                Messages.DENIED_USE_CRATE.msgSpecified(cc, p);
                            }
                        }

                        if (!PlacedCrate.crateExistsAt(cc, l)) // Checking that a crate doesn't already exist
                        {
                            if (!p.getGameMode().equals(GameMode.CREATIVE) || (Boolean) cc.getSettings().getConfigValues()
                                    .get("place-creative")) // Checking Creative rules
                            {
                                if (ot.isStatic() || crates.getCs().isRequireKey() ||
                                        crates.isMultiCrate()) // Does require key
                                {
                                    createCrateAt(crates, l);
                                }
                                else // Doesn't require key
                                {
                                    if (p.hasPermission(crates.getCs().getPermission()) ||
                                            crates.getCs().getPermission().equalsIgnoreCase("no permission"))
                                    {
                                        if (isInventoryTooEmpty(cc, p)) //Inventory has at least 1 space open
                                        {
                                            CrateCooldownEvent cce = pdm.getCrateCooldownEventByCrates(crates);
                                            if (cce == null || cce.isCooldownOverAsBoolean())
                                            {
                                                Bukkit.getScheduler().runTaskLater(cc, new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        PlacedCrate cm = PlacedCrate.get(cc, l);
                                                        cm.setup(crates, false);
                                                        cm.getCrates().getCs().getCh()
                                                                .tick(p, l, CrateState.OPEN, !cm.getCrates().isMultiCrate());
                                                        cm.getCrates().getCs().getCh().takeKeyFromPlayer(p, false);
                                                        cm.delete();
                                                        l.getBlock().setType(Material.AIR);
                                                        new CrateCooldownEvent(crates, System.currentTimeMillis(), true)
                                                                .addTo(pdm);
                                                    }
                                                }, 1);
                                                return true;
                                            }
                                            cce.playFailure(pdm);
                                            return true;
                                        }
                                        Messages.INVENTORY_TOO_FULL.msgSpecified(cc, p);
                                        crates.getCs().getCh().playFailToOpen(p, false);
                                        return true;
                                    }
                                    else
                                    {
                                        crates.getCs().getCh().playFailToOpen(p, false);
                                        Messages.NO_PERMISSION_CRATE.msgSpecified(cc, p);
                                    }
                                    return true;
                                }
                            }
                            else
                            {
                                crates.getCs().getCh().playFailToOpen(p, false);
                                Messages.DENY_CREATIVE_MODE.msgSpecified(cc, p);
                                return true;
                            }
                        }
                        else
                        {
                            ChatUtils.msgError(p, "There is, somehow, already a crate placed here.");
                        }

                        if (!b)
                        {
                            return false;
                        }
                    }
                    else
                    {
                        Messages.CRATE_DISABLED.msgSpecified(cc, p);
                        if (p.hasPermission("customcrates.admin") || p.isOp())
                        {
                            Messages.CRATE_DISABLED_ADMIN.msgSpecified(cc, p);
                        }
                        return false;
                    }
                }
                else
                {
                    Messages.DENIED_PLACE_LOCATION.msgSpecified(cc, p);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean left_click(Player p, Location l)
    {
        PlayerManager pm = PlayerManager.get(cc, p);
        if (PlacedCrate.crateExistsAt(cc, l))
        {
            PlacedCrate cm = PlacedCrate.get(cc, l);

            // Code for deleting crates using command or the shift + click shortcut
            if (pm.isDeleteCrate() ||
                    (p.isSneaking() && p.hasPermission("customcrates.admin") && p.getGameMode().equals(GameMode.CREATIVE)))
            {
                cm.delete();
                pm.setDeleteCrate(false);
                Messages.SUCCESS_DELETE.msgSpecified(cc, p, new String[]{"%crate%"}, new String[]{cm.getCrates().getName()});
                return true;
            }

            // Preventing crates from being broken and displaying reward menu if need be
            if (CrateUtils.isCrateUsable(cm))
            {

                if (!pm.isDeleteCrate() && (Boolean) SettingsValues.REWARD_DISPLAY_ENABLED.getValue(cc))
                {
                    if (!cm.getCrates().isMultiCrate())
                    {
                        new RewardDisplayer(cm.getCrates()).openFor(p);
                    }
                    return true;
                }
            }
            else // Crate is disabled
            {
                Messages.CRATE_DISABLED.msgSpecified(cc, p);
                if (p.hasPermission("customcrates.admin") || p.isOp())
                {
                    Messages.CRATE_DISABLED_ADMIN.msgSpecified(cc, p);
                }
                return true;
            }
        }

        return false;
    }

    public void useCrate(PlayerManager pm, PlacedCrate cm)
    {
        Player p = pm.getP();
        PlayerDataManager pdm = pm.getPdm();
        CrateSettings cs = cm.getCrates().getCs();
        Location l = cm.getL();
        if (p.hasPermission(cs.getPermission()) || cs.getPermission().equalsIgnoreCase("no permission"))
        {
            if (isInventoryTooEmpty(cc, p))
            {
                CrateCooldownEvent cce = pdm.getCrateCooldownEventByCrates(cs.getCrates());
                if (cce == null || cce.isCooldownOverAsBoolean())
                {
                    pm.setLastOpenedPlacedCrate(cm);
                    if (cm.getCrates().getCs().getCh().tick(p, l, CrateState.OPEN, !cm.getCrates().isMultiCrate()))
                    {
                        if (!cs.getOt().equals(ObtainType.STATIC))
                        {
                            cm.delete();
                            l.getBlock().setType(Material.AIR);
                        }
                        new CrateCooldownEvent(cs.getCrates(), System.currentTimeMillis(), true).addTo(pdm);
                        return;
                    }
                    pm.setLastOpenedPlacedCrate(null);
                    return;
                }
                cce.playFailure(pdm);
                return;
            }
            Messages.INVENTORY_TOO_FULL.msgSpecified(cc, p);
            return;
        }
        else
        {
            Messages.NO_PERMISSION_CRATE.msgSpecified(cc, p);
        }
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


    public void createCrateAt(Crate crates, Location l)
    {
        PlacedCrate cm = PlacedCrate.get(cc, l);
        cm.setup(crates, true);
    }

    public static boolean isInventoryTooEmpty(CustomCrates cc, Player p)
    {
        return Utils.getOpenInventorySlots(p) >= ((Integer) SettingsValues.REQUIRED_SLOTS.getValue(cc));
    }
}
