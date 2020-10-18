package me.ztowne13.customcrates.crates.types.animations.inventory;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.animations.AnimationDataHolder;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.entity.Player;

import java.util.Random;

public class MenuAnimation extends InventoryCrateAnimation {
    private final Random random = new Random();
    private int minRewards;
    private int maxRewards;
    private int inventoryRows = 0;

    public MenuAnimation(Crate crate) {
        super(crate, CrateAnimationType.INV_MENU);
    }

    @Override
    public void tickInventory(InventoryAnimationDataHolder dataHolder, boolean update) {
        MenuAnimationDataHolder madh = (MenuAnimationDataHolder) dataHolder;
        if (dataHolder.getCurrentState() == AnimationDataHolder.State.WAITING) {
            updateAndDrawRewards(madh);
        }
    }

    @Override
    public void drawIdentifierBlocks(InventoryAnimationDataHolder inventoryAnimationDataHolder) {
        // IGNORED
    }

    @Override
    public ItemBuilder getFiller() {
        return null;
    }

    @Override
    public boolean updateTicks(AnimationDataHolder dataHolder) {
        if (dataHolder.getCurrentState() == AnimationDataHolder.State.ENDING) {
            dataHolder.setWaitingTicks(dataHolder.getWaitingTicks() + 1);
        }
        return false;
    }

    @Override
    public void checkStateChange(AnimationDataHolder dataHolder, boolean update) {
        switch (dataHolder.getCurrentState()) {
            case PLAYING:
                dataHolder.setCurrentState(AnimationDataHolder.State.WAITING);
                break;
            case WAITING:
                dataHolder.setCurrentState(AnimationDataHolder.State.ENDING);
                break;
            case ENDING:
                if (dataHolder.getWaitingTicks() == 100) {
                    dataHolder.setCurrentState(AnimationDataHolder.State.COMPLETED);
                }
                break;
            default:
                break;
        }
    }

    public void updateAndDrawRewards(MenuAnimationDataHolder mdh) {
        int randomNumber = 0;
        if (getMaxRewards() > getMinRewards()) {
            randomNumber = this.random.nextInt(getMaxRewards() - getMinRewards());
        }

        int amountOfRewards = randomNumber + getMinRewards();

        for (int i = 0; i < amountOfRewards; i++) {
            int slot = Utils.getRandomNumberExcluding((getInventoryRows() * 9) - 1, mdh.getUsedNumbers());
            mdh.getUsedNumbers().add(slot);

            Reward reward = getCrate().getSettings().getReward().getRandomReward();
            mdh.getDisplayedRewards().add(reward);

            mdh.getInventoryBuilder().setItem(slot, reward.getDisplayBuilder());
        }
    }

    @Override
    public void endAnimation(AnimationDataHolder dataHolder) {
        MenuAnimationDataHolder mdh = (MenuAnimationDataHolder) dataHolder;
        Player player = mdh.getPlayer();

        finishAnimation(player, mdh.getDisplayedRewards(), null);
        getCrate().tick(mdh.getLocation(), CrateState.OPEN, player, mdh.getDisplayedRewards());
    }

    @Override
    public void loadDataValues(StatusLogger statusLogger) {
        invName = fileHandler.getFileDataLoader()
                .loadString(prefix + "inv-name", getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_MENU_INVNAME_SUCCESS);

        minRewards = fileHandler.getFileDataLoader()
                .loadInt(prefix + "minimum-rewards", 1, getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_MENU_MINIMUM_REWARDS_SUCCESS,
                        StatusLoggerEvent.ANIMATION_MENU_MINIMUM_REWARDS_INVALID);

        maxRewards = fileHandler.getFileDataLoader()
                .loadInt(prefix + "maximum-rewards", 4, getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_MENU_MAXIMUM_REWARDS_SUCCESS,
                        StatusLoggerEvent.ANIMATION_MENU_MAXIMUM_REWARDS_INVALID);

        inventoryRows = fileHandler.getFileDataLoader()
                .loadInt(prefix + "inventory-rows", 3, getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_MENU_INVENTORY_ROWS_SUCCESS,
                        StatusLoggerEvent.ANIMATION_MENU_INVENTORY_ROWS_INVALID);
    }

    @Override
    public String getInvName() {
        return invName;
    }

    @Override
    public void setInvName(String invName) {
        this.invName = invName;
    }

    public int getMinRewards() {
        return minRewards;
    }

    public void setMinRewards(int minRewards) {
        this.minRewards = minRewards;
    }

    public int getMaxRewards() {
        return maxRewards;
    }

    public void setMaxRewards(int maxRewards) {
        this.maxRewards = maxRewards;
    }

    public int getInventoryRows() {
        return inventoryRows;
    }

    public void setInventoryRows(int inventoryRows) {
        this.inventoryRows = inventoryRows;
    }


}
