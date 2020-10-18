package me.ztowne13.customcrates.crates.types.animations.inventory;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.animations.AnimationDataHolder;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Created by ztowne13 on 6/30/16.
 */
public class RouletteAnimation extends InventoryCrateAnimation {
    private final Random random = new Random();
    private double finalTickLength;
    private double tickIncrease;
    private int glassUpdateTicks = 2;
    private List<ItemBuilder> items;

    public RouletteAnimation(Crate crate) {
        super(crate, CrateAnimationType.INV_ROULETTE);
    }

    @Override
    public void tickInventory(InventoryAnimationDataHolder dataHolder, boolean update) {
        RouletteAnimationDataHolder rdh = (RouletteAnimationDataHolder) dataHolder;

        if (rdh.getCurrentState() == AnimationDataHolder.State.PLAYING) {
            drawFillers(rdh, glassUpdateTicks);

            if (update) {
                playSound(rdh);
                updateReward(rdh);
            }

            drawRewards(rdh);
        }
    }

    @Override
    public boolean updateTicks(AnimationDataHolder dataHolder) {
        RouletteAnimationDataHolder rdh = (RouletteAnimationDataHolder) dataHolder;

        switch (rdh.getCurrentState()) {
            case PLAYING:
                if (rdh.getIndividualTicks() * BASE_SPEED >= rdh.getCurrentTicks() - 1.1) {
                    rdh.setUpdates(rdh.getUpdates() + 1);
                    rdh.setIndividualTicks(0);

                    rdh.setCurrentTicks(.05 * Math.pow((getTickIncrease() / 40) + 1, rdh.getUpdates()));
                    return true;
                }
                break;
            case ENDING:
                rdh.setWaitingTicks(rdh.getWaitingTicks() + 1);
                break;
            default:
                break;
        }

        return false;
    }

    @Override
    public void checkStateChange(AnimationDataHolder dataHolder, boolean update) {
        RouletteAnimationDataHolder rdh = (RouletteAnimationDataHolder) dataHolder;
        AnimationDataHolder.State state = rdh.getCurrentState();

        if (state == AnimationDataHolder.State.PLAYING && rdh.getCurrentTicks() > getFinalTickLength()) {
            rdh.setCurrentState(AnimationDataHolder.State.ENDING);
        }

        if (state == AnimationDataHolder.State.ENDING && rdh.getWaitingTicks() == 50) {
            rdh.setCurrentState(AnimationDataHolder.State.COMPLETED);
        }
    }

    public void updateReward(RouletteAnimationDataHolder rdh) {
        Reward r = getCrate().getSettings().getRewards().getRandomReward();
        rdh.setLastShownReward(r);
    }

    public void drawRewards(RouletteAnimationDataHolder rdh) {
        InventoryBuilder inv = rdh.getInventoryBuilder();

        inv.setItem(13, rdh.getLastShownReward().getDisplayBuilder());
    }

    @Override
    public void drawIdentifierBlocks(InventoryAnimationDataHolder inventoryAnimationDataHolder) {
        // EMPTY
    }

    @Override
    public ItemBuilder getFiller() {
        return getItems().get(random.nextInt(getItems().size()));
    }

    @Override
    public void endAnimation(AnimationDataHolder dataHolder) {
        RouletteAnimationDataHolder rdh = (RouletteAnimationDataHolder) dataHolder;
        Player player = rdh.getPlayer();

        ArrayList<Reward> rewards = new ArrayList<>();
        rewards.add(rdh.getLastShownReward());

        finishAnimation(player, rewards, null);
        getCrate().tick(rdh.getLocation(), CrateState.OPEN, player, rewards);
    }

    @Override
    public void loadDataValues(StatusLogger statusLogger) {
        invName = fileHandler.getFileDataLoader()
                .loadString(prefix + "inv-name", getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ROULETTE_INVNAME_SUCCESS);

        tickSound = fileHandler.getFileDataLoader()
                .loadSound(prefix + "tick-sound", getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_SOUND_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_SOUND_FAILURE,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_VOLUME_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_VOLUME_INVALID,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_VOLUMEPITCH_FAILURE,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_PITCH_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_PITCH_INVALID);

        finalTickLength = fileHandler.getFileDataLoader().loadDouble(prefix + "final-crate-tick-length", 7, getStatusLogger(),
                StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT, StatusLoggerEvent.ANIMATION_ROULETTE_FINALTICKLENGTH_SUCCESS,
                StatusLoggerEvent.ANIMATION_ROULETTE_FINALTICKLENGTH_INVALID);

        glassUpdateTicks = fileHandler.getFileDataLoader()
                .loadInt(prefix + "tile-update-ticks", 2, getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ROULETTE_GLASSUPDATE_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ROULETTE_GLASSUPDATE_INVALID);

        tickIncrease = fileHandler.getFileDataLoader()
                .loadDouble(prefix + "tick-speed-per-run", 0.4, getStatusLogger(),
                        StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSPEED_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSPEED_INVALID);


        setItems(new ArrayList<>());

        try {
            for (String s : fileHandler.get().getStringList("CrateType.Inventory.Roulette.random-blocks")) {
                try {
                    Optional<XMaterial> optional = XMaterial.matchXMaterial(s);
                    if (!optional.isPresent()) {
                        StatusLoggerEvent.ANIMATION_ROULETTE_RANDOMBLOCK_MATERIAL_NONEXISTENT
                                .log(getStatusLogger(), new String[]{s});
                        continue;
                    }

                    getItems().add(new ItemBuilder(optional.get(), 1).setDisplayName("&f"));
                    StatusLoggerEvent.ANIMATION_ROULETTE_RANDOMBLOCK_MATERIAL_SUCCESS
                            .log(getStatusLogger(), new String[]{s});
                } catch (Exception exc) {
                    StatusLoggerEvent.ANIMATION_ROULETTE_RANDOMBLOCK_ITEM_INVALID.log(getStatusLogger(), new String[]{s});
                }
            }
        } catch (Exception exc) {
            StatusLoggerEvent.ANIMATION_ROULETTE_RANDOMBLOCK_NONEXISTENT.log(getStatusLogger());
        }
    }

    public List<ItemBuilder> getItems() {
        return items;
    }

    public void setItems(List<ItemBuilder> items) {
        this.items = items;
    }

    public double getFinalTickLength() {
        return finalTickLength;
    }

    public void setFinalTickLength(double finalTickLength) {
        this.finalTickLength = finalTickLength;
    }

    public double getTickIncrease() {
        return tickIncrease;
    }

    public void setTickIncrease(double tickIncrease) {
        this.tickIncrease = tickIncrease;
    }
}
