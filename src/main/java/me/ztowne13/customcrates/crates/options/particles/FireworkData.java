package me.ztowne13.customcrates.crates.options.particles;

import me.ztowne13.customcrates.FileSettings;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.UUID;

public class FireworkData
{
    SpecializedCrates cc;
    CrateSettings cs;

    String unLoaded;
    String id;

    Builder effect;
    int power = 1;

    ArrayList<String> colors = new ArrayList<>(), fadeColors = new ArrayList<>();
    boolean trail = false, flicker = false;
    FireworkEffect.Type feType = FireworkEffect.Type.BALL_LARGE;

    public FireworkData(SpecializedCrates cc, CrateSettings cs)
    {
        this.cc = cc;
        this.cs = cs;
        this.id = UUID.randomUUID().toString().substring(0, 8);
    }

    public void loadFromFirework(ItemStack stack)
    {
        FireworkMeta fm = (FireworkMeta) stack.getItemMeta();
        setEffect(FireworkEffect.builder());
        power = fm.getPower();
        for (FireworkEffect ef : fm.getEffects())
        {
            flicker = ef.hasFlicker();
            effect.flicker(flicker);

            trail = ef.hasTrail();
            effect.trail(trail);

            for (Color c : ef.getColors())
            {
                colors.add(c.asRGB() + "");
                effect.withColor(c);
            }

            for (Color c : ef.getFadeColors())
            {
                fadeColors.add(c.asRGB() + "");
                effect.withFade(c);
            }

            if (ef.getType() != null)
            {
                feType = ef.getType();
            }
            effect.with(feType);
        }

        unLoaded = asString();
    }

    public void load(String s)
    {
        String[] args = ChatUtils.stripFromWhitespace(s).split(FileSettings.splitter1);
        setEffect(FireworkEffect.builder());

        unLoaded = s;

        try
        {
            for (String colorUnParsed : args[0].split(FileSettings.splitter2))
            {
                try
                {
                    if (Utils.getColorFromString(colorUnParsed) != null)
                    {
                        Color c = Utils.getColorFromString(colorUnParsed);
                        colors.add(colorUnParsed);
                        getEffect().withColor(c);
                    }
                    else
                    {
                        Color c = Color.fromRGB(Integer.parseInt(colorUnParsed));
                        colors.add(colorUnParsed);
                        getEffect().withColor(c);
                    }
                }
                catch (Exception exc)
                {
                    StatusLoggerEvent.FIREWORK_DATA_INVALIDCOLOR
                            .log(getCs().getCrate(), new String[]{s, colorUnParsed, "color"});
                }
            }

            for (String colorUnParsed : args[1].split(";"))
            {
                try
                {
                    if (Utils.getColorFromString(colorUnParsed) != null)
                    {
                        Color c = Utils.getColorFromString(colorUnParsed);
                        fadeColors.add(colorUnParsed);
                        getEffect().withFade(c);
                    }
                    else
                    {
                        Color c = Color.fromRGB(Integer.parseInt(colorUnParsed));
                        fadeColors.add(colorUnParsed);
                        getEffect().withFade(c);
                    }
                }
                catch (Exception exc)
                {
                    StatusLoggerEvent.FIREWORK_DATA_INVALIDCOLOR
                            .log(getCs().getCrate(), new String[]{s, colorUnParsed, "fade"});
                }
            }

            String cause = args[2] + " is not true / false.";

            try
            {
                Boolean b = Boolean.valueOf(args[2].toLowerCase());
                getEffect().trail(b);
                trail = b;

                cause = "Improperly formatted FLICKER";
                cause = args[3] + " is not true / false.";

                b = Boolean.valueOf(args[3].toLowerCase());
                getEffect().flicker(b);
                flicker = b;

                cause = "Improperly formatted TYPE";
                cause = args[4] + " is not a valid Firework Effect Type.";

                FireworkEffect.Type ft = FireworkEffect.Type.valueOf(args[4].toUpperCase());
                getEffect().with(ft);
                feType = ft;

                cause = "Improperly formatted POWER";
                cause = args[5] + " is not a valid number / power.";
                setPower(Integer.valueOf(args[5]));

                StatusLoggerEvent.FIREWORK_DATA_SUCCESS.log(getCs().getCrate(), new String[]{s});
            }
            catch (Exception exc)
            {
                StatusLoggerEvent.FIREWORK_DATA_PARTIALSUCCESS.log(getCs().getCrate(), new String[]{s, cause});
            }
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.FIREWORK_DATA_FAILURE.log(getCs().getCrate(), new String[]{s});
        }

    }

    public void play(Location l)
    {
        final Firework fw = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
        FireworkMeta fm = fw.getFireworkMeta();
        fw.setCustomName("scf");
        fw.setCustomNameVisible(false);
        fm.addEffect(getEffect().build());
        fm.setPower(getPower());
        fw.setFireworkMeta(fm);

        if (getPower() == 0)
        {
            Bukkit.getScheduler().scheduleSyncDelayedTask(getCc(), new Runnable()
            {
                public void run()
                {
                    fw.detonate();
                }
            }, 2);
        }
    }

    // Formatted color;color, fade color;fade color, trail?, flicker?, effect type, power
    public String asString()
    {
        String serializedFw = "";
        for (String color : getColors())
        {
            serializedFw = color + ";";
        }

        serializedFw = serializedFw.substring(0, serializedFw.length() - 1) + ", ";

        for (String color : getFadeColors())
        {
            serializedFw = color + ";";
        }

        serializedFw = serializedFw.substring(0, serializedFw.length() - 1) + ", " + isTrail() + ", " + isFlicker() + ", " +
                getFeType().name() + ", " + getPower();
        return serializedFw;
    }

    public boolean equals(FireworkData fd)
    {
        return fd.toString().equalsIgnoreCase(toString());
    }

    public String toString()
    {
        return unLoaded;
    }

    public SpecializedCrates getCc()
    {
        return cc;
    }

    public void setCc(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    public CrateSettings getCs()
    {
        return cs;
    }

    public void setCs(CrateSettings cs)
    {
        this.cs = cs;
    }

    public Builder getEffect()
    {
        return effect;
    }

    public void setEffect(Builder effect)
    {
        this.effect = effect;
    }

    public int getPower()
    {
        return power;
    }

    public void setPower(int power)
    {
        this.power = power;
    }

    public boolean isTrail()
    {
        return trail;
    }

    public void setTrail(boolean trail)
    {
        this.trail = trail;
    }

    public boolean isFlicker()
    {
        return flicker;
    }

    public void setFlicker(boolean flicker)
    {
        this.flicker = flicker;
    }

    public FireworkEffect.Type getFeType()
    {
        return feType;
    }

    public void setFeType(FireworkEffect.Type feType)
    {
        this.feType = feType;
    }

    public ArrayList<String> getColors()
    {
        return colors;
    }

    public void setColors(ArrayList<String> colors)
    {
        this.colors = colors;
    }

    public ArrayList<String> getFadeColors()
    {
        return fadeColors;
    }

    public void setFadeColors(ArrayList<String> fadeColors)
    {
        this.fadeColors = fadeColors;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}
