package me.ztowne13.customcrates.crates.crateaction;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.CrateUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class AttemptCrateUseAction extends CrateAction {
    protected final boolean isBlockPlace;

    public AttemptCrateUseAction(SpecializedCrates cc, Player player, Location location, boolean isBlockPlace) {
        super(cc, player, location);
        this.isBlockPlace = isBlockPlace;
    }

    @SuppressWarnings("deprecated")
    @Override
    public boolean run() {
        final PlayerManager pm = PlayerManager.get(instance, player);

        // Check item in hand
        if (!Utils.hasItemInHand(player)) {
            return false;
        }

        final Crate crates = CrateUtils.searchByCrate(player.getItemInHand());
        // Check holding crate
        if (crates == null) {
            if (CrateUtils.searchByCrate(player.getItemInHand(), true) != null && (
                    player.hasPermission("customcrates.place.bypass") || player.hasPermission("customcrates.admin") ||
                            player.hasPermission("specializedcrates.place.bypass") || player.hasPermission("specializedcrates.admin")
            )) {
                ChatUtils.msgError(player, "This crate is disabled either manually or due to an error and cannot be used.");
            }
            return false;
        }

        if (!location.getBlock().getRelative(BlockFace.UP).getType().equals(Material.AIR)) {
            Messages.DENIED_PLACE_LOCATION.msgSpecified(instance, player);
            return true;
        }

        // Check crate usable
        if (!CrateUtils.isCrateUsable(crates)) {
            Messages.CRATE_DISABLED.msgSpecified(instance, player);
            if (player.hasPermission("customcrates.admin") || player.hasPermission("specializedcrates.admin") || player.isOp()) {
                Messages.CRATE_DISABLED_ADMIN.msgSpecified(instance, player);
            }
            return false;
        }

        ObtainType ot = crates.getSettings().getObtainType();
        boolean bypass = false;

        // The crate is a static crate
        if (ot.equals(ObtainType.STATIC)) {
            if (player.hasPermission("customcrates.place.bypass") || player.hasPermission("customcrates.admin") ||
                    player.hasPermission("specializedcrates.place.bypass") || player.hasPermission("specializedcrates.admin")) {
                Messages.BYPASS_BREAK_RESTRICTIONS.msgSpecified(instance, player);
                bypass = true;
            } else {
                Messages.DENIED_USE_CRATE.msgSpecified(instance, player);
                return true;
            }
        }

        // Check if a crate already exists
        if (PlacedCrate.crateExistsAt(location)) {
            ChatUtils.msgError(player, "There is, somehow, already a crate placed here.");
            return bypass;
        }

        // Check Creative rules
        if (player.getGameMode().equals(GameMode.CREATIVE) && instance.getSettings().getConfigValues().get("place-creative").equals(Boolean.FALSE)) {
            crates.getSettings().getCrateAnimation().playFailToOpen(player, false, true);
            Messages.DENY_CREATIVE_MODE.msgSpecified(instance, player);
            return true;
        }

        final PlacedCrate placedCrate = createCrateAt(crates, location);
        // For DYNAMIC crates that don't require a key, open immediately.
        if (crates.getSettings().getObtainType().equals(ObtainType.DYNAMIC) && !crates.getSettings().isRequireKey()) {
            Bukkit.getScheduler().runTaskLater(instance, () -> useCrate(pm, placedCrate, false), 1);
        }

        return false;
    }
}
