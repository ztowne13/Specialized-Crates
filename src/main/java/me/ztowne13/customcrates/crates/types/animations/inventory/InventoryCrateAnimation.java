package me.ztowne13.customcrates.crates.types.animations.inventory;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.sounds.SoundData;
import me.ztowne13.customcrates.crates.types.animations.AnimationDataHolder;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimation;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public abstract class InventoryCrateAnimation extends CrateAnimation {
    private static final int REDRAW_TICKS = 1;
    private static final boolean FORCE_CLOSE = false;

    protected SoundData tickSound = null;
    protected String invName = "";

    public InventoryCrateAnimation(Crate crate, CrateAnimationType crateAnimationType) {
        super(crate, crateAnimationType);
    }


    /**
     * This is the function responsible for drawing all neccessary things at each state
     * to the inventory that aren't drawn by default.
     *
     * @param dataHolder The data holder that stores the player's animation runtime information.
     * @param update     A boolean to be able to identify whether to update certain features.
     */
    public abstract void tickInventory(InventoryAnimationDataHolder dataHolder, boolean update);

    /**
     * This function is responsible for drawing any static blocks that remain in the menu for
     * the whole animation.
     *
     * @param inventoryAnimationDataHolder The data holder that stores the player's animation runtime information.
     */
    public abstract void drawIdentifierBlocks(InventoryAnimationDataHolder inventoryAnimationDataHolder);

    /**
     * This information returns the filler block(s) that are displayed in drawFillers()
     *
     * @return An instance of an ItemBuilder of the filler item.
     */
    public abstract ItemBuilder getFiller();


    @Override
    public void handleClick(AnimationDataHolder dataHolder, int slot) {
        InventoryAnimationDataHolder inventoryAnimationDataHolder = (InventoryAnimationDataHolder) dataHolder;
        if (!inventoryAnimationDataHolder.getClickedSlots().contains(slot)) {
            inventoryAnimationDataHolder.getClickedSlots().add(slot);
        }
    }

    @Override
    public void handleKeyPress(AnimationDataHolder dataHolder, KeyType type) {
        super.handleKeyPress(dataHolder, type);

        if (type.equals(KeyType.ESC) && crate.getSettings().isCanFastTrack()) {
            dataHolder.setFastTrack(true, false);
        }
    }

    @Override
    public void tickAnimation(AnimationDataHolder dataHolder, boolean update) {
        InventoryAnimationDataHolder inventoryDataHolder = (InventoryAnimationDataHolder) dataHolder;

        tickInventory(inventoryDataHolder, update);

        drawIdentifierBlocks(inventoryDataHolder);
        drawInventory(inventoryDataHolder);

        tickFastTrack(inventoryDataHolder);
    }

    public void drawInventory(InventoryAnimationDataHolder dataHolder) {
        if (dataHolder.getTotalTicks() % REDRAW_TICKS != 0 || dataHolder.isFastTrack())
            return;

        if (FORCE_CLOSE) {
            dataHolder.getPlayer().closeInventory();
        }

        InventoryBuilder builder = dataHolder.getInventoryBuilder();

        // Open inventory if it has been closed
        if (!dataHolder.getPlayer().getOpenInventory().getTopInventory().getType().equals(InventoryType.CHEST) ||
                dataHolder.getPlayer().getOpenInventory().getTopInventory().getSize() != builder.getInv().getSize()) {
            dataHolder.getPlayer().openInventory(dataHolder.getInventoryBuilder().getInv());
        }

        // Redraw items into inventory if it is still open
        Inventory topInventory = dataHolder.getPlayer().getOpenInventory().getTopInventory();
        for (int i = 0; i < builder.getInv().getSize(); i++) {
            if (builder.getInv().getItem(i) == null || topInventory.getItem(i) == null ||
                    !topInventory.getItem(i).equals(builder.getInv().getItem(i))) {
                dataHolder.getPlayer().getOpenInventory().getTopInventory().setItem(i, builder.getInv().getItem(i));
            }
        }

        dataHolder.getPlayer().updateInventory();
    }

    public void drawFillers(InventoryAnimationDataHolder dataHolder, int glassUpdateTicks) {
        if (dataHolder.getTotalTicks() % glassUpdateTicks != 0 || dataHolder.isFastTrack())
            return;

        InventoryBuilder inv = dataHolder.getInventoryBuilder();

        for (int i = 0; i < inv.getInv().getSize(); i++) {
            inv.setItem(i, getFiller());
        }
    }

    public void playSound(InventoryAnimationDataHolder dataHolder) {
        if (getTickSound() != null && !dataHolder.isFastTrack()) {
            Sound sound = getTickSound().getSound().parseSound();
            if (sound != null) {
                dataHolder.getPlayer().playSound(dataHolder.getLocation(), sound, getTickSound().getVolume(),
                        getTickSound().getPitch());
            }
        }
    }

    public void tickFastTrack(InventoryAnimationDataHolder dataHolder) {
        if (dataHolder.isFastTrack()) {
            for (int i = 0; i < dataHolder.getInventoryBuilder().getSize(); i++) {
                handleClick(dataHolder, i);
            }
        }

        if (dataHolder.isFastTrackWaitTick()) {
            dataHolder.setFastTrack(true, true);
        }
    }

    public SoundData getTickSound() {
        return tickSound;
    }

    public void setTickSound(SoundData tickSound) {
        this.tickSound = tickSound;
    }

    public String getInvName() {
        return invName;
    }

    public void setInvName(String invName) {
        this.invName = invName;
    }
}
