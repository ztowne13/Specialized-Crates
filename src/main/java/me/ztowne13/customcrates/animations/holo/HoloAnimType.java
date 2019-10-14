package me.ztowne13.customcrates.animations.holo;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.holograms.DynamicHologram;

import java.util.ArrayList;
import java.util.List;

public enum HoloAnimType
{
    NONE("No hologram animation, use the normal hologram."),

    SINGLELINE_CHANGE(
            "The hologram will update on whatever interval specified to all of the frames set. This is the same as TEXT_CHANGE."),

    TEXT_CHANGE(
            "The hologram will update on whatever interval specified to all of the frames set. This is the same as SINGLELINE_CHANGE.");

    //MULTILINE_CHANGE;

    String descriptor;

    HoloAnimType(String descriptor)
    {
        this.descriptor = descriptor;
    }

    public HoloAnimation getAsHoloAnimation(SpecializedCrates cc, DynamicHologram dh)
    {
        switch (this)
        {
            case SINGLELINE_CHANGE:
            case TEXT_CHANGE:
                return new TextChangeAnimation(cc, dh);
            default:
                return null;
        }
    }

    public static List<String> descriptors()
    {
        ArrayList<String> descriptors = new ArrayList<String>();

        for(HoloAnimType types : values())
            descriptors.add(types.descriptor);

        return descriptors;
    }
}
