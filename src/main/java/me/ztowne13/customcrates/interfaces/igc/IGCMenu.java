package me.ztowne13.customcrates.interfaces.igc;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 3/11/16.
 */
public abstract class IGCMenu
{
    SpecializedCrates cc;
    Player p;

    String inventoryName, cVal = "&7Current Value: &f";
    InventoryBuilder ib;

    IGCMenu lastMenu;
    InputMenu inputMenu;

    public IGCMenu(SpecializedCrates cc, Player p, IGCMenu lastMenu, String inventoryName)
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
        if(slots > 54)
            slots = 54;
        ib = new InventoryBuilder(p, slots, inventoryName, minSlots);
        return ib;
    }

    public void up()
    {
        //p.closeInventory();
        lastMenu.open();
    }

    public void reload()
    {
        long start = System.currentTimeMillis();
        getP().closeInventory();
        ChatUtils.msg(getP(), "&6&lINFO! &eReloading...");
        getCc().reload();
        ChatUtils.msgSuccess(getP(), "Reloaded the Specialized Crate plugin &7(" + (System.currentTimeMillis() - start) + "ms)&a.");
    }


    // Getters and Setters

    public SpecializedCrates getCc()
    {
        return cc;
    }

    public void setCc(SpecializedCrates cc)
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
