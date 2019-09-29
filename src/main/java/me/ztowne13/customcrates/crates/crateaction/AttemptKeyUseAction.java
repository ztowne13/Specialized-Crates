package me.ztowne13.customcrates.crates.crateaction;


import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.CrateUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AttemptKeyUseAction extends CrateAction
{
    public AttemptKeyUseAction(SpecializedCrates cc, Player player, Location location)
    {
        super(cc, player, location);
    }

    @Override
    public boolean run()
    {
        PlayerManager pm = PlayerManager.get(cc, player);

        if (PlacedCrate.crateExistsAt(cc, location))
        {
            PlacedCrate cm = PlacedCrate.get(cc, location);
            Crate crates = cm.getCrates();
            if (crates.isMultiCrate())
            {
                crates.getCs().getCmci()
                        .getInventory(player, crates.getCs().getCrateInventoryName() == null ? crates.getName() :
                                crates.getCs().getCrateInventoryName(), true).open();
                pm.setLastOpenCrate(location);
                pm.setLastOpenedPlacedCrate(cm);
                pm.openCrate(crates);
                return true;
            }
            else if (!player.getGameMode().equals(GameMode.CREATIVE) ||
                    (Boolean) cc.getSettings().getConfigValues().get("open-creative"))
            {
                if (CrateUtils.isCrateUsable(cm))
                {
                    useCrate(pm, cm, player.isSneaking() && (Boolean) SettingsValues.SHIFT_CLICK_OPEN_ALL.getValue(cc));
                    return true;
                }
                else
                {
                    Messages.CRATE_DISABLED.msgSpecified(cc, player);
                    if (player.hasPermission("customcrates.admin") || player.isOp())
                    {
                        Messages.CRATE_DISABLED_ADMIN.msgSpecified(cc, player);
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
        return false;
    }
}
