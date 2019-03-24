package me.ztowne13.customcrates.gui.ingame.rewards;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.gui.DynamicMaterial;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.gui.dynamicmenus.InputMenu;
import me.ztowne13.customcrates.gui.ingame.IGCDefaultItems;
import me.ztowne13.customcrates.gui.ingame.IGCMenu;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 3/21/16.
 */
public class IGCMenuRewards extends IGCMenu
{
    int page;

    public IGCMenuRewards(CustomCrates cc, Player p, IGCMenu lastMenu, int page)
    {
        super(cc, p, lastMenu, "&7&l> &6&lRewards.YML PG" + page);
        this.page = page;
    }

    @Override
    public void open()
    {
        getP().closeInventory();
        putInMenu();

        int slots = 0;

        if (CRewards.getAllRewards().size() - ((page - 1) * 28) > 28)
        {
            slots = 28;
        }
        else
        {
            slots = CRewards.getAllRewards().size() - ((page - 1) * 28);
        }
        slots = InventoryUtils.getRowsFor(2, slots) + 9;

        setInventoryName("&7&l> &6&lRewards.YML PG" + page);
        InventoryBuilder ib = createDefault(slots);

        ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());
        ib.setItem(9, IGCDefaultItems.RELOAD_BUTTON.getIb());
        ib.setItem(ib.getInv().getSize() - 9, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(4,
                new ItemBuilder(Material.PAPER, 1, 0).setName("&aCreate a new Reward").setLore("&7Click me to create a new")
                        .addLore("&7reward."));

        int i = 10;
        int toSkip = ((page - 1) * 28);
        int skipped = 0;
        int displayedRewards = 0;

        for (String rName : getCc().getRewardsFile().get().getKeys(false))
        {
            if (toSkip > skipped || displayedRewards >= 28)
            {
                skipped++;
                continue;
            }

            if (i % 9 == 8)
            {
                i += 2;
            }

            Reward r;

            if (CRewards.getAllRewards().keySet().contains(rName))
            {
                r = CRewards.getAllRewards().get(rName);
            }
            else
            {
                r = new Reward(getCc(), rName);
                r.loadFromConfig();
                r.loadChance();
                CRewards.allRewards.put(rName, r);
            }

            r.checkIsNeedMoreConfig();
            ItemBuilder newR;

            if (r.isNeedsMoreConfig())
                newR = new ItemBuilder(DynamicMaterial.BARRIER, 1).setName("&4&l" + rName)
                        .setLore("&cThis reward isn't fully configured,").addLore("&cplease fix it and reload the plugin.");
            else
                newR = new ItemBuilder(r.getDisplayItem()).setName("&a" + rName).setLore("&7Click to edit.");

            newR.addLore("").addLore("&7Used by crates:").addLore("");
            for (String s : r.delete(false).replace("[", "").replace("]", "").split(", "))
            {
                newR.addLore("&7- &f" + s);
            }


            ib.setItem(i, newR);
            i++;
            displayedRewards++;
        }

        if (page != 1)
        {
            ib.setItem(2, new ItemBuilder(Material.ARROW, 1, 0).setName("&aGo back a page"));
        }

        if ((CRewards.getAllRewards().size() / 28) + (CRewards.getAllRewards().size() % 28 == 0 ? 0 : 1) != page)
        {
            ib.setItem(6, new ItemBuilder(Material.ARROW, 1, 0).setName("&aGo forward a page"));
        }

        ib.open();
    }

    @Override
    public void manageClick(int slot)
    {
        if (slot == 0)
        {
            //getP().closeInventory();
            getCc().getRewardsFile().save();
            ChatUtils.msgSuccess(getP(), "Saved the Rewards.YML file.");
            //getCc().reload();
        }
        else if (slot == 2 && getIb().getInv().getItem(slot).getType() == Material.ARROW)
        {
            page--;
            open();
        }
        else if (slot == 6 && getIb().getInv().getItem(slot).getType() == Material.ARROW)
        {
            page++;
            open();
        }
        else if (slot == 9)
        {
            reload();
        }
        else if (slot == getIb().getInv().getSize() - 9)
        {
            up();
        }
        else if (slot == 4)
        {
            new InputMenu(getCc(), getP(), "rewardName", "null",
                    "No spaces allowed. No duplicate names. &7&oNote: These 'reward names' will never be seen by your player: they are just an 'identifier'.",
                    String.class, this);
        }
        else if (getIb().getInv().getItem(slot) != null)
        {
            String rName = ChatUtils.removeColor(getIb().getInv().getItem(slot).getItemMeta().getDisplayName());
            getP().closeInventory();
            new IGCMenuReward(getCc(), getP(), this, rName).open();
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        if (value.equalsIgnoreCase("rewardName"))
        {
            if (!input.contains(" "))
            {

                if (!CRewards.rewardNameExists(getCc(), input))
                {
                    new IGCMenuReward(getCc(), getP(), this, input).open();
                }
                else
                {
                    ChatUtils.msgError(getP(),
                            "This name already exists. &7&oNote: These 'reward names' will never be seen by your player: they are just an identifier.");
                }
            }
            else
            {
                ChatUtils.msgError(getP(),
                        "Your reward name cannot have a space. &7&oNote: These 'reward names' will never be seen by your player: they are just an identifier.");
            }
        }
        return false;
    }
}
