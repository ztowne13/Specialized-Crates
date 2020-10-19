package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.crates.*;
import me.ztowne13.customcrates.crates.options.holograms.DynamicHologram;
import me.ztowne13.customcrates.crates.options.holograms.animations.HoloAnimType;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class CHologram extends CSetting {
    private DynamicHologram dynamicHologram = null;
    private List<String> lines = new ArrayList<>();
    private int lineCount = 0;

    private String rewardHologram = "";
    private int rewardHoloDuration = 60;
    private double rewardHoloYOffset = 0;

    private HoloAnimType holoAnimType = HoloAnimType.NONE;
    private int speed = 20;
    private List<String> prefixes = new ArrayList<>();
    private double hologramOffset = -123.123;

    private boolean createdByPlayer = true;

    public CHologram(Crate crate) {
        super(crate, crate.getInstance());
    }

    public static void deleteAll() {
        for (PlacedCrate placedCrate : PlacedCrate.getPlacedCrates().values()) {
            if (placedCrate.getHologram() != null) {
                placedCrate.getHologram().getDynamicHologram().delete();
            }
        }

    }

    public void loadFor(CrateSettingsBuilder crateSettingsBuilder, CrateState crateState) {
        FileConfiguration fc = getCrate().getSettings().getFileConfiguration();

        if (crateSettingsBuilder.hasValue("hologram.reward-hologram")) {
            rewardHologram = fc.getString("hologram.reward-hologram");
            StatusLoggerEvent.HOLOGRAM_REWARD_HOLOGRAM.log(getCrate(), rewardHologram);
        }

        if (crateSettingsBuilder.hasValue("hologram.reward-hologram-duration")) {
            try {
                rewardHoloDuration = Integer.parseInt(fc.getString("hologram.reward-hologram-duration"));
                StatusLoggerEvent.HOLOGRAM_REWARD_HOLOGRAM_DURATION_SUCCESS.log(getCrate());
            } catch (Exception exc) {
                StatusLoggerEvent.HOLOGRAM_REWARD_HOLOGRAM_DURATION_INVALID.log(getCrate());
            }
        }

        if (crateSettingsBuilder.hasValue("hologram.reward-hologram-yoffset")) {
            try {
                rewardHoloYOffset = Double.parseDouble(fc.getString("hologram.reward-hologram-yoffset"));
                StatusLoggerEvent.HOLOGRAM_REWARD_HOLOGRAM_YOFFSET_SUCCESS.log(getCrate());
            } catch (Exception exc) {
                StatusLoggerEvent.HOLOGRAM_REWARD_HOLOGRAM_YOFFSET_INVALID.log(getCrate());
            }
        }

        if (crateSettingsBuilder.hasValue("hologram.lines")) {
            for (String s : fc.getStringList("hologram.lines")) {
                setLineCount(getLineCount() + 1);
                addLine(s);
            }
        }

        if (crateSettingsBuilder.hasValue("hologram.animation")) {
            try {
                HoloAnimType hat = HoloAnimType.valueOf(fc.getString("hologram.animation.type").toUpperCase());
                setHoloAnimType(hat);
            } catch (Exception exc) {
                if (!crateSettingsBuilder.hasValue("hologram.animation.type")) {
                    StatusLoggerEvent.HOLOGRAM_ANIMATION_TYPE_FAILURE_NONEXISTENT.log(getCrate());
                } else {
                    StatusLoggerEvent.HOLOGRAM_ANIMATION_TYPE_FAILURE_INVALID
                            .log(getCrate(), fc.getString("hologram.animation.type"));
                }
                return;
            }

            try {
                setSpeed(fc.getInt("hologram.animation.speed"));
            } catch (Exception exc) {
                setSpeed(10);
                if (!crateSettingsBuilder.hasValue("hologram.animation.speed")) {
                    StatusLoggerEvent.HOLOGRAM_ANIMATION_SPEED_FAILURE_NONEXISTENT.log(getCrate());
                } else {
                    StatusLoggerEvent.HOLOGRAM_ANIMATION_SPEED_FAILURE_INVALID
                            .log(getCrate(), fc.getString("hologram.animation.speed"));
                }
                return;
            }

            try {
                for (String s : fc.getStringList("hologram.animation.prefixes")) {
                    getPrefixes().add(s);
                }
            } catch (Exception exc) {
                setHoloAnimType(null);
                StatusLoggerEvent.HOLOGRAM_ANIMATION_PREFIXES_DISABLED.log(getCrate());
                if (!crateSettingsBuilder.hasValue("hologram.animation.prefixes")) {
                    StatusLoggerEvent.HOLOGRAM_ANIMATION_PREFIXES_NONEXISTENT.log(getCrate());
                } else {
                    StatusLoggerEvent.HOLOGRAM_ANIMATION_PREFIXES_MISFORMATTED.log(getCrate());
                }
            }
        }
    }

    public void saveToFile() {
        for (int i = 0; i < lines.size(); i++) {
            try {
                lines.set(i, ChatUtils.fromChatColor(lines.get(i)));
            } catch (Exception exc) {
                // IGNORED
            }
        }
        getFileHandler().get().set("hologram.lines", lines);

        for (int i = 0; i < prefixes.size(); i++) {
            try {
                prefixes.set(i, ChatUtils.fromChatColor(prefixes.get(i)));
            } catch (Exception exc) {
                // IGNORED
            }
        }
        getFileHandler().get().set("hologram.animation.prefixes", prefixes);
        getFileHandler().get().set("hologram.animation.type", getHoloAnimType().name());
        getFileHandler().get().set("hologram.animation.speed", getSpeed());

        getFileHandler().get().set("hologram.reward-hologram", getRewardHologram());
        getFileHandler().get().set("hologram.reward-hologram-duration", getRewardHoloDuration());
        getFileHandler().get().set("hologram.reward-hologram-yoffset", getRewardHoloYOffset());
    }

    public double getHologramOffset() {
        if (hologramOffset == -123.123) {
            try {
                hologramOffset = Double.parseDouble(instance.getSettings().getConfigValues().get("hologram-offset").toString());
            } catch (Exception exc) {
                ChatUtils.log("hologram-offset in the config.yml file is not a valid double (number) value.");
                hologramOffset = 0;
            }
        }
        return hologramOffset + getCrate().getSettings().getHologramOffset();
    }

    public CHologram clone() {
        CHologram ch = new CHologram(getCrate());
        ch.setLineCount(getLineCount());
        ch.setLines(getLines());
        ch.setHoloAnimType(getHoloAnimType());
        ch.setColors(getPrefixes());
        ch.setSpeed(getSpeed());
        return ch;
    }

    public void addLine(String line) {
        try {
            lines.add(line);
        } catch (Exception exc) {
            StatusLoggerEvent.HOLOGRAM_ADDLINE_FAIL_TOMANY
                    .log(getCrate(), line, (getLines().size() + 1) + "");
        }
    }

    public void removeLine(int lineNum) {
        lines.remove(lineNum - 1);
    }

    public DynamicHologram createHologram(Location location, DynamicHologram dynamicHologram) {
        dynamicHologram.create(location);

        CrateSettings settings = getCrate().getSettings();
        // This is a dynamic hologram and doesn't need them created when placed
        if (settings.getObtainType().equals(ObtainType.DYNAMIC) && !settings.isRequireKey() && createdByPlayer) {
            return dynamicHologram;
        }

        if (!lines.isEmpty()) {
            for (String s : getLines()) {
                try {
                    s = ChatUtils.toChatColor(s);
                    dynamicHologram.addLine(s);
                } catch (Exception exc) {
                    break;
                }
            }

            dynamicHologram.teleport(location);
        }
        return dynamicHologram;
    }

    public DynamicHologram createHologram(PlacedCrate placedCrate, Location location) {
        return createHologram(location, getLoadedInstance(placedCrate));
    }

    public DynamicHologram getLoadedInstance(PlacedCrate placedCrate) {
        return new DynamicHologram(instance, placedCrate);
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public int getRewardHoloDuration() {
        return rewardHoloDuration;
    }

    public void setRewardHoloDuration(int rewardHoloDuration) {
        this.rewardHoloDuration = rewardHoloDuration;
    }

    public double getRewardHoloYOffset() {
        return rewardHoloYOffset;
    }

    public void setRewardHoloYOffset(double rewardHoloYOffset) {
        this.rewardHoloYOffset = rewardHoloYOffset;
    }

    public HoloAnimType getHoloAnimType() {
        return holoAnimType;
    }

    public void setHoloAnimType(HoloAnimType holoAnimType) {
        this.holoAnimType = holoAnimType;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public List<String> getPrefixes() {
        return prefixes;
    }

    public void setColors(List<String> prefixes) {
        this.prefixes = prefixes;
    }

    public DynamicHologram getDynamicHologram() {
        return dynamicHologram;
    }

    public void setDynamicHologram(DynamicHologram dynamicHologram) {
        this.dynamicHologram = dynamicHologram;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public String getRewardHologram() {
        return rewardHologram;
    }

    public void setRewardHologram(String rewardHologram) {
        this.rewardHologram = rewardHologram;
    }

    public void setCreatedByPlayer(boolean createdByPlayer) {
        this.createdByPlayer = createdByPlayer;
    }
}
