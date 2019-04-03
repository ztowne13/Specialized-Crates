package me.ztowne13.customcrates.interfaces.inputmenus;

import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 6/13/16.
 */
public class AnvilInputMenu extends InputMenuGUI
{
    public AnvilInputMenu(InputMenu im)
    {
        super(im);
    }

    public void openMenu()
    {

    }

    public void closeMenu(boolean saved)
    {

    }

    @Override
    public void initMsg()
    {
        Player p = im.getP();
        p.closeInventory();
        for (int i = 0; i < 20; i++)
        {
            ChatUtils.msg(p, "");
        }
        ChatUtils.msg(p, "&7----------------------------------------");
        ChatUtils.msg(p, "&aYou are currently editing the &f" + im.value + " &avalue.");
        ChatUtils.msg(p, "&BCurrent Value: &f" + im.currentValue);
        if (!im.formatExp.equalsIgnoreCase(""))
        {
            ChatUtils.msg(p, "&a" + im.formatExp);
        }
        ChatUtils.msg(p, "&7----------------------------------------");
        ChatUtils.msg(p, "&6Please write the value you'd like to set it IN THE ANVIL.");
        ChatUtils.msg(p, "&e&oClose the inventory to exit the current configuration session.");
        ChatUtils.msg(p, "&c&oClick the paper to save your work.");
    }

    @Override
    public void runFor(IGCMenu igcm, String s)
    {

    }
}
