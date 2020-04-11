package me.ztowne13.customcrates.interfaces.igc.crates;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.InventoryUtils;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.fileconfigs.rewards.IGCMenuAddReward;
import me.ztowne13.customcrates.interfaces.igc.fileconfigs.rewards.IGCMenuRewards;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 4/3/16.
 */
public class IGCCrateRewards extends IGCMenuCrate
{
    boolean deleteMode = false;
    int page;

    public IGCCrateRewards(SpecializedCrates cc, Player p, IGCMenu lastMenu, Crate crates, int page)
    {
        super(cc, p, lastMenu, "&7&l> &6&lRewards PG" + page, crates);
        this.page = page;
    }

    @Override
    public void open()
    {

        setInventoryName("&7&l> &6&lReward PG" + page);

        int slots = 0;
        if (!(cs.getRewards().getCrateRewards() == null))
        {
            if (cs.getRewards().getCrateRewards().length - ((page - 1) * 30) > 30)
            {
                slots = 54;
            }
            else
            {
                slots = InventoryUtils.getRowsFor(4, cs.getRewards().getCrateRewards().length - ((page - 1) * 30));
            }
        }
        if (slots < 27)
        {
            slots = 27;
        }
        else if (slots > 54)
        {
            slots = 54;
        }

        InventoryBuilder ib = createDefault(slots);


        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
        if (!deleteMode)
        {
            getIb().setItem(8, new ItemBuilder(DynamicMaterial.RED_CARPET, 1).setName("&aEnable 'remove' mode")
                    .setLore("&7By enabling 'remove' mode").addLore("&7you can just click on rewards")
                    .addLore("&7to remove "));
        }
        else
        {
            getIb().setItem(8, new ItemBuilder(DynamicMaterial.RED_CARPET, 1).setName("&cDisable 'remove' mode")
                    .setLore("&7This will stop you from").addLore("&7removing rewards"));
        }
        ib.setItem(17, new ItemBuilder(Material.PAPER, 1, 0).setName("&aAdd a reward to this crate")
                .setLore("&7Reminder: you must save for").addLore("&7any changes to take effect."));
        ib.setItem(26, new ItemBuilder(DynamicMaterial.LIGHT_BLUE_DYE, 1).setName("&aGo to the reward editor")
                .setLore("&7This is a shortcut to the reward").addLore("&7menu so you can create rewards")
                .addLore("&7without jumping around the menus."));

        int toSkip = ((page - 1) * 30);
        int skipped = 0;
        int displayedRewards = 0;

        if (cs.getRewards().getCrateRewards() != null && cs.getRewards().getCrateRewards().length != 0)
        {
            int i = 2;
            for (Reward r : crates.getSettings().getRewards().getCrateRewards())
            {
                if (toSkip > skipped || displayedRewards >= 30)
                {
                    skipped++;
                    continue;
                }

                if (i % 9 == 7)
                {
                    i += 4;
                }

                ib.setItem(i, new ItemBuilder(r.getDisplayBuilder()).setName("&a" + r.getRewardName())
                        .setLore("&7- " + r.getDisplayName(true)).addLore("&7- " + r.getChance())
                        .addLore("&7- " + r.getRarity()));
                i++;
                displayedRewards++;
            }

            if (page != 1)
            {
                ib.setItem(9, new ItemBuilder(Material.ARROW, 1, 0).setName("&aGo up a page"));
            }

            if ((cs.getRewards().getCrateRewards().length / 30) + 1 != page)
            {
                ib.setItem(18, new ItemBuilder(Material.ARROW, 1, 0).setName("&aGo down a page"));
            }

        }

        ib.open();
        putInMenu();
    }

    @Override
    public void manageClick(int slot)
    {
        if (getIb().getInv().getItem(slot) == null)
            return;

        if (slot == 0)
        {
            up();
        }
        else if (slot == 9)
        {
            page--;
            open();
        }
        else if (slot == 18)
        {
            page++;
            open();
        }
        else if (slot == 8)
        {
            deleteMode = !deleteMode;
            if (!deleteMode)
            {
                getIb().setItem(8, new ItemBuilder(DynamicMaterial.RED_CARPET, 1).setName("&aEnable 'remove' mode")
                        .setLore("&7By enabling 'remove' mode").addLore("&7you can just click on rewards")
                        .addLore("&7to remove "));
            }
            else
            {
                getIb().setItem(8, new ItemBuilder(DynamicMaterial.RED_CARPET, 1).setName("&cDisable 'remove' mode")
                        .setLore("&7This will stop you from").addLore("&7removing rewards"));
            }
        }
//        else if (slot == 17)
//        {
//            new InputMenu(getCc(), getP(), "add reward", "null",
//                    "Addable rewards: " + getCc().getRewardsFile().get().getKeys(false).toString(), String.class, this, true);
//        }
        else if(slot == 17)
        {
            new IGCMenuAddReward(getCc(), getP(), this, crates, 1).open();
        }
        else if (slot == 26)
        {
            new IGCMenuRewards(getCc(), getP(), this, 1).open();
        }
        else if (getIb().getInv().getItem(slot) != null)
        {
            if (deleteMode)
            {
                String rName = ChatUtils.removeColor(getIb().getInv().getItem(slot).getItemMeta().getDisplayName());
                cs.getRewards().removeReward(rName);
                open();
            }
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        if (value.equalsIgnoreCase("add reward"))
        {
            if (cs.getRewards().addReward(input))
            {
                ChatUtils.msgSuccess(getP(), "Added the reward " + input);
                return true;
            }
            ChatUtils.msgError(getP(), input + " is not an existing reward OR is not fully/properly configured.");
        }
        return false;
    }
}
