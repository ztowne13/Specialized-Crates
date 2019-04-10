package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.animations.holo.HoloAnimType;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.holograms.*;
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class CHolograms extends CSetting
{
    DynamicHologram dh = null;
    ArrayList<String> lines = new ArrayList<>();
    int lineCount = 0;

    String rewardHologram = "";
    int rewardHoloDuration = 60;
    double rewardHoloYOffset = 0;

    HoloAnimType hat = HoloAnimType.NONE;
    int speed = 20;
    ArrayList<String> prefixes = new ArrayList<String>();
    double hologramOffset = -123.123;

    public CHolograms(Crate crates)
    {
        super(crates, crates.getCc());
    }

    public void loadFor(CrateSettingsBuilder csb, CrateState cs)
    {
        FileConfiguration fc = getCrates().getCs().getFc();

        if (csb.hasV("hologram.reward-hologram"))
        {
            rewardHologram = fc.getString("hologram.reward-hologram");
            StatusLoggerEvent.HOLOGRAM_REWARD_HOLOGRAM.log(getCrates(), new String[]{rewardHologram});
        }

        if (csb.hasV("hologram.reward-hologram-duration"))
        {
            try
            {
                rewardHoloDuration = Integer.parseInt(fc.getString("hologram.reward-hologram-duration"));
                StatusLoggerEvent.HOLOGRAM_REWARD_HOLOGRAM_DURATION_SUCCESS.log(getCrates());
            }
            catch (Exception exc)
            {
                StatusLoggerEvent.HOLOGRAM_REWARD_HOLOGRAM_DURATION_INVALID.log(getCrates());
            }
        }

        if (csb.hasV("hologram.reward-hologram-yoffset"))
        {
            try
            {
                rewardHoloYOffset = Double.parseDouble(fc.getString("hologram.reward-hologram-yoffset"));
                StatusLoggerEvent.HOLOGRAM_REWARD_HOLOGRAM_YOFFSET_SUCCESS.log(getCrates());
            }
            catch (Exception exc)
            {
                StatusLoggerEvent.HOLOGRAM_REWARD_HOLOGRAM_YOFFSET_INVALID.log(getCrates());
            }
        }

        if (csb.hasV("hologram.lines"))
        {
            for (String s : fc.getStringList("hologram.lines"))
            {
                setLineCount(getLineCount() + 1);
                addLine(s);
            }
        }

        if (csb.hasV("hologram.animation"))
        {
            try
            {
                HoloAnimType hat = HoloAnimType.valueOf(fc.getString("hologram.animation.type").toUpperCase());
                setHat(hat);
            }
            catch (Exception exc)
            {
                if (!csb.hasV("hologram.animation.type"))
                {
                    StatusLoggerEvent.HOLOGRAM_ANIMATION_TYPE_FAILURE_NONEXISTENT.log(getCrates());
                }
                else
                {
                    StatusLoggerEvent.HOLOGRAM_ANIMATION_TYPE_FAILURE_INVALID
                            .log(getCrates(), new String[]{fc.getString("hologram.animation.type")});
                }
                return;
            }

            try
            {
                setSpeed(fc.getInt("hologram.animation.speed"));
            }
            catch (Exception exc)
            {
                setSpeed(10);
                if (!csb.hasV("hologram.animation.speed"))
                {
                    StatusLoggerEvent.HOLOGRAM_ANIMATION_SPEED_FAILURE_NONEXISTENT.log(getCrates());
                }
                else
                {
                    StatusLoggerEvent.HOLOGRAM_ANIMATION_SPEED_FAILURE_INVALID
                            .log(getCrates(), new String[]{fc.getString("hologram.animation.speed")});
                }
                return;
            }

            try
            {
                for (String s : fc.getStringList("hologram.animation.prefixes"))
                {
                    getPrefixes().add(s);
                }
            }
            catch (Exception exc)
            {
                setHat(null);
                StatusLoggerEvent.HOLOGRAM_ANIMATION_PREFIXES_DISABLED.log(getCrates());
                if (!csb.hasV("hologram.animation.prefixes"))
                {
                    StatusLoggerEvent.HOLOGRAM_ANIMATION_PREFIXES_NONEXISTENT.log(getCrates());
                }
                else
                {
                    StatusLoggerEvent.HOLOGRAM_ANIMATION_PREFIXES_MISFORMATTED.log(getCrates());
                }

                return;
            }
        }
    }

    public void saveToFile()
    {
        for (int i = 0; i < lines.size(); i++)
        {
            try
            {
                lines.set(i, ChatUtils.fromChatColor(lines.get(i)));
            }
            catch (Exception exc)
            {

            }
        }
        getFu().get().set("hologram.lines", lines);

        for (int i = 0; i < prefixes.size(); i++)
        {
            try
            {
                prefixes.set(i, ChatUtils.fromChatColor(prefixes.get(i)));
            }
            catch (Exception exc)
            {

            }
        }
        getFu().get().set("hologram.animation.prefixes", prefixes);
        getFu().get().set("hologram.animation.type", getHat().name());
        getFu().get().set("hologram.animation.speed", getSpeed());

        getFu().get().set("hologram.reward-hologram", getRewardHologram());
        getFu().get().set("hologram.reward-hologram-duration", getRewardHoloDuration());
        getFu().get().set("hologram.reward-hologram-yoffset", getRewardHoloYOffset());
    }

    public double getHologramOffset()
    {
        if (hologramOffset == -123.123)
        {
            try
            {
                hologramOffset = Double.valueOf(getCc().getSettings().getConfigValues().get("hologram-offset").toString());
            }
            catch (Exception exc)
            {
                ChatUtils.log("hologram-offset in the config.yml file is not a valid double (number) value.");
                hologramOffset = 0;
            }
        }
        return hologramOffset + getCrates().getCs().getHologramOffset();
    }

    public CHolograms clone()
    {
        CHolograms ch = new CHolograms(getCrates());
        ch.setLineCount(getLineCount());
        ch.setLines(getLines());
        ch.setHat(getHat());
        ch.setColors(getPrefixes());
        ch.setSpeed(getSpeed());
        return ch;
    }

    public void addLine(String line)
    {
        try
        {
            lines.add(line);
        }
        catch (Exception exc)
        {
            StatusLoggerEvent.HOLOGRAM_ADDLINE_FAIL_TOMANY
                    .log(getCrates(), new String[]{line, (getLines().size() + 1) + ""});
        }
    }

    public void removeLine(int lineNum)
    {
        lines.remove(lineNum - 1);
    }

    public DynamicHologram createHologram(PlacedCrate cm, Location l, DynamicHologram h)
    {
        h.create(l);

        if (!lines.isEmpty())
        {

            for (String s : getLines())
            {
                try
                {
                    s = ChatUtils.toChatColor(s);
                    h.addLine(s);
                }
                catch (Exception exc)
                {
                    break;
                }
            }

            h.teleport(l);
        }
        return h;
    }

    public DynamicHologram createHologram(PlacedCrate cm, Location l)
    {
        return createHologram(cm, l, getLoadedInstance(cm));
    }

    public int getLinesAmount()
    {
        for (int i = lines.size() - 1; i >= 0; i--)
        {

        }
        return 0;
    }

    public DynamicHologram getLoadedInstance(PlacedCrate cm)
    {
        if (Utils.isPLInstalled("HolographicDisplays"))
        {
            Utils.addToInfoLog(getCc(), "Hologram Plugin", "HolographicDisplays");
            return new HolographicDisplaysHologram(getCc(), cm);
        }
        else if (Utils.isPLInstalled("IndividualHolograms"))
        {
            Utils.addToInfoLog(getCc(), "Hologram Plugin", "Individual Holograms");
            return new IndividualHologramsHologram(getCc(), cm);
        }
        else if (Utils.isPLInstalled("Holograms"))
        {
            Utils.addToInfoLog(getCc(), "Hologram Plugin", "Holograms");
            return new SaintXHologram(getCc(), cm);
        }
        else if (Utils.isPLInstalled("CMI"))
        {
            Utils.addToInfoLog(getCc(), "Hologram Plugin", "CMI");
            return new CMIHologram(getCc(), cm);
        }


        Utils.addToInfoLog(getCc(), "Hologram Plugin", "None");
        return new NoHologram(getCc(), cm);
    }

    public static void deleteAll()
    {
        for (PlacedCrate cm : PlacedCrate.getPlacedCrates().values())
        {
            if (!(cm.getCholo() == null))
            {
                cm.getCholo().getDh().delete();
            }
        }

    }

    public ArrayList<String> getLines()
    {
        return lines;
    }

    public void setLines(ArrayList<String> lines)
    {
        this.lines = lines;
    }

    public int getRewardHoloDuration()
    {
        return rewardHoloDuration;
    }

    public void setRewardHoloDuration(int rewardHoloDuration)
    {
        this.rewardHoloDuration = rewardHoloDuration;
    }

    public double getRewardHoloYOffset()
    {
        return rewardHoloYOffset;
    }

    public void setRewardHoloYOffset(double rewardHoloYOffset)
    {
        this.rewardHoloYOffset = rewardHoloYOffset;
    }

    public HoloAnimType getHat()
    {
        return hat;
    }

    public void setHat(HoloAnimType hat)
    {
        this.hat = hat;
    }

    public int getSpeed()
    {
        return speed;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

    public ArrayList<String> getPrefixes()
    {
        return prefixes;
    }

    public void setColors(ArrayList<String> prefixes)
    {
        this.prefixes = prefixes;
    }

    public DynamicHologram getDh()
    {
        return dh;
    }

    public void setDh(DynamicHologram dh)
    {
        this.dh = dh;
    }

    public int getLineCount()
    {
        return lineCount;
    }

    public void setLineCount(int lineCount)
    {
        this.lineCount = lineCount;
    }

    public String getRewardHologram()
    {
        return rewardHologram;
    }

    public void setRewardHologram(String rewardHologram)
    {
        this.rewardHologram = rewardHologram;
    }
}
