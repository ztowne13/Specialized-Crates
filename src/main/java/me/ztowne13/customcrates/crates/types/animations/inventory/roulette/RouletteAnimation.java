package me.ztowne13.customcrates.crates.types.animations.inventory.roulette;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.animations.AnimationDataHolder;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.crates.types.animations.inventory.InventoryAnimationDataHolder;
import me.ztowne13.customcrates.crates.types.animations.inventory.InventoryCrateAnimation;
import me.ztowne13.customcrates.crates.types.animations.inventory.csgo.CSGOAnimationDataHolder;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ztowne13 on 6/30/16.
 */
public class RouletteAnimation extends InventoryCrateAnimation
{
    protected double finalTickLength, tickIncrease;
    protected int glassUpdateTicks = 2;
    protected ArrayList<ItemStack> items;

    public RouletteAnimation(Crate crate)
    {
        super(crate, CrateAnimationType.INV_ROULETTE);
    }

    @Override
    public void tickInventory(InventoryAnimationDataHolder dataHolder, boolean update)
    {
        RouletteAnimationDataHolder rdh = (RouletteAnimationDataHolder) dataHolder;

        switch (rdh.getCurrentState())
        {
            case PLAYING:
                drawFillers(rdh, glassUpdateTicks);

                if (update)
                {
                    playSound(rdh);
                    updateReward(rdh);
                }

                drawRewards(rdh);
        }
    }

    @Override
    public boolean updateTicks(AnimationDataHolder dataHolder)
    {
        RouletteAnimationDataHolder rdh = (RouletteAnimationDataHolder) dataHolder;

        switch(rdh.getCurrentState())
        {
            case PLAYING:
                if (rdh.getIndividualTicks() * BASE_SPEED >= rdh.getCurrentTicks() - 1.1)
                {
                    rdh.setUpdates(rdh.getUpdates() + 1);
                    rdh.setIndividualTicks(0);

                    rdh.setCurrentTicks(.05 * Math.pow((getTickIncrease() / 40) + 1, rdh.getUpdates()));
                    return true;
                }
                break;
            case ENDING:
                rdh.setWaitingTicks(rdh.getWaitingTicks() + 1);
                break;
        }

        return false;
    }

    @Override
    public void checkStateChange(AnimationDataHolder dataHolder, boolean update)
    {
        RouletteAnimationDataHolder rdh = (RouletteAnimationDataHolder) dataHolder;

        switch (rdh.getCurrentState())
        {
            case PLAYING:
                if (rdh.getCurrentTicks() > getFinalTickLength())
                {
                    rdh.setCurrentState(AnimationDataHolder.State.ENDING);
                }
            case ENDING:
                if (rdh.getWaitingTicks() == 50)
                {
                    rdh.setCurrentState(CSGOAnimationDataHolder.State.COMPLETED);
                }
        }
    }

    public void updateReward(RouletteAnimationDataHolder rdh)
    {
        Reward r = getCrate().getSettings().getRewards().getRandomReward();
        rdh.setLastShownReward(r);
    }

    public void drawRewards(RouletteAnimationDataHolder rdh)
    {
        InventoryBuilder inv = rdh.getInventoryBuilder();

        inv.setItem(13, rdh.getLastShownReward().getDisplayBuilder());
    }


    @Override
    public void drawIdentifierBlocks(InventoryAnimationDataHolder cdh)
    {

    }

    @Override
    public ItemBuilder getFiller()
    {
        Random r = new Random();
        return new ItemBuilder(getItems().get(r.nextInt(getItems().size()))).setName("");
    }

    @Override
    public void endAnimation(AnimationDataHolder dataHolder)
    {
        RouletteAnimationDataHolder rdh = (RouletteAnimationDataHolder) dataHolder;
        Player player = rdh.getPlayer();

        ArrayList<Reward> rewards = new ArrayList<>();
        rewards.add(rdh.getLastShownReward());

        completeCrateRun(player, rewards, false, null);
        getCrate().tick(rdh.getLocation(), CrateState.OPEN, player, rewards);
    }

    @Override
    public void loadDataValues(StatusLogger sl)
    {
        invName = fu.getFileDataLoader()
                .loadString(prefix + "inv-name", getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ROULETTE_INVNAME_SUCCESS);

        tickSound = fu.getFileDataLoader()
                .loadSound(prefix + "tick-sound", getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_SOUND_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_SOUND_FAILURE,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_VOLUME_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_VOLUME_INVALID,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_VOLUMEPITCH_FAILURE,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_PITCH_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_PITCH_INVALID);

        finalTickLength = fu.getFileDataLoader().loadDouble(prefix + "final-crate-tick-length", 7, getStatusLogger(),
                StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT, StatusLoggerEvent.ANIMATION_ROULETTE_FINALTICKLENGTH_SUCCESS,
                StatusLoggerEvent.ANIMATION_ROULETTE_FINALTICKLENGTH_INVALID);

        glassUpdateTicks = fu.getFileDataLoader()
                .loadInt(prefix + "tile-update-ticks", 2, getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ROULETTE_GLASSUPDATE_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ROULETTE_GLASSUPDATE_INVALID);

        tickIncrease = fu.getFileDataLoader()
                .loadDouble(prefix + "tick-speed-per-run", 0.4, getStatusLogger(),
                        StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSPEED_SUCCESS,
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSPEED_INVALID);


        setItems(new ArrayList<ItemStack>());

        try
        {
            for (String s : fu.get().getStringList("CrateType.Inventory.Roulette.random-blocks"))
            {
                try
                {
                    DynamicMaterial m = null;
                    try
                    {
                        m = DynamicMaterial.fromString(s.toUpperCase());
                    }
                    catch (Exception exc)
                    {
                        StatusLoggerEvent.ANIMATION_ROULETTE_RANDOMBLOCK_MATERIAL_NONEXISTENT
                                .log(getStatusLogger(), new String[]{s});
                        continue;
                    }
                    getItems().add(new ItemBuilder(m, 1).get());
                    StatusLoggerEvent.ANIMATION_ROULETTE_RANDOMBLOCK_MATERIAL_SUCCESS
                            .log(getStatusLogger(), new String[]{s});
                }
                catch (Exception exc)
                {
                    StatusLoggerEvent.ANIMATION_ROULETTE_RANDOMBLOCK_ITEM_INVALID.log(getStatusLogger(), new String[]{s});
                }
            }
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.ANIMATION_ROULETTE_RANDOMBLOCK_NONEXISTENT.log(getStatusLogger());
        }
    }

    public ArrayList<ItemStack> getItems()
    {
        return items;
    }

    public void setItems(ArrayList<ItemStack> items)
    {
        this.items = items;
    }

    public double getFinalTickLength()
    {
        return finalTickLength;
    }

    public void setFinalTickLength(double finalTickLength)
    {
        this.finalTickLength = finalTickLength;
    }

    public double getTickIncrease()
    {
        return tickIncrease;
    }

    public void setTickIncrease(double tickIncrease)
    {
        this.tickIncrease = tickIncrease;
    }
}
