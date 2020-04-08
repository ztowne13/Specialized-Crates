package me.ztowne13.customcrates.crates.crateaction;


import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
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
            if(!cc.getAntiFraudSQLHandler().isAuthenticated())
            {
                ChatUtils.msgError(pm.getP(), "This plugin has been blacklisted because it has been assumed to be on more servers than just the" +
                        "person who purchased this plugin. If you believe this is in error, please try re-downloading the plugin" +
                        " (this does not mean deleting the plugin files, just the .jar) and try again. If the issue persists and" +
                        "you still believe it is in error, please contact the plugin author, Ztowne13.");
                return false;
            }

            // For SQL, to make sure the player data is loaded
            if(!pm.getPdm().isLoaded())
            {
                Messages.LOADING_FROM_DATABASE.msgSpecified(cc, player);
                return false;
            }

            PlacedCrate cm = PlacedCrate.get(cc, location);
            Crate crates = cm.getCrates();
            if (crates.isMultiCrate())
            {
                if(pm.isInCrate())
                {
                    return false;
                }

                crates.getSettings().getMultiCrateSettings()
                        .getInventory(player, crates.getSettings().getCrateInventoryName() == null ? crates.getName() :
                                crates.getSettings().getCrateInventoryName(), true).open();
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
                crates.getSettings().getAnimation().playFailToOpen(player, false, true);
                Messages.DENY_CREATIVE_MODE.msgSpecified(cc, player);
                return true;
            }
        }
        return false;
    }
}
