package me.ztowne13.customcrates.crates.types.animations.enclosement;

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
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by ztowne13 on 7/5/16.
 */
public class EnclosementAnimation extends InventoryCrateAnimation
{

    String invName;
    int inventoryRows, updateSpeed, rewardAmount;
    SoundData tickSound;
    ItemStack fillerItem;

    public EnclosementAnimation(Inventory inv, Crate crate)
    {
        super(CrateType.INV_ENCLOSE.getPrefixDotted(), crate, inv);
    }

    @Override
    public boolean tick(Player p, Location l, CrateState cs, boolean requireKeyInHand)
    {
        if (canExecuteFor(cs, CrateState.OPEN, p, requireKeyInHand))
        {
            EnclosementDataHolder edh = new EnclosementDataHolder(p, l, this);
            playSequence(edh, true);
            playRequiredOpenActions(p, !requireKeyInHand);
            return true;
        }

        playFailToOpen(p);
        return false;
    }

    public void playSequence(final EnclosementDataHolder edh, final boolean first)
    {
        if (first)
        {
            buildNewInventory(edh);
        }

        if (!edh.isCompleted())
        {
            Bukkit.getScheduler().scheduleSyncDelayedTask(getCc(), new Runnable()
            {
                @Override
                public void run()
                {
                    edh.setCurrentTicksIn(edh.getCurrentTicksIn() - 1);

                    if (getTickSound() != null)
                    {
                        edh.getP().playSound(edh.getL(), getTickSound().getSound(), getTickSound().getVolume(),
                                getTickSound().getPitch());
                    }

                    buildNewInventory(edh);

                    if (first || !edh.getP().getOpenInventory().getTitle()
                            .equals(edh.getIb().getName()))
                    {
                        edh.getIb().open();
                    }

                    if (edh.getCurrentTicksIn() <= 0)
                    {
                        finishUp(edh.getP(), 20);
                        return;
                    }

                    playSequence(edh, false);
                }
            }, updateSpeed);
        }
    }

    public InventoryBuilder buildNewInventory(EnclosementDataHolder edh)
    {
        InventoryBuilder ib = edh.getIb();

        edh.getLastDisplayRewards().clear();
        for (int i = 0; i < ib.getInv().getSize(); i++)
        {
            ib.setItem(i, new ItemBuilder(fillerItem).setName(" "));
        }

        int midPoint = (ib.getInv().getSize() / 2) + 1;
        for (int i = 0; i < ib.getInv().getSize(); i++)
        {
            if (i >= midPoint - edh.getCurrentTicksIn() - 1 - rewardAmount &&
                    i < midPoint + edh.getCurrentTicksIn() + rewardAmount)
            {
                Reward r = getCrates().getCs().getCr().getRandomReward(edh.getP());
                ib.setItem(i, r.getDisplayBuilder());
                edh.getLastDisplayRewards().add(r);
            }
        }

        return ib;
    }

    @Override
    public void finishUp(Player p)
    {
        EnclosementDataHolder edh = EnclosementDataHolder.getHolders().get(p);
        edh.setCompleted(true);

        completeCrateRun(p, edh.getLastDisplayRewards(), false);
        getCrates().tick(edh.getL(), CrateState.OPEN, p, edh.getLastDisplayRewards());

        edh.getHolders().remove(p);
    }

    @Override
    public void loadValueFromConfig(StatusLogger sl)
    {
        FileConfiguration fc = cc.getCrateconfigFile().get();
        try
        {
            setInvName(fc.getString(prefix + "inv-name").replace("%crate%", crates.getName()));
            StatusLoggerEvent.ANIMATION_ENCLOSEMENT_INVNAME_SUCCESS.log(getSl());
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.ANIMATION_ENCLOSEMENT_INVNAME_INVALID.log(getSl());
        }

        try
        {
            setInventoryRows(Integer.parseInt(fc.getString(prefix + "inventory-rows")));
            StatusLoggerEvent.ANIMATION_ENCLOSEMENT_INVROWS_SUCCESS.log(getSl());
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.ANIMATION_ENCLOSEMENT_INVROWS_INVALID.log(getSl());
        }

        String cause = "The 'fill-block' value is non-existent.";
        try
        {
            String s = fc.getString(prefix + "fill-block");
            cause = s + " is not a valid material.";

            DynamicMaterial m = DynamicMaterial.fromString(s);

            fillerItem = new ItemBuilder(m, 1).setName(" ").get();
            StatusLoggerEvent.ANIMATION_ENCLOSEMENT_FILLBLOCK_SUCCESS.log(getSl());
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.ANIMATION_ENCLOSEMENT_FILLBLOCK_INVALID.log(getSl(), new String[]{cause});
        }

        try
        {
            String[] args = fu.get().getString(prefix + "tick-sound").replace(" ", "").split(",");

            SoundData sd = new SoundData(Sound.valueOf(args[0].toUpperCase()));

            StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_SOUND_SUCCESS.log(getSl());

            if (args.length >= 2)
            {
                if (Utils.isInt(args[1]))
                {
                    sd.setVolume(Integer.parseInt(args[1]));
                    StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_VOLUME_SUCCESS.log(getSl());
                }
                else
                {
                    sd.setVolume(5);
                    StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_VOLUME_INVALID.log(getSl(), new String[]{args[1]});
                }

                if (args.length >= 3)
                {
                    if (Utils.isInt(args[2]))
                    {
                        sd.setPitch(Integer.parseInt(args[2]));
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_PITCH_SUCCESS.log(getSl());
                    }
                    else
                    {
                        sd.setPitch(5);
                        StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_PITCH_INVALID.log(getSl(), new String[]{args[2]});
                    }
                }
                else
                {
                    sd.setPitch(5);
                }
            }
            else
            {
                StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_VOLUMEPITCH_FAILURE.log(getSl());
                sd.setVolume(5);
                sd.setPitch(5);
            }

            setTickSound(sd);
        }
        catch (Exception exc)
        {
            //setTickSound(new SoundData(Sound.FALL_BIG));
            StatusLoggerEvent.ANIMATION_ENCLOSEMENT_TICKSOUND_SOUND_FAILURE.log(getSl());
        }

        try
        {
            updateSpeed = Integer.parseInt(fc.getString(prefix + "update-speed"));
            StatusLoggerEvent.ANIMATION_ENCLOSEMENT_UPDATESPEED_SUCCESS.log(getSl());
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.ANIMATION_ENCLOSEMENT_UPDATESPEED_INVALID.log(getSl());
        }

        try
        {
            rewardAmount = Integer.parseInt(fc.getString(prefix + "reward-amount"));
            if (rewardAmount % 2 == 0)
            {
                rewardAmount++;
            }

            rewardAmount--;

            rewardAmount = rewardAmount == 0 ? 0 : rewardAmount / 2;

            StatusLoggerEvent.ANIMATION_ENCLOSEMENT_REWARDAMOUNT_SUCCESS.log(getSl());
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.ANIMATION_ENCLOSEMENT_REWARDAMOUNT_INVALID.log(getSl());
        }
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
