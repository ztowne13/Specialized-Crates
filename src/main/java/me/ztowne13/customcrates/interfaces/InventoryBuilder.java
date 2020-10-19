package me.ztowne13.customcrates.interfaces;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class InventoryBuilder {
    protected final int minimumSlots;
    private final String invName;
    private Inventory inv;
    private Player player;

    public InventoryBuilder(Player player, int slots, String invName) {
        this(player, slots, invName, 0);
    }

    public InventoryBuilder(Player player, int slots, String invName, int minimumSlots) {
        this.invName = invName;
        this.minimumSlots = minimumSlots;
        this.player = player;

        String title = ChatColor.translateAlternateColorCodes('&', invName);
        if (VersionUtils.Version.v1_12.isServerVersionOrEarlier() && title.length() > 31) {
            title = title.substring(0, 32);
        }
        setInv(Bukkit.createInventory(player, min(54, max(minimumSlots, slots)), title));
    }

    public void clear() {
        for (int i = 0; i < inv.getSize(); i++)
            setItem(i, new ItemBuilder(XMaterial.AIR, 1));
    }

    public int getSize() {
        return getInv().getSize();
    }

    public String getName() {
        return invName;
    }

    public void setItem(int slot, ItemStack stack) {
        getInv().setItem(slot, stack);
    }

    public void setItem(int slot, ItemBuilder builder) {
        getInv().setItem(slot, builder.getStack());
    }

    public void open() {
        getPlayer().openInventory(getInv());
    }

    public Inventory getInv() {
        return inv;
    }

    public void setInv(Inventory inv) {
        this.inv = inv;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
