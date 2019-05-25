package me.ztowne13.customcrates.crates.types;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.logging.StatusLogger;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.players.data.events.HistoryEvent;
import me.ztowne13.customcrates.utils.FileHandler;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public abstract class CrateHead
{
    protected CustomCrates cc;
    protected Crate crates;
    protected FileHandler fu;

    public CrateHead(Crate crates)
    {
        this.crates = crates;
        this.cc = crates.getCc();
        this.fu = cc.getCrateconfigFile();
    }

    public abstract boolean tick(Player p, Location l, CrateState cs, boolean requireKeyInHand);

    public abstract void loadValueFromConfig(StatusLogger sl);

    public abstract void finishUp(Player p);

    public void finishUp(final Player p, long ticks)
    {
        Bukkit.getScheduler().scheduleSyncDelayedTask(getCc(), new Runnable()
        {
            @Override
            public void run()
            {
                finishUp(p);
            }
        }, ticks);
    }

    public boolean itemNotNull(Player p)
    {
        boolean b = false;
        try
        {
            b = p.getItemInHand() != null;
        }
        catch (Exception exc)
        {

        }

        return b;
    }

    public boolean playerPassesKeyTest(Player p, boolean requireKeyInHand)
    {
        CrateSettings settings = getCrates().getCs();
        return !settings.isRequireKey() ||
                (requireKeyInHand ? hasItemInHand(p, settings) : hasKeyInInventory(p, settings)) ||
                PlayerManager.get(cc, p).getPdm().getVCCrateData(getCrates()).getKeys() > 0;
    }

    public boolean hasItemInHand(Player p, CrateSettings settings)
    {
        try
        {
            return p.getItemInHand().getType().equals(settings.getKey(1).getType()) &&
                    p.getItemInHand().getItemMeta().getDisplayName()
                            .equalsIgnoreCase(settings.getKey(1).getItemMeta().getDisplayName()) &&
                    keysLoreMatches(p.getItemInHand(), settings);
        }
        catch (Exception exc)
        {
            return false;
        }
    }

    public boolean hasKeyInInventory(Player p, CrateSettings settings)
    {
        for (ItemStack stack : p.getInventory().getContents())
        {
            try
            {
                if (stack.getType().equals(settings.getKey(1).getType()) && stack.getItemMeta().getDisplayName()
                        .equalsIgnoreCase(settings.getKey(1).getItemMeta().getDisplayName()) &&
                        keysLoreMatches(stack, settings))
                {
                    return true;
                }
            }
            catch (Exception exc)
            {

            }
        }
        return false;
    }

    public boolean keysLoreMatches(ItemStack stack, CrateSettings settings)
    {
        if (!settings.getKey(1).hasItemMeta() || !settings.getKey(1).getItemMeta().hasLore())
        {
            return true;
        }
        if ((Boolean) SettingsValues.REQUIRE_KEY_LORE.getValue(getCc()) == true)
        {
            if (stack.hasItemMeta())
            {
                if (stack.getItemMeta().hasLore())
                {
                    for (int i = 0; i < settings.getKey(1).getItemMeta().getLore().size(); i++)
                    {
                        try
                        {
                            String stackLore = stack.getItemMeta().getLore().get(i);
                            String keyLore = settings.getKey(1).getItemMeta().getLore().get(i);
                            if (!stackLore.equalsIgnoreCase(keyLore))
                            {
                                return false;
                            }
                        }
                        catch (Exception exc)
                        {
                            return false;
                        }
                    }
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        return true;
    }

    public boolean canExecuteFor(CrateState current, CrateState cs, Player p, boolean requireKeyInHand)
    {
        boolean isOpenAction = current.equals(cs);
        boolean hasPropperOpeningTools =
                (itemNotNull(p) && playerPassesKeyTest(p, requireKeyInHand)) || !getCrates().getCs().isRequireKey();
        boolean passesInventoryCheck =
                !PlayerManager.get(cc, p).isInCrate() || PlayerManager.get(cc, p).getOpenCrate().isMultiCrate();

        return isOpenAction && hasPropperOpeningTools && passesInventoryCheck;
    }

    public void playFailToOpen(Player p)
    {
        playFailToOpen(p, true);
    }

    public void playFailToOpen(Player p, boolean playMessage)
    {
        playFailToOpen(p, true, true);
    }

    public void playFailToOpen(Player p, boolean playMessage, boolean failOpen)
    {
        if (!(p == null))
        {
            if ((Boolean) SettingsValues.PUSHBACK.getValue(getCc()) && !getCrates().isMultiCrate())
            {
                p.setVelocity(p.getLocation().getDirection().multiply(-1));
            }
            if (playMessage)
            {
                if(failOpen)
                    Messages.FAIL_OPEN.msgSpecified(cc, p);
                else
                    Messages.ALREADY_OPENING_CRATE.msgSpecified(cc, p);
            }
        }
    }

    public void playRequiredOpenActions(Player p, boolean fromInv)
    {
        PlayerManager pm = PlayerManager.get(cc, p);

        if (!(pm.getOpenCrate() == null) && pm.getOpenCrate().isMultiCrate() &&
                !getCrates().getCs().getOt().equals(ObtainType.STATIC))
        {
            PlacedCrate pc = PlacedCrate.get(cc, pm.getLastOpenCrate());
            pc.delete();
        }

        pm.openCrate(this.getCrates());
        //pm.setCanClose(false);
        if (getCrates().getCs().isRequireKey())
        {
            takeKeyFromPlayer(p, fromInv);
        }

        getCrates().getCs().getCa().playAll(p, true);
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
                            if (stack.getType().equals(getCrates().getCs().getKey(1).getType()) &&
                                    stack.getItemMeta().getDisplayName()
                                            .equalsIgnoreCase(getCrates().getCs().getKey(1).getItemMeta().getDisplayName()))
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
                if (stack.getType().equals(getCrates().getCs().getKey(1).getType()) && stack.getItemMeta().getDisplayName()
                        .equalsIgnoreCase(getCrates().getCs().getKey(1).getItemMeta().getDisplayName()))
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

    public void completeCrateRun(Player p)
    {
        PlayerManager pm = PlayerManager.get(getCc(), p);
        pm.setInRewardMenu(false);
        pm.setCanClose(true);
        pm.closeCrate();
        new HistoryEvent(Utils.currentTimeParsed(), getCrates(), null, true).addTo(PlayerManager.get(cc, p).getPdm());
    }

    public void completeCrateRun(Player p, ArrayList<Reward> rewards, boolean overrideAutoClose)
    {
        PlayerManager pm = PlayerManager.get(getCc(), p);
        pm.setInRewardMenu(false);
        pm.setCanClose(true);

        new HistoryEvent(Utils.currentTimeParsed(), getCrates(), rewards, true)
                .addTo(PlayerManager.get(getCc(), p).getPdm());

        if (!getCrates().getCs().isAutoClose() || overrideAutoClose)
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
    }

    public double getRandomTickTime(double basedOff)
    {
        Random r = new Random();
        return basedOff + r.nextInt(3) - r.nextInt(3);
    }

    public StatusLogger getSl()
    {
        return crates.getCs().getSl();
    }

    public Crate getCrates()
    {
        return crates;
    }

    public void setCrates(Crate crates)
    {
        this.crates = crates;
    }

    public CustomCrates getCc()
    {
        return cc;
    }

    public void setCc(CustomCrates cc)
    {
        this.cc = cc;
    }

    public FileHandler getFu()
    {
        return fu;
    }

    public void setFu(FileHandler fu)
    {
        this.fu = fu;
    }

}
