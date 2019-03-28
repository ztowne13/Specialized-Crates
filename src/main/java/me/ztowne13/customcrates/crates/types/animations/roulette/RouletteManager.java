package me.ztowne13.customcrates.crates.types.animations.roulette;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.options.sounds.SoundData;
import me.ztowne13.customcrates.crates.types.InventoryCrate;
import me.ztowne13.customcrates.gui.DynamicMaterial;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.logging.StatusLogger;
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class RouletteManager extends InventoryCrate
{
    protected SoundData tickSound = null;
    protected String invName = "";
    protected double finalTickLength, tickIncrease;
    protected int glassUpdateTicks = 2;
    protected ArrayList<ItemStack> items;

    public RouletteManager(Inventory inv, Crate crates)
    {
        super(inv, crates);
    }

    @Override
    public boolean tick(Player p, Location l, CrateState cs, boolean requireKeyInHand)
    {
        if (canExecuteFor(cs, CrateState.OPEN, p, requireKeyInHand))
        {
            RouletteDataHolder rdh = new RouletteDataHolder(p, l, this);
            playSequence(rdh);
            playRequiredOpenActions(p, !requireKeyInHand);
            return true;
        }

        playFailToOpen(p);
        return false;
    }

    public void playSequence(final RouletteDataHolder rdh)
    {
        if (!rdh.isCompleted())
        {
            Bukkit.getScheduler().runTaskLater(getCc(), new Runnable()
            {
                @Override
                public void run()
                {
                    rdh.getP().openInventory(buildNewInventory(rdh, true, true).getInv());

                    if (getTickSound() != null)
                    {
                        rdh.getP().playSound(rdh.getL(), getTickSound().getSound(), getTickSound().getVolume(),
                                getTickSound().getPitch());
                    }

                    if (rdh.getCurrentTicks() > rdh.getDisplayAmount())
                    {
                        finishUp(rdh.getP(), 20);
                        return;
                    }

                    rdh.setCurrentTicks(rdh.getCurrentTicks() + getTickIncrease());

                    playSequence(rdh);
                }
            }, (long) rdh.getCurrentTicks());

        }
    }

    public InventoryBuilder buildNewInventory(RouletteDataHolder rdh, boolean updateGlass, boolean updateItems)
    {
        InventoryBuilder inv = rdh.getInv();
        for (int i = 0; i < inv.getInv().getSize(); i++)
        {
            if (i == 13)
            {
                if (updateItems)
                {
                    Reward r = getCrates().getCs().getCr().getRandomReward(rdh.getP());
                    rdh.setLastShownReward(r);
                    inv.setItem(i, r.getDisplayItem());
                }
                else
                {
                    inv.setItem(i, rdh.getLastShownReward().getDisplayItem());
                }
            }
            else
            {
                if (updateGlass)
                {
                    inv.setItem(i, getRandomFiller());
                }
            }
        }

        return inv;
    }

    public ItemStack getRandomFiller()
    {
        Random r = new Random();
        return new ItemBuilder(getItems().get(r.nextInt(getItems().size()))).setName(" ").get();
    }

    @Override
    public void finishUp(Player p)
    {
        RouletteDataHolder rdh = RouletteDataHolder.getHolders().get(p);
        rdh.setCompleted(true);

        ArrayList<Reward> rewards = new ArrayList<>();
        rewards.add(rdh.getLastShownReward());

        completeCrateRun(p, rewards, false);
        getCrates().tick(rdh.getL(), CrateState.OPEN, p, rewards);
        rdh.getHolders().remove(p);
    }

    @Override
    public void loadValueFromConfig(StatusLogger sl)
    {
        try
        {
            String s = fu.get().getString("CrateType.Inventory.Roulette.inv-name").replace("%crate%", crates.getName());
            if (s.length() > 31)
            {
                s = s.substring(0, 31);
            }
            setInvName(s);

            StatusLoggerEvent.ANIMATION_ROULETTE_INVENTORYNAME_SUCCESS.log(getSl(), new String[]{getInvName()});
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.ANIMATION_ROULETTE_INVENTORYNAME_NONEXISTENT.log(getSl());
            setInvName("Improperly configured");
        }

        try
        {
            String[] args = fu.get().getString("CrateType.Inventory.Roulette.tick-sound").replace(" ", "").split(",");

            SoundData sd = new SoundData(Sound.valueOf(args[0].toUpperCase()));

            StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_SOUND_SUCCESS.log(getSl());

            if (args.length >= 2)
            {
                if (Utils.isInt(args[1]))
                {
                    sd.setVolume(Integer.parseInt(args[1]));
                    StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_VOLUME_SUCCESS.log(getSl());
                }
                else
                {
                    sd.setVolume(5);
                    StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_VOLUME_INVALID.log(getSl(), new String[]{args[1]});
                }

                if (args.length >= 3)
                {
                    if (Utils.isInt(args[2]))
                    {
                        sd.setPitch(Integer.parseInt(args[2]));
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_PITCH_SUCCESS.log(getSl());
                    }
                    else
                    {
                        sd.setPitch(5);
                        StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_PITCH_INVALID.log(getSl(), new String[]{args[2]});
                    }
                }
                else
                {
                    sd.setPitch(5);
                }
            }
            else
            {
                StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_VOLUMEPITCH_FAILURE.log(getSl());
                sd.setVolume(5);
                sd.setPitch(5);
            }

            setTickSound(sd);
        }
        catch (Exception exc)
        {
            //setTickSound(new SoundData(Sound.FALL_BIG));
            StatusLoggerEvent.ANIMATION_ROULETTE_TICKSOUND_SOUND_FAILURE.log(getSl());
        }

        try
        {
            double d = Double.valueOf(fu.get().getString("CrateType.Inventory.Roulette.final-crate-tick-length"));
            setFinalTickLength(d);
            StatusLoggerEvent.ANIMATION_ROULETTE_FINALTICKLENGTH_SUCCESS.log(getSl());
        }
        catch (Exception exc)
        {
            setFinalTickLength(7);
            StatusLoggerEvent.ANIMATION_ROULETTE_FINALTICKLENGTH_INVALID.log(getSl());
        }

        try
        {
            glassUpdateTicks = Integer.parseInt(fu.get().getString("CrateType.Inventory.Roulette.tile-update-ticks"));
            StatusLoggerEvent.ANIMATION_ROULETTE_GLASSUPDATE_SUCCESS.log(getSl());
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.ANIMATION_ROULETTE_GLASSUPDATE_INVALID.log(getSl());
        }

        try
        {
            double d = Double.valueOf(fu.get().getString("CrateType.Inventory.Roulette.tick-speed-per-run"));
            setTickIncrease(d);
            StatusLoggerEvent.ANIMATION_ROULETTE_TICKSPEED_SUCCESS.log(getSl());
        }
        catch (Exception exc)
        {
            setTickIncrease(.4);
            StatusLoggerEvent.ANIMATION_ROULETTE_TICKSPEED_INVALID.log(getSl());
        }

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
                        StatusLoggerEvent.ANIMATION_ROULETTE_RANDOMBLOCK_MATERIAL_NONEXISTENT.log(getSl(), new String[]{s});
                        continue;
                    }
                    getItems().add(new ItemBuilder(m, 1).get());
                    StatusLoggerEvent.ANIMATION_ROULETTE_RANDOMBLOCK_MATERIAL_SUCCESS.log(getSl(), new String[]{s});
                }
                catch (Exception exc)
                {
                    StatusLoggerEvent.ANIMATION_ROULETTE_RANDOMBLOCK_ITEM_INVALID.log(getSl(), new String[]{s});
                }
            }
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.ANIMATION_ROULETTE_RANDOMBLOCK_NONEXISTENT.log(getSl());
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
