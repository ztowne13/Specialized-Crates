package me.ztowne13.customcrates.interfaces.igc.crates;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCListEditor;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 4/5/16.
 */
public class IGCMineCrate extends IGCMenuCrate
{
    public IGCMineCrate(CustomCrates cc, Player p, IGCMenu lastMenu, Crate crates)
    {
        super(cc, p, lastMenu, "&7&l> &6&lMine Crate", crates);
    }

    @Override
    public void open()
    {

        InventoryBuilder ib = createDefault(27);

        ib.setItem(9, IGCDefaultItems.EXIT_BUTTON.getIb());

        ib.setItem(11, new ItemBuilder(Material.FISHING_ROD, 1, 0).setName("&aChance").setLore("&7Current value: ")
                .addLore("&7" + cs.getClc().getChance() + "/" + cs.getClc().getOutOfChance()).addLore("")
                .addAutomaticLore("&f", 30,
                        "These are the odds that a crate will appear while mining. Formatted 'number/number'."));
        ib.setItem(12, new ItemBuilder(DynamicMaterial.LIGHT_GRAY_DYE, 1).setName("&aBlacklist")
                .addLore("&7Current value: ").addLore("&7" + cs.getClc().isBLWL() + "").addLore("")
                .addAutomaticLore("&f", 30, "Set whether the block-list is a blacklist or not."));
        ItemBuilder bList =
                new ItemBuilder(Material.STONE, 1, 0).setName("&aEdit the block-list").setLore("&7Current values: ");

        for (Material m : cs.getClc().getWhiteList())
        {
            bList.addLore("&7" + m.name());
        }
        bList.addLore("").addAutomaticLore("&f", 30, "These are all the materials on the blacklist (or whitelist).");
        ib.setItem(13, bList);

        ItemBuilder wList =
                new ItemBuilder(DynamicMaterial.PURPLE_DYE, 1).setName("&aEdit the worlds").setLore("&7Current values: ");
        for (String w : cs.getClc().getWorldsRaw())
        {
            wList.addLore("&7" + w);
        }
        wList.addLore("").addAutomaticLore("&f", 30,
                "These are the worlds where mine crates can be found. Remove all the worlds for ALL worlds to be allowed.");
        ib.setItem(14, wList);

        ib.open();
        putInMenu();
    }

    @Override
    public void manageClick(int slot)
    {
        switch (slot)
        {
            case 9:
                up();
                break;
            case 11:
                new InputMenu(getCc(), getP(), "chance", cs.getClc().getChance() + "/" + cs.getClc().getOutOfChance(),
                        "Format it 'chance/out of what chance'.", String.class, this);
                break;
            case 12:
                new InputMenu(getCc(), getP(), "blacklist", cs.getClc().isBLWL() + "", Boolean.class, this);
                break;
            case 13:
                new IGCListEditor(getCc(), getP(), this, "Block List", "Block", cs.getClc().getWhiteList(),
                        DynamicMaterial.STONE, 1, Material.class, "valueOf", "That is not a valid block name. Try STONE.").open();
                break;
            case 14:
                new IGCListEditor(getCc(), getP(), this, "Worlds List", "World", cs.getClc().getWorldsRaw(),
                        DynamicMaterial.MAP, 1).open();
                break;
            case 16:
                new InputMenu(getCc(), getP(), "remove worlds", cs.getClc().getWorlds().toString(),
                        "Current valid worlds: " + Bukkit.getWorlds(), String.class, this);
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        if (value.equalsIgnoreCase("chance"))
        {
            try
            {
                String[] split = input.split("/");
                if (Utils.isInt(split[0]))
                {
                    if (Utils.isInt(split[1]))
                    {
                        cs.getClc().setChance(Double.valueOf(split[0]));
                        cs.getClc().setOutOfChance(Double.valueOf(split[1]));
                        ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
                        return true;
                    }
                    else
                    {
                        ChatUtils.msgError(getP(), split[1] + " is not a valid number.");
                    }
                }
                else
                {
                    ChatUtils.msgError(getP(), split[0] + " is not a valid number.");
                }
            }
            catch (Exception exc)
            {
                ChatUtils.msgError(getP(), input + " is not formatted 'number/number' or 'chance/out of chance'");
            }
        }
        else if (value.equalsIgnoreCase("blacklist"))
        {
            try
            {
                Boolean b = Boolean.valueOf(input);
                cs.getClc().setBLWL(b);
                ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
                return true;
            }
            catch (Exception exc)
            {
                ChatUtils.msgError(getP(), input + " is not a valid true/false value.");
            }
        }
//        else if (value.equalsIgnoreCase("add block-list"))
//        {
//            try
//            {
//                Material m = DynamicMaterial.fromString(input.toUpperCase()).parseMaterial();
//                cs.getClc().getWhiteList().add(m);
//                ChatUtils.msgSuccess(getP(), "Added " + input + " to the whitelist / blacklist.");
//                return true;
//            }
//            catch (Exception exc)
//            {
//                ChatUtils.msgError(getP(), input + " is not a valid material.");
//            }
//        }
        else if (value.equalsIgnoreCase("remove block-list"))
        {
            try
            {
                Material m = DynamicMaterial.fromString(input.toUpperCase()).parseMaterial();
                if (cs.getClc().getWhiteList().contains(m))
                {
                    cs.getClc().getWhiteList().remove(m);
                    ChatUtils.msgSuccess(getP(), "Removed the " + input + " value from the whitelist / blacklist");
                    return true;
                }
                else
                {
                    ChatUtils.msgError(getP(), input + " does not exist in the blacklist / whitelist.");
                }
            }
            catch (Exception exc)
            {
                ChatUtils.msgError(getP(), input + " is not a valid material.");
            }
        }
        else if (value.equalsIgnoreCase("add worlds"))
        {
            try
            {
                World w = Bukkit.getWorld(input);
                cs.getClc().getWorlds().add(w);
                ChatUtils.msgSuccess(getP(), "Added " + input + " to the list of allowed worlds.");
                return true;
            }
            catch (Exception exc)
            {
                ChatUtils.msgError(getP(),
                        input + " is a non-existent world from the list of worlds: " + Bukkit.getWorlds().toString());
            }
        }
        else if (value.equalsIgnoreCase("remove worlds"))
        {
            try
            {
                World w = Bukkit.getWorld(input);
                if (w != null)
                {
                    if (cs.getClc().getWorlds().contains(w))
                    {
                        cs.getClc().getWorlds().remove(w);
                        ChatUtils.msgSuccess(getP(), "Removed " + input + " from the list of allowed worlds.");
                        return true;
                    }
                    else
                    {
                        ChatUtils.msgError(getP(),
                                input + " is not currently allowed in the worlds list to be remobed. Current worlds: " +
                                        cs.getClc().getWorlds().toString());
                    }
                }
                else
                {
                    throw new Exception();
                }
            }
            catch (Exception exc)
            {
                ChatUtils.msgError(getP(),
                        input + " is a non-existent world from the list of worlds: " + Bukkit.getWorlds().toString());
            }
        }
        return false;
    }
}
