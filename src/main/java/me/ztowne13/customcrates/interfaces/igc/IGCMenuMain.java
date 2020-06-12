package me.ztowne13.customcrates.interfaces.igc;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.fileconfigs.*;
import me.ztowne13.customcrates.interfaces.igc.fileconfigs.rewards.IGCMenuRewards;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.players.data.SQLDataHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 3/11/16.
 */
public class IGCMenuMain extends IGCMenu
{
    public IGCMenuMain(SpecializedCrates cc, Player p, IGCMenu lastMenu)
    {
        super(cc, p, lastMenu, "&7&l> &6&lHome");
    }

    @Override
    public void openMenu()
    {
        InventoryBuilder ib = createDefault(36);
        ib.setItem(0, new ItemBuilder(IGCDefaultItems.SAVE_ONLY_BUTTON.getIb()).setName("&aSave every file/crate"));
        ib.setItem(9, IGCDefaultItems.RELOAD_BUTTON.getIb());
        ib.setItem(27, IGCDefaultItems.EXIT_BUTTON.getIb());

        ItemBuilder paper = new ItemBuilder(Material.PAPER, 1, 0);

        paper.setDisplayName("&aConfig.YML");
        paper.setLore("").addLore("&6&lEdit various plugin features").addLore("")
                .addLore("&7Amount of values: &f" + getCc().getSettings().getConfigValues().keySet().size());
        ib.setItem(11, paper);

        paper.setDisplayName("&aCrateConfig.YML");
        paper.setLore("").addLore("&6&lEdit crate animations").addLore("")
                .addLore("&7Amount of animations: &f" + CrateAnimationType.values().length);
        ib.setItem(12, paper);

        paper.setDisplayName("&aRewards.YML");
        paper.setLore("").addLore("&6&lCreate / edit rewards").addLore("")
                .addLore("&7Amount of rewards: &f" + CRewards.allRewards.keySet().size());
        ib.setItem(13, paper);

        paper.setDisplayName("&aMessages.YML");
        paper.setLore("").addLore("&6&lChange chat messages").addLore("")
                .addLore("&7Amount of messages: &f" + (Messages.values().length - 5));
        ib.setItem(14, paper);

        ib.setItem(16,
                new ItemBuilder(Material.CHEST, 1, 0).setName("&aCrates").setLore("").addLore("&6&lCreate / edit crates")
                        .addLore("")
                        .addLore("&7Amount: &f" + Crate.getLoadedCrates().keySet().size())
                        .addLore("&7Amount Placed: &f" + PlacedCrate.getPlacedCrates().keySet().size()));

        ItemBuilder sqlYml = new ItemBuilder(DynamicMaterial.PAPER, 1);
        sqlYml.setDisplayName("&aSQL.YML");
        sqlYml.addLore("").addLore("&6&lEdit the MySQL database info").addLore("").addLore("&7Amount of values: &f5");
        ib.setItem(20, sqlYml);

        boolean dbStatus = (SQLDataHandler.sql == null || !SQLDataHandler.sql.getSqlc().isOpen());
        ItemBuilder sqlStatus = new ItemBuilder(dbStatus ? DynamicMaterial.RED_DYE : DynamicMaterial.GREEN_DYE, 1);
        sqlStatus.setDisplayName("&eMySQL Database Status");
        sqlStatus.addLore("").addLore(dbStatus ? "&c&lNot Connected" : "&a&lConnected!");
        sqlStatus.addLore("")
                .addAutomaticLore("&f", 30, "If you are not using MYSQL as the data handler, you can ignore this!");

        ib.setItem(35, sqlStatus);

        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot)
    {
        switch (slot)
        {
            case 0:
                getCc().saveEverything();
                break;
            case 9:
                reload();
                break;
            case 27:
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
            case 20:
                new IGCMenuSQL(getCc(), getP(), this).open();
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        return false;
    }
}
