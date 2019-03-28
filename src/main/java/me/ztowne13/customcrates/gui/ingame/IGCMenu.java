package me.ztowne13.customcrates.gui.ingame;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.dynamicmenus.InputMenu;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 3/11/16.
 */
public abstract class IGCMenu
{
    CustomCrates cc;
    Player p;

    String inventoryName, cVal = "&7Current Value: &f";
    InventoryBuilder ib;

    IGCMenu lastMenu;
    InputMenu inputMenu;

    public IGCMenu(CustomCrates cc, Player p, IGCMenu lastMenu, String inventoryName)
    {
        this.cc = cc;
        this.p = p;
        this.inventoryName = inventoryName.length() >= 32 ? inventoryName.substring(0, 31) : inventoryName;
        this.lastMenu = lastMenu;
    }

    public abstract void open();

    public abstract void manageClick(int slot);

    public abstract boolean handleInput(String value, String input);

    public void putInMenu()
    {
        PlayerManager pm = PlayerManager.get(cc, p);
        pm.setOpenMenu(this);
    }

    public InventoryBuilder createDefault(int slots)
    {
        ib = new InventoryBuilder(p, slots, inventoryName);
        return ib;
    }

    public InventoryBuilder createDefault(int slots, int minSlots)
    {
        ib = new InventoryBuilder(p, slots, inventoryName, minSlots);
        return ib;
    }

    public void up()
    {
        p.closeInventory();
        lastMenu.open();
    }

    public void reload()
    {
        getP().closeInventory();
        ChatUtils.msg(getP(), "&6&lINFO! &eReloading...");
        getCc().reload();
        ChatUtils.msgSuccess(getP(), "Reloaded the Specialized Crate plugin.");
    }


    // Getters and Setters

    public CustomCrates getCc()
    {
        return cc;
    }

    public void setCc(CustomCrates cc)
    {
        this.cc = cc;
    }

    public Player getP()
    {
        return p;
    }

    public void setP(Player p)
    {
        this.p = p;
    }

    public String getInventoryName()
    {
        return inventoryName;
    }

    public void setInventoryName(String inventoryName)
    {
        this.inventoryName = inventoryName;
    }

    public InventoryBuilder getIb()
    {
        return ib;
    }

    public void setIb(InventoryBuilder ib)
    {
        this.ib = ib;
    }

    public InputMenu getInputMenu()
    {
        return inputMenu;
    }

    public void setInputMenu(InputMenu inputMenu)
    {
        this.inputMenu = inputMenu;
    }

    public boolean isInInputMenu()
    {
        return !(inputMenu == null);
    }

    public IGCMenu getLastMenu()
    {
        return lastMenu;
    }

    public void setLastMenu(IGCMenu lastMenu)
    {
        this.lastMenu = lastMenu;
    }

    public String getcVal()
    {
        return cVal;
    }

    public void setcVal(String cVal)
    {
        this.cVal = cVal;
    }
}
