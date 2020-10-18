package me.ztowne13.customcrates.crates.options;

import com.cryptomorin.xseries.XSound;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.options.sounds.SoundData;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class CSound extends CSetting {
    private Map<String, List<SoundData>> sounds = new HashMap<>();

    public CSound(Crate crate) {
        super(crate, crate.getInstance());
    }

    @Override
    public void loadFor(CrateSettingsBuilder crateSettingsBuilder, CrateState crateState) {
        if (crateSettingsBuilder.hasValue("open.sounds")) {
            addSoundsFromList("OPEN", crateSettingsBuilder.getSettings().getFileConfiguration().getStringList("open.sounds"));
        }
        if (crateSettingsBuilder.hasValue("open.crate-tiers")) {
            for (String id : getSettings().getFileConfiguration().getConfigurationSection("open.crate-tiers").getKeys(false)) {
                if (crateSettingsBuilder.hasValue("open.crate-tiers." + id + ".sounds")) {
                    addSoundsFromList(id.toUpperCase(), getSettings().getFileConfiguration().getStringList("open.crate-tiers." + id + ".sounds"));
                }
            }
        }
    }

    public void saveToFile() {
        if (!sounds.isEmpty()) {
            for (Map.Entry<String, List<SoundData>> entry : sounds.entrySet()) {
                String tier = entry.getKey();
                ArrayList<String> listToSet = new ArrayList<>();
                for (SoundData sd : entry.getValue()) {
                    String parsedSoundData = sd.getSound().name() + ", " + sd.getPitch() + ", " + sd.getVolume();
                    listToSet.add(parsedSoundData);
                }

                String path = "open." + (tier.equalsIgnoreCase("OPEN") ? "" : "crate-tiers." + tier) + "sounds";
                getFileHandler().get().set(path, listToSet);
            }
        }
    }

    public void addSoundsFromList(String id, List<String> list) {
        for (String sound : list) {
            try {
                String[] args = sound.replace(" ", "").split(",");

                SoundData sd;

                Optional<XSound> optional = XSound.matchXSound(args[0]);
                if (optional.isPresent()) {
                    sd = new SoundData(optional.get());
                } else {
                    StatusLoggerEvent.SOUND_NONEXISTENT.log(getCrate(), new String[]{sound, args[0]});
                    continue;
                }

                try {
                    int pitch = Integer.parseInt(args[1]);
                    sd.setPitch(pitch);
                } catch (Exception exc) {
                    if (args.length > 0) {
                        StatusLoggerEvent.SOUND_PITCH_INVALID.log(getCrate(), new String[]{sd.getSound().name(), args[1]});
                    } else {
                        StatusLoggerEvent.SOUND_PITCH_NONEXISTENT.log(getCrate(), new String[]{sd.getSound().name()});
                    }
                    continue;
                }

                try {
                    int volume = Integer.parseInt(args[2]);
                    sd.setVolume(volume);
                } catch (Exception exc) {
                    if (args.length > 1) {
                        StatusLoggerEvent.SOUND_VOLUME_INVALID
                                .log(getCrate(), new String[]{sd.getSound().name(), args[2]});
                    } else {
                        StatusLoggerEvent.SOUND_VOLUME_NONEXISTENT.log(getCrate(), new String[]{sd.getSound().name()});
                    }
                    continue;
                }

                addSound(id, sd);
                StatusLoggerEvent.SOUND_ADD_SUCCESS.log(getCrate(), new String[]{sd.getSound().name()});
            } catch (Exception exc) {
                StatusLoggerEvent.SOUND_ADD_IMPROPER_SETUP.log(getCrate(), new String[]{sound});
                exc.printStackTrace();
            }
        }
    }

    public void addSound(String id, SoundData soundData) {
        id = id.toUpperCase();
        List<SoundData> list = getSounds().getOrDefault(id, new ArrayList<>());
        list.add(soundData);
        getSounds().put(id, list);
    }

    public void runAll(Player player, Location location, List<Reward> rewards) {
        for (String tier : getSounds().keySet()) {
            if ((tier.equalsIgnoreCase("OPEN") && (!getSettings().isTiersOverrideDefaults() || rewards.isEmpty() ||
                    !getSounds().containsKey(rewards.get(0).getRarity().toUpperCase()))) ||
                    (!rewards.isEmpty() && rewards.get(0).getRarity().equalsIgnoreCase(tier))) {
                for (SoundData sd : getSounds().get(tier)) {
                    sd.playTo(player, location);
                }
            }
        }
    }

    public SoundData getSoundFromName(String tier, XSound sound) {
        SoundData sd = null;

        for (SoundData soundData : getSounds().get(tier)) {
            if (soundData.getSound().equals(sound)) {
                sd = soundData;
                break;
            }
        }

        return sd;
    }

    public Map<String, List<SoundData>> getSounds() {
        return sounds;
    }

    public void setSounds(Map<String, List<SoundData>> sounds) {
        this.sounds = sounds;
    }
}
