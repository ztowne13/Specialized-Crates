package me.ztowne13.customcrates.crates.types.animations.inventory;

import me.ztowne13.customcrates.crates.types.animations.AnimationDataHolder;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimation;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public abstract class InventoryAnimationDataHolder extends AnimationDataHolder {
    private final ArrayList<Integer> clickedSlots;
    private BukkitRunnable runnable;
    private InventoryBuilder inventoryBuilder;

    public InventoryAnimationDataHolder(Player player, Location location, CrateAnimation crateAnimation, int slots) {
        super(player, location, crateAnimation);

        String inventoryName = getProperInventoryName(crateAnimation);
        setInventoryBuilder(new InventoryBuilder(player, slots, inventoryName));

        clickedSlots = new ArrayList<>();
    }

    @Override
    public InventoryCrateAnimation getCrateAnimation() {
        return (InventoryCrateAnimation) super.getCrateAnimation();
    }

    public String getProperInventoryName(CrateAnimation crateAnimation) {
        String inventoryName;
        if (getCrateAnimation().getCrate().getSettings().getCrateInventoryName() == null) {
            inventoryName = getCrateAnimation().getInvName();
        } else {
            inventoryName = getCrateAnimation().getCrate().getSettings().getCrateInventoryName();
        }

        inventoryName = inventoryName.replace("%crate%", crateAnimation.getCrate().getDisplayName());

        if (inventoryName.length() > 31) {
            inventoryName = inventoryName.substring(0, 31);
        }

        return inventoryName;
    }

    public List<Integer> getClickedSlots() {
        return clickedSlots;
    }

    public BukkitRunnable getRunnable() {
        return runnable;
    }

    public void setRunnable(BukkitRunnable runnable) {
        this.runnable = runnable;
    }

    public InventoryBuilder getInventoryBuilder() {
        return inventoryBuilder;
    }

    public void setInventoryBuilder(InventoryBuilder inventoryBuilder) {
        this.inventoryBuilder = inventoryBuilder;
    }
}
