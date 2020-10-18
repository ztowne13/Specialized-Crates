package me.ztowne13.customcrates.interfaces.igc.crates;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.sounds.SoundData;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.InventoryUtils;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by ztowne13 on 4/2/16.
 */
public class IGCCrateSounds extends IGCTierMenu {
    HashMap<Integer, SoundData> slots = new HashMap<>();
    SoundData sd;

    public IGCCrateSounds(SpecializedCrates cc, Player p, IGCMenu lastMenu, Crate crates, String tier) {
        super(cc, p, lastMenu, "&7&l> &6&lSounds", crates, tier);
    }

    @Override
    public void openMenu() {
        InventoryBuilder ib = createDefault(InventoryUtils.getRowsFor(4,
                (crates.getSettings().getSound().getSounds().containsKey(tier) ? crates.getSettings().getSound().getSounds().get(tier).size() :
                        0) + 9));

        ib.setItem(9, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(8, new ItemBuilder(XMaterial.PAPER).setDisplayName("&aCreate a new sound").setLore("&7Make sure you save")
                .addLore("&7when you're done."));

        if (crates.getSettings().getSound().getSounds().containsKey(tier)) {
            int i = 2;
            for (SoundData sd : crates.getSettings().getSound().getSounds().get(tier)) {
                if (i % 9 == 7) {
                    i += 4;
                }

                ib.setItem(i, new ItemBuilder(XMaterial.NOTE_BLOCK).setDisplayName("&a" + sd.getSound().name())
                        .setLore("&7Pitch: &f" + sd.getPitch()).addLore("&7Volume: &f" + sd.getVolume()));
                slots.put(i, sd);
                i++;
            }
        }

        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot) {
        if (slot == 9) {
            up();
        } else if (slot == 8) {
            new InputMenu(getCc(), getP(), "sound type", "null",
                    "Click for a list of sounds -> https://www.spigotmc.org/wiki/cc-sounds-list/", String.class, this, true);
        } else if (getIb().getInv().getItem(slot) != null && XMaterial.NOTE_BLOCK.isSimilar(getIb().getInv().getItem(slot))) {
            //SoundData sd = crates.getCs().getCs().getSoundFromName(tier, Sound.valueOf(ChatUtils.removeColor(getIb().getInv().getItem(slot).getItemMeta().getDisplayName()).toUpperCase()));
            SoundData sd = slots.get(slot);
            new IGCCrateSound(getCc(), getP(), this, crates, sd, tier).open();
        }
    }

    @Override
    public boolean handleInput(String value, String input) {
        if (value.equalsIgnoreCase("sound type")) {
            Optional<XSound> optional = XSound.matchXSound(input);
            if (optional.isPresent()) {
                sd = new SoundData(optional.get());
                new InputMenu(getCc(), getP(), "sound pitch", "null", "The pitch the sound will play at.", Integer.class,
                        this);
            } else {
                ChatUtils.msgError(getP(), input +
                        " is not a valid sound. Click for a list of sounds -> https://www.spigotmc.org/wiki/cc-sounds-list/");
            }
        } else if (value.equalsIgnoreCase("sound pitch")) {
            if (Utils.isInt(input)) {
                sd.setPitch(Integer.parseInt(input));
                new InputMenu(getCc(), getP(), "sound volume", "null", "The volume the sound will play at.", Integer.class,
                        this);
            } else {
                ChatUtils.msgError(getP(), input + " is not a valid integer (number).");
            }
        } else if (value.equalsIgnoreCase("sound volume")) {
            if (Utils.isInt(input)) {
                sd.setVolume(Integer.parseInt(input));

                cs.getSound().addSound(tier, sd);

                getP().closeInventory();
                new IGCCrateSound(getCc(), getP(), this, crates, sd, tier).open();
            } else {
                ChatUtils.msgError(getP(), input + " is not a valid integer (number).");
            }
        }
        return false;
    }
}
