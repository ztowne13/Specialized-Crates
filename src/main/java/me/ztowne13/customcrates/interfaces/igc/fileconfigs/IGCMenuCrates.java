package me.ztowne13.customcrates.interfaces.igc.fileconfigs;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.InventoryUtils;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.crates.IGCCratesMain;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ztowne13 on 3/20/16.
 */
public class IGCMenuCrates extends IGCMenu
{
    int page;

    public IGCMenuCrates(SpecializedCrates cc, Player p, IGCMenu lastMenu)
    {
        this(cc, p, lastMenu, 1);
    }

    public IGCMenuCrates(SpecializedCrates specializedCrates, Player p, IGCMenu lastMenu, int page)
    {
        super(specializedCrates, p, lastMenu, "&7&l> &6&lCrates PG " + page);

        this.page = page;
    }

    @Override
    public void openMenu()
    {
        int values = Crate.getLoadedCrates().keySet().size();
        int inThisInv = values - ((page - 1) * 30);

        InventoryBuilder ib = createDefault(InventoryUtils.getRowsFor(4, values, page), 18);
        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(8,
                new ItemBuilder(Material.PAPER, 1, 0).setName("&aCreate a new crate").setLore("&7Please set the crate and")
                        .addLore("&7key once you are done configuring").addLore("&7for it to save properly."));

        ItemBuilder nextPage = new ItemBuilder(DynamicMaterial.ARROW, 1);
        nextPage.setDisplayName("&aNext Page");
        nextPage.addLore("").addAutomaticLore("&7", 30, "Click to go to the next page of crates.");

        ItemBuilder lastPage = new ItemBuilder(DynamicMaterial.ARROW, 1);
        lastPage.setDisplayName("&aPrevious Page");
        lastPage.addLore("").addAutomaticLore("&7", 30, "Click to go to the previous page of crates.");

        if(page != 1)
            ib.setItem(9, lastPage);
        if(inThisInv > 30)
            ib.setItem(18, nextPage);


        ArrayList<String> names = new ArrayList<>(Crate.getLoadedCrates().keySet());
        Collections.sort(names);

        int i = 2;
        int toSkip = ((page - 1) * 30);
        int skipped = 0;

        for (String crateName : names)
        {
            if (i % 9 == 7)
            {
                i += 4;
            }

            if(i > 54)
                break;

            if(skipped < toSkip)
            {
                skipped++;
            }
            else
            {
                Crate crate = Crate.getLoadedCrates().get(crateName);
                ib.setItem(i, new ItemBuilder(Material.CHEST, 1, 0).setName((crate.isEnabled() ? "&a" : "&c") + crateName)
                        .setLore("&7Placed crates: &f" + crate.getPlacedCount()).addLore(
                                "&7Errors: " + (crate.getSettings().getStatusLogger().getFailures() == 0 ? "&f" : "&c") +
                                        crate.getSettings().getStatusLogger().getFailures()));
                i++;
            }
        }

        getIb().open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot)
    {
        if (slot == 0)
            up();
        else if (slot == 8)
            new IGCMenuCrateOrMulticrate(getCc(), getP(), this).open();
        else if (slot == 9)
            new IGCMenuCrates(getCc(), getP(), getLastMenu(), page - 1).open();
        else if(slot == 18)
            new IGCMenuCrates(getCc(), getP(), getLastMenu(), page + 1).open();
        else if (getIb().getInv().getItem(slot) != null && getIb().getInv().getItem(slot).getType() == Material.CHEST)
        {
            String name = ChatUtils.removeColor(getIb().getInv().getItem(slot).getItemMeta().getDisplayName());
            new IGCCratesMain(getCc(), getP(), this, Crate.getCrate(getCc(), name)).open();
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        return false;
    }
}
