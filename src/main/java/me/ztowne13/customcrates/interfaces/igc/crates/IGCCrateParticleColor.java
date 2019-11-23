package me.ztowne13.customcrates.interfaces.igc.crates;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.particles.ParticleData;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

public class IGCCrateParticleColor extends IGCTierMenu
{
    ParticleData particleData;

    public IGCCrateParticleColor(SpecializedCrates cc, Player p, IGCMenu lastMenu, Crate crates, ParticleData particleData, String tier)
    {
        super(cc, p, lastMenu, "&7&l> &6&lSound", crates, tier);
        this.particleData = particleData;
    }

    @Override
    public void open()
    {

        InventoryBuilder ib = createDefault(27);

        ib.setItem(9, IGCDefaultItems.EXIT_BUTTON.getIb());

        ItemBuilder red = new ItemBuilder(DynamicMaterial.RED_DYE, 1);
        red.setDisplayName("&cEdit the RED of RGB");
        red.addLore("").addLore("&7Current value: ").addLore("&f" + particleData.getColorRed());

        ItemBuilder green = new ItemBuilder(DynamicMaterial.LIME_DYE, 1);
        green.setDisplayName("&aEdit the GREEN of RGB");
        green.addLore("").addLore("&7Current value: ").addLore("&f" + particleData.getColorGreen());

        ItemBuilder blue = new ItemBuilder(DynamicMaterial.LIGHT_BLUE_DYE, 1);
        blue.setDisplayName("&bEdit the BLUE of RGB");
        blue.addLore("").addLore("&7Current value: ").addLore("&f" + particleData.getColorBlue());

        ItemBuilder size = new ItemBuilder(DynamicMaterial.PAPER, 1);
        size.setDisplayName("&aEdit the SIZE of the redstone dust");
        size.addLore("").addLore("&7Current value: ").addLore("&f" + particleData.getSize());

        ItemBuilder colorEnabled = new ItemBuilder(particleData.isColorEnabled() ? DynamicMaterial.LIME_WOOL : DynamicMaterial.RED_WOOL, 1);
        colorEnabled.setDisplayName("&aEdit if color is enabled");
        colorEnabled.addLore("&7Current value: ").addLore("&f" + particleData.isColorEnabled());
        colorEnabled.addLore("").addAutomaticLore("&7", 30, "If this is enabled, the particle will" +
                " be the color specified. If it is disabled, it will simply use the default minecraft particle, whether that's" +
                " rainbow or just black. This will ALWAYS be enabled if using the REDSTONE particle.");

        ib.setItem(11, colorEnabled);
        ib.setItem(12, red);
        ib.setItem(13, green);
        ib.setItem(14, blue);

        if(particleData.getParticleName().equalsIgnoreCase("REDSTONE"))
            ib.setItem(16, size);

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
            case 12:
                new InputMenu(getCc(), getP(), "red", particleData.getColorRed() + "",
                        "The RED of RGB.", Float.class, this);
                break;
            case 13:
                new InputMenu(getCc(), getP(), "green", particleData.getColorGreen() + "",
                        "The GREEN of RGB.", Float.class, this);
                break;
            case 14:
                new InputMenu(getCc(), getP(), "blue", particleData.getColorBlue() + "",
                        "The BLUE of RGB.", Float.class, this);
                break;
            case 11:
                particleData.setColorEnabled(!particleData.isColorEnabled());
                open();
                break;
            case 16:
                if(particleData.getParticleName().equalsIgnoreCase("REDSTONE"))
                    new InputMenu(getCc(), getP(), "size", particleData.getSize() + "",
                        "The size of the redstone.", Float.class, this);
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        int inputAsFloat;
        try
        {
            inputAsFloat = Integer.parseInt(input);
        }
        catch(Exception exc)
        {
            ChatUtils.msgError(getP(), input + " is not a valid number.");
            return false;
        }

        if(value.equalsIgnoreCase("red"))
            particleData.setColorRed(inputAsFloat);
        else if(value.equalsIgnoreCase("green"))
            particleData.setColorGreen(inputAsFloat);
        else if(value.equalsIgnoreCase("blue"))
            particleData.setColorBlue(inputAsFloat);
        else if(value.equalsIgnoreCase("size"))
            particleData.setSize(inputAsFloat);

        ChatUtils.msgSuccess(getP(), "Successfully set " + value + " to " + input + ".");
        return true;
    }
}
