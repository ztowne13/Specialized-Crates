package me.ztowne13.customcrates.interfaces.igc.crates.previeweditor;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom.CustomRewardDisplayer;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom.DisplayPage;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.InventoryUtils;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.crates.IGCMenuCrate;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class IGCCratePreviewPageChooser extends IGCMenuCrate
{
    int page;
    List<Integer> values;
    CustomRewardDisplayer displayer;

    public IGCCratePreviewPageChooser(SpecializedCrates specializedCrates, Player player, Crate crate, IGCMenu lastMenu, int page)
    {
        super(specializedCrates, player, lastMenu, "&7&l> &6&lReward Preview Menu", crate);

        this.displayer = (CustomRewardDisplayer) getCrates().getSettings().getDisplayer();
        this.page = page;

        values = new ArrayList<Integer>();

        for(Integer i : displayer.getPages().keySet())
            values.add(i);
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

        setInventoryName("&7&l> &6&lPreview Menu PG" + page);
        InventoryBuilder ib = createDefault(slots, 18);

        ib.setItem(9, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());

        ItemBuilder builder = new ItemBuilder(DynamicMaterial.PAPER, 1);
        builder.setDisplayName("&aAdd a page");

        ib.setItem(8, builder);

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

            item = new ItemBuilder(DynamicMaterial.BOOK, 1).setName("&a" + val);

            item.addLore("")
                    .addLore("&7&oClick to edit this page.");

            added++;

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
            getCrates().getSettings().getFileHandler().save();
            ChatUtils.msgSuccess(getP(), "Saved!");
        }
        else if(slot == 9)
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
        else if(slot == 8)
        {
            for(int i = 1; i < 1000; i++)
            {
                if (!values.contains(i))
                {
                    DisplayPage page = new DisplayPage(displayer, i);
                    page.load();
                    displayer.getPages().put(i, page);

                    new IGCCratePreviewPageChooser(getCc(), getP(), getCrates(), getLastMenu(), 1).open();
                    break;
                }
            }
        }
        else if (getIb().getInv().getItem(slot) != null)
        {
            int row = slot / 9;
            int slotInRow = slot % 9;
            int num = ((page - 1) * 28) + ((row - 1) * 7) + (slotInRow - 1);

            int pageNum = values.get(num);

            new IGCCratePreviewEditor(getCc(), getP(), getCrates(), this, displayer.getPages().get(pageNum)).open();
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        return true;
    }
}
