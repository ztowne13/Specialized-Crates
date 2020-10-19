package me.ztowne13.customcrates.interfaces.igc.crates;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.particles.ParticleData;
import me.ztowne13.customcrates.crates.options.particles.ParticleEffect;
import me.ztowne13.customcrates.crates.options.particles.effects.OffsetTiltedRingsPA;
import me.ztowne13.customcrates.crates.options.particles.effects.PEAnimationType;
import me.ztowne13.customcrates.crates.options.particles.effects.TiltedRingsPA;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCListSelector;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import me.ztowne13.customcrates.utils.VersionUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Created by ztowne13 on 4/2/16.
 */
public class IGCCrateParticle extends IGCTierMenu {
    ParticleData pd;

    String[] colorableParticles = new String[]{
            "SPELL_MOB",
            "SPELL_MOB_AMBIENT",
            "NOTE",
            "REDSTONE"
    };

    public IGCCrateParticle(SpecializedCrates cc, Player p, IGCMenu lastMenu, Crate crates, ParticleData pd, String tier) {
        super(cc, p, lastMenu, "&7&l> &6&lParticles", crates, tier);
        this.pd = pd;
    }

    @Override
    public void openMenu() {
        InventoryBuilder ib = createDefault(pd.getParticleAnimationEffect() == null ? 36 : 27);

        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(8, new ItemBuilder(XMaterial.RED_CARPET).setDisplayName("&cDelete this particle")
                .setLore("&7NOTE: This action cannot").addLore("&7be undone!"));

        ib.setItem(10, new ItemBuilder(XMaterial.BEACON).setDisplayName("&aParticle Animation")
                .addLore("&7Current value: ")
                .addLore("&7" + (pd.getParticleAnimationEffect() == null ? "none" :
                        PEAnimationType.getFromParticleAnimationEffect(pd.getParticleAnimationEffect()).name())).addLore("")
                .addAutomaticLore("&f", 30,
                        "&fThe animation type of the particle. Set this value to 'none' to have no animation."));
        ib.setItem(11, new ItemBuilder(XMaterial.NETHER_STAR).setDisplayName("&aParticle Type").setLore("&7Current value: ")
                .addLore("&7" + pd.getParticleName()).addLore("")
                .addAutomaticLore("&f", 30, "The particle type that this particle will display."));

        ItemBuilder colorEditor = new ItemBuilder(XMaterial.LIME_DYE, 1);
        colorEditor.setDisplayName("&aEdit the color of the particle");

        for (String s : colorableParticles)
            if (s.equalsIgnoreCase(pd.getParticleName()))
                ib.setItem(20, colorEditor);

        if (pd.getParticleAnimationEffect() == null) {
            ib.setItem(12,
                    new ItemBuilder(XMaterial.BLAZE_ROD).setDisplayName("&aX Range")
                            .addLore("&7Current value: ")
                            .addLore("&7" + pd.getRangeX()).addLore("")
                            .addAutomaticLore("&f", 30, "Range particles will spawn in the X direction."));
            ib.setItem(13,
                    new ItemBuilder(XMaterial.BLAZE_ROD).setDisplayName("&aY Range")
                            .addLore("&7Current value: ")
                            .addLore("&7" + pd.getRangeY()).addLore("")
                            .addAutomaticLore("&f", 30, "Range particles will spawn in the Y direction."));
            ib.setItem(14,
                    new ItemBuilder(XMaterial.BLAZE_ROD).setDisplayName("&aZ Range")
                            .addLore("&7Current value: ")
                            .addLore("&7" + pd.getRangeZ()).addLore("")
                            .addAutomaticLore("&f", 30, "Range particles will spawn in the Z direction."));
            ib.setItem(15, new ItemBuilder(XMaterial.SUGAR).setDisplayName("&aParticle speed").setLore("&7Current value: ")
                    .addLore("&7" + pd.getSpeed()).addLore("").addAutomaticLore("&f", 30,
                            "The speed the particles will move around at. For some particles, this changes their color."));
            ib.setItem(16, new ItemBuilder(XMaterial.BUCKET).setDisplayName("&aParticle amount").setLore("&7Current value: ")
                    .addLore("&7" + pd.getAmount()).addLore("")
                    .addAutomaticLore("&f", 30, "The amount of particles that will be displayed every tick."));

            ItemBuilder xCenter = new ItemBuilder(XMaterial.BLAZE_POWDER);
            xCenter.setDisplayName("&aCenter Offset X");
            xCenter.addLore("&7Current Value:");
            xCenter.addLore("&7" + pd.getCenterX());
            xCenter.addLore("");
            xCenter.addAutomaticLore("&f", 30,
                    "The x offset for the middle of where all the particles will spawn. This is useful if the particles spawn in a compact area and that area is off-centered.");

            ItemBuilder yCenter = new ItemBuilder(XMaterial.BLAZE_POWDER);
            yCenter.setDisplayName("&aCenter Offset Y");
            yCenter.addLore("&7Current Value:");
            yCenter.addLore("&7" + pd.getCenterY());
            yCenter.addLore("");
            yCenter.addAutomaticLore("&f", 30,
                    "The y offset for the middle of where all the particles will spawn. This is useful if the particles spawn in a compact area and that area is off-centered.");

            ItemBuilder zCenter = new ItemBuilder(XMaterial.BLAZE_POWDER);
            zCenter.setDisplayName("&aCenter Offset Z");
            zCenter.addLore("&7Current Value:");
            zCenter.addLore("&7" + pd.getCenterZ());
            zCenter.addLore("");
            zCenter.addAutomaticLore("&f", 30,
                    "The z offset for the middle of where all the particles will spawn. This is useful if the particles spawn in a compact area and that area is off-centered.");


            getIb().setItem(21, xCenter);
            getIb().setItem(22, yCenter);
            getIb().setItem(23, zCenter);

        } else {
            ib.setItem(12,
                    new ItemBuilder(XMaterial.BLAZE_ROD).setDisplayName("&aRadius")
                            .addAutomaticLore("&f", 30,
                                    "What radius (how 'large') the effect will have. Ideally should be around 1-2.")
                            .addLore("")
                            .addLore("&7Current value: ")
                            .addLore("&7" + pd.getRangeX()));
            ib.setItem(13,
                    new ItemBuilder(XMaterial.BLAZE_ROD).setDisplayName("&aY Offset")
                            .addAutomaticLore("&f", 30,
                                    "The Y-Offset to center the animation on. Negative numbers make it go down, positive numbers make it go up. Decimals are OK.")
                            .addLore("")
                            .addLore("&7Current value: ")
                            .addLore("&7" + pd.getRangeY()));

            if (pd.getParticleAnimationEffect() instanceof TiltedRingsPA ||
                    pd.getParticleAnimationEffect() instanceof OffsetTiltedRingsPA) {
                ib.setItem(14,
                        new ItemBuilder(XMaterial.BLAZE_ROD).setDisplayName("&aRotation")
                                .addAutomaticLore("&f", 30,
                                        "The rotation (in degrees) of the animation. This helps if the animation is playing sideways. Setting it to 90 will rotate it a quarter turn.")
                                .addLore("").addLore("&7Current value: ")
                                .addLore("&7" + pd.getRangeZ()));
            } else {
                ib.setItem(14,
                        new ItemBuilder(XMaterial.BLAZE_ROD).setDisplayName("&aHeight")
                                .addAutomaticLore("&f", 30,
                                        "How high up and down the animation will go. A value of 1 to 3 will keep it around the crate.")
                                .addLore("").addLore("&7Current value: ")
                                .addLore("&7" + pd.getRangeZ()));
            }

            ib.setItem(15, new ItemBuilder(XMaterial.SUGAR).setDisplayName("&aAnimation Speed")
                    .addAutomaticLore("&f", 30, "The speed of the animation. A value of 20 is a fairly reasonable speed.")
                    .addLore("").addLore("&7Current value: ").addLore("&7" + pd.getSpeed()));
            ib.setItem(16, new ItemBuilder(XMaterial.BUCKET).setDisplayName("&aParticle amount")
                    .addAutomaticLore("&f", 30,
                            "The amount of particles per animation tick. A value of 3 will create a full/filled looking animation.")
                    .addLore("").addLore("&7Current value: ")
                    .addLore("&7" + pd.getAmount()));
        }

        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot) {
        switch (slot) {
            case 8:
                cs.getParticle().deleteParticle(tier, pd);
                up();
                break;
            case 0:
                up();
                break;
            case 10:
                new IGCListSelector(getCc(), getP(), this, "Particle Animation", Arrays.asList(PEAnimationType.values()),
                        XMaterial.BEACON, 1, null).open();
                break;
            case 11:
                if (VersionUtils.Version.v1_9.isServerVersionOrEarlier())
                    new IGCListSelector(getCc(), getP(), this, "Particle Type", Arrays.asList(ParticleEffect.values()),
                            XMaterial.NETHER_STAR, 1, null).open();
                else
                    new IGCListSelector(getCc(), getP(), this, "Particle Type", Arrays.asList(org.bukkit.Particle.values()),
                            XMaterial.NETHER_STAR, 1, null).open();
                break;
            case 12:
                new InputMenu(getCc(), getP(), "x range", pd.getRangeX() + "",
                        "Distance particles will spawn in the x direction relative to the crate.", Double.class, this);
                break;
            case 13:
                new InputMenu(getCc(), getP(), "y range", pd.getRangeY() + "",
                        "Distance particles will spawn in the y direction relative to the crate.", Double.class, this);
                break;
            case 14:
                new InputMenu(getCc(), getP(), "z range", pd.getRangeZ() + "",
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
            case 20:
                for (String s : colorableParticles)
                    if (s.equalsIgnoreCase(pd.getParticleName()))
                        new IGCCrateParticleColor(getCc(), getP(), this, getCrates(), pd, tier).open();
                break;
            case 21:
                if (pd.getParticleAnimationEffect() == null)
                    new InputMenu(getCc(), getP(), "x center offset", pd.getCenterX() + "",
                            "Adjust the center of where the particles will spawn in the x direction.", Double.class, this);
                break;
            case 22:
                if (pd.getParticleAnimationEffect() == null)
                    new InputMenu(getCc(), getP(), "y center offset", pd.getCenterY() + "",
                            "Adjust the center of where the particles will spawn in the y direction.", Double.class, this);
                break;
            case 23:
                if (pd.getParticleAnimationEffect() == null)
                    new InputMenu(getCc(), getP(), "z center offset", pd.getCenterZ() + "",
                            "Adjust the center of where the particles will spawn in the z direction.", Double.class, this);
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input) {
        if (value.equalsIgnoreCase("amount")) {
            if (Utils.isInt(input)) {
                pd.setAmount(Integer.parseInt(input));
                ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input + ".");
                return true;
            } else {
                ChatUtils.msgError(getP(), input + " is not a valid integer (number).");
            }
        } else if (value.equalsIgnoreCase("Particle Type")) {
            if (pd.setParticle(input)) {
                ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input + ".");
                return true;
            } else {
                ChatUtils.msgError(getP(),
                        input + " is not valid from the list of particles: " + Arrays.toString(ParticleEffect.values()));
            }
        } else if (value.equalsIgnoreCase("Particle Animation")) {
            try {
                if (input.equalsIgnoreCase("none")) {
                    pd.setParticleAnimationEffect(null);
                    ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input + ".");
                    return true;
                } else {
                    PEAnimationType peAnimationType = PEAnimationType.valueOf(input.toUpperCase());
                    pd.setParticleAnimationEffect(peAnimationType.getAnimationEffectInstance(getCc(), pd));
                    pd.setHasAnimation(true);
                    ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input + ".");
                    return true;
                }
            } catch (Exception exc) {
                ChatUtils.msgError(getP(),
                        input + " is not valid from the list of animations: " + Arrays.toString(PEAnimationType.values()));
                return false;
            }
        } else if (getInputMenu().getType() == Double.class) {
            if (Utils.isDouble(input)) {
                Float parsedIn = Float.valueOf(input);
                if (value.equalsIgnoreCase("x range")) {
                    pd.setRangeX(parsedIn);
                } else if (value.equalsIgnoreCase("y range")) {
                    pd.setRangeY(parsedIn);
                } else if (value.equalsIgnoreCase("z range")) {
                    pd.setRangeZ(parsedIn);
                } else if (value.equalsIgnoreCase("x center offset")) {
                    pd.setCenterX(parsedIn);
                } else if (value.equalsIgnoreCase("y center offset")) {
                    pd.setCenterY(parsedIn);
                } else if (value.equalsIgnoreCase("z center offset")) {
                    pd.setCenterZ(parsedIn);
                } else if (value.equalsIgnoreCase("speed")) {
                    pd.setSpeed(parsedIn);
                }
                ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input + ".");
                return true;
            } else {
                ChatUtils.msgError(getP(), input + " is not a valid double (number).");
            }
        }
        return false;
    }


}
