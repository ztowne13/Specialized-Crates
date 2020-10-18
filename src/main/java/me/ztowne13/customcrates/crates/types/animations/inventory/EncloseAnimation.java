package me.ztowne13.customcrates.crates.types.animations.inventory;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.options.sounds.SoundData;
import me.ztowne13.customcrates.crates.types.animations.AnimationDataHolder;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;

/**
 * Created by ztowne13 on 7/5/16.
 */
public class EncloseAnimation extends InventoryCrateAnimation {
    private int inventoryRows;
    private int updateSpeed;
    private int rewardAmount;
    private ItemBuilder fillerItem;

    public EncloseAnimation(Crate crate) {
        super(crate, CrateAnimationType.INV_ENCLOSE);
    }

    @Override
    public void tickInventory(InventoryAnimationDataHolder dataHolder, boolean update) {
        EncloseAnimationDataHolder eadh = (EncloseAnimationDataHolder) dataHolder;

        if (eadh.getCurrentState() == AnimationDataHolder.State.PLAYING) {
            if (update) {
                playSound(eadh);
                updateRewards(eadh);
            }
            drawFillers(eadh, 1);
            drawRewards(eadh);
        }
    }

    @Override
    public void checkStateChange(AnimationDataHolder dataHolder, boolean update) {
        EncloseAnimationDataHolder eadh = (EncloseAnimationDataHolder) dataHolder;
        switch (eadh.getCurrentState()) {
            case PLAYING:
                if (eadh.getCurrentTicksIn() < 0) {
                    eadh.setCurrentState(AnimationDataHolder.State.ENDING);
                }
                break;
            case ENDING:
                if (dataHolder.getWaitingTicks() == 50) {
                    dataHolder.setCurrentState(AnimationDataHolder.State.COMPLETED);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void drawIdentifierBlocks(InventoryAnimationDataHolder inventoryAnimationDataHolder) {
        // EMPTY
    }

    @Override
    public ItemBuilder getFiller() {
        return fillerItem;
    }

    @Override
    public boolean updateTicks(AnimationDataHolder dataHolder) {
        EncloseAnimationDataHolder eadh = (EncloseAnimationDataHolder) dataHolder;

        switch (eadh.getCurrentState()) {
            case PLAYING:
                if ((eadh.getIndividualTicks() + updateSpeed - 1) % updateSpeed == 0) {
                    eadh.setCurrentTicksIn(eadh.getCurrentTicksIn() - 1);
                    eadh.setIndividualTicks(1);
                    return true;
                }
                break;
            case ENDING:
                dataHolder.setWaitingTicks(dataHolder.getWaitingTicks() + 1);
                break;
            default:
                break;
        }

        return false;
    }

    public void updateRewards(EncloseAnimationDataHolder eadh) {
        eadh.getLastDisplayRewards().clear();

        int size = eadh.getInventoryBuilder().getSize();
        int midPoint = (size / 2) + 1;

        for (int i = 0; i < size; i++) {
            if (i >= midPoint - eadh.getCurrentTicksIn() - 1 - rewardAmount &&
                    i < midPoint + eadh.getCurrentTicksIn() + rewardAmount) {
                Reward r = getCrate().getSettings().getRewards().getRandomReward();
                eadh.getLastDisplayRewards().add(r);
            }
        }

    }

    public void drawRewards(EncloseAnimationDataHolder eadh) {
        int size = eadh.getInventoryBuilder().getSize();
        int midPoint = (size / 2) + 1;

        int currentDisplayedReward = 0;

        for (int i = 0; i < size; i++) {
            if (i >= midPoint - eadh.getCurrentTicksIn() - 1 - rewardAmount &&
                    i < midPoint + eadh.getCurrentTicksIn() + rewardAmount) {
                Reward r = eadh.getLastDisplayRewards().get(currentDisplayedReward);
                eadh.getInventoryBuilder().setItem(i, r.getDisplayBuilder());
                currentDisplayedReward++;
            }
        }
    }

    @Override
    public void endAnimation(AnimationDataHolder dataHolder) {
        EncloseAnimationDataHolder edh = (EncloseAnimationDataHolder) dataHolder;

        finishAnimation(edh.getPlayer(), edh.getLastDisplayRewards(), null);
        getCrate().tick(edh.getLocation(), CrateState.OPEN, edh.getPlayer(), edh.getLastDisplayRewards());
    }

    @Override
    public void loadDataValues(StatusLogger statusLogger) {
        invName = fileHandler.getFileDataLoader()
                .loadString(prefix + "inv-name", getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_INVNAME_SUCCESS);

        inventoryRows = fileHandler.getFileDataLoader()
                .loadInt(prefix + "inventory-rows", 2, getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_INVROWS_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_INVROWS_INVALID);

        if (getInventoryRows() > 2) {
            setInventoryRows(2);
        }

        fillerItem = fileHandler.getFileDataLoader()
                .loadItem(prefix + "fill-block", new ItemBuilder(XMaterial.PURPLE_STAINED_GLASS_PANE),
                        getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_FILLBLOCK_INVALID
                );
        fillerItem.setDisplayName("&f");

        tickSound = fileHandler.getFileDataLoader()
                .loadSound(prefix + "tick-sound", getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_SOUND_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_SOUND_FAILURE,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_VOLUME_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_VOLUME_INVALID,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_VOLUMEPITCH_FAILURE,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_PITCH_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_PITCH_INVALID);

        updateSpeed = fileHandler.getFileDataLoader()
                .loadInt(prefix + "update-speed", 2, getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_UPDATESPEED_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_UPDATESPEED_INVALID);

        rewardAmount = fileHandler.getFileDataLoader().loadInt(prefix + "reward-amount", 1, getStatusLogger(),
                StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT, StatusLoggerEvent.ANIMATION_ENCLOSEMENT_REWARDAMOUNT_SUCCESS,
                StatusLoggerEvent.ANIMATION_ENCLOSEMENT_REWARDAMOUNT_INVALID);

        if (rewardAmount % 2 == 0) {
            rewardAmount++;
        }
        rewardAmount--;
        rewardAmount = rewardAmount == 0 ? 0 : rewardAmount / 2;
    }

    @Override
    public String getInvName() {
        return invName;
    }

    @Override
    public void setInvName(String invName) {
        this.invName = invName;
    }

    public int getInventoryRows() {
        return inventoryRows;
    }

    public void setInventoryRows(int inventoryRows) {
        this.inventoryRows = inventoryRows;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    @Override
    public SoundData getTickSound() {
        return tickSound;
    }

    @Override
    public void setTickSound(SoundData tickSound) {
        this.tickSound = tickSound;
    }
}
