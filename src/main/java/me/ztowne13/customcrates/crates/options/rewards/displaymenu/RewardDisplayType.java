package me.ztowne13.customcrates.crates.options.rewards.displaymenu;

import java.util.ArrayList;
import java.util.List;

public enum RewardDisplayType {
    IN_ORDER("Fills the preview menu with all the rewards in a random order."),

    SORTED_LOW_TO_HIGH("Fills the preview menu with the rewards sorted from the lowest to highest chance."),

    SORTED_HIGH_TO_LOW("Fills the preview menu with the rewards sorted from the highest to lowest chance."),

    CUSTOM("You custom design the entire reward preview menu with any items and any rewards. This supports multiple pages of rewards too!");

    private final String description;

    RewardDisplayType(String description) {
        this.description = description;
    }

    public static List<String> descriptions() {
        ArrayList<String> list = new ArrayList<>();
        for (RewardDisplayType rewardDisplayType : values()) {
            list.add(rewardDisplayType.description);
        }

        return list;
    }
}
