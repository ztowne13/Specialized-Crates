package me.ztowne13.customcrates.gui.ingame;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.gui.DynamicMaterial;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.gui.dynamicmenus.InputMenu;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class IGCListEditor extends IGCMenu
{
    int page;
    String header;
    String identifier;
    DynamicMaterial displayItem;
    List<String> values;
    boolean deleteMode = false;

    public IGCListEditor(CustomCrates cc, Player p, IGCMenu lastMenu, String header, String identifier,
                         List<String> values, DynamicMaterial displayItem, int page)
    {
        super(cc, p, lastMenu, "&7&l> &6&l" + header + " PG" + page);
        this.header = header;
        this.values = values;
        this.identifier = identifier;
        this.page = page;
        this.displayItem = displayItem;
    }

    @Override
    public void open()
    {
        getP().closeInventory();
        putInMenu();

        int slots;

        if (values.size() - ((page - 1) * 28) > 28)
            slots = 28;
        else
            slots = values.size() - ((page - 1) * 28);

        slots = InventoryUtils.getRowsFor(2, slots) + 9;

        setInventoryName("&7&l> &6&l" + header + " PG" + page);
        InventoryBuilder ib = createDefault(slots, 18);

        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(8, new ItemBuilder(DynamicMaterial.PAPER, 1)
                .setName("&aAdd a new " + ChatUtils.removeColor(identifier).toLowerCase())
                .setLore("&7Reminder: you must save for").addLore("&7any changes to take effect."));

        updateDeleteMode();

        int i = 10;
        int toSkip = ((page - 1) * 28);
        int skipped = 0;
        int displayedItems = 0;
        int itemNum = (page - 1) * 28;

        for (String val : values)
        {
            if (toSkip > skipped || displayedItems >= 28)
            {
                skipped++;
                continue;
            }

            if (i % 9 == 8)
            {
                i += 2;
            }

            itemNum++;
            ItemBuilder item = new ItemBuilder(displayItem, 1).setName("&a" + identifier + " " + itemNum);

            item.addLore("&f" + val);
            item.addLore("").addLore("")
                    .addLore("&7&oClick to edit this " + ChatUtils.removeColor(identifier).toLowerCase() + ".");


            ib.setItem(i, item);
            i++;
            displayedItems++;
        }

        if (page != 1)
        {
            ib.setItem(2, new ItemBuilder(Material.ARROW, 1, 0).setName("&aGo back a page"));
        }

        if (((values.size() / 28) + (values.size() % 28 == 0 ? 0 : 1) != page) && values.size() != 0)
        {
            ib.setItem(6, new ItemBuilder(Material.ARROW, 1, 0).setName("&aGo forward a page"));
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
        else if (slot == 2 && getIb().getInv().getItem(slot).getType() == Material.ARROW)
        {
            page--;
            open();
        }
        else if (slot == 6 && getIb().getInv().getItem(slot).getType() == Material.ARROW)
        {
            page++;
            open();
        }
        else if (slot == 8)
        {
            new InputMenu(getCc(), getP(), "new " + ChatUtils.removeColor(identifier), "null", String.class, this);
        }
        else if (slot == 17)
        {
            deleteMode = !deleteMode;
            updateDeleteMode();
        }
        else if (getIb().getInv().getItem(slot).getType().equals(displayItem.parseMaterial()))
        {
            ItemBuilder clickedItem = new ItemBuilder(getIb().getInv().getItem(slot));
            if (deleteMode)
            {
                String[] split = ChatUtils.removeColor(clickedItem.getName(true)).split(" ");
                int id = Integer.parseInt(split[split.length - 1]);
                values.remove(id - 1);
                open();
            }
            else
            {
                new InputMenu(getCc(), getP(), ChatUtils.removeColor(clickedItem.getName(true)),
                        clickedItem.im().getLore().get(0), String.class, this);
            }
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {

        if (value.equalsIgnoreCase("new " + ChatUtils.removeColor(identifier)))
        {
            values.add(input);
        }
        else
        {
            String[] split = value.split(" ");
            int id = Integer.parseInt(split[split.length - 1]);

            values.set(id - 1, input);
        }
        return true;
    }

    public void updateDeleteMode()
    {
        if (!deleteMode)
        {
            getIb().setItem(17, new ItemBuilder(DynamicMaterial.RED_CARPET, 1).setName("&aEnable 'remove' mode")
                    .setLore("&7By enabling 'remove' mode")
                    .addLore("&7you can just click on " + ChatUtils.removeColor(identifier) + "s")
                    .addLore("&7to remove them").addLore("").addLore("&fDelete every item to use the")
                    .addLore("&fdefault lore in the config.yml"));
        }
        else
        {
            getIb().setItem(17, new ItemBuilder(DynamicMaterial.RED_CARPET, 1).setName("&cDisable 'remove' mode")
                    .setLore("&7This will stop you from").addLore("&7removing " + ChatUtils.removeColor(identifier) + "s")
                    .addLore("").addLore("&fDelete every item to use the").addLore("&fdefault lore in the config.yml"));
        }
    }
}
