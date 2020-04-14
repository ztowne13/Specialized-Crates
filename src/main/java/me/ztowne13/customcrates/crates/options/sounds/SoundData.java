package me.ztowne13.customcrates.crates.options.sounds;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundData
{
    Sound sound;
    int volume = 5;
    int pitch = 5;

    public SoundData(Sound sound)
    {
        setSound(sound);
    }

    public SoundData(Sound sound, int volume)
    {
        setSound(sound);
        this.volume = 0;
    }

    public void playTo(Player p, Location l)
    {
        p.playSound(l, getSound(), getVolume(), getPitch());
    }

    public Sound getSound()
    {
        return sound;
    }

    public void setSound(Sound sound)
    {
        this.sound = sound;
    }

    public int getVolume()
    {
        return volume;
    }

    public void setVolume(int volume)
    {
        this.volume = volume;
    }

    public int getPitch()
    {
        return pitch;
    }

    public void setPitch(int pitch)
    {
        this.pitch = pitch;
    }


}
