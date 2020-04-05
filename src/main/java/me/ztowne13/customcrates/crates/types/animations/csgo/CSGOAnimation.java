package me.ztowne13.customcrates.crates.types.animations.csgo;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.options.sounds.SoundData;
import me.ztowne13.customcrates.crates.types.animations.CrateType;
import me.ztowne13.customcrates.crates.types.animations.InventoryCrateAnimation;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ztowne13 on 6/30/16.
 */
public class CSGOAnimation extends InventoryCrateAnimation
{
    static double BASE_SPEED = 1;
    static int REDRAW_TICKS = 1;

    protected SoundData tickSound = null;
    protected String invName = "";

    protected double finalTickLength, tickIncrease;
    protected int glassUpdateTicks = 2, closeSpeed = -1;

    protected ItemStack identifierBlock = null;
    protected ArrayList<ItemStack> fillerBlocks = new ArrayList<ItemStack>();

    public CSGOAnimation(Inventory inv, Crate crate)
    {
        super(CrateType.INV_CSGO.getPrefixDotted(), crate, inv);
    }

    @Override
    public boolean runAnimation(Player p, Location l, CrateState cs, boolean requireKeyInHand, boolean force)
    {
        if (force || canExecuteFor(cs, CrateState.OPEN, p, requireKeyInHand))
        {
            CSGOPlayerDataHolder cdh = new CSGOPlayerDataHolder(p, l, this);

            playSequence(cdh);

            playRequiredOpenActions(p, !requireKeyInHand, force);
            return true;
        }

        playFailToOpen(p, true, true);
        return false;
    }

    public void playSequence(final CSGOPlayerDataHolder cdh)
    {

        if (!cdh.getCurrentState().equals(CSGOPlayerDataHolder.State.COMPLETED))
        {
            Bukkit.getScheduler().scheduleSyncDelayedTask(getSc(), new Runnable()
            {
                @Override
                public void run()
                {
                    boolean update = updateTicks(cdh);

                    checkStateChange(cdh, update);

                    switch(cdh.getCurrentState())
                    {
                        case SCROLLING:
                            drawGlass(cdh);

                            if(update)
                            {
                                playSound(cdh);
                                updateRewards(cdh);
                            }

                            drawRewards(cdh, 0);
                            break;
                        case WAITING:
                            drawRewards(cdh, 0);
                            break;
                        case CLOSING:
                            if(cdh.getTotalTicks() % getCloseSpeed() == 0)
                            {
                                drawGlass(cdh);
                            }
                        case ENDING:
                            drawRewards(cdh, cdh.getAnimatedCloseTicks());
                            break;
                        case COMPLETED:
                            return;
                    }

                    drawIdentifierBlocks(cdh);
                    drawInventory(cdh);

                    playSequence(cdh);

                }
            }, (long) BASE_SPEED);
        }
    }

    public void checkStateChange(CSGOPlayerDataHolder cdh, boolean update)
    {
        switch(cdh.getCurrentState())
        {
            case SCROLLING:
                if (update && cdh.getCurrentTicks() > getFinalTickLength())
                    cdh.setCurrentState(CSGOPlayerDataHolder.State.WAITING);
                break;
            case WAITING:
                cdh.setWaitingTicks(cdh.getWaitingTicks() + 1);
                if(cdh.getWaitingTicks() == 10)
                {
                    if(getCloseSpeed() > - 1)
                        cdh.setCurrentState(CSGOPlayerDataHolder.State.CLOSING);
                    else
                        cdh.setCurrentState(CSGOPlayerDataHolder.State.ENDING);
                }
                break;
            case CLOSING:
                if(cdh.getTotalTicks() % getCloseSpeed() == 0)
                {
                    cdh.setAnimatedCloseTicks(cdh.getAnimatedCloseTicks() + 1);
                    if (cdh.getAnimatedCloseTicks() == 4)
                    {
                        cdh.setWaitingTicks(0);
                        cdh.setCurrentState(CSGOPlayerDataHolder.State.ENDING);
                    }
                }
                break;
            case ENDING:
                cdh.setWaitingTicks(cdh.getWaitingTicks() + 1);
                if(cdh.getWaitingTicks() == 40)
                {
                    endAnimation(cdh.getP());
                    cdh.setCurrentState(CSGOPlayerDataHolder.State.COMPLETED);
                }
        }
    }

    /**
     * Updates all of the tick values that track how fast and slow the plugin is going
     *
     * @return Returns whether or not the rewards should be updated and sound should be played.
     */
    public boolean updateTicks(CSGOPlayerDataHolder cdh)
    {
        cdh.setIndividualTicks(cdh.getIndividualTicks() + BASE_SPEED);
        cdh.setTotalTicks(cdh.getTotalTicks() + BASE_SPEED);

        if (cdh.getIndividualTicks() * BASE_SPEED >= cdh.getCurrentTicks() - 1.1)
        {
            cdh.setUpdates(cdh.getUpdates() + 1);
            cdh.setIndividualTicks(0);

            cdh.setCurrentTicks(.05 * Math.pow((getTickIncrease() / 40) + 1, cdh.getUpdates()));

            return true;
        }

        return false;
    }

    public void playSound(CSGOPlayerDataHolder cdh)
    {
        if (getTickSound() != null)
        {
            cdh.getP().playSound(cdh.getL(), getTickSound().getSound(), getTickSound().getVolume(),
                    getTickSound().getPitch());
        }
    }

    public void updateRewards(CSGOPlayerDataHolder cdh)
    {
        for (int i = 0; i < cdh.getDisplayedRewards().length; i++)
        {
            Reward r = cdh.getDisplayedRewards()[i];
            int numToSet = i - 1;

            if (r != null && numToSet >= 0)
            {
                cdh.getDisplayedRewards()[numToSet] = cdh.getDisplayedRewards()[i];
            }
        }

        Reward r = getCrate().getSettings().getRewards().getRandomReward(cdh.getP());
        cdh.getDisplayedRewards()[cdh.getDisplayedRewards().length - 1] = r;
    }

    public void drawInventory(CSGOPlayerDataHolder cdh)
    {
        if(cdh.getTotalTicks() % REDRAW_TICKS != 0)
            return;

        InventoryBuilder builder = cdh.getInv();

        // Open inventory if it has been closed
        if(!cdh.getP().getOpenInventory().getTopInventory().getType().equals(InventoryType.CHEST) ||
                cdh.getP().getOpenInventory().getTopInventory().getSize() != builder.getInv().getSize())
        {
            cdh.getP().openInventory(cdh.getInv().getInv());
        }

        // Redraw items into inventory if it is still open
        for(int i = 0; i < builder.getInv().getSize(); i++)
        {
            cdh.getP().getOpenInventory().getTopInventory().setItem(i, builder.getInv().getItem(i));
        }
    }

    public void drawGlass(CSGOPlayerDataHolder cdh)
    {
        if(cdh.getTotalTicks() % glassUpdateTicks != 0)
            return;

        InventoryBuilder inv = cdh.getInv();

        for (int i = 0; i < inv.getInv().getSize(); i++)
        {
            inv.setItem(i, getRandomFiller());
        }
    }

    public void drawIdentifierBlocks(CSGOPlayerDataHolder cdh)
    {
        InventoryBuilder inv = cdh.getInv();

        inv.setItem(4, getIdentifierBlock());
        inv.setItem(22, getIdentifierBlock());
    }

    public void drawRewards(CSGOPlayerDataHolder cdh, int sideIndent)
    {
        InventoryBuilder inv = cdh.getInv();

        for (int i = sideIndent; i < cdh.getDisplayedRewards().length - sideIndent; i++)
        {
            Reward r = cdh.getDisplayedRewards()[i];
            if (r != null)
            {
                inv.setItem(i + 10, r.getDisplayBuilder());
            }
        }
    }

    public ItemStack getRandomFiller()
    {
        Random r = new Random();
        return new ItemBuilder(getFillerBlocks().get(r.nextInt(getFillerBlocks().size()))).setName(" ").get();
    }

    @Override
    public void endAnimation(Player p)
    {
        CSGOPlayerDataHolder cdh = CSGOPlayerDataHolder.getHolders().get(p);
        cdh.setCurrentState(CSGOPlayerDataHolder.State.COMPLETED);
       // PlayerManager.get(crates.getCc(), p).setCanClose(null);

        ArrayList<Reward> rewards = new ArrayList<>();
        rewards.add(cdh.getDisplayedRewards()[cdh.getDisplayedRewards().length / 2]);

        completeCrateRun(p, rewards, false, null);
        getCrate().tick(cdh.getL(), CrateState.OPEN, p, rewards);
        cdh.getHolders().remove(p);
    }

    @Override
    public void loadValueFromConfig(StatusLogger sl)
    {


        try
        {
            String s = fu.get().getString("CrateType.Inventory.CSGO.inv-name").replace("%crate%", crates.getName());
            if (s.length() > 31)
            {
                s = s.substring(0, 31);
            }
            setInvName(s);
        }
        catch (Exception exc)
        {
            ChatUtils.log(new String[]{"Failed to load CSGO inv-name"});
        }

        try
        {
            String[] args = fu.get().getString("CrateType.Inventory.CSGO.tick-sound").replace(" ", "").split(",");

            SoundData sd = new SoundData(Sound.valueOf(args[0].toUpperCase()));

            StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_SOUND_SUCCESS.log(getStatusLogger());

            if (args.length >= 2)
            {
                if (Utils.isInt(args[1]))
                {
                    sd.setVolume(Integer.parseInt(args[1]));
                    StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_VOLUME_SUCCESS.log(getStatusLogger());
                }
                else
                {
                    sd.setVolume(5);
                    StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_VOLUME_INVALID.log(getStatusLogger(), new String[]{args[1]});
                }

                if (args.length >= 3)
                {
                    if (Utils.isInt(args[2]))
                    {
                        sd.setPitch(Integer.parseInt(args[2]));
                        StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_PITCH_SUCCESS.log(getStatusLogger());
                    }
                    else
                    {
                        sd.setPitch(5);
                        StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_PITCH_INVALID.log(getStatusLogger(), new String[]{args[2]});
                    }
                }
                else
                {
                    sd.setPitch(5);
                }
            }
            else
            {
                StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_VOLUMEPITCH_FAILURE.log(getStatusLogger());
                sd.setVolume(5);
                sd.setPitch(5);
            }

            setTickSound(sd);
        }
        catch (Exception exc)
        {
            //setTickSound(new SoundData(Sound.FALL_BIG));
            StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_SOUND_FAILURE.log(getStatusLogger());
        }

        try
        {
            String unParsed = fu.get().getString("CrateType.Inventory.CSGO.identifier-block");
            try
            {
                DynamicMaterial m = DynamicMaterial.fromString(unParsed.toUpperCase());
                setIdentifierBlock(new ItemBuilder(m, 1).setName(" ").get());
                StatusLoggerEvent.ANIMATION_CSGO_IDBLOCK_SUCCESS.log(getStatusLogger());
            }
            catch (Exception exc)
            {
                StatusLoggerEvent.ANIMATION_CSGO_IDBLOCK_INVALID.log(getStatusLogger(), new String[]{unParsed});
            }
        }
        catch (Exception exc)
        {
            setIdentifierBlock(new ItemStack(Material.AIR));
            StatusLoggerEvent.ANIMATION_CSGO_IDBLOCK_NONEXISTENT.log(getStatusLogger());
        }

        try
        {
            double d = Double.valueOf(fu.get().getString("CrateType.Inventory.CSGO.final-crate-tick-length"));
            setFinalTickLength(d);
            StatusLoggerEvent.ANIMATION_CSGO_FINALTICKLENGTH_SUCCESS.log(getStatusLogger());
        }
        catch (Exception exc)
        {
            setFinalTickLength(7);
            StatusLoggerEvent.ANIMATION_CSGO_FINALTICKLENGTH_INVALID.log(getStatusLogger());
        }

        try
        {
            glassUpdateTicks = Integer.parseInt(fu.get().getString("CrateType.Inventory.CSGO.tile-update-ticks"));
            StatusLoggerEvent.ANIMATION_CSGO_GLASSUPDATE_SUCCESS.log(getStatusLogger());
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.ANIMATION_CSGO_GLASSUPDATE_INVALID.log(getStatusLogger());
        }

        try
        {
            closeSpeed = Integer.parseInt(fu.get().getString("CrateType.Inventory.CSGO.close-speed"));
            StatusLoggerEvent.ANIMATION_CSGO_CLOSESPEED_SUCCESS.log(getStatusLogger());
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.ANIMATION_CSGO_CLOSESPEED_INVALID.log(getStatusLogger());
        }

        try
        {
            double d = Double.valueOf(fu.get().getString("CrateType.Inventory.CSGO.tick-speed-per-run"));
            setTickIncrease(d);
            StatusLoggerEvent.ANIMATION_CSGO_TICKSPEED_SUCCESS.log(getStatusLogger());
        }
        catch (Exception exc)
        {
            setTickIncrease(.4);
            StatusLoggerEvent.ANIMATION_CSGO_TICKSPEED_INVALID.log(getStatusLogger());
        }

        try
        {
            for (String unParsed : getFileHandler().get().getStringList("CrateType.Inventory.CSGO.filler-blocks"))
            {
                String[] args = unParsed.split(";");
                try
                {
                    DynamicMaterial m = null;
                    try
                    {
                        m = DynamicMaterial.fromString(unParsed.toUpperCase());
                    }
                    catch (Exception exc)
                    {
                        StatusLoggerEvent.ANIMATION_CSGO_FILLERBLOCK_MATERIAL_INVALID.log(getStatusLogger(), new String[]{args[0]});
                        continue;
                    }
                    int byt = unParsed.contains(";") ? Byte.valueOf(args[1]) : 0;
                    getFillerBlocks().add(new ItemBuilder(m, 1).get());

                    StatusLoggerEvent.ANIMATION_CSGO_FILLERBLOCK_MATERIAL_SUCCESS.log(getStatusLogger(), new String[]{unParsed});
                }
                catch (Exception exc)
                {
                    StatusLoggerEvent.ANIMATION_CSGO_FILLERBLOCK_ITEM_INVALID.log(getStatusLogger(), new String[]{unParsed});
                }
            }
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.ANIMATION_CSGO_FILLERBLOCK_NONEXISTENT.log(getStatusLogger());
        }
    }

    public SoundData getTickSound()
    {
        return tickSound;
    }

    public void setTickSound(SoundData tickSound)
    {
        this.tickSound = tickSound;
    }

    public String getInvName()
    {
        return invName;
    }

    public void setInvName(String invName)
    {
        this.invName = invName;
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

    public int getGlassUpdateTicks()
    {
        return glassUpdateTicks;
    }

    public void setGlassUpdateTicks(int glassUpdateTicks)
    {
        this.glassUpdateTicks = glassUpdateTicks;
    }

    public int getCloseSpeed()
    {
        return closeSpeed;
    }

    public void setCloseSpeed(int closeSpeed)
    {
        this.closeSpeed = closeSpeed;
    }

    public ItemStack getIdentifierBlock()
    {
        return identifierBlock;
    }

    public void setIdentifierBlock(ItemStack identifierBlock)
    {
        this.identifierBlock = identifierBlock;
    }

    public ArrayList<ItemStack> getFillerBlocks()
    {
        return fillerBlocks;
    }

    public void setFillerBlocks(ArrayList<ItemStack> fillerBlocks)
    {
        this.fillerBlocks = fillerBlocks;
    }
}
