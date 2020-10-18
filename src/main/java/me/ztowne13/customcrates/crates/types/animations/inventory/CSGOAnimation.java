package me.ztowne13.customcrates.crates.types.animations.inventory;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.options.sounds.SoundData;
import me.ztowne13.customcrates.crates.types.animations.AnimationDataHolder;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimation;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Created by ztowne13 on 6/30/16.
 */
public class CSGOAnimation extends InventoryCrateAnimation {
    private final ArrayList<ItemBuilder> fillerBlocks = new ArrayList<>();
    private final Random random = new Random();
    private double finalTickLength;
    private double tickIncrease;
    private int glassUpdateTicks = 2;
    private int closeSpeed = -1;
    private ItemBuilder identifierBlock = null;

    public CSGOAnimation(Crate crate) {
        super(crate, CrateAnimationType.INV_CSGO);
    }

    @Override
    public void tickInventory(InventoryAnimationDataHolder dataHolder, boolean update) {
        CSGOAnimationDataHolder cdh = (CSGOAnimationDataHolder) dataHolder;

        switch (cdh.getCurrentState()) {
            case PLAYING:
                drawFillers(cdh, glassUpdateTicks);

                if (update) {
                    playSound(cdh);
                    updateRewards(cdh);
                }

                drawRewards(cdh, 0);
                break;
            case WAITING:
                drawRewards(cdh, 0);
                break;
            case CLOSING:
            case ENDING:
                if (cdh.isUpdateAnimatedClose()) {
                    cdh.setUpdateAnimatedClose(false);
                    drawFillers(cdh, 1);
                }
                drawRewards(cdh, cdh.getAnimatedCloseTicks());
                break;
            default:
                break;
        }
    }

    @Override
    public void checkStateChange(AnimationDataHolder dataHolder, boolean update) {
        CSGOAnimationDataHolder cdh = (CSGOAnimationDataHolder) dataHolder;

        switch (cdh.getCurrentState()) {
            case PLAYING:
                if (update && cdh.getCurrentTicks() > getFinalTickLength())
                    cdh.setCurrentState(AnimationDataHolder.State.WAITING);
                break;
            case WAITING:
                if (cdh.getWaitingTicks() == 10) {
                    if (getCloseSpeed() > -1)
                        cdh.setCurrentState(AnimationDataHolder.State.CLOSING);
                    else
                        cdh.setCurrentState(AnimationDataHolder.State.ENDING);
                }
                break;
            case CLOSING:
                if (cdh.getAnimatedCloseTicks() == 3) {
                    cdh.setWaitingTicks(0);
                    cdh.setCurrentState(AnimationDataHolder.State.ENDING);
                }
                break;
            case ENDING:
                if (cdh.getWaitingTicks() == 50) {
                    cdh.setCurrentState(AnimationDataHolder.State.COMPLETED);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Updates all of the tick values that track how fast and slow the plugin is going
     *
     * @return Returns whether or not the rewards should be updated and sound should be played.
     */
    @Override
    public boolean updateTicks(AnimationDataHolder dataHolder) {
        CSGOAnimationDataHolder cdh = (CSGOAnimationDataHolder) dataHolder;

        switch (cdh.getCurrentState()) {
            case PLAYING:
                if (cdh.getIndividualTicks() * CrateAnimation.BASE_SPEED >= cdh.getCurrentTicks() - 1.1) {
                    cdh.setUpdates(cdh.getUpdates() + 1);
                    cdh.setIndividualTicks(0);

                    cdh.setCurrentTicks(.05 * Math.pow((getTickIncrease() / 40) + 1, cdh.getUpdates()));

                    return true;
                }
                break;
            case CLOSING:
                if (cdh.getTotalTicks() % getCloseSpeed() == 0) {
                    cdh.setUpdateAnimatedClose(true);
                    cdh.setAnimatedCloseTicks(cdh.getAnimatedCloseTicks() + 1);
                }
                break;
            case WAITING:
            case ENDING:
                cdh.setWaitingTicks(cdh.getWaitingTicks() + 1);
                break;
            default:
                break;
        }

        return false;
    }

    public void updateRewards(CSGOAnimationDataHolder cdh) {
        for (int i = 0; i < cdh.getDisplayedRewards().length; i++) {
            Reward r = cdh.getDisplayedRewards()[i];
            int numToSet = i - 1;

            if (r != null && numToSet >= 0) {
                cdh.getDisplayedRewards()[numToSet] = cdh.getDisplayedRewards()[i];
            }
        }

        Reward r = getCrate().getSettings().getRewards().getRandomReward();
        cdh.getDisplayedRewards()[cdh.getDisplayedRewards().length - 1] = r;
    }

    public void drawRewards(CSGOAnimationDataHolder cdh, int sideIndent) {
        InventoryBuilder inv = cdh.getInventoryBuilder();

        for (int i = sideIndent; i < cdh.getDisplayedRewards().length - sideIndent; i++) {
            Reward r = cdh.getDisplayedRewards()[i];
            if (r != null) {
                inv.setItem(i + 10, r.getDisplayBuilder());
            }
        }

    }

    @Override
    public void drawIdentifierBlocks(InventoryAnimationDataHolder inventoryAnimationDataHolder) {
        InventoryBuilder inv = inventoryAnimationDataHolder.getInventoryBuilder();

        inv.setItem(4, getIdentifierBlock());
        inv.setItem(22, getIdentifierBlock());
    }

    @Override
    public ItemBuilder getFiller() {
        return getFillerBlocks().get(random.nextInt(getFillerBlocks().size()));
    }

    @Override
    public void endAnimation(AnimationDataHolder dataHolder) {
        CSGOAnimationDataHolder cdh = (CSGOAnimationDataHolder) dataHolder;

        ArrayList<Reward> rewards = new ArrayList<>();
        rewards.add(cdh.getDisplayedRewards()[cdh.getDisplayedRewards().length / 2]);

        finishAnimation(dataHolder.getPlayer(), rewards, null);
        getCrate().tick(cdh.getLocation(), CrateState.OPEN, dataHolder.getPlayer(), rewards);
    }

    @Override
    public void loadDataValues(StatusLogger statusLogger) {
        invName = fileHandler.getFileDataLoader()
                .loadString(prefix + "inv-name", getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_CSGO_INVNAME_SUCCESS);

        tickSound = fileHandler.getFileDataLoader()
                .loadSound(prefix + "tick-sound", getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_SOUND_SUCCESS,
                        StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_SOUND_FAILURE,
                        StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_VOLUME_SUCCESS,
                        StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_VOLUME_INVALID,
                        StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_VOLUMEPITCH_FAILURE,
                        StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_PITCH_SUCCESS,
                        StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_PITCH_INVALID);

        identifierBlock = fileHandler.getFileDataLoader()
                .loadItem(prefix + "identifier-block", new ItemBuilder(XMaterial.AIR), getStatusLogger(),
                        StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_CSGO_IDBLOCK_INVALID
                );
        identifierBlock.setDisplayName("&f");

        finalTickLength = fileHandler.getFileDataLoader()
                .loadDouble(prefix + "final-crate-tick-length", 7, getStatusLogger(),
                        StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_CSGO_FINALTICKLENGTH_SUCCESS,
                        StatusLoggerEvent.ANIMATION_CSGO_FINALTICKLENGTH_INVALID);

        glassUpdateTicks = fileHandler.getFileDataLoader().loadInt(prefix + "tile-update-ticks", 2, getStatusLogger(),
                StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT, StatusLoggerEvent.ANIMATION_CSGO_GLASSUPDATE_SUCCESS,
                StatusLoggerEvent.ANIMATION_CSGO_GLASSUPDATE_INVALID);

        closeSpeed = fileHandler.getFileDataLoader()
                .loadInt(prefix + "close-speed", -1, getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_CSGO_CLOSESPEED_SUCCESS,
                        StatusLoggerEvent.ANIMATION_CSGO_CLOSESPEED_INVALID);

        tickIncrease = fileHandler.getFileDataLoader().loadDouble(prefix + "tick-speed-per-run", 0.4, getStatusLogger(),
                StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                StatusLoggerEvent.ANIMATION_CSGO_TICKSPEED_SUCCESS, StatusLoggerEvent.ANIMATION_CSGO_TICKSPEED_INVALID);

        try {
            for (String unParsed : getFileHandler().get().getStringList("CrateType.Inventory.CSGO.filler-blocks")) {
                String[] args = unParsed.split(";");
                try {
                    Optional<XMaterial> optional = XMaterial.matchXMaterial(unParsed);
                    if (!optional.isPresent()) {
                        StatusLoggerEvent.ANIMATION_CSGO_FILLERBLOCK_MATERIAL_INVALID
                                .log(getStatusLogger(), new String[]{args[0]});
                        continue;
                    }
                    getFillerBlocks().add(new ItemBuilder(optional.get(), 1).setDisplayName("&f"));

                    StatusLoggerEvent.ANIMATION_CSGO_FILLERBLOCK_MATERIAL_SUCCESS
                            .log(getStatusLogger(), new String[]{unParsed});
                } catch (Exception exc) {
                    StatusLoggerEvent.ANIMATION_CSGO_FILLERBLOCK_ITEM_INVALID.log(getStatusLogger(), new String[]{unParsed});
                }
            }
        } catch (Exception exc) {
            StatusLoggerEvent.ANIMATION_CSGO_FILLERBLOCK_NONEXISTENT.log(getStatusLogger());
        }
    }

    @Override
    public void setTickSound(SoundData tickSound) {
        this.tickSound = tickSound;
    }

    @Override
    public String getInvName() {
        return invName;
    }

    @Override
    public void setInvName(String invName) {
        this.invName = invName;
    }

    public double getFinalTickLength() {
        return finalTickLength;
    }

    public double getTickIncrease() {
        return tickIncrease;
    }

    public int getCloseSpeed() {
        return closeSpeed;
    }

    public ItemBuilder getIdentifierBlock() {
        return identifierBlock;
    }

    public List<ItemBuilder> getFillerBlocks() {
        return fillerBlocks;
    }

}
