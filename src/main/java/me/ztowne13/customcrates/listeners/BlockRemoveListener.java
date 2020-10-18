package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

public class BlockRemoveListener implements Listener {
    SpecializedCrates cc;

    public BlockRemoveListener(SpecializedCrates cc) {
        this.cc = cc;
    }

    @EventHandler
    public void onBlockChange(BlockExplodeEvent e) {
        if (cc.isAllowTick())
            if ((Boolean) SettingsValue.EXPLODE_DYNAMIC.getValue(cc))
                e.blockList().removeIf(b -> PlacedCrate.crateExistsAt(b.getLocation()));
    }

    @EventHandler
    public void onPistonPush(BlockPistonExtendEvent e) {
        if (cc.isAllowTick() && shouldCancel(e.getBlocks(), e.getDirection()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (cc.isAllowTick())
            if ((Boolean) SettingsValue.EXPLODE_DYNAMIC.getValue(cc))
                e.blockList().removeIf(b -> PlacedCrate.crateExistsAt(b.getLocation()));
    }

    public boolean shouldCancel(List<Block> blocks, BlockFace bf) {
        boolean shouldCancel = false;
        if ((Boolean) SettingsValue.EXPLODE_DYNAMIC.getValue(cc)) {
            for (Block b : blocks) {
                if (PlacedCrate.crateExistsAt(b.getLocation()) ||
                        PlacedCrate.crateExistsAt(b.getRelative(bf).getLocation())) {
                    shouldCancel = true;
                    break;
                }
            }
        }
        return shouldCancel;
    }
}
