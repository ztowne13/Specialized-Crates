package me.ztowne13.customcrates.interfaces.igc.fileconfigs;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.types.CrateType;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.crates.crateanimations.*;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.FileHandler;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ztowne13 on 3/20/16.
 */
public class IGCMenuCrateConfig extends IGCMenu
{
    public IGCMenuCrateConfig(CustomCrates cc, Player p, IGCMenu lastMenu)
    {
        super(cc, p, lastMenu, "&7&l> &6&lCrateConfig.YML");
    }

    @Override
    public void open()
    {
        getP().closeInventory();
        putInMenu();

        FileHandler fu = getCc().getCrateconfigFile();
        FileConfiguration fc = fu.get();

        InventoryBuilder ib = createDefault(27);

        ib.setItem(18, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());
        ib.setItem(9, IGCDefaultItems.RELOAD_BUTTON.getIb());

        ib.setItem(11,
                new ItemBuilder(Material.PAPER, 1, 0).setName("&aCSGO Animation").setLore("&7Animation name: &fINV_CSGO")
                        .addLore("").addLore("&7Used by crates: &f" + CrateType.INV_CSGO.getUses()));
        ib.setItem(12, new ItemBuilder(Material.PAPER, 1, 0).setName("&aRoulette Animation")
                .setLore("&7Animation name: &fINV_ROULETTE").addLore("")
                .addLore("&7Used by crates: &f" + CrateType.INV_ROULETTE.getUses()));
        ib.setItem(13,
                new ItemBuilder(Material.PAPER, 1, 0).setName("&aMenu Animation").setLore("&7Animation name: &fINV_MENU")
                        .addLore("").addLore("&7Used by crates: &f" + CrateType.INV_MENU.getUses()));
        ib.setItem(14, new ItemBuilder(Material.PAPER, 1, 0).setName("&aEnclose Animation")
                .setLore("&7Animation name: &fINV_ENCLOSE").addLore("")
                .addLore("&7Used by crates: &f" + CrateType.INV_ENCLOSE.getUses()));
        ib.setItem(15, new ItemBuilder(Material.PAPER, 1, 0).setName("&aDiscover Animation")
                .setLore("&7Animation name: &fINV_DISCOVER").addLore("")
                .addLore("&7Used by crates: &f" + CrateType.INV_DISCOVER.getUses()));
        ib.setItem(16, new ItemBuilder(Material.PAPER, 1, 0).setName("&aOpen Chest Animation")
                .setLore("&7Animation name: &fBLOCK_CRATEOPEN").addLore("")
                .addLore("&7Used by crates: &f" + CrateType.BLOCK_CRATEOPEN.getUses()));

		/*ib.setItem(27, IGCDefaultItems.EXIT_BUTTON.getIb());

		ItemBuilder nameDisplay = new ItemBuilder(Material.PAPER, 1, 0).setLore("").addLore("&7---->");
		ItemBuilder nameEditor = new ItemBuilder(Material.BOOK, 1, 0).setName("&aChange the inv-name");

		// Roulette
		ib.setItem(1, nameDisplay.setName("&aRoulette Animation"));
		ib.setItem(2, nameEditor.setLore("&7Current value:").addLore("&f" + getValue("Roulette", "inv-name")));
		ib.setItem(3, new ItemBuilder(Material.RECORD_3, 1, 0).setName("&aChange the tick-sound").setLore("&7Current value:").addLore("&f" + getValue("Roulette", "tick-sound")));
		ib.setItem(4, new ItemBuilder(Material.STONE_BUTTON, 1, 0).setName("&aChange the tick-speed-per-run").setLore("&7Current value:").addLore("&f" + getValue("Roulette", "tick-speed-per-run")));
		ib.setItem(5, new ItemBuilder(Material.STONE_BUTTON, 1, 0).setName("&aChange the final-crate-tick-length").setLore("&7Current value:").addLore("&f" + getValue("Roulette", "final-crate-tick-length")));

		ItemBuilder randomBlocks = new ItemBuilder(Material.STONE, 1, 0).setName("&aAdd to random-blocks").setLore("&7Current values: ");
		for(String s: fc.getStringList("CrateType.Inventory.Roulette.random-blocks"))
		{
			randomBlocks.addLore("&f" + s);
		}

		ib.setItem(6, randomBlocks);
		ib.setItem(7, randomBlocks.setName("&aRemove from random-blocks"));

		// CS:GO
		ib.setItem(19, nameDisplay.setName("&aCSGO Animation"));
		ib.setItem(20, nameEditor.setLore("&7Current value:").addLore("&f" + getValue("CSGO", "inv-name")));
		ib.setItem(21, new ItemBuilder(Material.RECORD_3, 1, 0).setName("&aChange the tick-sound").setLore("&7Current value:").addLore("&f" + getValue("CSGO", "tick-sound")));
		ib.setItem(22, new ItemBuilder(Material.STONE_BUTTON, 1, 0).setName("&aChange the tick-speed-per-run").setLore("&7Current value:").addLore("&f" + getValue("CSGO", "tick-speed-per-run")));
		ib.setItem(23, new ItemBuilder(Material.STONE_BUTTON, 1, 0).setName("&aChange the final-crate-tick-length").setLore("&7Current value:").addLore("&f" + getValue("CSGO", "final-crate-tick-length")));

		randomBlocks = new ItemBuilder(Material.STONE, 1, 0).setName("&aAdd to filler-blocks").setLore("&7Current values: ");
		for(String s: fc.getStringList("CrateType.Inventory.CSGO.filler-blocks"))
		{
			randomBlocks.addLore("&f" + s);
		}

		// Menu
		ib.setItem(24, randomBlocks);
		ib.setItem(25, randomBlocks.setName("&aRemove from filler-blocks"));
		ib.setItem(26, new ItemBuilder(Material.REDSTONE_TORCH_ON, 1, 0).setName("&aChange the identifier-block").setLore("&7Current value:").addLore("&f" + getValue("CSGO", "identifier-block")));

		ib.setItem(37, nameDisplay.setName("&aMenu Animation"));
		ib.setItem(38, nameEditor.setLore("&7Current value:").addLore("&f" + getValue("Menu", "inv-name")));
		ib.setItem(39, new ItemBuilder(Material.FENCE, 1, 0).setName("&aChange the inventory-rows").setLore("&7Current value:").addLore("&f" + getValue("Menu", "inventory-rows")));
		ib.setItem(40, new ItemBuilder(Material.WOOD_BUTTON, 1, 0).setName("&aChange the minimum-rewards").setLore("&7Current value:").addLore("&f" + getValue("Menu", "minimum-rewards")));
		ib.setItem(41, new ItemBuilder(Material.WOOD_BUTTON, 1, 0).setName("&aChange the maximum-rewards").setLore("&7Current value:").addLore("&f" + getValue("Menu", "maximum-rewards")));*/

        ib.open();
    }

    @Override
    public void manageClick(int slot)
    {
        switch (slot)
        {
            case 18:
                up();
                break;
            case 0:
                getCc().getCrateconfigFile().save();
                ChatUtils.msgSuccess(getP(), "CrateConfig.YML saved!");
                break;
            case 9:
                reload();
                break;
            case 11:
                new IGCAnimCSGO(getCc(), getP(), this).open();
                break;
            case 12:
                new IGCAnimRoulette(getCc(), getP(), this).open();
                break;
            case 13:
                new IGCAnimMenu(getCc(), getP(), this).open();
                break;
            case 14:
                new IGCAnimEnclose(getCc(), getP(), this).open();
                break;
            case 15:
                new IGCAnimDiscover(getCc(), getP(), this).open();
                break;
            case 16:
                new IGCAnimOpenChest(getCc(), getP(), this).open();
                break;
        }

    }

    @Override
    public boolean handleInput(String value, String input)
    {
        Object type = getInputMenu().getType();
        if (type == Double.class)
        {
            if (Utils.isDouble(input))
            {
                getCc().getCrateconfigFile().get().set(getPath(value), Double.valueOf(input));
                ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
                return true;
            }
            else
            {
                ChatUtils.msgError(getP(), "This is not a valid decimal value, please try again.");
            }
        }
        else if (type == Integer.class)
        {
            if (Utils.isInt(input))
            {
                getCc().getCrateconfigFile().get().set(getPath(value), Integer.parseInt(input));
                ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
                return true;
            }
            else
            {
                ChatUtils.msgError(getP(), "This is not a valid number, please try again.");
            }
        }
        else
        {
            if (value.equalsIgnoreCase("add Roulette.random-blocks") || value.equalsIgnoreCase("add CSGO.filler-blocks"))
            {
                try
                {
                    String[] split = input.split(";");
                    DynamicMaterial m = DynamicMaterial.fromString(input.toUpperCase());
                    if (Utils.isInt(split[1]))
                    {
                        int id = Integer.parseInt(split[1]);
                        List<String> currentList = getCc().getCrateconfigFile().get().contains(getPath(value.substring(4))) ?
                                getCc().getCrateconfigFile().get().getStringList(getPath(value.substring(4))) :
                                new ArrayList<String>();
                        currentList.add(m.name() + ";" + id);
                        getCc().getCrateconfigFile().get().set(getPath(value.substring(4)), currentList);
                        return true;
                    }
                    else
                    {
                        ChatUtils.msgError(getP(), split[1] + " is not a valid number.");
                    }
                }
                catch (Exception exc)
                {
                    ChatUtils.msgError(getP(), input + " does not have a valid material or is not formatted MATERIAL;DATA");
                }
            }
            else if (value.equalsIgnoreCase("remove Roulette.random-blocks") ||
                    value.equalsIgnoreCase("remove CSGO.filler-blocks"))
            {
                if (getCc().getCrateconfigFile().get().contains(getPath(value.substring(7))))
                {
                    boolean found = false;
                    List<String> newList = new ArrayList<>();
                    for (String s : getCc().getCrateconfigFile().get().getStringList(getPath(value.substring(7))))
                    {
                        if (s.equalsIgnoreCase(input))
                        {
                            found = true;
                        }
                        else
                        {
                            newList.add(s);
                        }
                    }

                    if (found)
                    {
                        ChatUtils.msgSuccess(getP(), "Removed the " + input + " value.");
                        getCc().getCrateconfigFile().get().set(getPath(value.substring(7)), newList);
                        return true;
                    }
                    else
                    {
                        ChatUtils.msgError(getP(), input + " does not exist in the filler / random blocks: " +
                                getCc().getCrateconfigFile().get().getStringList(getPath(value.substring(7))));
                    }
                }
                else
                {
                    ChatUtils.msgError(getP(), "No filler blocks currently exist to remove.");
                    return true;
                }
            }
            else
            {
                getCc().getCrateconfigFile().get().set(getPath(value), input);
                ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
                return true;
            }
        }
        return false;
    }

    public String getPath(String value)
    {
        return "CrateType.Inventory." + value;
    }

    public String getValue(String crateType, String value)
    {
        FileHandler fu = getCc().getCrateconfigFile();
        FileConfiguration fc = fu.get();
        return fc.get("CrateType.Inventory." + crateType + "." + value).toString();
    }
}
