package me.ztowne13.customcrates.interfaces.igc;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.InventoryUtils;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class IGCListSelector extends IGCMenu
{
    int page;
    String header;
    DynamicMaterial displayItem;
    List values;
    List<ItemBuilder> builders = null;
    List<String> descriptors = null;
    boolean reopen = true;

    public IGCListSelector(SpecializedCrates cc, Player p, IGCMenu lastMenu, String header,
                           List values, DynamicMaterial displayItem, int page, List<String> descriptors, boolean reopen)
    {
        super(cc, p, lastMenu, "&7&l> &6&l" + header + " PG" + page);
        this.header = header;
        this.values = values;
        this.page = page;
        this.displayItem = displayItem;
        this.descriptors = descriptors;
        this.reopen = reopen;
    }

    public IGCListSelector(SpecializedCrates cc, Player p, IGCMenu lastMenu, String header,
                           List values, DynamicMaterial displayItem, int page, List<String> descriptors)
    {
        this(cc, p, lastMenu, header, values, displayItem, page, descriptors, true);
    }

    public IGCListSelector(SpecializedCrates cc, Player p, IGCMenu lastMenu, String header,
                           List values, DynamicMaterial displayItem, int page, List<String> descriptors,
                           List<ItemBuilder> builders)
    {
        this(cc, p, lastMenu, header, values, displayItem, page, descriptors);
        this.builders = builders;
    }

    @Override
    public void open()
    {
        int slots;

        if (values.size() - ((page - 1) * 28) > 28)
            slots = 28;
        else
            slots = values.size() - ((page - 1) * 28);

        slots = InventoryUtils.getRowsFor(2, slots) + 9;

        setInventoryName("&7&l> &6&l" + header + " PG" + page);
        InventoryBuilder ib = createDefault(slots, 18);

        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());

        int i = 10;
        int toSkip = ((page - 1) * 28);
        int skipped = 0;
        int displayedItems = 0;
        int itemNum = (page - 1) * 28;

        int added = 0;
        for (Object val : values)
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
            ItemBuilder item;

            if (builders == null)
            {
                item = new ItemBuilder(displayItem, 1).setName("&a" + val);

                if (descriptors != null)
                    item.addAutomaticLore("&f", 30, descriptors.get(added));

                item.addLore("")
                        .addLore("&7&oClick to select this.");

                added++;
            }
            else
            {
                item = builders.get(added);
                added++;
            }

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
        putInMenu();
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
        else if (getIb().getInv().getItem(slot) != null)
        {
            int row = slot / 9;
            int slotInRow = slot % 9;
            int num = ((page - 1) * 28) + ((row - 1) * 7) + (slotInRow - 1);

            if(reopen)
                up();

            lastMenu.handleInput(header, values.get(num).toString());
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        return true;
    }
}
