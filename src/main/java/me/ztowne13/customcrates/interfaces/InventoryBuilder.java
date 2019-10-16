package me.ztowne13.customcrates.interfaces;

import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static java.lang.Math.max;

public class InventoryBuilder
{
    String invName = "";
    Inventory inv;
    Player p;
    int minimumSlots = 0;

    public InventoryBuilder(Player p, int slots, String invName)
    {
        this(p, slots, invName, 0);
    }

    public InventoryBuilder(Player p, int slots, String invName, int minimumSlots)
    {
        this.invName = invName;
        this.minimumSlots = minimumSlots;
        this.p = p;

//        if(slots < 27)
//            minimumSlots = 27;
//        else if (slots > 27)
//            minimumSlots = 27 * 2;
        setInv(Bukkit.createInventory(p, max(minimumSlots, slots), ChatColor.translateAlternateColorCodes('&', invName)));
    }

    public void clear()
    {
        for(int i = 0; i < inv.getSize(); i++)
            setItem(i, new ItemBuilder(DynamicMaterial.AIR , 1));
    }

    public String getName()
    {
        return invName;
    }

    public void setItem(int slot, ItemStack stack)
    {
        getInv().setItem(slot, stack);
    }

    public void setItem(int slot, ItemBuilder builder)
    {
        getInv().setItem(slot, builder.get());
    }

    public void open()
    {
        getP().openInventory(getInv());
    }

    public Inventory getInv()
    {
        return inv;
    }

    public void setInv(Inventory inv)
    {
        this.inv = inv;
    }

    public Player getP()
    {
        return p;
    }

    public void setP(Player p)
    {
        this.p = p;
    }
}
