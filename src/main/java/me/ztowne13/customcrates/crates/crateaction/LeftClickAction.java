package me.ztowne13.customcrates.crates.crateaction;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.CrateUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LeftClickAction extends CrateAction
{
    public LeftClickAction(SpecializedCrates cc, Player player, Location location)
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

            // Code for deleting crates using command or the shift + click shortcut
            if (pm.isDeleteCrate() ||
                    (player.isSneaking() && player.hasPermission("customcrates.admin") && player.getGameMode().equals(GameMode.CREATIVE)))
            {
                cm.delete();
                pm.setDeleteCrate(false);
                Messages.SUCCESS_DELETE.msgSpecified(cc, player, new String[]{"%crate%"}, new String[]{cm.getCrates().getDisplayName()});
                return true;
            }

            // Preventing crates from being broken and displaying reward menu if need be
            if (CrateUtils.isCrateUsable(cm))
            {
                if (!pm.isDeleteCrate() && (Boolean) SettingsValues.REWARD_DISPLAY_ENABLED.getValue(cc))
                {
                    if (!cm.getCrates().isMultiCrate())
                        cm.getCrates().getSettings().getDisplayer().openFor(player);
                    return true;
                }
            }
            else // Crate is disabled
            {
                Messages.CRATE_DISABLED.msgSpecified(cc, player);
                if (player.hasPermission("customcrates.admin") || player.isOp())
                {
                    Messages.CRATE_DISABLED_ADMIN.msgSpecified(cc, player);
                }
                return true;
            }

            return true;
        }

        return false;
    }
}
