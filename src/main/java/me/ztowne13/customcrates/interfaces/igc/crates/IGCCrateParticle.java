package me.ztowne13.customcrates.interfaces.igc.crates;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.particles.ParticleData;
import me.ztowne13.customcrates.crates.options.particles.ParticleEffect;
import me.ztowne13.customcrates.crates.options.particles.effects.OffsetTiltedRingsPA;
import me.ztowne13.customcrates.crates.options.particles.effects.PEAnimationType;
import me.ztowne13.customcrates.crates.options.particles.effects.TiltedRingsPA;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by ztowne13 on 4/2/16.
 */
public class IGCCrateParticle extends IGCTierMenu
{
    ParticleData pd;

    public IGCCrateParticle(CustomCrates cc, Player p, IGCMenu lastMenu, Crate crates, ParticleData pd, String tier)
    {
        super(cc, p, lastMenu, "&7&l> &6&lParticles", crates, tier);
        this.pd = pd;
    }

    @Override
    public void open()
    {
        getP().closeInventory();
        putInMenu();

        InventoryBuilder ib = createDefault(27);

        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(8, new ItemBuilder(DynamicMaterial.RED_CARPET, 1).setName("&cDelete this particle")
                .setLore("&7NOTE: This action cannot").addLore("&7be undone!"));

        ib.setItem(10, new ItemBuilder(Material.BEACON, 1, 0).setName("&aParticle Animation")
                .addLore("&7Current value: ")
                .addLore("&7" + (pd.getParticleAnimationEffect() == null ? "none" :
                        PEAnimationType.getFromParticleAnimationEffect(pd.getParticleAnimationEffect()).name())).addLore("")
                .addAutomaticLore("&f", 30,
                        "&fThe animation type of the particle. Set this value to 'none' to have no animation."));
        ib.setItem(11, new ItemBuilder(Material.NETHER_STAR, 1, 0).setName("&aParticle Type").setLore("&7Current value: ")
                .addLore("&7" + pd.getParticleName()).addLore("")
                .addAutomaticLore("&f", 30, "The particle type that this particle will display."));

        if (pd.getParticleAnimationEffect() == null)
        {
            ib.setItem(12,
                    new ItemBuilder(Material.BLAZE_ROD, 1, 0).setName("&aX Offset")
                            .addLore("&7Current value: ")
                            .addLore("&7" + pd.getOffX()).addLore("")
                            .addAutomaticLore("&f", 30, "Range particles will spawn in the X direction."));
            ib.setItem(13,
                    new ItemBuilder(Material.BLAZE_ROD, 1, 0).setName("&aY Offset")
                            .addLore("&7Current value: ")
                            .addLore("&7" + pd.getOffY()).addLore("")
                            .addAutomaticLore("&f", 30, "Range particles will spawn in the Y direction."));
            ib.setItem(14,
                    new ItemBuilder(Material.BLAZE_ROD, 1, 0).setName("&aZ Offset")
                            .addLore("&7Current value: ")
                            .addLore("&7" + pd.getOffZ()).addLore("")
                            .addAutomaticLore("&f", 30, "Range particles will spawn in the Z direction."));
            ib.setItem(15, new ItemBuilder(Material.SUGAR, 1, 0).setName("&aParticle speed").setLore("&7Current value: ")
                    .addLore("&7" + pd.getSpeed()).addLore("").addAutomaticLore("&f", 30,
                            "The speed the particles will move around at. For some particles, this changes their color."));
            ib.setItem(16, new ItemBuilder(Material.BUCKET, 1, 0).setName("&aParticle amount").setLore("&7Current value: ")
                    .addLore("&7" + pd.getAmount()).addLore("")
                    .addAutomaticLore("&f", 30, "The amount of particles that will be displayed every tick."));
        }
        else
        {
            ib.setItem(12,
                    new ItemBuilder(Material.BLAZE_ROD, 1, 0).setName("&aRadius")
                            .addAutomaticLore("&f", 30,
                                    "What radius (how 'large') the effect will have. Ideally should be around 1-2.")
                            .addLore("")
                            .addLore("&7Current value: ")
                            .addLore("&7" + pd.getOffX()));
            ib.setItem(13,
                    new ItemBuilder(Material.BLAZE_ROD, 1, 0).setName("&aY Offset")
                            .addAutomaticLore("&f", 30,
                                    "The Y-Offset to center the animation on. Negative numbers make it go down, positive numbers make it go up. Decimals are OK.")
                            .addLore("")
                            .addLore("&7Current value: ")
                            .addLore("&7" + pd.getOffY()));

            if (pd.getParticleAnimationEffect() instanceof TiltedRingsPA ||
                    pd.getParticleAnimationEffect() instanceof OffsetTiltedRingsPA)
            {
                ib.setItem(14,
                        new ItemBuilder(Material.BLAZE_ROD, 1, 0).setName("&aRotation")
                                .addAutomaticLore("&f", 30,
                                        "The rotation (in degrees) of the animation. This helps if the animation is playing sideways. Setting it to 90 will rotate it a quarter turn.")
                                .addLore("").addLore("&7Current value: ")
                                .addLore("&7" + pd.getOffZ()));
            }
            else
            {
                ib.setItem(14,
                        new ItemBuilder(Material.BLAZE_ROD, 1, 0).setName("&aHeight")
                                .addAutomaticLore("&f", 30,
                                        "How high up and down the animation will go. A value of 1 to 3 will keep it around the crate.")
                                .addLore("").addLore("&7Current value: ")
                                .addLore("&7" + pd.getOffZ()));
            }

            ib.setItem(15, new ItemBuilder(Material.SUGAR, 1, 0).setName("&aAnimation Speed")
                    .addAutomaticLore("&f", 30, "The speed of the animation. A value of 20 is a fairly reasonable speed.")
                    .addLore("").addLore("&7Current value: ").addLore("&7" + pd.getSpeed()));
            ib.setItem(16, new ItemBuilder(Material.BUCKET, 1, 0).setName("&aParticle amount")
                    .addAutomaticLore("&f", 30,
                            "The amount of particles per animation tick. A value of 3 will create a full/filled looking animation.")
                    .addLore("").addLore("&7Current value: ")
                    .addLore("&7" + pd.getAmount()));
        }

        ib.open();
    }

    @Override
    public void manageClick(int slot)
    {
        switch (slot)
        {
            case 8:
                cs.getCp().getParticles().get(tier).remove(pd);
                up();
                break;
            case 0:
                up();
                break;
            case 10:
                new InputMenu(getCc(), getP(), "particle animation", pd.getParticleName(),
                        "Type 'NONE' to remove animations\nAvaialable animations: " +
                                Arrays.toString(PEAnimationType.values()), String.class, this);
                break;
            case 11:
                new InputMenu(getCc(), getP(), "particle type", pd.getParticleName(),
                        "Avaialable particles: " + Arrays.toString(ParticleEffect.values()), String.class, this);
                break;
            case 12:
                new InputMenu(getCc(), getP(), "x offset", pd.getOffX() + "",
                        "Distance particles will spawn in the x direction relative to the crate.", Double.class, this);
                break;
            case 13:
                new InputMenu(getCc(), getP(), "y offset", pd.getOffY() + "",
                        "Distance particles will spawn in the y direction relative to the crate.", Double.class, this);
                break;
            case 14:
                new InputMenu(getCc(), getP(), "z offset", pd.getOffZ() + "",
                        "Distance particles will spawn in the z direction relative to the crate.", Double.class, this);
                break;
            case 15:
                new InputMenu(getCc(), getP(), "speed", pd.getSpeed() + "",
                        "Changes the speed of most of the particles. For some, like music notes, it changes the color.",
                        Double.class, this);
                break;
            case 16:
                new InputMenu(getCc(), getP(), "amount", pd.getAmount() + "",
                        "How many particles spawn every tick (1/20th of a second).", Integer.class, this);
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        if (getInputMenu().getType() == Double.class)
        {
            if (Utils.isDouble(input))
            {
                Float parsedIn = Float.valueOf(input);
                if (value.equalsIgnoreCase("x offset"))
                {
                    pd.setOffX(parsedIn);
                }
                else if (value.equalsIgnoreCase("y offset"))
                {
                    pd.setOffY(parsedIn);
                }
                else if (value.equalsIgnoreCase("z offset"))
                {
                    pd.setOffZ(parsedIn);
                }
                else if (value.equalsIgnoreCase("speed"))
                {
                    pd.setSpeed(parsedIn);
                }
                ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input + ".");
                return true;
            }
            else
            {
                ChatUtils.msgError(getP(), input + " is not a valid double (number).");
            }
        }
        else if (value.equalsIgnoreCase("amount"))
        {
            if (Utils.isInt(input))
            {
                pd.setAmount(Integer.parseInt(input));
                ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input + ".");
                return true;
            }
            else
            {
                ChatUtils.msgError(getP(), input + " is not a valid integer (number).");
            }
        }
        else if (value.equalsIgnoreCase("particle type"))
        {
            if (pd.setParticle(input))
            {
                ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input + ".");
                return true;
            }
            else
            {
                ChatUtils.msgError(getP(),
                        input + " is not valid from the list of particles: " + Arrays.toString(ParticleEffect.values()));
            }
        }
        else if (value.equalsIgnoreCase("particle animation"))
        {
            try
            {
                if (input.equalsIgnoreCase("none"))
                {
                    pd.setParticleAnimationEffect(null);
                    ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input + ".");
                    return true;
                }
                else
                {
                    PEAnimationType peAnimationType = PEAnimationType.valueOf(input.toUpperCase());
                    pd.setParticleAnimationEffect(peAnimationType.getAnimationEffectInstance(getCc(), pd));
                    pd.setHasAnimation(true);
                    ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input + ".");
                    return true;
                }
            }
            catch (Exception exc)
            {
                ChatUtils.msgError(getP(),
                        input + " is not valid from the list of animations: " + Arrays.toString(PEAnimationType.values()));
                return false;
            }
        }
        return false;
    }


}
