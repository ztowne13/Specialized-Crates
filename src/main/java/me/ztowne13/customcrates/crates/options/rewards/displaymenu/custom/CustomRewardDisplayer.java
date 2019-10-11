package me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.RewardDisplayer;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.items.SaveableItemBuilder;
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.FileHandler;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CustomRewardDisplayer extends RewardDisplayer
{
    public static String PREFIX = "reward-display.custom-display.items";

    HashMap<Integer, DisplayPage> pages = new HashMap<>();
    HashMap<String, ItemBuilder> items = new HashMap<>();
    String nextPageItem;
    String prevPageItem;

    public CustomRewardDisplayer(Crate crate)
    {
        super(crate);
    }

    @Override
    public void openFor(Player p)
    {
        p.openInventory(createInventory(p).getInv());
        PlayerManager.get(getCrates().getCc(), p).setInRewardMenu(true);
    }

    @Override
    public InventoryBuilder createInventory(Player p)
    {
        if(pages.containsKey(1))
            pages.get(1).buildInventoryBuilder(p);
        return null;
    }

    @Override
    public void load()
    {
        loadDefaults();

        FileHandler fileHandler = getCrates().getCs().getFu();
        FileConfiguration fc = fileHandler.get();

        ConfigurationSection configSection = fc.getConfigurationSection(PREFIX);

        for(String key : configSection.getKeys(false))
        {
            SaveableItemBuilder itemBuilder = new SaveableItemBuilder(DynamicMaterial.STONE, 1);
            itemBuilder.loadItem(getCrates().getCs().getFu(), PREFIX + "." + key, getCrates().getCs().getSl(),
                    StatusLoggerEvent.SETTINGS_REWARD_DISPLAYER_ITEM_FAILURE,
                    StatusLoggerEvent.SETTINGS_REWARD_DISPLAYER_ENCHANTMENT_ADD_FAILURE,
                    StatusLoggerEvent.SETTINGS_REWARD_DISPLAYER_POTION_ADD_FAILURE,
                    StatusLoggerEvent.SETTINGS_REWARD_DISPLAYER_GLOW_FAILURE,
                    StatusLoggerEvent.SETTINGS_REWARD_DISPLAYER_AMOUNT_FAILURE);
            items.put(key, itemBuilder);
        }

        configSection = fc.getConfigurationSection(DisplayPage.PREFIX);

        for(String key : configSection.getKeys(false))
        {
            if(Utils.isInt(key))
            {
                int pageNum = Integer.parseInt(key);
                DisplayPage displayPage = new DisplayPage(this, pageNum);
                displayPage.load();

                pages.put(pageNum, displayPage);
            }
        }
    }

    public HashMap<String, ItemBuilder> getItems()
    {
        return items;
    }

    public String getNextPageItem()
    {
        return nextPageItem;
    }

    public String getPrevPageItem()
    {
        return prevPageItem;
    }
}
