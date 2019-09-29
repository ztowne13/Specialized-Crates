package me.ztowne13.customcrates.interfaces.igc;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.crates.types.CrateType;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.fileconfigs.IGCMenuConfig;
import me.ztowne13.customcrates.interfaces.igc.fileconfigs.IGCMenuCrateConfig;
import me.ztowne13.customcrates.interfaces.igc.fileconfigs.IGCMenuCrates;
import me.ztowne13.customcrates.interfaces.igc.fileconfigs.IGCMenuMessages;
import me.ztowne13.customcrates.interfaces.igc.fileconfigs.rewards.IGCMenuRewards;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 3/11/16.
 */
public class IGCMenuMain extends IGCMenu
{
    public IGCMenuMain(CustomCrates cc, Player p, IGCMenu lastMenu)
    {
        super(cc, p, lastMenu, "&7&l> &6&lHome");
    }

    @Override
    public void open()
    {
        InventoryBuilder ib = createDefault(27);
        ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb().setName("&aSave every file/crate"));
        ib.setItem(9, IGCDefaultItems.RELOAD_BUTTON.getIb());
        ib.setItem(18, IGCDefaultItems.EXIT_BUTTON.getIb());

        ItemBuilder paper = new ItemBuilder(Material.PAPER, 1, 0);

        paper.setDisplayName("&aConfig.YML");
        paper.setLore("").addLore("&6&lEdit various plugin features").addLore("").addLore("&7Amount of values: &f" + getCc().getSettings().getConfigValues().keySet().size());
        ib.setItem(11, paper);

        paper.setDisplayName("&aCrateConfig.YML");
        paper.setLore("").addLore("&6&lEdit crate animations").addLore("").addLore("&7Amount of animations: &f" + CrateType.values().length);
        ib.setItem(12, paper);

        paper.setDisplayName("&aRewards.YML");
        paper.setLore("").addLore("&6&lCreate / edit rewards").addLore("").addLore("&7Amount of rewards: &f" + CRewards.allRewards.keySet().size());
        ib.setItem(13, paper);

        paper.setDisplayName("&aMessages.YML");
        paper.setLore("").addLore("&6&lChange chat messages").addLore("").addLore("&7Amount of messages: &f" + (Messages.values().length - 5));
        ib.setItem(14, paper);

        ib.setItem(16, new ItemBuilder(Material.CHEST, 1, 0).setName("&aCrates").setLore("").addLore("&6&lCreate / edit crates").addLore("")
                .addLore("&7Amount: &f" + Crate.getLoadedCrates().keySet().size())
                .addLore("&7Amount Placed: &f" + PlacedCrate.getPlacedCrates().keySet().size()));
        ib.open();
        putInMenu();
    }

    @Override
    public void manageClick(int slot)
    {
        switch (slot)
        {
            case 0:
                getCc().saveEverything();
                break;
            case 9:
                reload();
                break;
            case 18:
                getP().closeInventory();
                break;
            case 11:
                new IGCMenuConfig(getCc(), getP(), this).open();
                break;
            case 12:
                new IGCMenuCrateConfig(getCc(), getP(), this).open();
                break;
            case 13:
                new IGCMenuRewards(getCc(), getP(), this, 1).open();
                break;
            case 14:
                new IGCMenuMessages(getCc(), getP(), this).open();
                break;
            case 16:
                new IGCMenuCrates(getCc(), getP(), this).open();
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        return false;
    }
}
