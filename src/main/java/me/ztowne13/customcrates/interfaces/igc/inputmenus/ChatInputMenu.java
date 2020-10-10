package me.ztowne13.customcrates.interfaces.igc.inputmenus;

import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 6/13/16.
 */
public class ChatInputMenu extends InputMenuGUI {
    public ChatInputMenu(InputMenu im) {
        super(im);
    }

    @Override
    public void initMsg() {
        Player p = im.getP();

        p.closeInventory();
        for (int i = 0; i < 20; i++) {
            ChatUtils.msg(p, "");
        }
        ChatUtils.msg(p, "&7----------------------------------------");
        ChatUtils.msg(p, "&aYou are currently editing the &f" + im.value + " &avalue.");
        ChatUtils.msg(p, "&BCurrent Value: &f" + im.currentValue);
        if (!im.formatExp.equalsIgnoreCase("")) {
            ChatUtils.msg(p, "&a" + im.formatExp);
        }
        ChatUtils.msg(p, "&7----------------------------------------");
        ChatUtils.msg(p, "&6Please write the value you'd like to set it to below.");
        ChatUtils.msg(p, "&e&oType 'exit' to exit the current configuration session.");
    }

    @Override
    public void runFor(IGCMenu igcm, String s) {
        if (s.equalsIgnoreCase("exit")) {
            igcm.open();
            igcm.setInputMenu(null);
        } else {

            if (im.inputMenu.handleInput(im.value, s)) {
                igcm.open();
                igcm.setInputMenu(null);
            }
        }
    }
}
