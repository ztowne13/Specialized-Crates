package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.crates.Crate;

import java.util.ArrayList;
import java.util.List;

public enum ObtainType {
    STATIC(true, false, "The crate will always stay where it is, no matter how many times it's opened."),

    DYNAMIC(false, true, "The crate will disappear once it is used once."),

    LUCKYCHEST(false, false, "The crate will spawn randomly when a player is mining according to the lucky chest settings.");

    private final boolean isStatic;
    private final boolean canPlace;
    private final String descriptor;

    ObtainType(boolean isStatic, boolean canPlace, String descriptor) {
        this.isStatic = isStatic;
        this.canPlace = canPlace;
        this.descriptor = descriptor;
    }

    public static boolean getReqKey(Crate crates) {
        return crates.getSettings().isRequireKey();
    }

    public static List<String> descriptors() {
        ArrayList<String> descriptors = new ArrayList<>();

        for (ObtainType val : values())
            descriptors.add(val.descriptor);

        return descriptors;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isCanPlace() {
        return canPlace;
    }
}
