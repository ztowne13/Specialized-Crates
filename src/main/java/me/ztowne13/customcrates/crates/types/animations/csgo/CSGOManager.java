package me.ztowne13.customcrates.crates.types.animations.csgo;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.options.sounds.SoundData;
import me.ztowne13.customcrates.crates.types.CrateType;
import me.ztowne13.customcrates.crates.types.InventoryCrateAnimation;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.logging.StatusLogger;
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class CSGOManager extends InventoryCrateAnimation
{

    protected SoundData tickSound = null;
    protected String invName = "";

    protected double finalTickLength, tickIncrease;
    protected int glassUpdateTicks = 2, closeSpeed = -1;

    protected ItemStack identifierBlock = null;
    protected ArrayList<ItemStack> fillerBlocks = new ArrayList<ItemStack>();

    public CSGOManager(Inventory inv, Crate crate)
    {
        super(CrateType.INV_CSGO.getPrefixDotted(), crate, inv);
    }

    @Override
    public boolean tick(Player p, Location l, CrateState cs, boolean requireKeyInHand)
    {
        if (canExecuteFor(cs, CrateState.OPEN, p, requireKeyInHand))
        {
            CSGODataHolder cdh = new CSGODataHolder(p, l, this);
            playSequence(cdh);
            playRequiredOpenActions(p, !requireKeyInHand);
            return true;
        }

        playFailToOpen(p);
        return false;
    }

    public void playSequence(final CSGODataHolder cdh)
    {
        if (!cdh.isCompleted())
        {
            Bukkit.getScheduler().scheduleSyncDelayedTask(getCc(), new Runnable()
            {
                @Override
                public void run()
                {
                    cdh.getP().openInventory(buildNewInventory(cdh, true, true, 0).getInv());
                    if (getTickSound() != null)
                    {
                        cdh.getP().playSound(cdh.getL(), getTickSound().getSound(), getTickSound().getVolume(),
                                getTickSound().getPitch());
                    }

                    if (cdh.getCurrentTicks() > cdh.getDisplayAmount())
                    {
                        finishUp(cdh.getP(), 20);
                        return;
                    }

                    cdh.setCurrentTicks(cdh.getCurrentTicks() + getTickIncrease());

                    playSequence(cdh);
                }
            }, (long) cdh.getCurrentTicks());
        }
    }

    public InventoryBuilder buildNewInventory(CSGODataHolder cdh, boolean updateGlass, boolean updateItems,
                                              int dontDisplayFromSides)
    {
        InventoryBuilder inv = cdh.getInv();

        for (int i = 0; i < inv.getInv().getSize(); i++)
        {
            if (i == 4 || i == 22)
            {
                inv.setItem(i, getIdentifierBlock());
            }
            else
            {
                if (updateGlass)
                {
                    inv.setItem(i, getRandomFiller());
                }
            }
        }

        if (updateItems)
        {
            for (int i = 0; i < cdh.getDisplayedRewards().length; i++)
            {
                Reward r = cdh.getDisplayedRewards()[i];
                int numToSet = i - 1;
                if (r != null && numToSet >= 0)
                {
                    cdh.getDisplayedRewards()[numToSet] = cdh.getDisplayedRewards()[i];

                    inv.setItem(numToSet + 10, cdh.getDisplayedRewards()[numToSet].getDisplayBuilder());
                }
            }

            Reward r = getCrates().getCs().getCr().getRandomReward(cdh.getP());
            cdh.getDisplayedRewards()[cdh.getDisplayedRewards().length - 1] = r;
            inv.setItem(cdh.getDisplayedRewards().length + 9, r.getDisplayBuilder());
        }
        else
        {
            for (int i = dontDisplayFromSides; i < cdh.getDisplayedRewards().length - dontDisplayFromSides; i++)
            {
                Reward r = cdh.getDisplayedRewards()[i];
                if (r != null)
                {
                    inv.setItem(i + 10, r.getDisplayBuilder());
                }
            }
        }

        return inv;
    }

    public void closeAnim(final CSGODataHolder cdh)
    {
        if (getCloseSpeed() > -1)
        {
            Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
            {
                @Override
                public void run()
                {
                    cdh.setAnimatedCloseTicks(cdh.getAnimatedCloseTicks() + 1);

                    buildNewInventory(cdh, true, false, cdh.getAnimatedCloseTicks());

                    if (cdh.getAnimatedCloseTicks() < 3)
                    {
                        closeAnim(cdh);
                    }
                }
            }, getCloseSpeed());
        }
    }

    public ItemStack getRandomFiller()
    {
        Random r = new Random();
        return new ItemBuilder(getFillerBlocks().get(r.nextInt(getFillerBlocks().size()))).setName(" ").get();
    }

    @Override
    public void finishUp(Player p)
    {
        CSGODataHolder cdh = CSGODataHolder.getHolders().get(p);
        cdh.setCompleted(true);

        ArrayList<Reward> rewards = new ArrayList<>();
        rewards.add(cdh.getDisplayedRewards()[cdh.getDisplayedRewards().length / 2]);

        completeCrateRun(p, rewards, false);
        getCrates().tick(cdh.getL(), CrateState.OPEN, p, rewards);
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

            StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_SOUND_SUCCESS.log(getSl());

            if (args.length >= 2)
            {
                if (Utils.isInt(args[1]))
                {
                    sd.setVolume(Integer.parseInt(args[1]));
                    StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_VOLUME_SUCCESS.log(getSl());
                }
                else
                {
                    sd.setVolume(5);
                    StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_VOLUME_INVALID.log(getSl(), new String[]{args[1]});
                }

                if (args.length >= 3)
                {
                    if (Utils.isInt(args[2]))
                    {
                        sd.setPitch(Integer.parseInt(args[2]));
                        StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_PITCH_SUCCESS.log(getSl());
                    }
                    else
                    {
                        sd.setPitch(5);
                        StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_PITCH_INVALID.log(getSl(), new String[]{args[2]});
                    }
                }
                else
                {
                    sd.setPitch(5);
                }
            }
            else
            {
                StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_VOLUMEPITCH_FAILURE.log(getSl());
                sd.setVolume(5);
                sd.setPitch(5);
            }

            setTickSound(sd);
        }
        catch (Exception exc)
        {
            //setTickSound(new SoundData(Sound.FALL_BIG));
            StatusLoggerEvent.ANIMATION_CSGO_TICKSOUND_SOUND_FAILURE.log(getSl());
        }

        try
        {
            String unParsed = fu.get().getString("CrateType.Inventory.CSGO.identifier-block");
            try
            {
                DynamicMaterial m = DynamicMaterial.fromString(unParsed.toUpperCase());
                setIdentifierBlock(new ItemBuilder(m, 1).setName(" ").get());
                StatusLoggerEvent.ANIMATION_CSGO_IDBLOCK_SUCCESS.log(getSl());
            }
            catch (Exception exc)
            {
                StatusLoggerEvent.ANIMATION_CSGO_IDBLOCK_INVALID.log(getSl(), new String[]{unParsed});
            }
        }
        catch (Exception exc)
        {
            setIdentifierBlock(new ItemStack(Material.AIR));
            StatusLoggerEvent.ANIMATION_CSGO_IDBLOCK_NONEXISTENT.log(getSl());
        }

        try
        {
            double d = Double.valueOf(fu.get().getString("CrateType.Inventory.CSGO.final-crate-tick-length"));
            setFinalTickLength(d);
            StatusLoggerEvent.ANIMATION_CSGO_FINALTICKLENGTH_SUCCESS.log(getSl());
        }
        catch (Exception exc)
        {
            setFinalTickLength(7);
            StatusLoggerEvent.ANIMATION_CSGO_FINALTICKLENGTH_INVALID.log(getSl());
        }

        try
        {
            glassUpdateTicks = Integer.parseInt(fu.get().getString("CrateType.Inventory.CSGO.tile-update-ticks"));
            StatusLoggerEvent.ANIMATION_CSGO_GLASSUPDATE_SUCCESS.log(getSl());
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.ANIMATION_CSGO_GLASSUPDATE_INVALID.log(getSl());
        }

        try
        {
            closeSpeed = Integer.parseInt(fu.get().getString("CrateType.Inventory.CSGO.close-speed"));
            StatusLoggerEvent.ANIMATION_CSGO_CLOSESPEED_SUCCESS.log(getSl());
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.ANIMATION_CSGO_CLOSESPEED_INVALID.log(getSl());
        }

        try
        {
            double d = Double.valueOf(fu.get().getString("CrateType.Inventory.CSGO.tick-speed-per-run"));
            setTickIncrease(d);
            StatusLoggerEvent.ANIMATION_CSGO_TICKSPEED_SUCCESS.log(getSl());
        }
        catch (Exception exc)
        {
            setTickIncrease(.4);
            StatusLoggerEvent.ANIMATION_CSGO_TICKSPEED_INVALID.log(getSl());
        }

        try
        {
            for (String unParsed : getFu().get().getStringList("CrateType.Inventory.CSGO.filler-blocks"))
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
                        StatusLoggerEvent.ANIMATION_CSGO_FILLERBLOCK_MATERIAL_INVALID.log(getSl(), new String[]{args[0]});
                        continue;
                    }
                    int byt = unParsed.contains(";") ? Byte.valueOf(args[1]) : 0;
                    getFillerBlocks().add(new ItemBuilder(m, 1).get());

                    StatusLoggerEvent.ANIMATION_CSGO_FILLERBLOCK_MATERIAL_SUCCESS.log(getSl(), new String[]{unParsed});
                }
                catch (Exception exc)
                {
                    StatusLoggerEvent.ANIMATION_CSGO_FILLERBLOCK_ITEM_INVALID.log(getSl(), new String[]{unParsed});
                }
            }
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.ANIMATION_CSGO_FILLERBLOCK_NONEXISTENT.log(getSl());
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
}
