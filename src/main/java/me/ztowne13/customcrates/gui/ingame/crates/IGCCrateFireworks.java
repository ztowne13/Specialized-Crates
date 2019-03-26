package me.ztowne13.customcrates.gui.ingame.crates;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.CFireworks;
import me.ztowne13.customcrates.crates.options.particles.FireworkData;
import me.ztowne13.customcrates.gui.DynamicMaterial;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.gui.dynamicmenus.InputMenu;
import me.ztowne13.customcrates.gui.ingame.IGCDefaultItems;
import me.ztowne13.customcrates.gui.ingame.IGCMenu;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 4/3/16.
 */
public class IGCCrateFireworks extends IGCTierMenu
{
    boolean deleteMode = false;

    public IGCCrateFireworks(CustomCrates cc, Player p, IGCMenu lastMenu, Crate crates, String tier)
    {
        super(cc, p, lastMenu, "&7&l> &6&lFireworks", crates, tier);
    }

    @Override
    public void open()
    {
        getP().closeInventory();
        putInMenu();

        InventoryBuilder ib = createDefault(InventoryUtils.getRowsFor(4,
                (crates.getCs().getCf().getFireworks().containsKey(tier) ? cs.getCf().getFireworks().get(tier).size() : 0)) +
                9, 18);

        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(17, new ItemBuilder(Material.PAPER, 1, 0).setName("&aAdd a new firework")
                .setLore("&7Please hold the firework in").addLore("&7your hand. Type 'add' to")
                .addLore("&7add a firework and 'done'").addLore("&7to finish editing and return")
                .addLore("&7to this menu."));

        updateDeleteModeItem();

        if (cs.getCf().getFireworks().containsKey(tier))
        {
            int i = 2;
            for (FireworkData fd : cs.getCf().getFireworks().get(tier))
            {
                if (i % 9 == 7)
                {
                    i += 4;
                }

                ib.setItem(i, new ItemBuilder(DynamicMaterial.FIREWORK_ROCKET.parseMaterial(), 1, 0)
                        .setName("&aColors: &7" + fd.getColors()).setLore("&aFade Colors: &7" + fd.getFadeColors())
                        .addLore("&aPower: &7" + fd.getPower()).addLore("&aFlicker: &7" + fd.isFlicker())
                        .addLore("&aTrail: &7" + fd.isTrail()).addLore("&aType: &7" + fd.getFeType().name())
                        .addLore(fd.getId()));
                i++;
            }
        }

        ib.open();
    }

    @Override
    public void manageClick(int slot)
    {
        if (slot == 0)
        {
            up();
        }
        else if (slot == 8)
        {
            deleteMode = !deleteMode;
            updateDeleteModeItem();
        }
        else if (slot == 17)
        {
            new InputMenu(getCc(), getP(), "add firework", "null",
                    "Please hold whatever fireworks in your hand you want to add and type 'add'. Type 'done' to finish and return to the menu.",
                    String.class, this);
        }
        else if (getIb().getInv().getItem(slot) != null &&
                DynamicMaterial.FIREWORK_ROCKET.isSameMaterial(getIb().getInv().getItem(slot)))
        {
            if (deleteMode)
            {
                cs.getCf().removeFireworks(tier, cs.getCf().getByItemStack(tier, getIb().getInv().getItem(slot)));
                open();
            }
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        if (value.equalsIgnoreCase("add firework"))
        {
            if (input.equalsIgnoreCase("add"))
            {
                if (getP().getItemInHand() != null &&
                        getP().getItemInHand().getType().equals(DynamicMaterial.FIREWORK_ROCKET.parseMaterial()))
                {
                    FireworkData fd = new FireworkData(getCc(), cs);
                    fd.loadFromFirework(getP().getItemInHand());
                    cs.getCf().addFirework(tier, fd);
                    ChatUtils.msgSuccess(getP(),
                            "Added the firework you are holding to the crate. Please type 'add' to add another firework you are holding or 'done' to return to the menu.");
                }
                else
                {
                    ChatUtils.msgError(getP(), "You are not holding a firework!");
                }
            }
            else if (input.equalsIgnoreCase("done"))
            {
                return true;
            }
            else
            {
                ChatUtils.msgError(getP(), "You can only type 'add' to add a firework and 'done' to return to the menu.");
            }
        }
        return false;
    }

    void updateDeleteModeItem()
    {
        ItemBuilder deleteModeItem = new ItemBuilder(DynamicMaterial.RED_CARPET, 1);
        if (!deleteMode)
        {
            getIb().setItem(8, deleteModeItem.setName("&aEnable 'delete' mode").setLore("&7By enabling 'delete' mode")
                    .addLore("&7you can just click on fireworks").addLore("&7to remove them"));
        }
        else
        {
            getIb().setItem(8, deleteModeItem.setName("&cDisable 'delete' mode").setLore("&7This will stop you from")
                    .addLore("&7removing fireworks"));
        }
    }
}
