package me.ztowne13.customcrates.crates.options.sounds;

import com.cryptomorin.xseries.XSound;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundData {
    private XSound sound;
    private int volume;
    private int pitch = 5;

    public SoundData(XSound sound) {
        this(sound, 5);
    }

    public SoundData(XSound sound, int volume) {
        setSound(sound);
        this.volume = volume;
    }

    public void playTo(Player player, Location location) {
        Sound parsedSound = getSound().parseSound();
        if (parsedSound != null) {
            player.playSound(location, parsedSound, volume, pitch);
        }
    }

    public XSound getSound() {
        return sound;
    }

    public void setSound(XSound sound) {
        this.sound = sound;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }


}
