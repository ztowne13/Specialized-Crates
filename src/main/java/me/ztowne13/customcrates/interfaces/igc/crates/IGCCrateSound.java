package me.ztowne13.customcrates.interfaces.igc.crates;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.sounds.SoundData;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 4/2/16.
 */
public class IGCCrateSound extends IGCTierMenu
{
    SoundData sd;

    public IGCCrateSound(SpecializedCrates cc, Player p, IGCMenu lastMenu, Crate crates, SoundData sd, String tier)
    {
        super(cc, p, lastMenu, "&7&l> &6&lSound", crates, tier);
        this.sd = sd;
    }

    @Override
    public void openMenu()
    {

        InventoryBuilder ib = createDefault(27);

        ib.setItem(9, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(8, new ItemBuilder(DynamicMaterial.RED_CARPET, 1).setName("&cDelete this sound")
                .addAutomaticLore("&7", 30, "NOTE: This action cannot be undone!"));

        ib.setItem(11, new ItemBuilder(Material.NOTE_BLOCK, 1, 0).setName("&aSound type").setLore("&7Current value: ")
                .addLore("&7" + sd.getSound().name()).addLore("").addAutomaticLore("&f", 30,
                        "Set the 'type' of the sound. The values for this can be found online. An example would be AMBIENT_CAVE"));
        ib.setItem(13, new ItemBuilder(Material.STONE_BUTTON, 1, 0).setName("&aSound pitch").setLore("&7Current value: ")
                .addLore("&7" + sd.getPitch()).addLore("").addAutomaticLore("&f", 30,
                        "This is the pitch that the sound will play at. 5 is a good normal-sounding pitch."));
        ib.setItem(15, new ItemBuilder(Material.LEVER, 1, 0).setName("&aSound volume").setLore("&7Current value: ")
                .addLore("&7" + sd.getVolume()).addLore("")
                .addAutomaticLore("&f", 30, "This is the volume that the sound will play at. 5 is a good 'normal' volume."));

        ItemBuilder testSound = new ItemBuilder(DynamicMaterial.MUSIC_DISC_13);
        testSound.setDisplayName("&aPreview the sound");
        testSound.addLore("")
                .addAutomaticLore("&f", 30, "Clicking this will play exactly what the sound is like when it is used!");
        ib.setItem(26, testSound);


        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot)
    {
        switch (slot)
        {
            case 8:
                cs.getSounds().getSounds().get(tier).remove(sd);
            case 9:
                up();
                break;
            case 11:
                new InputMenu(getCc(), getP(), "sound type", sd.getSound().name(),
                        "Click for a list of sounds -> https://www.spigotmc.org/wiki/cc-sounds-list/", String.class, this,
                        true);
                break;
            case 13:
                new InputMenu(getCc(), getP(), "sound pitch", sd.getSound().name(), "Change the pitch of the sound.",
                        Integer.class, this);
                break;
            case 15:
                new InputMenu(getCc(), getP(), "sound volume", sd.getSound().name(), "Change the volume of the sound.",
                        Integer.class, this);
                break;
            case 26:
                sd.playTo(getP(), getP().getLocation());
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        if (value.equalsIgnoreCase("sound type"))
        {
            try
            {
                Sound s = Sound.valueOf(input.toUpperCase());
                sd.setSound(s);
                ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input + ".");
                return true;
            }
            catch (Exception exc)
            {
                ChatUtils.msgError(getP(), input +
                        " is not a valid sound. Click for a list of sounds -> https://www.spigotmc.org/wiki/cc-sounds-list/");
            }
        }
        else if (value.equalsIgnoreCase("sound pitch"))
        {
            if (Utils.isInt(input))
            {
                sd.setPitch(Integer.parseInt(input));
                ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input + ".");
                return true;
            }
            else
            {
                ChatUtils.msgError(getP(), input + " is not a valid integer (number). Make sure it has no decimals.");
            }
        }
        else if (value.equalsIgnoreCase("sound volume"))
        {
            if (Utils.isInt(input))
            {
                sd.setVolume(Integer.parseInt(input));
                ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input + ".");
                return true;
            }
            else
            {
                ChatUtils.msgError(getP(), input + " is not a valid integer (number). Make sure it has no decimals.");
            }
        }
        return false;
    }
}
