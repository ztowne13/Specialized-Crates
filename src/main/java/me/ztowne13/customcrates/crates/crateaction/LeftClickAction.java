package me.ztowne13.customcrates.crates.crateaction;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.CrateUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LeftClickAction extends CrateAction {
    public LeftClickAction(SpecializedCrates cc, Player player, Location location) {
        super(cc, player, location);
    }

    @Override
    public boolean run() {
        if (!PlacedCrate.crateExistsAt(location)) {
            return false;
        }

        PlayerManager playerManager = PlayerManager.get(instance, player);
        PlacedCrate placedCrate = PlacedCrate.get(instance, location);

        // Code for deleting crates using command or the shift + click shortcut
        if (playerManager.isDeleteCrate() ||
                (player.isSneaking() && (player.hasPermission("customcrates.admin") ||
                        player.hasPermission("specializedcrates.admin")) &&
                        player.getGameMode().equals(GameMode.CREATIVE))) {
            placedCrate.delete();
            playerManager.setDeleteCrate(false);
            Messages.SUCCESS_DELETE
                    .msgSpecified(instance, player, new String[]{"%crate%"}, new String[]{placedCrate.getCrate().getName()});
            return true;
        }

        // Preventing crates from being broken and displaying reward menu if need be
        if (!CrateUtils.isCrateUsable(placedCrate)) {
            Messages.CRATE_DISABLED.msgSpecified(instance, player);
            if (player.hasPermission("customcrates.admin") || player.hasPermission("specializedcrates.admin") ||
                    player.isOp()) {
                Messages.CRATE_DISABLED_ADMIN.msgSpecified(instance, player);
            }
            return true;
        }

        if (!playerManager.isDeleteCrate() && SettingsValue.REWARD_DISPLAY_ENABLED.getValue(instance).equals(Boolean.TRUE)) {
            if (!placedCrate.getCrate().isMultiCrate())
                placedCrate.getCrate().getSettings().getDisplayer().openFor(player);
            return true;
        }

        return true;
    }
}
