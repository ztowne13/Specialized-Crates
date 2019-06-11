package me.ztowne13.customcrates.interfaces.igc.fileconfigs.rewards;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Created by ztowne13 on 8/7/16.
 */
public class IGCDragAndDrop extends IGCMenu
{
    public IGCDragAndDrop(CustomCrates cc, Player p, IGCMenu lastMenu)
    {
        super(cc, p, lastMenu, "&4&lDrag and Drop");
    }

    @Override
    public void open()
    {

        InventoryBuilder ib = createDefault(54);

        ib.setItem(53, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(52, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());

        setIb(ib);
        getIb().open();
        putInMenu();
    }

    @Override // For this menu it has been repurposed to be used as the "save" function
    public void manageClick(int slot)
    {
        if(slot == 53)
        {
            for (int i = 0; i < getIb().getInv().getSize() - 2; i++)
            {
                ItemStack stack = getIb().getInv().getItem(i);
                if (stack != null && !stack.getType().equals(Material.AIR))
                {
                    getP().getInventory().addItem(stack);
                }
            }
            ChatUtils.msgInfo(getP(), "Added all items back to your inventory.");
            up();
        }
        else if(slot == 52)
        {
            ArrayList<String> cmds = new ArrayList<>();

            for (int i = 0; i < getIb().getInv().getSize() - 2; i++)
            {
                ItemStack stack = getIb().getInv().getItem(i);
                if (stack != null && !stack.getType().equals(Material.AIR))
                {
                    String rewardName = getNameFor(stack);
                    Reward r = new Reward(getCc(), rewardName);

                    r.setChance(10);
                    r.setRarity("default");
                    r.setCommands((ArrayList<String>) cmds.clone());
                    r.setItemBuilder(new ItemBuilder(stack));
                    r.setGiveDisplayItem(true);
                    r.setGiveDisplayItemLore(false);
                    r.setNeedsMoreConfig(false);

                    r.writeToFile();
                }
            }
            ChatUtils.msgSuccess(getP(), "Saved! Remember to update the commands and chance's for all the rewards");
            up();
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        return false;
    }

    public String getNameFor(ItemStack stack)
    {

        String rewardName = ChatUtils.removeColor(
                stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName() :
                        stack.getType().name().toLowerCase());
        rewardName = rewardName.replaceAll(" ", "_");

        int i = 0;
        while (true)
        {
            String name = rewardName + (i == 0 ? "" : i);
            if (!CRewards.rewardNameExists(getCc(), name))
            {

                return name;
            }
            i++;
        }
    }
}
