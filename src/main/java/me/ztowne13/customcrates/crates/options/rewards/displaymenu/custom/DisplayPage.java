package me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.FileHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class DisplayPage
{
    public static String PREFIX = "reward-display.custom-display.pages";

    CustomRewardDisplayer customRewardDisplayer;
    int pageNum;
    int slots;

    String[][] unformattedInv;

    public DisplayPage(CustomRewardDisplayer customRewardDisplayer, int pageNum)
    {
        this.customRewardDisplayer = customRewardDisplayer;
        this.pageNum = pageNum;
    }

    public boolean load()
    {
        FileHandler fileHandler = customRewardDisplayer.getCrates().getCs().getFu();
        FileConfiguration fc = fileHandler.get();

        if(!fc.contains(PREFIX + "." + pageNum))
            return false;

        List<String> values = null;
        try
        {
            values = (List<String>) fc.getList(PREFIX + "." + pageNum);
        }
        catch(Exception exc)
        {
            exc.printStackTrace();
            return false;
        }

        slots = values.size() * 9;

        unformattedInv = new String[values.size()][9];

        for(int i = 0; i < values.size(); i++)
        {
            String line = values.get(i).replaceAll("\\s", "");
            String[] args = line.split(",");

            for (int j = 0; j < 9; j++)
            {
                if(args.length <= j)
                    unformattedInv[i][j] = "";
                else
                    unformattedInv[i][j] = args[j];
            }
        }

        return true;
    }

    public InventoryBuilder buildInventoryBuilder(Player player)
    {
        InventoryBuilder ib = new InventoryBuilder(player, slots, customRewardDisplayer.getInvName());

        for(int x = 0; x < unformattedInv.length; x++)
        {
            for(int y = 0; y < unformattedInv[0].length; y++)
            {
                String symbol = unformattedInv[x][y];

                if(!symbol.equalsIgnoreCase(""))
                {
                    int slot = (x*9) + y;

                    if(customRewardDisplayer.getItems().containsKey(symbol))
                    {
                        ib.setItem(slot, customRewardDisplayer.getItems().get(symbol));
                    }
                    else
                    {
                        Reward reward = customRewardDisplayer.getCrates().getCs().getCr().getByName(symbol);
                        if(reward != null)
                            ib.setItem(slot, reward.getDisplayBuilder());
                    }
                }
            }
        }

        ib.open();


        PlayerManager pm = PlayerManager.get(customRewardDisplayer.getCrates().getCc(), player);
        pm.setLastPage(this);
        pm.setInRewardMenu(true);

        return ib;
    }

    public void handleInput(Player player, int slot)
    {
        SpecializedCrates sc = customRewardDisplayer.getCrates().getCc();
        int x = slot / 9;
        sc.getDu().log("handleInput() - x: " + x, getClass());
        int y = slot % 9;
        sc.getDu().log("handleInput() - y: " + y, getClass());
        String symbolAt = unformattedInv[x][y];
        sc.getDu().log("handleInput() - symbolAt: " + symbolAt, getClass());
        sc.getDu().log("handleInput() - nextPageItem: " + customRewardDisplayer.getNextPageItem(), getClass());
        sc.getDu().log("handleInput() - backpageitem: " + customRewardDisplayer.getPrevPageItem(), getClass());

        if(symbolAt.equalsIgnoreCase(customRewardDisplayer.getNextPageItem()))
        {
            if(customRewardDisplayer.getPages().containsKey(pageNum + 1))
                customRewardDisplayer.getPages().get(pageNum + 1).buildInventoryBuilder(player);
            else
                ChatUtils.msgError(player, "Page " + (pageNum + 1) + " does not exist. Please contact an administrator" +
                        "to create the next page of the reward-preview menu OR remove this 'next page arrow' because there" +
                        "is no next page.");
        }
        else if(symbolAt.equalsIgnoreCase(customRewardDisplayer.getPrevPageItem()))
        {
            if(customRewardDisplayer.getPages().containsKey(pageNum - 1))
                customRewardDisplayer.getPages().get(pageNum - 1).buildInventoryBuilder(player);
            else
                ChatUtils.msgError(player, "Page " + (pageNum - 1) + " does not exist. Please contact an administrator" +
                        "to create the previous page of the reward-preview menu OR remove this 'previous page arrow' because there" +
                        "is no previous page.");
        }
    }
}
