package me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom;

import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
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

    public void load()
    {
        FileHandler fileHandler = customRewardDisplayer.getCrates().getCs().getFu();
        FileConfiguration fc = fileHandler.get();

        List<String> values = (List<String>) fc.getList(PREFIX + "." + pageNum);
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

        return ib;
    }
}
