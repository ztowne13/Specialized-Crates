package me.ztowne13.customcrates.crates.crateaction;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.CrateUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class AttemptCrateUseAction extends CrateAction
{
    public AttemptCrateUseAction(SpecializedCrates cc, Player player, Location location)
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

                    if(!cc.getAntiFraudSQLHandler().isAuthenticated())
                    {
                        ChatUtils.msgError(pm.getP(), "This plugin has been blacklisted because it has been assumed to be on more servers than just the" +
                                "person who purchased this plugin. If you believe this is in error, please try re-downloading the plugin" +
                                " (this does not mean deleting the plugin files, just the .jar) and try again. If the issue persists and" +
                                "you still believe it is in error, please contact the plugin author, Ztowne13.");
                        return false;
                    }

                    // is crate enabled
                    if (CrateUtils.isCrateUsable(crates))
                    {
                        ObtainType ot = crates.getSettings().getObtainType();

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
                                createCrateAt(crates, location);
                            }
                            else
                            {
                                crates.getSettings().getAnimation().playFailToOpen(player, false, true);
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
