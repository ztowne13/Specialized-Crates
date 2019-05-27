package me.ztowne13.customcrates.crates.crateaction;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.crates.types.CrateAnimation;
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

public class AttemptCrateUseAction extends CrateAction
{
    public AttemptCrateUseAction(CustomCrates cc, Player player, Location location)
    {
        super(cc, player, location);
    }

    @Override
    public boolean run()
    {
        PlayerManager pm = PlayerManager.get(cc, player);
        final PlayerDataManager pdm = pm.getPdm();

        // Has an item in hand
        if (Utils.hasItemInHand(player))
        {
            final Crate crates = CrateUtils.searchByCrate(player.getItemInHand());
            // Are they holding a crate
            if (!(crates == null))
            {
                if (location.getBlock().getRelative(BlockFace.UP).getType().equals(Material.AIR))
                {
                    // is crate enabled
                    if (CrateUtils.isCrateUsable(crates))
                    {
                        ObtainType ot = crates.getCs().getOt();

                        boolean b = true;

                        // The crate is a static crate
                        if (ot.equals(ObtainType.STATIC))
                        {
                            if (player.hasPermission("customcrates.place.bypass"))
                            {
                                Messages.BYPASS_BREAK_RESTRICTIONS.msgSpecified(cc, player);
                                b = false;
                            }
                            else
                            {
                                Messages.DENIED_USE_CRATE.msgSpecified(cc, player);
                            }
                        }

                        // Checking that a crate doesn't already exist
                        if (!PlacedCrate.crateExistsAt(cc, location))
                        {
                            // Checking Creative rules
                            if (!player.getGameMode().equals(GameMode.CREATIVE) || (Boolean) cc.getSettings().getConfigValues()
                                    .get("place-creative"))
                            {
                                // Does require key AND IS STATIC
                                if (ot.isStatic() || crates.getCs().isRequireKey() || crates.isMultiCrate())
                                {
                                    createCrateAt(crates, location);
                                }
                                // Doesn't require key AND ISN'T STATIC
                                else
                                {
                                    CrateSettings cs = crates.getCs();
                                    if (player.hasPermission(cs.getPermission()) ||
                                            cs.getPermission().equalsIgnoreCase("no permission"))
                                    {
                                        //Inventory has at least 1 space open
                                        if (isInventoryTooEmpty(cc, player))
                                        {
                                            CrateCooldownEvent cce = pdm.getCrateCooldownEventByCrates(crates);
                                            if (cce == null || cce.isCooldownOverAsBoolean())
                                            {
                                                Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        PlacedCrate cm = PlacedCrate.get(cc, location);
                                                        Crate crates = cm.getCrates();
                                                        CrateAnimation ch = crates.getCs().getCh();

                                                        cm.setup(crates, false);
                                                        ch.tick(player, location, CrateState.OPEN, !cm.getCrates().isMultiCrate());
                                                        ch.takeKeyFromPlayer(player, false);
                                                        cm.delete();

                                                        location.getBlock().setType(Material.AIR);
                                                        new CrateCooldownEvent(crates, System.currentTimeMillis(), true)
                                                                .addTo(pdm);
                                                    }
                                                }, 1);
                                                return true;
                                            }
                                            cce.playFailure(pdm);
                                            return true;
                                        }
                                        Messages.INVENTORY_TOO_FULL.msgSpecified(cc, player);
                                        crates.getCs().getCh().playFailToOpen(player, false);
                                        return true;
                                    }
                                    else
                                    {
                                        crates.getCs().getCh().playFailToOpen(player, false);
                                        Messages.NO_PERMISSION_CRATE.msgSpecified(cc, player);
                                    }
                                    return true;
                                }
                            }
                            else
                            {
                                crates.getCs().getCh().playFailToOpen(player, false);
                                Messages.DENY_CREATIVE_MODE.msgSpecified(cc, player);
                                return true;
                            }
                        }
                        else
                        {
                            ChatUtils.msgError(player, "There is, somehow, already a crate placed here.");
                        }

                        if (!b)
                        {
                            return false;
                        }
                    }
                    else
                    {
                        Messages.CRATE_DISABLED.msgSpecified(cc, player);
                        if (player.hasPermission("customcrates.admin") || player.isOp())
                        {
                            Messages.CRATE_DISABLED_ADMIN.msgSpecified(cc, player);
                        }
                        return false;
                    }
                }
                else
                {
                    Messages.DENIED_PLACE_LOCATION.msgSpecified(cc, player);
                    return true;
                }
            }
        }
        return false;
    }
}
