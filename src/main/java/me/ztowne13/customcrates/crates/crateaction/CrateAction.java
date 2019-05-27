package me.ztowne13.customcrates.crates.crateaction;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.players.data.events.CrateCooldownEvent;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class CrateAction
{

    CustomCrates cc;
    Player player;
    Location location;

    public CrateAction(CustomCrates cc, Player player, Location location)
    {
        this.cc = cc;
        this.player = player;
        this.location = location;
    }

    public abstract boolean run();

    public void useCrate(PlayerManager pm, PlacedCrate cm)
    {
        Player player = pm.getP();
        PlayerDataManager pdm = pm.getPdm();
        Crate crates = cm.getCrates();
        CrateSettings cs = crates.getCs();
        Location location = cm.getL();
        if (player.hasPermission(cs.getPermission()) || cs.getPermission().equalsIgnoreCase("no permission"))
        {
            if (isInventoryTooEmpty(cc, player))
            {
                CrateCooldownEvent cce = pdm.getCrateCooldownEventByCrates(crates);
                if (cce == null || cce.isCooldownOverAsBoolean())
                {
                    pm.setLastOpenedPlacedCrate(cm);
                    if (cs.getCh().tick(player, location, CrateState.OPEN, !crates.isMultiCrate()))
                    {
                        // Crate isn't static but it ALSO isn't special handling (i.e. the BLOCK_ CrateTypes)
                        if (!cs.getOt().equals(ObtainType.STATIC) && !cs.getCt().isSpecialDynamicHandling())
                        {
                            cm.delete();
                            location.getBlock().setType(Material.AIR);
                        }
                        new CrateCooldownEvent(crates, System.currentTimeMillis(), true).addTo(pdm);
                        return;
                    }
                    pm.setLastOpenedPlacedCrate(null);
                    return;
                }
                cce.playFailure(pdm);
                return;
            }
            Messages.INVENTORY_TOO_FULL.msgSpecified(cc, player);
        }
        else
        {
            Messages.NO_PERMISSION_CRATE.msgSpecified(cc, player);
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
