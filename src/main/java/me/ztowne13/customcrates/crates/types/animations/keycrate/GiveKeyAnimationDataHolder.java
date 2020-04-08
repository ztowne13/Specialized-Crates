package me.ztowne13.customcrates.crates.types.animations.keycrate;

import me.ztowne13.customcrates.crates.types.animations.AnimationDataHolder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GiveKeyAnimationDataHolder extends AnimationDataHolder
{

    public GiveKeyAnimationDataHolder(Player player, Location location, GiveKeyAnimation crateAnimation)
    {
        super(player, location, crateAnimation);
    }
}
