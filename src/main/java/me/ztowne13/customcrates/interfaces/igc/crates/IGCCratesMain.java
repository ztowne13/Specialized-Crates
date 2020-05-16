package me.ztowne13.customcrates.interfaces.igc.crates;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.crates.previeweditor.IGCCratePreviewOrRewardMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ztowne13 on 3/26/16.
 */
public class IGCCratesMain extends IGCMenuCrate
{
    public IGCCratesMain(SpecializedCrates cc, Player p, IGCMenu lastMenu, Crate crates)
    {
        super(cc, p, lastMenu, "&7&l> &6&l" + crates.getName(), crates);
    }

    @Override
    public void openMenu()
    {

        InventoryBuilder ib = createDefault(crates.isMultiCrate() ? 27 : 45);

        ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());
        ib.setItem(9, IGCDefaultItems.RELOAD_BUTTON.getIb());
        ib.setItem(crates.isMultiCrate() ? 18 : 36, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(3, new ItemBuilder(DynamicMaterial.RED_DYE, 1).setName("&4&lDelete this crate")
                .setLore("&cThis action CANNOT be undone.").addLore("&e&oNote: This does not delete rewards").addLore("")
                .addLore("&7This will delete the entire").addLore("&7file for this crate and will")
                .addLore("&7erase all data for it."));

        ItemBuilder rename = new ItemBuilder(DynamicMaterial.WRITABLE_BOOK, 1);
        rename.setName("&aRename this crate");
        rename.addLore("&7Current Value:").addLore("&7" + getCrates().getName());
        rename.addLore("").addAutomaticLore("&f", 30,
                "This WILL reload the plugin when finished, please make sure all changes are saved.");

        ib.setItem(4, rename);

        ib.setItem(8, new ItemBuilder(DynamicMaterial.RED_CARPET, 1).setName("&aDelete all placed instances")
                .setLore("&7Click this runs the command:").addLore("&7/ccrates delallcratetype " + crates.getName())
                .addLore("").addLore("&7It deletes all crates").addLore("&7of this type that have been")
                .addLore("&7placed."));
        //ib.setItem(4, new ItemBuilder(Material.CHEST, 1, 0).setName("&a" + crates.getName()));

        int errors = crates.getSettings().getStatusLogger().getFailures();
        ib.setItem(5, new ItemBuilder(DynamicMaterial.REDSTONE_BLOCK, 1)
                .setName(errors == 0 ? "&aThis crate has no errors!" : "&c&lClick to view ERRORS")
                .addLore("&7Errors: " + errors));

        ib.setItem(10, new ItemBuilder(Material.STONE_BUTTON, 1, 0).setName("&aThe &lEssentials")
                .setLore("&7This includes things such as").addLore("&7crate / key material,")
                .addLore("&7crate-animation, obtain methods").addLore("&7and more"));
        ib.setItem(16,
                new ItemBuilder(Material.NETHER_STAR, 1, 0).setName("&a&lParticles").setLore("&7Modify particles for play")
                        .addLore("&7and open use."));
        ib.setItem(21, new ItemBuilder(Material.BOOK, 1, 0).setName("&a&lHolograms").setLore("&7Modify the holograms."));

        if (!crates.isMultiCrate())
        {
            ib.setItem(23,
                    new ItemBuilder(Material.NOTE_BLOCK, 1, 0).setName("&a&lSounds").setLore("&7Modify the sounds for when")
                            .addLore("&7the crate is opened and").addLore("&7reward is given."));
            ib.setItem(28, new ItemBuilder(DynamicMaterial.FIREWORK_ROCKET, 1).setName("&a&lFireworks")
                    .setLore("&7Modify the fireworks."));
            ib.setItem(34,
                    new ItemBuilder(Material.PAPER, 1, 0).setName("&a&lActions").setLore("&7Modify messages, broadcasts,")
                            .addLore("&7titles, subtitles, and").addLore("&7actionbars."));

            ItemBuilder rewards = new ItemBuilder(DynamicMaterial.LIGHT_BLUE_DYE, 1);
            rewards.setDisplayName("&a&lRewards");
            rewards.addAutomaticLore("&7", 30, "Edit the rewards and edit the reward preview menu.");

            ib.setItem(40, rewards);
        }
        else
        {
            ib.setItem(23, new ItemBuilder(Material.LADDER, 1, 0).setName("&aMultiCrate Values")
                    .setLore("&7Modify the multicrate inventory and").addLore("&7other values."));
        }

        if (cs.getObtainType().equals(ObtainType.LUCKYCHEST))
        {
            ib.setItem(crates.isMultiCrate() ? 35 : 44, new ItemBuilder(Material.ENDER_CHEST, 1, 0).setName("&aMine Chest")
                    .setLore("&7Config all values for the").addLore("&7mine chest."));
        }

        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot)
    {
        if (crates.isMultiCrate() && (slot == 28 || slot == 34 || slot == 40))
        {
            return;
        }

        switch (slot)
        {
            case 0:
                if (!crates.isMultiCrate() &&
                        (cs.getRewards().getCrateRewards() == null || cs.getRewards().getCrateRewards().length == 0))
                {
                    getIb().setItem(slot,
                            new ItemBuilder(getIb().getInv().getItem(slot)).setName("&cPlease add some rewards")
                                    .setLore("&cbefore saving!"));
                }
                else
                {
                    try
                    {
                        crates.getSettings().saveAll();
                        ChatUtils.msgSuccess(getP(), "Saved the file!");
                    }
                    catch (Exception exc)
                    {
                        exc.printStackTrace();
                        ChatUtils.msgError(getP(), "Failed to save the file!");
                    }
                }
                break;
            case 3:
                if (ChatUtils.removeColor(getIb().getInv().getItem(3).getItemMeta().getDisplayName())
                        .equalsIgnoreCase("Delete this crate"))
                {
                    getIb().setItem(3, new ItemBuilder(DynamicMaterial.RED_DYE, 1).setName("&6CONFIRM DELETE")
                            .setLore("&4&lTHIS CANNOT BE UNDONE!"));
                }
                else
                {
                    getP().closeInventory();
                    ChatUtils.msg(getP(), "&6&lNote: &eDeleting...");
                    String path = crates.deleteCrate();
                    ChatUtils.msgSuccess(getP(), "Successfully deleted file on path &e" + path);
                }
                break;
            case 4:
                new InputMenu(getCc(), getP(), "Crate name", crates.getName(), "Do not use ANY spaces or special characters",
                        String.class, this, true);
                break;
            case 5:
                getP().closeInventory();
                getP().performCommand("scrates errors " + crates.getName());
                break;
            case 9:
                reload();
                break;
            case 18:
                if (!crates.isMultiCrate())
                {
                    return;
                }
            case 36:
                if (getLastMenu() == null)
                {
                    getP().closeInventory();
                    break;
                }
                up();
                break;
            case 8:
                getP().chat("/ccrates delallcratetype " + crates.getName());
                break;
            case 10:
                new IGCCratesEssentials(getCc(), getP(), this, crates).open();
                break;
            case 16:
                Set<String> blankParticles = new HashSet<String>();
                blankParticles.add("PLAY");
                if (!crates.isMultiCrate())
                {
                    blankParticles.add("OPEN");
                }
                new IGCTierSelector(getCc(), getP(), this, crates, blankParticles,
                        new IGCCrateParticles(getCc(), getP(), this, crates, "")).open();
                break;
            case 21:
                new IGCCrateHolograms(getCc(), getP(), this, crates).open();
                break;
            case 23:
                if (crates.isMultiCrate())
                {
                    new IGCMultiCrateMain(getCc(), getP(), this, crates).open();
                }
                else
                {
                    Set<String> blankSounds = new HashSet<String>();
                    blankSounds.add("OPEN");
                    new IGCTierSelector(getCc(), getP(), this, crates, blankSounds,
                            new IGCCrateSounds(getCc(), getP(), this, crates, "")).open();
                }
                break;
            case 28:
                Set<String> blankFireworks = new HashSet<String>();
                blankFireworks.add("OPEN");
                new IGCTierSelector(getCc(), getP(), this, crates, blankFireworks,
                        new IGCCrateFireworks(getCc(), getP(), this, crates, "")).open();
                break;
            case 34:
                Set<String> blankActions = new HashSet<String>();
                blankActions.add("DEFAULT");
                new IGCTierSelector(getCc(), getP(), this, crates, blankActions,
                        new IGCCrateActions(getCc(), getP(), this, crates, "")).open();
                break;
            case 35:
                if (!crates.isMultiCrate())
                {
                    return;
                }
            case 40:
                new IGCCratePreviewOrRewardMenu(getCc(), getP(), crates, this).open();
                break;
            case 44:
                if (cs.getObtainType().equals(ObtainType.LUCKYCHEST))
                {
                    new IGCMineCrate(getCc(), getP(), this, crates).open();
                }
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
		if(value.equalsIgnoreCase("crate name"))
		{
            String regex = "^[a-zA-Z0-9]+$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);
			if(matcher.matches())
			{
			    if(crates.rename(input))
                {
                    ChatUtils.msgSuccess(getP(), "Successfully renamed " + crates.getName() + " to " + input);
                    ChatUtils.msgInfo(getP(), "Reloading the plugin to let changes take effect.");
                    return false;
                }
                ChatUtils.msgError(getP(), "Failed to rename the crate. This likely because the crate name " + input + " already exists. Please try a different name.");
                return false;
			}
			ChatUtils.msgError(getP(), input + " is not alphanumeric (no spaces and only letters)");
			return false;
		}
        return false;
    }
}
