package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class SpawnCrate extends SubCommand {
    public SpawnCrate() {
        super("spawncrate", 5, "/scrates spawncrate (crate) (world) X Y Z");
    }

    @Override
    public boolean run(SpecializedCrates cc, Commands cmds, String[] args) {
        if (Crate.exists(args[1])) {
            Crate toSpawn = Crate.getCrate(cc, args[1]);
            World world = Bukkit.getWorld(args[2]);

            if (world == null) {
                cmds.msgError(args[2] + " is not a valid world.");
                return false;
            }

            if (!Utils.isInt(args[5])) {
                cmds.msgError(args[5] + " is not a valid number.");
                return false;
            }
            if (!Utils.isInt(args[3])) {
                cmds.msgError(args[3] + " is not a valid number.");
                return false;
            }
            if (!Utils.isInt(args[4])) {
                cmds.msgError(args[4] + " is not a valid number.");
                return false;
            }

            int x = Integer.parseInt(args[3]);
            int y = Integer.parseInt(args[4]);
            int z = Integer.parseInt(args[5]);

            Location location = new Location(world, x, y, z);

            PlacedCrate cm = PlacedCrate.get(cc, location);
            cm.setup(toSpawn, true, false);
            cmds.msgSuccess("Created crate in world " + world.getName() + " at " + x + ", " + y + ", " + z);
        }
        return true;
    }
}
