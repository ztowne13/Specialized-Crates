package me.ztowne13.customcrates.crates.types.animations;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.types.animations.block.OpenChestAnimation;
import me.ztowne13.customcrates.crates.types.animations.inventory.*;
import me.ztowne13.customcrates.crates.types.animations.minimal.GiveKeyAnimation;
import me.ztowne13.customcrates.crates.types.animations.minimal.GiveKeyAnimationDataHolder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum CrateAnimationType {
    INV_ROULETTE("Roulette", Category.INVENTORY, true),

    INV_MENU("Menu", Category.INVENTORY, true),

    INV_CSGO("CSGO", Category.INVENTORY, true),

    INV_ENCLOSE("Enclose", Category.INVENTORY, true),

    INV_DISCOVER("Discover", Category.INVENTORY, false),

    BLOCK_CRATEOPEN("OpenChest", Category.CHEST, true),

    //BLOCK_CRATEOPEN_ROLLING("CrateType.Block.OpenChestRolling", Category.CHEST),

    GIVE_KEY("", Category.NONE, true);

    private final String prefix;
    private final Category category;
    private final boolean canFastTrackOnReload;

    CrateAnimationType(String prefix, Category category, boolean canFastTrackOnReload) {
        this.prefix = prefix;
        this.category = category;
        this.canFastTrackOnReload = canFastTrackOnReload;
    }

    public void setupFor(Crate crates) {
        CrateAnimation ch;

        switch (this) {
            case INV_ROULETTE:
                ch = new RouletteAnimation(crates);
                break;
            case INV_CSGO:
                ch = new CSGOAnimation(crates);
                break;
            case INV_MENU:
                ch = new MenuAnimation(crates);
                break;
            case INV_ENCLOSE:
                ch = new EncloseAnimation(crates);
                break;
            case INV_DISCOVER:
                ch = new DiscoverAnimation(crates);
                break;
            case BLOCK_CRATEOPEN:
                ch = new OpenChestAnimation(crates);
                break;
            /*case BLOCK_CRATEOPEN_ROLLING:
                ch = new OpenChestRollingAnimation(null, crates);
                break;*/
            case GIVE_KEY:
            default:
                ch = new GiveKeyAnimation(crates);
        }

        crates.getSettings().setCrateAnimation(ch);
    }

    public AnimationDataHolder newDataHolderInstance(Player player, Location location, CrateAnimation crateAnimation) {
        switch (this) {
            case INV_ROULETTE:
                return new RouletteAnimationDataHolder(player, location, (RouletteAnimation) crateAnimation);
            case INV_CSGO:
                return new CSGOAnimationDataHolder(player, location, (CSGOAnimation) crateAnimation);
            case INV_DISCOVER:
                return new DiscoverAnimationDataHolder(player, location, (DiscoverAnimation) crateAnimation);
            case INV_ENCLOSE:
                return new EncloseAnimationDataHolder(player, location, (EncloseAnimation) crateAnimation);
            case INV_MENU:
                return new MenuAnimationDataHolder(player, location, (MenuAnimation) crateAnimation);
            case BLOCK_CRATEOPEN:
            case GIVE_KEY:
                return new GiveKeyAnimationDataHolder(player, location, (GiveKeyAnimation) crateAnimation);
            default:
                return null;
        }
    }

    public int getUses() {
        int uses = 0;
        for (Crate crate : Crate.getLoadedCrates().values()) {
            if (!crate.isMultiCrate() && crate.getSettings().getCrateType().equals(this)) {
                uses++;
            }
        }

        return uses;
    }

    public String getPrefix() {
        return "CrateType." + getCategory().getPrefix() + prefix;
    }

    public String getPrefixDotted() {
        return getPrefix() + ".";
    }

    public Category getCategory() {
        return category;
    }

    public boolean isSpecialDynamicHandling() {
        return category.isSpecialDynamicHandling();
    }

    public boolean isCanFastTrackOnReload() {
        return canFastTrackOnReload;
    }

    public enum Category {
        INVENTORY(false, "Inventory."),

        BLOCK(true, "Block."),

        CHEST(true, "Block."),

        NONE(false, "");

        boolean specialDynamicHandling;
        String prefix;

        Category(boolean specialDynamicHandling, String prefix) {
            this.specialDynamicHandling = specialDynamicHandling;
            this.prefix = prefix;
        }

        public boolean isSpecialDynamicHandling() {
            return specialDynamicHandling;
        }

        public String getPrefix() {
            return prefix;
        }
    }
}
