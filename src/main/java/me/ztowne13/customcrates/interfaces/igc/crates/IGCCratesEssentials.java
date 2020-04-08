package me.ztowne13.customcrates.interfaces.igc.crates;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.crates.types.display.EntityTypes;
import me.ztowne13.customcrates.crates.types.display.MaterialPlaceholder;
import me.ztowne13.customcrates.crates.types.display.npcs.Citizens2NPCPlaceHolder;
import me.ztowne13.customcrates.crates.types.display.npcs.MobPlaceholder;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCItemEditor;
import me.ztowne13.customcrates.interfaces.igc.IGCListSelector;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.NPCUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class IGCCratesEssentials extends IGCMenuCrate
{
    public IGCCratesEssentials(SpecializedCrates cc, Player p, IGCMenu lastMenu, Crate crates)
    {
        super(cc, p, lastMenu, "&7&l> &6&lThe Defaults", crates);
    }

    @Override
    public void open()
    {

        InventoryBuilder ib = createDefault(18);

        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());

        //9-17 (11-15)
        ib.setItem(9, new ItemBuilder(crates.isEnabled() ? DynamicMaterial.LIME_WOOL : DynamicMaterial.RED_WOOL, 1)
                .setName(crates.isEnabled() ? "&aEnabled" : "&cDisabled").addLore("&7Click me to toggle the crate.")
                .addLore("").addAutomaticLore("&f", 30, "Completely enable or disable the crate."));
        ib.setItem(2, new ItemBuilder(Material.BOOK, 1, 0).setName("&aSet the crate permission").setLore("&7Current value: ")
                .addLore("&7" + cs.getPermission()).addLore("")
                .addAutomaticLore("&f", 30,
                        "The permissions that is required to open this crate. Great for monthly crates, donor crates."));
        ib.setItem(3, new ItemBuilder(Material.BUCKET, 1, 0).setName("&aSet the obtain-method").setLore("&7Current value: ")
                .addLore("&7" + cs.getObtainType().name()).addLore("")
                .addAutomaticLore("&f", 30,
                        "STATIC: Crate stays in place forever. DYNAMIC: Crate disappears when used. LUCKYCHEST (Mine Crate): Find this crate while mining."));
        ib.setItem(4, new ItemBuilder(Material.PAPER, 1, 0).setName("&aSet the inventory-name").setLore("&7Current value: ")
                .addLore("&7" + cs.getCrateInventoryName()).addLore("").addAutomaticLore("&f", 30,
                        "The name of the inventory for the animation, or name of the MultiCrate. If this value is set to 'none' the inventory name in the CrateConfig.YML for that animation will be used."));
        ib.setItem(6, new ItemBuilder(DynamicMaterial.BIRCH_BUTTON, 1).setName("&aSet the display.type")
                .setLore("&7Current value: ").addLore("&7" + cs.getPlaceholder()).addLore("")
                .addAutomaticLore("&f", 30, "How the crate will appear to players (block, npc, mob, etc.)"));

        if (crates.getSettings().getPlaceholder().toString().equalsIgnoreCase("mob") ||
                crates.getSettings().getPlaceholder().toString().equalsIgnoreCase("npc"))
        {
            ib.setItem(15, new ItemBuilder(Material.STONE_BUTTON, 1, 0).setName("&aSet the " + cs.getPlaceholder() + " type")
                    .setLore("&7Current value: ").addLore("&7" + cs.getPlaceholder().getType()).addLore("")
                    .addAutomaticLore("&f", 30, "Set the type of mob it will be or playername for the NPC."));
        }

        ib.setItem(12,
                new ItemBuilder(DynamicMaterial.BIRCH_FENCE_GATE, 1).setName("&aSet auto-close").setLore("&7Current value: ")
                        .addLore("&7" + cs.isAutoClose()).addLore("").addAutomaticLore("&f", 30,
                        "If the crate is in an inventory, should it automatically close when it is done?"));
        ib.setItem(11,
                new ItemBuilder(DynamicMaterial.SNOWBALL, 1).setName("&aSet the cooldown").setLore("&7Current value: ")
                        .addLore("&7" + cs.getCooldown()).addLore("").addAutomaticLore("&f", 30,
                        "The duration of time, in seconds, between when a player can open the crate. Set to -1 to have no cooldown."));
        ib.setItem(8, new ItemBuilder(DynamicMaterial.CHEST, 1).setName("&a&lEdit the crate item.").addLore("")
                .addAutomaticLore("&f", 30, "Click to open the item editor."));
        if (!crates.isMultiCrate())
        {
            ib.setItem(17,
                    new ItemBuilder(DynamicMaterial.TRIPWIRE_HOOK, 1).setName("&a&lEdit the crate key.").addLore("")
                            .addAutomaticLore("&f", 30,
                                    "Click to open the item editor."));
            ib.setItem(5, new ItemBuilder(Material.ITEM_FRAME, 1, 0).setName("&aSet the crate animation")
                    .setLore("&7Current Value: ").addLore("&7" + cs.getCrateType().name()).addLore("")
                    .addAutomaticLore("&f", 30, "This is the animation that will play when the crate is opened."));
            ib.setItem(13,
                    new ItemBuilder(Material.SLIME_BALL, 1, 0).setName("&aSet require key").setLore("&7Current value: ")
                            .addLore("&7" + cs.isRequireKey()).addLore("").addAutomaticLore("&f", 30,
                            "Does the crate require a key? Best for keyless weekly/monthly crates or DYNAMIC crates that can be placed to open without a key. Or if minecrates can be opened without needing a key."));

            ItemBuilder cost = new ItemBuilder(DynamicMaterial.DIAMOND, 1);
            cost.setDisplayName("&aEdit the cost.");
            cost.addLore("&7Current Value:").addLore("&7" + crates.getSettings().getCost());
            cost.addLore("").addAutomaticLore("&f", 30, "This is how much it costs to open the crate, " +
                    "regardless of whether or not require-key is set to true or false.");
            cost.addLore("").addAutomaticLore("&c", 30, "REQUIRES Vault");
            ib.setItem(14, cost);
        }

        getIb().open();
        putInMenu();
    }

    @Override
    public void manageClick(int slot)
    {
        if ((slot == 5 || slot == 7 || slot == 16) && crates.isMultiCrate())
        {
            return;
        }

        switch (slot)
        {
            case 0:
                up();
                break;
            case 9:
                if (crates.isCanBeEnabled())
                {
                    crates.setEnabled(!crates.isEnabled());
                    getIb().setItem(9,
                            new ItemBuilder(crates.isEnabled() ? DynamicMaterial.LIME_WOOL : DynamicMaterial.RED_WOOL, 1)
                                    .setName(crates.isEnabled() ? "&aEnabled" : "&cDisabled")
                                    .addLore("&7Click me to toggle the crate."));
                }
                else
                {
                    getIb().setItem(9, new ItemBuilder(getIb().getInv().getItem(9)).setName("&4You cannot do this")
                            .setLore("&4This crate cannot be enabled").addLore("&4for it failed to load,")
                            .addLore("&4due to a misconfiguration, on").addLore("&4startup. Please fix any errors,")
                            .addLore("&4reload the plugin, and try again."));
                }
                break;
            case 2:
                new InputMenu(getCc(), getP(), "permission", cs.getPermission(), "Type 'none' to remove the permission.",
                        String.class, this, true);
                break;
            case 3:
//                new InputMenu(getCc(), getP(), "obtain-method", cs.getOt().name(),
//                        "Available obtain methods: " + Arrays.toString(ObtainType.values()), String.class, this, true);
                new IGCListSelector(getCc(), getP(), this, "Obtain Type", Arrays.asList(ObtainType.values()),
                        DynamicMaterial.PAPER, 1, ObtainType.descriptors()).open();
                break;
            case 4:
                new InputMenu(getCc(), getP(), "inventory-name", cs.getCrateInventoryName(),
                        "Set to 'none' to use the inv-name from the CrateConfig.YML", String.class, this, true);
                break;
            case 6:
//                new InputMenu(getCc(), getP(), "display.type", cs.getDcp().toString(),
//                        "Available display types: block, mob, npc",
//                        String.class, this, true);
                new IGCListSelector(getCc(), getP(), this, "Display Type",
                        Arrays.asList(new String[]{"BLOCK", "MOB", "NPC"}), DynamicMaterial.PAPER, 1,
                        Arrays.asList(new String[]{"", "Requires Citizens v2", "Requires Citizens v2"})).open();
                break;
            case 15:
                if (cs.getPlaceholder().toString().equalsIgnoreCase("npc"))
                {
                    new InputMenu(getCc(), getP(),
                            "display." + (cs.getPlaceholder().toString().equalsIgnoreCase("mob") ? "creature" : "name"),
                            cs.getPlaceholder().getType(), cs.getPlaceholder().toString().equalsIgnoreCase("mob") ?
                            "Available mob types: " + Arrays.toString(EntityTypes.values()) : "Use a player's name",
                            String.class, this, true);
                }
                else if (cs.getPlaceholder().toString().equalsIgnoreCase("mob"))
                {
                    new IGCListSelector(getCc(), getP(), this, "Mob Type", Arrays.asList(EntityTypes.values()),
                            DynamicMaterial.PAPER, 1, null).open();
                }
                break;
            case 11:
                new InputMenu(getCc(), getP(), "cooldown", cs.getCooldown() + "", "Time is measured in seconds.",
                        Integer.class, this);
                break;
            case 12:
                cs.setAutoClose(!cs.isAutoClose());
                open();
                break;
            case 8:
                new IGCItemEditor(getCc(), getP(), this, crates.getSettings().getCrateItemHandler().getItem()).open();
                break;
            case 17:
                new IGCItemEditor(getCc(), getP(), this, crates.getSettings().getKeyItemHandler().getItem()).open();
                break;
            case 5:
//                new InputMenu(getCc(), getP(), "animation", cs.getCt().name(),
//                        "Available animations: " + Arrays.toString(CrateType.values()), String.class, this, true);
                new IGCListSelector(getCc(), getP(), this, "Animation Type", Arrays.asList(CrateAnimationType.values()),
                        DynamicMaterial.PAPER, 1, null).open();
                break;
            case 13:
                cs.setRequireKey(!cs.isRequireKey());
                open();
                break;
            case 14:
                new InputMenu(getCc(), getP(), "cost", cs.getCost() + "",
                        "How much the crate will cost to open. Set to 0 to have no cost.", Integer.class, this, true);
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        if (value.equalsIgnoreCase("Obtain Type"))
        {
            try
            {
                ObtainType ot = ObtainType.valueOf(input.toUpperCase());
                cs.setObtainType(ot);
                ChatUtils.msgSuccess(getP(), "Set the obtain type to " + input);
                return true;
            }
            catch (Exception exc)
            {
                ChatUtils.msgError(getP(),
                        input + " is not one of the obtain types: " + Arrays.toString(ObtainType.values()));
            }
        }
        else if (value.equalsIgnoreCase("permission"))
        {
            if (input.equalsIgnoreCase("none"))
            {
                cs.setPermission("no permission");
                ChatUtils.msgSuccess(getP(), "Removed the permission.");
            }
            else
            {
                cs.setPermission(input);
                ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
            }
            return true;
        }
        else if (value.equalsIgnoreCase("Animation Type"))
        {
            try
            {
                CrateAnimationType ct = CrateAnimationType.valueOf(input.toUpperCase());
                cs.setCrateType(ct);
                ChatUtils.msgSuccess(getP(), "Set the Animation Type to " + input);
                return true;
            }
            catch (Exception exc)
            {
                ChatUtils.msgError(getP(),
                        input + " is not valid in the list of crate animations: " + Arrays.toString(CrateAnimationType.values()));
            }
        }
        else if (value.equalsIgnoreCase("inventory-name"))
        {
            if (input.length() < 33)
            {
                if (input.equalsIgnoreCase("null") || input.equalsIgnoreCase("none"))
                {
                    cs.setCrateInventoryName(null);
                    return true;
                }
                cs.setCrateInventoryName(input);
                ChatUtils.msgSuccess(getP(), "Set the " + value + " to " + input);
                return true;
            }
            ChatUtils.msgError(getP(), input + " as an inventory-name cannot be longer than 32 characters.");
        }
        else if (value.equalsIgnoreCase("Display Type"))
        {
            switch (input.toUpperCase())
            {
                case "BLOCK":
                    cs.setPlaceholder(new MaterialPlaceholder(getCc()));
                    return true;
                case "NPC":
                    if (!NPCUtils.isCitizensInstalled())
                    {
                        ChatUtils.msgError(getP(), "Citizens is not installed!");
                        return false;
                    }
                    cs.setPlaceholder(new Citizens2NPCPlaceHolder(getCc()));
                    ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
                    manageClick(15);
                    break;
                case "MOB":
                    if (!NPCUtils.isCitizensInstalled())
                    {
                        ChatUtils.msgError(getP(), "Citizens is not installed!");
                        return false;
                    }
                    cs.setPlaceholder(new MobPlaceholder(getCc()));
                    ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
                    manageClick(15);
                    break;
                default:
                    ChatUtils.msgError(getP(), input + " is not BLOCK, NPC, or MOB");
            }

            if (input.equalsIgnoreCase("mob") || input.equalsIgnoreCase("npc"))
            {
                getIb().setItem(15,
                        new ItemBuilder(Material.STONE_BUTTON, 1, 0).setName("&aSet the " + cs.getPlaceholder() + " type")
                                .setLore("&7Current value: ").addLore("&7" + cs.getPlaceholder().getType()));
            }
        }
        else if (value.equalsIgnoreCase("Mob Type"))
        {
            try
            {
                EntityTypes et = EntityTypes.valueOf(input.toUpperCase());
                cs.getPlaceholder().setType(et.name());
                ChatUtils.msgSuccess(getP(), "Set mob type to " + input);
                return true;
            }
            catch (Exception exc)
            {
                ChatUtils.msgError(getP(), input + " is not a valid entity type: " + Arrays.toString(EntityTypes.values()));
            }
        }
        else if (value.equalsIgnoreCase("display.name"))
        {
            cs.getPlaceholder().setType(input);
            ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
            return true;
        }
        else if (value.equalsIgnoreCase("cooldown"))
        {
            if (Utils.isInt(input))
            {
                cs.setCooldown(Integer.parseInt(input));
                ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
                return true;
            }
            else
            {
                ChatUtils.msgError(getP(), input + " is not a valid number.");
            }
        }
        else if (value.equalsIgnoreCase("cost"))
        {
            if (Utils.isInt(input))
            {
                cs.setCost(Integer.valueOf(input));
                ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
                return true;
            }
            else
            {
                ChatUtils.msgError(getP(), input + " is not a valid number.");
            }
        }
        return false;
    }
}
