package me.ztowne13.customcrates.interfaces.igc.fileconfigs;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 3/20/16.
 */
public class IGCMenuSQL extends IGCMenu {
    public IGCMenuSQL(SpecializedCrates cc, Player p, IGCMenu lastMenu) {
        super(cc, p, lastMenu, "&7&l> &6&lCrateConfig.YML");
    }

    @Override
    public void openMenu() {

        FileHandler fu = getCc().getSqlFile();
        FileConfiguration fc = fu.get();

        InventoryBuilder ib = createDefault(27);

        ib.setItem(18, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());
        ib.setItem(9, IGCDefaultItems.RELOAD_BUTTON.getIb());

        ib.setItem(11, new ItemBuilder(Material.PAPER, 1, 0).setName("&aDatabase Name").addLore("&7Current Value:")
                .addLore("&7" + fc.getString("database.name")));
        ib.setItem(12, new ItemBuilder(Material.PAPER, 1, 0).setName("&aDatabase IP").addLore("&7Current Value:")
                .addLore("&7" + fc.getString("database.ip")));
        ib.setItem(13, new ItemBuilder(Material.PAPER, 1, 0).setName("&aDatabase Port").addLore("&7Current Value:")
                .addLore("&7" + fc.getString("database.port")));
        ib.setItem(14, new ItemBuilder(Material.PAPER, 1, 0).setName("&aDatabase Username").addLore("&7Current Value:")
                .addLore("&7" + fc.getString("database.username")));
        ib.setItem(15, new ItemBuilder(Material.PAPER, 1, 0).setName("&aDatabase Password").addLore("&7Current Value:")
                .addLore("&7" + fc.getString("database.password")));

        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot) {
        switch (slot) {
            case 18:
                up();
                ChatUtils.msgInfo(getP(), "Remember to change the store-data value in the config.yml if you want to enable SQL for storage.");
                break;
            case 0:
                getCc().getSqlFile().save();
                ChatUtils.msgInfo(getP(), "Remember to change the store-data value in the config.yml if you want to enable SQL for storage.");
                ChatUtils.msgSuccess(getP(), "SQL.YML saved! PLEASE RELOAD OR RESTART SERVER FOR CHANGES TO TAKE EFFECT.");
                break;
            case 9:
                reload();
                ChatUtils.msgInfo(getP(), "Remember to change the store-data value in the config.yml if you want to enable SQL for storage.");
                break;
            case 11:
                new InputMenu(getCc(), getP(), "database.name", getCc().getSqlFile().get().getString("database.name"),
                        String.class, this, true);
                break;
            case 12:
                new InputMenu(getCc(), getP(), "database.ip", getCc().getSqlFile().get().getString("database.ip"),
                        String.class, this, true);
                break;
            case 13:
                new InputMenu(getCc(), getP(), "database.port", getCc().getSqlFile().get().getString("database.port"),
                        String.class, this, true);
                break;
            case 14:
                new InputMenu(getCc(), getP(), "database.username", getCc().getSqlFile().get().getString("database.username"),
                        String.class, this, true);
                break;
            case 15:
                new InputMenu(getCc(), getP(), "database.password", getCc().getSqlFile().get().getString("database.password"),
                        String.class, this, true);
                break;
        }

    }

    @Override
    public boolean handleInput(String value, String input) {
        FileHandler fileHandler = getCc().getSqlFile();
        fileHandler.get().set(value, input);

        ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input + ".");
        return true;
    }
}
