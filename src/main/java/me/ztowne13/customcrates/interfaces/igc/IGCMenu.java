package me.ztowne13.customcrates.interfaces.igc;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.buttons.IGCButton;
import me.ztowne13.customcrates.interfaces.igc.buttons.IGCButtonType;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 3/11/16.
 */
public abstract class IGCMenu {
    static int GLOBAL_MIN = 9;

    SpecializedCrates cc;
    Player p;

    String inventoryName, cVal = "&7Current Value: &f";
    InventoryBuilder ib;

    IGCMenu lastMenu;
    InputMenu inputMenu;

    IGCButton[] buttons;
    int[] buttonSpots;

    public IGCMenu(SpecializedCrates cc, Player p, IGCMenu lastMenu, String inventoryName) {
        this(cc, p, lastMenu, inventoryName, new IGCButtonType[]{}, new int[]{});
    }


    public IGCMenu(SpecializedCrates cc, Player p, IGCMenu lastMenu, String inventoryName, IGCButtonType[] buttonTypes, int[] buttonSpots) {
        this.cc = cc;
        this.p = p;
        this.inventoryName = inventoryName.length() >= 32 ? inventoryName.substring(0, 31) : inventoryName;
        this.lastMenu = lastMenu;
        this.buttonSpots = buttonSpots;
        this.buttons = new IGCButton[buttonTypes.length];
        for (int i = 0; i < buttonTypes.length; i++) {
            this.buttons[i] = buttonTypes[i].createInstance();
        }
    }

    public abstract void openMenu();

    public abstract void handleClick(int slot);

    public abstract boolean handleInput(String value, String input);

    public void open() {
        openMenu();

        for (int i = 0; i < buttonSpots.length; i++) {
            int buttonSpot = buttonSpots[i];
            IGCButton button = buttons[i];
            getIb().setItem(buttonSpot, button.getButtonItem());
        }
    }

    public void manageClick(int slot) {
        for (int i = 0; i < buttonSpots.length; i++) {
            int buttonSpot = buttonSpots[i];
            IGCButton button = buttons[i];
            if (buttonSpot == slot) {
                if (button.handleClick(this)) {
                    open();
                }
                return;
            }
        }

        handleClick(slot);
    }

    public void putInMenu() {
        PlayerManager pm = PlayerManager.get(cc, p);
        pm.setOpenMenu(this);
    }

    public InventoryBuilder createDefault(int slots) {
        if (slots < GLOBAL_MIN) {
            slots = GLOBAL_MIN;
        }
        ib = new InventoryBuilder(p, slots > 54 ? 54 : slots, inventoryName);
        return ib;
    }

    public InventoryBuilder createDefault(int slots, int minSlots) {
        if (slots < GLOBAL_MIN) {
            slots = GLOBAL_MIN;
        }
        if (slots > 54)
            slots = 54;
        ib = new InventoryBuilder(p, slots, inventoryName, minSlots);
        return ib;
    }

    public void up() {
        lastMenu.open();
    }

    public void reload() {
        long start = System.currentTimeMillis();
        getP().closeInventory();
        ChatUtils.msg(getP(), "&6&lINFO! &eReloading...");
        getCc().reload();
        ChatUtils.msgSuccess(getP(), "Reloaded the Specialized Crate plugin &7(" + (System.currentTimeMillis() - start) + "ms)&a.");
    }


    // Getters and Setters

    public SpecializedCrates getCc() {
        return cc;
    }

    public void setCc(SpecializedCrates cc) {
        this.cc = cc;
    }

    public Player getP() {
        return p;
    }

    public void setP(Player p) {
        this.p = p;
    }

    public String getInventoryName() {
        return inventoryName;
    }

    public void setInventoryName(String inventoryName) {
        this.inventoryName = inventoryName;
    }

    public InventoryBuilder getIb() {
        return ib;
    }

    public void setIb(InventoryBuilder ib) {
        this.ib = ib;
    }

    public InputMenu getInputMenu() {
        return inputMenu;
    }

    public void setInputMenu(InputMenu inputMenu) {
        this.inputMenu = inputMenu;
    }

    public boolean isInInputMenu() {
        return !(inputMenu == null);
    }

    public IGCMenu getLastMenu() {
        return lastMenu;
    }

    public void setLastMenu(IGCMenu lastMenu) {
        this.lastMenu = lastMenu;
    }

    public String getcVal() {
        return cVal;
    }

    public void setcVal(String cVal) {
        this.cVal = cVal;
    }

    public IGCButton[] getButtons() {
        return buttons;
    }
}
