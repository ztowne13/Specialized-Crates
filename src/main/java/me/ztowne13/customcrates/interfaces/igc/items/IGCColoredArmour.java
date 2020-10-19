package me.ztowne13.customcrates.interfaces.igc.items;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.EditableItem;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.items.attributes.RGBColor;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

public class IGCColoredArmour extends IGCMenu {
    EditableItem item;

    public IGCColoredArmour(SpecializedCrates cc, Player p, IGCMenu lastMenu, EditableItem item) {
        super(cc, p, lastMenu, "&7&l> &6&lColor");
        this.item = item;
    }

    @Override
    public void openMenu() {

        InventoryBuilder ib = createDefault(27);

        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());

        ItemBuilder red = new ItemBuilder(XMaterial.RED_DYE, 1);
        red.setDisplayName("&cEdit the RED of RGB");
        red.addLore("").addLore("&7Current value: ").addLore("&f" + item.getColor().getRed());

        ItemBuilder green = new ItemBuilder(XMaterial.LIME_DYE, 1);
        green.setDisplayName("&aEdit the GREEN of RGB");
        green.addLore("").addLore("&7Current value: ").addLore("&f" + item.getColor().getGreen());

        ItemBuilder blue = new ItemBuilder(XMaterial.LIGHT_BLUE_DYE, 1);
        blue.setDisplayName("&bEdit the BLUE of RGB");
        blue.addLore("").addLore("&7Current value: ").addLore("&f" + item.getColor().getBlue());

        ib.setItem(11, red);
        ib.setItem(13, green);
        ib.setItem(15, blue);

        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot) {
        switch (slot) {
            case 0:
                up();
                break;
            case 11:
                new InputMenu(getCc(), getP(), "red", item.getColor().getRed() + "",
                        "The RED of RGB.", Float.class, this);
                break;
            case 13:
                new InputMenu(getCc(), getP(), "green", item.getColor().getGreen() + "",
                        "The GREEN of RGB.", Float.class, this);
                break;
            case 15:
                new InputMenu(getCc(), getP(), "blue", item.getColor().getBlue() + "",
                        "The BLUE of RGB.", Float.class, this);
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input) {
        int inputAsFloat;
        try {
            inputAsFloat = Integer.parseInt(input);
        } catch (Exception exc) {
            ChatUtils.msgError(getP(), input + " is not a valid number.");
            return false;
        }

        RGBColor rgbColor = item.getColor();

        if (value.equalsIgnoreCase("red"))
            rgbColor.setRed(inputAsFloat);
        else if (value.equalsIgnoreCase("green"))
            rgbColor.setGreen(inputAsFloat);
        else if (value.equalsIgnoreCase("blue"))
            rgbColor.setBlue(inputAsFloat);

        item.setColor(rgbColor);

        ChatUtils.msgSuccess(getP(), "Successfully set " + value + " to " + input + ".");
        return true;
    }
}
