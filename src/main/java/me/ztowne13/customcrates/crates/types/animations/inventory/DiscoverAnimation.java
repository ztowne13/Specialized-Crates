package me.ztowne13.customcrates.crates.types.animations.inventory;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.options.sounds.SoundData;
import me.ztowne13.customcrates.crates.types.animations.AnimationDataHolder;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ztowne13 on 7/7/16.
 * <p>
 * inv-name: '&8&l> &6&l%crate%'
 * tick-sound: BLOCK_STONE_HIT, 5, 5
 * click-sound: ENTITY_HORSE_GALLOP, 5, 5
 * uncover-sound: ENTITY_PLAYER_LEVELUP, 5, 5
 * minimum-rewards: 1
 * maximum-rewards: 4
 * count: true
 * random-display-duration: 50
 * uncover-block: CHEST;0
 */
public class DiscoverAnimation extends InventoryCrateAnimation
{
    SoundData clickSound, uncoverSound;
    int minRewards, maxRewards, shuffleDisplayDuration, invRows;
    String coverBlockName = "&aReward #%numbetsorr%";
    String coverBlockLore = "&7You have &f%remaining-clicks% rewards to chose from.";
    String rewardBlockName = "&aReward";
    String rewardBlockUnlockName = "&aClick me to unlock your reward.";
    String rewardBlockWaitingName = "&aUncover all rewards to unlock";
    ItemBuilder uncoverBlock;
    ItemBuilder rewardBlock;
    boolean count;

    public DiscoverAnimation(Crate crate)
    {
        super(crate, CrateAnimationType.INV_DISCOVER);
    }

    @Override
    public void tickInventory(InventoryAnimationDataHolder dataHolder, boolean update)
    {
        DiscoverAnimationDataHolder dadh = (DiscoverAnimationDataHolder) dataHolder;

        switch (dadh.getCurrentState())
        {
            case PLAYING:
                drawFillers(dadh, 1);
                updateUncoverTiles(dadh);
                drawUncoverTiles(dadh);
                break;
            case WAITING:
                drawFillers(dadh, 1);
                drawUncoverTiles(dadh);
                break;
            case SHUFFLING:
                drawFillers(dadh, 1);
                updateShufflingTiles(dadh);
                drawShufflingTiles(dadh);
                break;
            case UNCOVERING:
                drawFillers(dadh, 1);
                updateWinningTiles(dadh);
                drawWinningTiles(dadh);
                break;
            case ENDING:
                drawFillers(dadh, 1);
                drawWinningTiles(dadh);
        }
    }

    @Override
    public void drawIdentifierBlocks(InventoryAnimationDataHolder cdh)
    {

    }

    @Override
    public ItemBuilder getFiller()
    {
        return new ItemBuilder(DynamicMaterial.AIR);
    }

    @Override
    public boolean updateTicks(AnimationDataHolder dataHolder)
    {
        DiscoverAnimationDataHolder dadh = (DiscoverAnimationDataHolder) dataHolder;

        switch(dadh.getCurrentState())
        {
            case WAITING:
            case ENDING:
                dadh.setWaitingTicks(dataHolder.getWaitingTicks() + 1);
                break;
            case SHUFFLING:
                dadh.setShuffleTicks(dadh.getShuffleTicks() + (int) BASE_SPEED);
        }

        return false;
    }

    @Override
    public void checkStateChange(AnimationDataHolder dataHolder, boolean update)
    {
        DiscoverAnimationDataHolder dadh = (DiscoverAnimationDataHolder) dataHolder;

        switch(dadh.getCurrentState())
        {
            case PLAYING:
                if (dadh.getRemainingClicks() <= 0)
                {
                    dadh.setCurrentState(AnimationDataHolder.State.WAITING);
                }
                break;
            case WAITING:
                if(dadh.getWaitingTicks() == 20)
                {
                    dadh.setWaitingTicks(0);
                    dadh.setCurrentState(AnimationDataHolder.State.SHUFFLING);
                }
                break;
            case SHUFFLING:
                if(dadh.getShuffleTicks() > getShuffleDisplayDuration())
                {
                    dadh.setCurrentState(AnimationDataHolder.State.UNCOVERING);
                }
                break;
            case UNCOVERING:
                if (dadh.getAlreadyDisplayedRewards().keySet().size() == dadh.getAlreadyChosenSlots().size())
                {
                    dadh.setCurrentState(AnimationDataHolder.State.ENDING);
                }
                break;
            case ENDING:
                if(dadh.getWaitingTicks() == 50)
                {
                    dadh.setCurrentState(AnimationDataHolder.State.COMPLETED);
                }
                break;
        }
    }

    public void drawUncoverTiles(DiscoverAnimationDataHolder dadh)
    {
        InventoryBuilder inventoryBuilder = dadh.getInventoryBuilder();
        
        ItemBuilder uncoverBlockIb = uncoverBlock.setLore("")
                .addLore(coverBlockLore.replaceAll("%remaining-clicks%", dadh.getRemainingClicks() + ""));
        ItemBuilder alreadyUncoveredIb = rewardBlock;
        alreadyUncoveredIb.setDisplayName(rewardBlockWaitingName);
        
        for (int i = 0; i < inventoryBuilder.getSize(); i++)
        {
            if (dadh.getAlreadyChosenSlots().contains(i))
            {
                inventoryBuilder.setItem(i, alreadyUncoveredIb);
            }
            else
            {
                uncoverBlockIb.setDisplayName(coverBlockName.replaceAll("%number%", (i + 1) + ""));
                if (count)
                {
                    uncoverBlockIb.get().setAmount(i + 1);
                }
                inventoryBuilder.setItem(i, uncoverBlockIb);
            }
        }
    }

    public void updateUncoverTiles(DiscoverAnimationDataHolder dadh)
    {
        for(int slot : dadh.getClickedSlots())
        {
            if (!dadh.getAlreadyChosenSlots().contains(slot) && dadh.getRemainingClicks() != 0)
            {
                dadh.getAlreadyChosenSlots().add(slot);
                dadh.setRemainingClicks(dadh.getRemainingClicks() - 1);

                if (clickSound != null)
                    clickSound.playTo(dadh.getPlayer(), dadh.getLocation());
            }
        }

        dadh.getClickedSlots().clear();
    }
    
    public void drawShufflingTiles(DiscoverAnimationDataHolder dadh)
    {
        ItemBuilder reward = rewardBlock;
        reward.setDisplayName(rewardBlockName);
        
        for(int i = 0; i < dadh.getShufflingTiles().size(); i++)
        {
            dadh.getInventoryBuilder().setItem(dadh.getShufflingTiles().get(i), reward);
        }
    }
    
    public void updateShufflingTiles(DiscoverAnimationDataHolder dadh)
    {
        dadh.getShufflingTiles().clear();
        
        Random r = new Random();

        for (int i = 0; i < dadh.getInventoryBuilder().getSize(); i++)
        {
            if (r.nextInt(7) == 1)
            {
                dadh.getShufflingTiles().add(i);
            }
        }
    }
    
    public void drawWinningTiles(DiscoverAnimationDataHolder dadh)
    {
        InventoryBuilder inventoryBuilder = dadh.getInventoryBuilder();
        
        ItemBuilder reward = rewardBlock;
        reward.setDisplayName(rewardBlockUnlockName);
        
        for (int i : dadh.getAlreadyChosenSlots())
        {
            if (dadh.getAlreadyDisplayedRewards().keySet().contains(i))
            {
                inventoryBuilder.setItem(i, dadh.getAlreadyDisplayedRewards().get(i).getDisplayBuilder());
            }
            else
            {
                inventoryBuilder.setItem(i, reward);
            }
        }
    }

    public void updateWinningTiles(DiscoverAnimationDataHolder dadh)
    {
        for(int slot : dadh.getClickedSlots())
        {
            if (dadh.getAlreadyChosenSlots().contains(slot))
            {
                if (!dadh.getAlreadyDisplayedRewards().keySet().contains(slot))
                {
                    Reward newR = getCrate().getSettings().getRewards().getRandomReward();

                    if (uncoverSound != null)
                        uncoverSound.playTo(dadh.getPlayer(), dadh.getLocation());

                    dadh.getAlreadyDisplayedRewards().put(slot, newR);
                }
            }
        }

        dadh.getClickedSlots().clear();
    }

    @Override
    public void endAnimation(AnimationDataHolder dataHolder)
    {
        DiscoverAnimationDataHolder dadh = (DiscoverAnimationDataHolder) dataHolder;
        Player player = dadh.getPlayer();

        ArrayList<Reward> rewards = new ArrayList<>();
        rewards.addAll(dadh.getAlreadyDisplayedRewards().values());

        finishAnimation(player, rewards, false, null);
        getCrate().tick(dadh.getLocation(), CrateState.OPEN, player, rewards);
    }

    @Override
    public void loadDataValues(StatusLogger sl)
    {
        FileConfiguration fc = getFileHandler().get();

        invName = fc.getString(prefix + "inv-name");

        invRows = fu.getFileDataLoader()
                .loadInt(prefix + "inventory-rows", 3, sl, StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_DISCOVER_INVROWS_SUCCESS,
                        StatusLoggerEvent.ANIMATION_DISCOVER_INVROWS_INVALID);

        minRewards = fu.getFileDataLoader()
                .loadInt(prefix + "minimum-rewards", 1, sl, StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_DISCOVER_MINREWARDS_SUCCESS,
                        StatusLoggerEvent.ANIMATION_DISCOVER_MINREWARDS_INVALID);

        maxRewards = fu.getFileDataLoader()
                .loadInt(prefix + "maximum-rewards", 1, sl, StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_DISCOVER_MAXREWARDS_SUCCESS,
                        StatusLoggerEvent.ANIMATION_DISCOVER_MAXREWARDS_INVALID);

        shuffleDisplayDuration = fu.getFileDataLoader()
                .loadInt(prefix + "random-display-duration", 1, sl, StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_DISCOVER_RANDDISPLAYLOCATION_SUCCESS,
                        StatusLoggerEvent.ANIMATION_DISCOVER_RANDDISPLAYLOCATION_INVALID);

        uncoverBlock =
                fu.getFileDataLoader().loadItem(prefix + "cover-block", new ItemBuilder(DynamicMaterial.CHEST), sl,
                        StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_DISCOVER_COVERBLOCK_MATERIAL_INVALID,
                        StatusLoggerEvent.ANIMATION_DISCOVER_COVERBLOCK_DURABILITY_INVALID,
                        StatusLoggerEvent.ANIMATION_DISCOVER_COVERBLOCK_INVALID,
                        StatusLoggerEvent.ANIMATION_DISCOVER_COVERBLOCK_SUCCESS);

        count = fu.getFileDataLoader().loadBoolean(prefix + "count", true, sl, StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                StatusLoggerEvent.ANIMATION_DISCOVER_COUNT_SUCCESS,
                StatusLoggerEvent.ANIMATION_DISCOVER_COUNT_INVALID);

        tickSound =
                fu.getFileDataLoader().loadSound(prefix + "tick-sound", sl, StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_DISCOVER_TICKSOUND_SOUND_SUCCESS,
                        StatusLoggerEvent.ANIMATION_DISCOVER_TICKSOUND_SOUND_FAILURE,
                        StatusLoggerEvent.ANIMATION_DISCOVER_TICKSOUND_VOLUME_SUCCESS,
                        StatusLoggerEvent.ANIMATION_DISCOVER_TICKSOUND_VOLUME_INVALID,
                        StatusLoggerEvent.ANIMATION_DISCOVER_TICKSOUND_PITCHVOL_INVALID,
                        StatusLoggerEvent.ANIMATION_DISCOVER_TICKSOUND_PITCH_SUCCESS,
                        StatusLoggerEvent.ANIMATION_DISCOVER_TICKSOUND_PITCH_INVALID);

        clickSound =
                fu.getFileDataLoader().loadSound(prefix + "click-sound", sl, StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_DISCOVER_CLICKSOUND_SOUND_SUCCESS,
                        StatusLoggerEvent.ANIMATION_DISCOVER_CLICKSOUND_SOUND_FAILURE,
                        StatusLoggerEvent.ANIMATION_DISCOVER_CLICKSOUND_VOLUME_SUCCESS,
                        StatusLoggerEvent.ANIMATION_DISCOVER_CLICKSOUND_VOLUME_INVALID,
                        StatusLoggerEvent.ANIMATION_DISCOVER_CLICKSOUND_PITCHVOL_INVALID,
                        StatusLoggerEvent.ANIMATION_DISCOVER_CLICKSOUND_PITCH_SUCCESS,
                        StatusLoggerEvent.ANIMATION_DISCOVER_CLICKSOUND_PITCH_INVALID);

        uncoverSound =
                fu.getFileDataLoader().loadSound(prefix + "uncover-sound", sl, StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_DISCOVER_UNCOVERSOUND_SOUND_SUCCESS,
                        StatusLoggerEvent.ANIMATION_DISCOVER_UNCOVERSOUND_SOUND_FAILURE,
                        StatusLoggerEvent.ANIMATION_DISCOVER_UNCOVERSOUND_VOLUME_SUCCESS,
                        StatusLoggerEvent.ANIMATION_DISCOVER_UNCOVERSOUND_VOLUME_INVALID,
                        StatusLoggerEvent.ANIMATION_DISCOVER_UNCOVERSOUND_PITCHVOL_INVALID,
                        StatusLoggerEvent.ANIMATION_DISCOVER_UNCOVERSOUND_PITCH_SUCCESS,
                        StatusLoggerEvent.ANIMATION_DISCOVER_UNCOVERSOUND_PITCH_INVALID);

        rewardBlock = fu.getFileDataLoader()
                .loadItem("reward-block", new ItemBuilder(DynamicMaterial.GREEN_STAINED_GLASS_PANE), sl,
                        StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_DISCOVER_REWARDBLOCK_MATERIAL_INVALID,
                        StatusLoggerEvent.ANIMATION_DISCOVER_REWARDBLOCK_DURABILITY_INVALID,
                        StatusLoggerEvent.ANIMATION_DISCOVER_REWARDBLOCK_INVALID,
                        StatusLoggerEvent.ANIMATION_DISCOVER_REWARDBLOCK_SUCCESS);
        rewardBlock.setDisplayName("");

        coverBlockName = fu.getFileDataLoader()
                .loadString(prefix + "cover-block-name", getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_DISCOVER_COVER_BLOCK_NAME_SUCCESS);

        coverBlockLore = fu.getFileDataLoader()
                .loadString(prefix + "cover-block-lore", getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_DISCOVER_COVER_BLOCK_LORE_SUCCESS);

        rewardBlockName = fu.getFileDataLoader()
                .loadString(prefix + "reward-block-name", getStatusLogger(), StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                        StatusLoggerEvent.ANIMATION_DISCOVER_REWARD_BLOCK_NAME_SUCCESS);

        rewardBlockUnlockName = fu.getFileDataLoader().loadString(prefix + "reward-block-unlock-name", getStatusLogger(),
                StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                StatusLoggerEvent.ANIMATION_DISCOVER_REWARD_BLOCK_UNBLOCK_NAME_SUCCESS);

        rewardBlockWaitingName = fu.getFileDataLoader().loadString(prefix + "reward-block-waiting-name", getStatusLogger(),
                StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                StatusLoggerEvent.ANIMATION_DISCOVER_REWARD_BLOCK_WAITING_NAME_SUCCESS);
    }

    public String getInvName()
    {
        return invName;
    }

    public void setInvName(String invName)
    {
        this.invName = invName;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public SoundData getTickSound()
    {
        return tickSound;
    }

    public void setTickSound(SoundData tickSound)
    {
        this.tickSound = tickSound;
    }


    public int getMinRewards()
    {
        return minRewards;
    }

    public int getMaxRewards()
    {
        return maxRewards;
    }

    public int getShuffleDisplayDuration()
    {
        return shuffleDisplayDuration;
    }

    public boolean isCount()
    {
        return count;
    }

    public void setCount(boolean count)
    {
        this.count = count;
    }

    public int getInvRows()
    {
        return invRows;
    }
}
