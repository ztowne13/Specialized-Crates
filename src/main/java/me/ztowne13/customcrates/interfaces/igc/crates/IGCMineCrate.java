package me.ztowne13.customcrates.interfaces.igc.crates;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCListEditor;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Created by ztowne13 on 4/5/16.
 */
public class IGCMineCrate extends IGCMenuCrate {
    public IGCMineCrate(SpecializedCrates cc, Player p, IGCMenu lastMenu, Crate crates) {
        super(cc, p, lastMenu, "&7&l> &6&lMine Crate", crates);
    }

    @Override
    public void openMenu() {

        InventoryBuilder ib = createDefault(27);

        ib.setItem(9, IGCDefaultItems.EXIT_BUTTON.getIb());

        ib.setItem(11, new ItemBuilder(XMaterial.FISHING_ROD).setDisplayName("&aChance").setLore("&7Current value: ")
                .addLore("&7" + cs.getLuckyChestSettings().getChance() + "/" + cs.getLuckyChestSettings().getOutOfChance())
                .addLore("")
                .addAutomaticLore("&f", 30,
                        "These are the odds that a crate will appear while mining. Formatted 'number/number'."));
        ib.setItem(12, new ItemBuilder(XMaterial.LIGHT_GRAY_DYE).setDisplayName("&aWhitelist")
                .addLore("&7Current value: ").addLore("&7" + cs.getLuckyChestSettings().isBLWL() + "").addLore("")
                .addAutomaticLore("&f", 30, "Set whether the block-list is a whitelist or not."));
        ItemBuilder bList =
                new ItemBuilder(XMaterial.STONE).setDisplayName("&aEdit the block-list").setLore("&7Current values: ");

        for (Material m : cs.getLuckyChestSettings().getWhiteList()) {
            bList.addLore("&7" + m.name());
        }
        bList.addLore("").addAutomaticLore("&f", 30, "These are all the materials on the blacklist (or whitelist).");
        ib.setItem(13, bList);

        ItemBuilder wList =
                new ItemBuilder(XMaterial.PURPLE_DYE, 1).setDisplayName("&aEdit the worlds").setLore("&7Current values: ");
        for (String w : cs.getLuckyChestSettings().getWorldsRaw()) {
            wList.addLore("&7" + w);
        }
        wList.addLore("").addAutomaticLore("&f", 30,
                "These are the worlds where mine crates can be found. Remove all the worlds for ALL worlds to be allowed.");
        ib.setItem(14, wList);

        ItemBuilder requirePermission = new ItemBuilder(XMaterial.BOOK);
        requirePermission.setDisplayName("&aRequire Permission to Find");
        requirePermission.addLore("&7Current Value:")
                .addLore("&7" + crates.getSettings().getLuckyChestSettings().isRequirePermission());
        requirePermission.addLore("").addAutomaticLore("&f", 30,
                "Set whether or not the crate's permission is required for the player to find the crate.");
        ib.setItem(15, requirePermission);

        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot) {
        switch (slot) {
            case 9:
                up();
                break;
            case 11:
                new InputMenu(getCc(), getP(), "chance",
                        cs.getLuckyChestSettings().getChance() + "/" + cs.getLuckyChestSettings().getOutOfChance(),
                        "Format it 'chance/out of what chance'.", String.class, this, true);
                break;
            case 12:
                cs.getLuckyChestSettings().setBLWL(!cs.getLuckyChestSettings().isBLWL());
                open();
                break;
            case 13:
                new IGCListEditor(getCc(), getP(), this, "Block List", "Block", cs.getLuckyChestSettings().getWhiteList(),
                        XMaterial.STONE, 1, XMaterial.class, "valueOf", "That is not a valid block name. Try STONE.")
                        .open();
                break;
            case 14:
                new IGCListEditor(getCc(), getP(), this, "Worlds List", "World", cs.getLuckyChestSettings().getWorldsRaw(),
                        XMaterial.MAP, 1).open();
                break;
            case 15:
                cs.getLuckyChestSettings().setRequirePermission(!cs.getLuckyChestSettings().isRequirePermission());
                open();
                break;
            case 16:
                new InputMenu(getCc(), getP(), "remove worlds", cs.getLuckyChestSettings().getWorlds().toString(),
                        "Current valid worlds: " + Bukkit.getWorlds(), String.class, this, true);
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input) {
        if (value.equalsIgnoreCase("chance")) {
            try {
                String[] split = input.split("/");
                if (Utils.isInt(split[0])) {
                    if (Utils.isInt(split[1])) {
                        cs.getLuckyChestSettings().setChance(Double.parseDouble(split[0]));
                        cs.getLuckyChestSettings().setOutOfChance(Double.parseDouble(split[1]));
                        ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
                        return true;
                    } else {
                        ChatUtils.msgError(getP(), split[1] + " is not a valid number.");
                    }
                } else {
                    ChatUtils.msgError(getP(), split[0] + " is not a valid number.");
                }
            } catch (Exception exc) {
                ChatUtils.msgError(getP(), input + " is not formatted 'number/number' or 'chance/out of chance'");
            }
        } else if (value.equalsIgnoreCase("remove block-list")) {
            Optional<XMaterial> optional = XMaterial.matchXMaterial(input);
            if (optional.isPresent()) {
                Material m = optional.get().parseMaterial();
                if (cs.getLuckyChestSettings().getWhiteList().contains(m)) {
                    cs.getLuckyChestSettings().getWhiteList().remove(m);
                    ChatUtils.msgSuccess(getP(), "Removed the " + input + " value from the whitelist / blacklist");
                    return true;
                } else {
                    ChatUtils.msgError(getP(), input + " does not exist in the blacklist / whitelist.");
                }
            } else {
                ChatUtils.msgError(getP(), input + " is not a valid material.");
            }
        } else if (value.equalsIgnoreCase("add worlds")) {
            try {
                World w = Bukkit.getWorld(input);
                cs.getLuckyChestSettings().getWorlds().add(w);
                ChatUtils.msgSuccess(getP(), "Added " + input + " to the list of allowed worlds.");
                return true;
            } catch (Exception exc) {
                ChatUtils.msgError(getP(),
                        input + " is a non-existent world from the list of worlds: " + Bukkit.getWorlds().toString());
            }
        } else if (value.equalsIgnoreCase("remove worlds")) {
            try {
                World w = Bukkit.getWorld(input);
                if (w != null) {
                    if (cs.getLuckyChestSettings().getWorlds().contains(w)) {
                        cs.getLuckyChestSettings().getWorlds().remove(w);
                        ChatUtils.msgSuccess(getP(), "Removed " + input + " from the list of allowed worlds.");
                        return true;
                    } else {
                        ChatUtils.msgError(getP(),
                                input + " is not currently allowed in the worlds list to be remobed. Current worlds: " +
                                        cs.getLuckyChestSettings().getWorlds().toString());
                    }
                } else {
                    throw new Exception();
                }
            } catch (Exception exc) {
                ChatUtils.msgError(getP(),
                        input + " is a non-existent world from the list of worlds: " + Bukkit.getWorlds().toString());
            }
        }
        return false;
    }
}
