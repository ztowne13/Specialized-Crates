package me.ztowne13.customcrates.crates.types.animations;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.players.data.events.HistoryEvent;
import me.ztowne13.customcrates.utils.FileHandler;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public abstract class CrateAnimation
{
    protected SpecializedCrates cc;
    protected Crate crates;
    protected FileHandler fu;
    protected String prefix;

    public CrateAnimation(String prefix, Crate crates)
    {
        this.prefix = prefix;
        this.crates = crates;
        this.cc = crates.getCc();
        this.fu = cc.getCrateconfigFile();
    }

    public abstract boolean runAnimation(Player p, Location l, CrateState cs, boolean requireKeyInHand, boolean force);

    public abstract void loadValueFromConfig(StatusLogger sl);

    public abstract void endAnimation(Player p);

    public void endAnimationAfter(final Player p, long ticks)
    {
        Bukkit.getScheduler().scheduleSyncDelayedTask(getSc(), new Runnable()
        {
            @Override
            public void run()
            {
                endAnimation(p);
            }
        }, ticks);
    }

    public boolean playerPassesKeyTest(Player p, boolean requireKeyInHand)
    {
        if(p.getItemInHand() == null)
        {
            return false;
        }

        CrateSettings settings = getCrate().getSettings();
        return !settings.isRequireKey() ||
                (requireKeyInHand ?
                        crates.getSettings().getKeyItemHandler().keyMatchesToStack(p.getItemInHand(), true) :
                        crates.getSettings().getKeyItemHandler().hasKeyInInventory(p)) ||
                PlayerManager.get(cc, p).getPdm().getVCCrateData(getCrate()).getKeys() > 0;
    }

    public boolean canExecuteFor(CrateState current, CrateState cs, Player p, boolean requireKeyInHand)
    {
        PlayerManager playerManager = PlayerManager.get(getSc(), p);
        boolean isOpenAction = current.equals(cs);
        boolean hasPropperOpeningTools = playerPassesKeyTest(p, requireKeyInHand) || !getCrate().getSettings().isRequireKey();
        boolean passesInventoryCheck = !playerManager.isInCrate() || playerManager.getOpenCrate().isMultiCrate();

        return isOpenAction && hasPropperOpeningTools && passesInventoryCheck;
    }

    public void playFailToOpen(Player p, boolean playMessage, boolean failOpen)
    {
        if (!(p == null))
        {
            if ((Boolean) SettingsValues.PUSHBACK.getValue(getSc()) && !getCrate().isMultiCrate())
            {
                p.setVelocity(p.getLocation().getDirection().multiply(-1));
            }
            if (playMessage)
            {
                if(failOpen)
                    Messages.FAIL_OPEN.msgSpecified(cc, p, new String[]{"%crate%"}, new String[]{crates.getDisplayName()});
                else
                    Messages.ALREADY_OPENING_CRATE.msgSpecified(cc, p);
            }
        }
    }

    public void playRequiredOpenActions(Player p, boolean fromInv, boolean force)
    {
        PlayerManager pm = PlayerManager.get(cc, p);

        if (!(pm.getOpenCrate() == null) && pm.getOpenCrate().isMultiCrate() && !getCrate().getSettings().getObtainType().equals(ObtainType.STATIC))
        {
            PlacedCrate pc = PlacedCrate.get(cc, pm.getLastOpenCrate());
            pc.delete();
        }

        pm.openCrate(this.getCrate());

        if (getCrate().getSettings().isRequireKey() && !force)
        {
            takeKeyFromPlayer(p, fromInv);
        }

        getCrate().getSettings().getActions().playAll(p, true);
    }

    public void takeKeyFromPlayer(Player p, boolean fromInv)
    {
        boolean prioritzePhysical = (boolean) cc.getSettings().getConfigValues().get("prioritize-physical-key");
        if (!takeKeyFromPlayer(p, fromInv, prioritzePhysical))
        {
            takeKeyFromPlayer(p, fromInv, !prioritzePhysical);
        }
    }

    public boolean takeKeyFromPlayer(Player p, boolean fromInv, boolean checkPhysical)
    {

        if (checkPhysical)
        {
            if (fromInv)
            {
                for (int i = 0; i < 36; i++)
                {
                    try
                    {
                        if (!(p.getInventory().getItem(i) == null))
                        {
                            ItemStack stack = p.getInventory().getItem(i);
                            if (stack.getType().equals(getCrate().getSettings().getKeyItemHandler().getItem(1).getType()) &&
                                    stack.getItemMeta().getDisplayName()
                                            .equalsIgnoreCase(
                                                    getCrate().getSettings().getKeyItemHandler().getItem(1).getItemMeta().getDisplayName()))
                            {
                                if (stack.getAmount() == 1)
                                {
                                    p.getInventory().setItem(i, null);
                                }
                                else
                                {
                                    stack.setAmount(stack.getAmount() - 1);
                                }
                                return true;
                            }
                        }
                    }
                    catch (Exception exc)
                    {
                        exc.printStackTrace();
                    }
                }
            }
            else
            {
                ItemStack stack = p.getItemInHand();
                if (stack.getType().equals(getCrate().getSettings().getKeyItemHandler().getItem(1).getType()) && stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName()
                        .equalsIgnoreCase(getCrate().getSettings().getKeyItemHandler().getItem(1).getItemMeta().getDisplayName()))
                {
                    if (p.getItemInHand().getAmount() == 1)
                    {
                        p.setItemInHand(null);
                    }
                    else
                    {
                        ItemStack st = p.getItemInHand();
                        st.setAmount(st.getAmount() - 1);
                        p.setItemInHand(st);
                    }
                    return true;
                }
            }
        }
        else
        {
            PlayerDataManager pdm = PlayerManager.get(cc, p).getPdm();
            if (pdm.getVCCrateData(crates).getKeys() > 0)
            {
                pdm.setVirtualCrateKeys(crates, pdm.getVCCrateData(crates).getKeys() - 1);
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public void completeCrateRun(Player p)
    {
        PlayerManager pm = PlayerManager.get(getSc(), p);
        pm.setInRewardMenu(false);
        pm.closeCrate();
        new HistoryEvent(Utils.currentTimeParsed(), getCrate(), null, true).addTo(PlayerManager.get(cc, p).getPdm());
    }

    public void completeCrateRun(Player p, ArrayList<Reward> rewards, boolean overrideAutoClose, final PlacedCrate placedCrate)
    {
        PlayerManager pm = PlayerManager.get(getSc(), p);
        pm.setInRewardMenu(false);

        new HistoryEvent(Utils.currentTimeParsed(), getCrate(), rewards, true)
                .addTo(PlayerManager.get(getSc(), p).getPdm());

        if (!getCrate().getSettings().isAutoClose() || overrideAutoClose)
        {
            pm.setWaitingForClose(rewards);
            return;
        }

        for (Reward r : rewards)
        {
            r.runCommands(p);
        }

        pm.closeCrate();
        p.closeInventory();

        // Handle the DYNAMIC crates
        if(getCrate().getSettings().getCrateType().isSpecialDynamicHandling() && !getCrate().getSettings().getObtainType().isStatic())
        {
            if(getCrate().getSettings().getCrateType().getCategory().equals(CrateType.Category.CHEST))
            {
                Bukkit.getScheduler().runTaskLater(cc, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        placedCrate.delete();
                        placedCrate.getL().getBlock().setType(Material.AIR);
                    }
                }, 20);
            }
            else
            {
                placedCrate.delete();
                placedCrate.getL().getBlock().setType(Material.AIR);
            }
        }
    }

    public double getRandomTickTime(double basedOff)
    {
        Random r = new Random();
        return basedOff + r.nextInt(3) - r.nextInt(3);
    }

    public StatusLogger getStatusLogger()
    {
        return crates.getSettings().getStatusLogger();
    }

    public Crate getCrate()
    {
        return crates;
    }

    public void setCrate(Crate crates)
    {
        this.crates = crates;
    }

    public SpecializedCrates getSc()
    {
        return cc;
    }

    public void setSc(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    public FileHandler getFileHandler()
    {
        return fu;
    }
}
