package me.ztowne13.customcrates.crates.types.animations.inventory;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.options.sounds.SoundData;
import me.ztowne13.customcrates.crates.types.animations.AnimationDataHolder;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Created by ztowne13 on 7/5/16.
 */
public class EncloseAnimation extends InventoryCrateAnimation
{
    int inventoryRows, updateSpeed, rewardAmount;
    ItemBuilder fillerItem;

    public EncloseAnimation(Crate crate)
    {
        super(crate, CrateAnimationType.INV_ENCLOSE);
    }

    @Override
    public void tickInventory(InventoryAnimationDataHolder dataHolder, boolean update)
    {
        EncloseAnimationDataHolder eadh = (EncloseAnimationDataHolder) dataHolder;

        switch (eadh.getCurrentState())
        {
            case PLAYING:
                if (update)
                {
                    playSound(eadh);
                    updateRewards(eadh);
                }
                drawFillers(eadh, 1);
                drawRewards(eadh);
                break;
        }
    }

    @Override
    public void checkStateChange(AnimationDataHolder dataHolder, boolean update)
    {
        EncloseAnimationDataHolder eadh = (EncloseAnimationDataHolder) dataHolder;
        switch (eadh.getCurrentState())
        {
            case PLAYING:
                if (eadh.getCurrentTicksIn() < 0)
                {
                    eadh.setCurrentState(AnimationDataHolder.State.ENDING);
                }
                break;
            case ENDING:
                if (dataHolder.getWaitingTicks() == 50)
                {
                    dataHolder.setCurrentState(AnimationDataHolder.State.COMPLETED);
                }
        }
    }

    @Override
    public void drawIdentifierBlocks(InventoryAnimationDataHolder cdh) { }

    @Override
    public ItemBuilder getFiller()
    {
        return fillerItem;
    }

    @Override
    public boolean updateTicks(AnimationDataHolder dataHolder)
    {
        EncloseAnimationDataHolder eadh = (EncloseAnimationDataHolder) dataHolder;

        switch(eadh.getCurrentState())
        {
            case PLAYING:
                if ((eadh.getIndividualTicks() + updateSpeed - 1) % updateSpeed == 0)
                {
                    eadh.setCurrentTicksIn(eadh.getCurrentTicksIn() - 1);
                    eadh.setIndividualTicks(1);
                    return true;
                }
                break;
            case ENDING:
                dataHolder.setWaitingTicks(dataHolder.getWaitingTicks() + 1);
        }

        return false;
    }

    public void updateRewards(EncloseAnimationDataHolder eadh)
    {
        eadh.getLastDisplayRewards().clear();

        int size = eadh.getInventoryBuilder().getSize();
        int midPoint = (size / 2) + 1;

        for (int i = 0; i < size; i++)
        {
            if (i >= midPoint - eadh.getCurrentTicksIn() - 1 - rewardAmount &&
                    i < midPoint + eadh.getCurrentTicksIn() + rewardAmount)
            {
                Reward r = getCrate().getSettings().getRewards().getRandomReward();
                eadh.getLastDisplayRewards().add(r);
            }
        }

    }

    public void drawRewards(EncloseAnimationDataHolder eadh)
    {
        int size = eadh.getInventoryBuilder().getSize();
        int midPoint = (size / 2) + 1;

        int currentDisplayedReward = 0;

        for (int i = 0; i < size; i++)
        {
            if (i >= midPoint - eadh.getCurrentTicksIn() - 1 - rewardAmount &&
                    i < midPoint + eadh.getCurrentTicksIn() + rewardAmount)
            {
                Reward r = eadh.getLastDisplayRewards().get(currentDisplayedReward);
                eadh.getInventoryBuilder().setItem(i, r.getDisplayBuilder());
                currentDisplayedReward++;
            }
        }
    }

    @Override
    public void endAnimation(AnimationDataHolder dataHolder)
    {
        EncloseAnimationDataHolder edh = (EncloseAnimationDataHolder) dataHolder;

        finishAnimation(edh.getPlayer(), edh.getLastDisplayRewards(), false, null);
        getCrate().tick(edh.getLocation(), CrateState.OPEN, edh.getPlayer(), edh.getLastDisplayRewards());
    }

    @Override
    public void loadDataValues(StatusLogger sl)
    {
        FileConfiguration fc = cc.getCrateconfigFile().get();

        invName = fu.getFileDataLoader()
                .loadString(prefix + "inv-name", getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_INVNAME_SUCCESS);

        inventoryRows = fu.getFileDataLoader()
                .loadInt(prefix + "inventory-rows", 2, getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_INVROWS_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_INVROWS_INVALID);

        if (getInventoryRows() > 2)
        {
            setInventoryRows(2);
        }

        fillerItem = fu.getFileDataLoader()
                .loadItem(prefix + "fill-block", new ItemBuilder(DynamicMaterial.PURPLE_STAINED_GLASS_PANE),
                        getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_FILLBLOCK_INVALID_MATERIAL,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_FILLBLOCK_INVALID_BYTE,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_FILLBLOCK_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_FILLBLOCK_INVALID);
        fillerItem.setDisplayName("&f");

        tickSound = fu.getFileDataLoader()
                .loadSound(prefix + "tick-sound", getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_SOUND_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_SOUND_FAILURE,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_VOLUME_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_VOLUME_INVALID,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_VOLUMEPITCH_FAILURE,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_PITCH_INVALID,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_PITCH_SUCCESS);

        updateSpeed = fu.getFileDataLoader()
                .loadInt(prefix + "update-speed", 2, getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_UPDATESPEED_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_UPDATESPEED_INVALID);

        rewardAmount = fu.getFileDataLoader().loadInt(prefix + "reward-amount", 1, getStatusLogger(),
                StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT, StatusLoggerEvent.ANIMATION_ENCLOSEMENT_REWARDAMOUNT_SUCCESS,
                StatusLoggerEvent.ANIMATION_ENCLOSEMENT_REWARDAMOUNT_INVALID);

        if (rewardAmount % 2 == 0)
        {
            rewardAmount++;
        }
        rewardAmount--;
        rewardAmount = rewardAmount == 0 ? 0 : rewardAmount / 2;
    }

    public String getInvName()
    {
        return invName;
    }

    public void setInvName(String invName)
    {
        this.invName = invName;
    }

    public int getInventoryRows()
    {
        return inventoryRows;
    }

    public void setInventoryRows(int inventoryRows)
    {
        this.inventoryRows = inventoryRows;
    }

    public int getRewardAmount()
    {
        return rewardAmount;
    }

    public SoundData getTickSound()
    {
        return tickSound;
    }

    public void setTickSound(SoundData tickSound)
    {
        this.tickSound = tickSound;
    }
}
