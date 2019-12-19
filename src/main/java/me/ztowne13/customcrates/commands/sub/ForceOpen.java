package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ForceOpen extends SubCommand
{
    public ForceOpen()
    {
        super("forceopen", 3, "Usage: /scrates forceopen [crate] {[player], all}");
    }

    @Override
    public boolean run(SpecializedCrates cc, Commands cmds, String[] args)
    {
        if(args.length == 3)
        {
            String crateName = args[1];
            String playerName = args[2];

            if(Crate.exists(crateName))
            {
                Crate crate = Crate.getCrate(cc, args[1]);

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
                if(!offlinePlayer.isOnline())
                {
                    try
                    {
                        UUID uuid = UUID.fromString(playerName);
                        offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    }
                    catch(Exception exc)
                    {

                    }
                }

                if(offlinePlayer.isOnline())
                {
                    Player player = offlinePlayer.getPlayer();
                    run(crate, player);
                    return true;
                }

                if(playerName.equalsIgnoreCase("all"))
                {
                    for(Player player : Bukkit.getOnlinePlayers())
                        run(crate, player);

                    return true;
                }

                cmds.msgError(playerName + " is not a valid online player's name or UUID.");
                return false;
            }
            cmds.msgError(crateName + " is not a valid crate name.");
        }

        return false;
    }

    public void run(Crate crate, Player player)
    {
        crate.getCs().getCh().tick(player, player.getLocation(), CrateState.OPEN, !crate.isMultiCrate(), true);
    }
}
