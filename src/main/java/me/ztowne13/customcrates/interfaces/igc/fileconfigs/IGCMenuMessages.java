package me.ztowne13.customcrates.interfaces.igc.fileconfigs;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.InventoryUtils;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by ztowne13 on 3/15/16.
 */
public class IGCMenuMessages extends IGCMenu
{
    static int msgLoreLength = 40;

    public IGCMenuMessages(SpecializedCrates cc, Player p, IGCMenu lastMenu)
    {
        super(cc, p, lastMenu, "&7&l> &6&lMessages.YML");
    }

    @Override
    public void open()
    {
        InventoryBuilder ib = createDefault(InventoryUtils.getRowsFor(2, Messages.values().length - 1));
        ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());
        ib.setItem(9, IGCDefaultItems.RELOAD_BUTTON.getIb());
        ib.setItem(ib.getInv().getSize() - 9, IGCDefaultItems.EXIT_BUTTON.getIb());

        ArrayList<Messages> msgs = new ArrayList<>();

        for (Messages msg : Messages.values())
        {
            if (msg.getMsg().equalsIgnoreCase(""))
            {
                msgs.add(msg);
            }
        }

        int i = 2;
        for (Messages msg : msgs)
        {
            if (i % 9 == 0)
            {
                i += 2;
            }

            String properMsg = msg.getPropperMsg(getCc());
//            ib.setItem(i, new ItemBuilder(DynamicMaterial.BOOK, 1).setName("&a" + msg.toString().toLowerCase()).addLore(
//                    properMsg.substring(0, properMsg.length() > msgLoreLength ? msgLoreLength : properMsg.length()) +
//                            (properMsg.length() > msgLoreLength ? "..." : "")));

            ib.setItem(i, new ItemBuilder(DynamicMaterial.BOOK, 1).setName("&a" + msg.toString().toLowerCase())
                    .addAutomaticLore(30, properMsg).addLore("").addLore("")
                    .addAutomaticLore("&f", 30, "Set to 'none' to remove the message."));
            i++;
        }

        ib.open();
        putInMenu();
    }

    @Override
    public void manageClick(int slot)
    {
        if (slot == 0)
        {
            getCc().getMessageFile().save();
            ChatUtils.msgSuccess(getP(), "Messages.YML saved!");
        }
        else if (slot == 9)
        {
            reload();
        }
        else if (slot == getIb().getInv().getSize() - 9)
        {
            up();
        }
        else if (!(getIb().getInv().getItem(slot) == null))
        {
            Messages msg = Messages.valueOf(
                    ChatUtils.removeColor(getIb().getInv().getItem(slot).getItemMeta().getDisplayName()).toUpperCase());
            new InputMenu(getCc(), getP(), msg.name(), msg.getPropperMsg(getCc()), String.class, this);
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        Messages msg = Messages.valueOf(value.toUpperCase());
        msg.writeValue(getCc(), input);
        ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
        return true;
    }
}
