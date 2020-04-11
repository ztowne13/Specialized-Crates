package me.ztowne13.customcrates.interfaces.igc.crates;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCListSelector;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by ztowne13 on 4/2/16.
 */
public class IGCCrateAnimation extends IGCMenuCrate
{

    public IGCCrateAnimation(SpecializedCrates cc, Player p, IGCMenu lastMenu, Crate crates)
    {
        super(cc, p, lastMenu, "&7&l> &6&lAnimation Settings", crates);
    }

    @Override
    public void open()
    {

        InventoryBuilder ib = createDefault(27);

        ib.setItem(9, IGCDefaultItems.EXIT_BUTTON.getIb());

        ib.setItem(13,
                new ItemBuilder(DynamicMaterial.BIRCH_FENCE_GATE, 1).setName("&aSet auto-close").setLore("&7Current value: ")
                        .addLore("&7" + cs.isAutoClose()).addLore("").addAutomaticLore("&f", 30,
                        "If the crate is in an inventory, should it automatically close when it is done?"));

        ib.setItem(11, new ItemBuilder(Material.ITEM_FRAME, 1, 0).setName("&aSet the crate animation")
                .setLore("&7Current Value: ").addLore("&7" + cs.getCrateType().name()).addLore("")
                .addAutomaticLore("&f", 30, "This is the animation that will play when the crate is opened."));

        ItemBuilder skipAnimation = new ItemBuilder(DynamicMaterial.GUNPOWDER);
        skipAnimation.setDisplayName("&aSet allow-skip-animation");
        skipAnimation.addLore("&7Current Value:").addLore("&7" + cs.isCanFastTrack());
        skipAnimation.addLore("").addAutomaticLore("&f", 30,
                "Whether or not the player can end the animation at any point while opening the crate.");

        ib.setItem(15, skipAnimation);


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
            case 13:
                cs.setAutoClose(!cs.isAutoClose());
                open();
                break;
            case 11:
                new IGCListSelector(getCc(), getP(), this, "Animation Type", Arrays.asList(CrateAnimationType.values()),
                        DynamicMaterial.PAPER, 1, null).open();
                break;
            case 15:
                cs.setCanFastTrack(!cs.isCanFastTrack());
                open();
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        if (value.equalsIgnoreCase("Animation Type"))
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
        return false;
    }
}
