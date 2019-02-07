package me.ztowne13.customcrates.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static java.lang.Math.max;

public class InventoryBuilder 
{
	Inventory inv;
	Player p;
	int minimumSlots = 0;

	public InventoryBuilder(Player p, int slots, String invName)
	{
		super();
		this.p = p;
		setInv(Bukkit.createInventory(p, slots, ChatColor.translateAlternateColorCodes('&', invName)));
	}

	public InventoryBuilder(Player p, int slots, String invName, int minimumSlots) {
		super();
		this.minimumSlots = minimumSlots;
		this.p = p;
		setInv(Bukkit.createInventory(p, max(minimumSlots, slots), ChatColor.translateAlternateColorCodes('&', invName)));
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
