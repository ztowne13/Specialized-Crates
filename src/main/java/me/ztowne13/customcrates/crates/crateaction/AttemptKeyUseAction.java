package me.ztowne13.customcrates.crates.crateaction;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.CrateUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AttemptKeyUseAction extends CrateAction {
    public AttemptKeyUseAction(SpecializedCrates cc, Player player, Location location) {
        super(cc, player, location);
    }

    @Override
    public boolean run() {
        PlayerManager pm = PlayerManager.get(instance, player);

        if (!PlacedCrate.crateExistsAt(location)) {
            return false;
        }

        long curTime = System.currentTimeMillis();
        if (curTime - pm.getLastClickedCrateTime() < 500) {
            return true;
        }
        pm.setLastClickedCrateTime(System.currentTimeMillis());

        // For SQL, to make sure the player data is loaded
        if (!pm.getPlayerDataManager().isLoaded()) {
            Messages.LOADING_FROM_DATABASE.msgSpecified(instance, player);
            return true;
        }

        PlacedCrate cm = PlacedCrate.get(instance, location);
        Crate crate = cm.getCrate();

        if (crate.isMultiCrate() && !CrateUtils.isCrateUsable(cm)) {
            Messages.CRATE_DISABLED.msgSpecified(instance, player);
            if (player.hasPermission("customcrates.admin") || player.isOp()) {
                Messages.CRATE_DISABLED_ADMIN.msgSpecified(instance, player);
            }
            return true;
        }

        Crate crates = cm.getCrate();

        if (crates.isMultiCrate()) {
            if (pm.isInCrate()) {
                return true;
            }

            crate.getSettings().getMultiCrateSettings().openFor(player, cm);
            return true;
        } else if (!player.getGameMode().equals(GameMode.CREATIVE) || instance.getSettings().getConfigValues().get("open-creative").equals(Boolean.TRUE)) {
            useCrate(pm, cm, player.isSneaking() && (Boolean) SettingsValue.SHIFT_CLICK_OPEN_ALL.getValue(instance));
            return true;
        } else {
            crate.getSettings().getCrateAnimation().playFailToOpen(player, false, true);
            Messages.DENY_CREATIVE_MODE.msgSpecified(instance, player);
            return true;
        }
    }
}
