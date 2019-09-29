package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.interfaces.igc.crates.IGCCratesMain;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.TreeSet;

/**
 * Created by ztowne13 on 7/4/16.
 */
public class Edit extends SubCommand
{
    public Edit()
    {
        super("edit", 1, "");
    }

    @Override
    public boolean run(SpecializedCrates cc, Commands cmds, String[] args)
    {
        if (cmds.getCmdSender() instanceof Player)
        {
            Player p = (Player) cmds.getCmdSender();

            if (args.length == 1)
            {
                TreeSet<Material> set = new TreeSet<>();
                set.add(Material.AIR);
                if (!(p.getTargetBlock(set, 20) == null))
                {
                    Block b = p.getTargetBlock(set, 20);
                    if (PlacedCrate.crateExistsAt(cc, b.getLocation()))
                    {
                        PlacedCrate pc = PlacedCrate.get(cc, b.getLocation());
                        new IGCCratesMain(cc, p, null, pc.getCrates()).open();
                        ChatUtils.msgSuccess(p, "Opening config menu for crate: " + pc.getCrates().getName());
                        return true;
                    }
                }
                ChatUtils.msgError(p, "You are not looking at a crate to open!");
            }
            else
            {
                if (Crate.crateAlreadyExist(args[1]))
                {
                    Crate crate = Crate.getCrate(cc, args[1]);
                    new IGCCratesMain(cc, p, null, crate).open();
                    ChatUtils.msgSuccess(p, "Opening config menu for crate: " + crate.getName());
                }
                ChatUtils.msgError(p, args[1] + " is not a valid crate name.");
            }
        }
        else
        {
            cmds.msg("This command can only be run from in-game.");
        }
        return false;
    }
}
