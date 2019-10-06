package me.ztowne13.customcrates.crates.types.animations.menu;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.CrateType;
import me.ztowne13.customcrates.crates.types.InventoryCrateAnimation;
import me.ztowne13.customcrates.logging.StatusLogger;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Random;

public class MenuAnimation extends InventoryCrateAnimation
{
    protected String invName = "";
    protected int minRewards, maxRewards, inventoryRows = 0;

    public MenuAnimation(Inventory inv, Crate crate)
    {
        super(CrateType.INV_MENU.getPrefixDotted(), crate, inv);
    }

    @Override
    public boolean tick(Player p, Location l, CrateState cs, boolean requireKeyInHand)
    {
        if (canExecuteFor(cs, CrateState.OPEN, p, requireKeyInHand))
        {
            MenuDataHolder mdh = new MenuDataHolder(p, l, this);
            playSequence(mdh);
            playRequiredOpenActions(p, !requireKeyInHand);
            return true;
        }

        playFailToOpen(p);
        return false;
    }

    public void playSequence(MenuDataHolder mdh)
    {
        Random r = new Random();

        int amountOfRewards = r.nextInt(getMaxRewards() - getMinRewards()) + getMinRewards();

        for (int i = 0; i < amountOfRewards; i++)
        {
            int slot = Utils.getRandomNumberExcluding((getInventoryRows() * 9) - 1, mdh.getUsedNumbers());
            mdh.getUsedNumbers().add(slot);

            Reward reward = getCrates().getCs().getCr().getRandomReward(mdh.getP());
            mdh.getDisplayedRewards().add(reward);

            mdh.getInv().setItem(slot, reward.getDisplayBuilder());
        }

        mdh.getInv().open();

        finishUp(mdh.getP(), 50);
    }

    @Override
    public void finishUp(Player p)
    {
        MenuDataHolder mdh = MenuDataHolder.getHolders().get(p);

        completeCrateRun(p, mdh.getDisplayedRewards(), true);
        getCrates().tick(mdh.getL(), CrateState.OPEN, p, mdh.getDisplayedRewards());
        MenuDataHolder.getHolders().remove(p);
    }

    @Override
    public void loadValueFromConfig(StatusLogger sl)
    {
        try
        {
            String s = fu.get().getString("CrateType.Inventory.Menu.inv-name").replace("%crate%", crates.getName());
            if (s.length() > 31)
            {
                s = s.substring(0, 31);
            }
            setInvName(s);
        }
        catch (Exception exc)
        {
            ChatUtils.log(new String[]{"Failed to load Menu inv-name"});
        }

        try
        {
            setMinRewards(fu.get().getInt("CrateType.Inventory.Menu.minimum-rewards"));
        }
        catch (Exception exc)
        {
            ChatUtils.log(new String[]{"Failed to load Menu minimum-rewards, it is invalid."});
        }

        try
        {
            setMaxRewards(fu.get().getInt("CrateType.Inventory.Menu.maximum-rewards"));
        }
        catch (Exception exc)
        {
            ChatUtils.log(new String[]{"Failed to load Menu maximum-rewards, it is invalid."});
        }

        try
        {
            setInventoryRows(fu.get().getInt("CrateType.Inventory.Menu.inventory-rows"));
        }
        catch (Exception exc)
        {
            ChatUtils.log(new String[]{"Failed to load Menu inventory-rows, it is invalid."});
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

    public int getMinRewards()
    {
        return minRewards;
    }

    public void setMinRewards(int minRewards)
    {
        this.minRewards = minRewards;
    }

    public int getMaxRewards()
    {
        return maxRewards;
    }

    public void setMaxRewards(int maxRewards)
    {
        this.maxRewards = maxRewards;
    }

    public int getInventoryRows()
    {
        return inventoryRows;
    }

    public void setInventoryRows(int inventoryRows)
    {
        this.inventoryRows = inventoryRows;
    }


}
