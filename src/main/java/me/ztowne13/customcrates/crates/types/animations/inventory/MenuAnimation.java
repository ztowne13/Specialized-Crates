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

public class MenuAnimation extends InventoryCrateAnimation
{
    protected int minRewards, maxRewards, inventoryRows = 0;

    public MenuAnimation(Crate crate)
    {
        super(crate, CrateAnimationType.INV_MENU);
    }

    @Override
    public void tickInventory(InventoryAnimationDataHolder dataHolder, boolean update)
    {
        MenuAnimationDataHolder madh = (MenuAnimationDataHolder) dataHolder;
        switch (dataHolder.getCurrentState())
        {
            case WAITING:
                updateAndDrawRewards(madh);
        }
    }

    @Override
    public void drawIdentifierBlocks(InventoryAnimationDataHolder cdh)
    {
    }

    @Override
    public ItemBuilder getFiller()
    {
        return null;
    }

    @Override
    public boolean updateTicks(AnimationDataHolder dataHolder)
    {
        switch (dataHolder.getCurrentState())
        {
            case ENDING:
                dataHolder.setWaitingTicks(dataHolder.getWaitingTicks() + 1);
                break;
        }
        return false;
    }

    @Override
    public void checkStateChange(AnimationDataHolder dataHolder, boolean update)
    {
        switch (dataHolder.getCurrentState())
        {
            case PLAYING:
                dataHolder.setCurrentState(AnimationDataHolder.State.WAITING);
                break;
            case WAITING:
                dataHolder.setCurrentState(AnimationDataHolder.State.ENDING);
                break;
            case ENDING:
                if (dataHolder.getWaitingTicks() == 100)
                {
                    dataHolder.setCurrentState(AnimationDataHolder.State.COMPLETED);
                }
        }
    }

    public void updateAndDrawRewards(MenuAnimationDataHolder mdh)
    {
        Random r = new Random();

        int amountOfRewards = r.nextInt(getMaxRewards() - getMinRewards()) + getMinRewards();

        for (int i = 0; i < amountOfRewards; i++)
        {
            int slot = Utils.getRandomNumberExcluding((getInventoryRows() * 9) - 1, mdh.getUsedNumbers());
            mdh.getUsedNumbers().add(slot);

            Reward reward = getCrate().getSettings().getRewards().getRandomReward();
            mdh.getDisplayedRewards().add(reward);

            mdh.getInventoryBuilder().setItem(slot, reward.getDisplayBuilder());
        }
    }

    @Override
    public void endAnimation(AnimationDataHolder dataHolder)
    {
        MenuAnimationDataHolder mdh = (MenuAnimationDataHolder) dataHolder;
        Player player = mdh.getPlayer();

        finishAnimation(player, mdh.getDisplayedRewards(), true, null);
        getCrate().tick(mdh.getLocation(), CrateState.OPEN, player, mdh.getDisplayedRewards());
    }

    @Override
    public void loadDataValues(StatusLogger sl)
    {
        invName = fu.getFileDataLoader()
                .loadString(prefix + "inv-name", getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_MENU_INVNAME_SUCCESS);

        minRewards = fu.getFileDataLoader()
                .loadInt(prefix + "minimum-rewards", 1, getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_MENU_MINIMUM_REWARDS_SUCCESS,
                        StatusLoggerEvent.ANIMATION_MENU_MINIMUM_REWARDS_INVALID);

        maxRewards = fu.getFileDataLoader()
                .loadInt(prefix + "maximum-rewards", 4, getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_MENU_MAXIMUM_REWARDS_SUCCESS,
                        StatusLoggerEvent.ANIMATION_MENU_MAXIMUM_REWARDS_INVALID);

        inventoryRows = fu.getFileDataLoader()
                .loadInt(prefix + "inventory-rows", 3, getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_MENU_INVENTORY_ROWS_SUCCESS,
                        StatusLoggerEvent.ANIMATION_MENU_INVENTORY_ROWS_INVALID);
    }

    public String getInvName()
    {
        return invName;
    }

    public void setInvName(String invName)
    {
        this.invName = invName;
    }

    public int getMinRewards()
    {
        return minRewards;
    }

    public void setMinRewards(int minRewards)
    {
        this.minRewards = minRewards;
    }

    public int getMaxRewards()
    {
        return maxRewards;
    }

    public void setMaxRewards(int maxRewards)
    {
        this.maxRewards = maxRewards;
    }

    public int getInventoryRows()
    {
        return inventoryRows;
    }

    public void setInventoryRows(int inventoryRows)
    {
        this.inventoryRows = inventoryRows;
    }


}
