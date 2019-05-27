package me.ztowne13.customcrates.crates.types;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.types.animations.csgo.CSGOAnimation;
import me.ztowne13.customcrates.crates.types.animations.discover.DiscoverAnimation;
import me.ztowne13.customcrates.crates.types.animations.enclosement.EnclosementAnimation;
import me.ztowne13.customcrates.crates.types.animations.keycrate.AnimationKeyCrate;
import me.ztowne13.customcrates.crates.types.animations.menu.MenuAnimation;
import me.ztowne13.customcrates.crates.types.animations.openchest.OpenChestAnimation;
import me.ztowne13.customcrates.crates.types.animations.roulette.RouletteAnimation;

public enum CrateType
{

    INV_ROULETTE( "CrateType.Inventory.Roulette", Category.INVENTORY),

    INV_MENU("CrateType.Inventory.Menu", Category.INVENTORY),

    INV_CSGO("CrateType.Inventory.CSGO", Category.INVENTORY),

    INV_ENCLOSE("CrateType.Inventory.Enclose", Category.INVENTORY),

    INV_DISCOVER("CrateType.Inventory.Discover", Category.INVENTORY),

    BLOCK_CRATEOPEN("CrateType.Block.OpenChest", Category.CHEST),

    //BLOCK_CRATEOPEN_ROLLING("CrateType.Block.OpenChestRolling"),

    GIVE_KEY("", Category.NONE);

    String prefix;
    Category category;

    CrateType(String prefix, Category category)
    {
        this.prefix = prefix;
        this.category = category;
    }

    public enum Category
    {
        INVENTORY(false),

        BLOCK(true),

        CHEST(true),

        NONE(false);

        boolean specialDynamicHandling;

        Category(boolean specialDynamicHandling)
        {
            this.specialDynamicHandling = specialDynamicHandling;
        }

        public boolean isSpecialDynamicHandling()
        {
            return specialDynamicHandling;
        }
    }

    public Category getCategory()
    {
        return category;
    }

    public boolean isSpecialDynamicHandling()
    {
        return category.isSpecialDynamicHandling();
    }

    public void setupFor(Crate crates)
    {
        CrateAnimation ch;
        switch (this)
        {
            case INV_ROULETTE:
                ch = new RouletteAnimation(null, crates);
                break;
            case INV_CSGO:
                //ch = new CSGOManager(null, crates);
                ch = new CSGOAnimation(null, crates);
                break;
            case INV_MENU:
                ch = new MenuAnimation(null, crates);
                break;
            case INV_ENCLOSE:
                ch = new EnclosementAnimation(null, crates);
                break;
            case INV_DISCOVER:
                ch = new DiscoverAnimation(null, crates);
                break;
            case BLOCK_CRATEOPEN:
                ch = new OpenChestAnimation(null, crates);
                break;
//            case BLOCK_CRATEOPEN_ROLLING:
//                ch = new OpenChestRollingAnimation(null, crates);
//                break;
            case GIVE_KEY:
            default:
                ch = new AnimationKeyCrate(crates);
        }
        crates.getCs().setCh(ch);
    }

    public int getUses()
    {
        int uses = 0;
        for (Crate crate : Crate.getLoadedCrates().values())
        {
            if (!crate.isMultiCrate())
            {
                if (crate.getCs().getCt().equals(this))
                {
                    uses++;
                }
            }
        }

        return uses;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public String getPrefixDotted()
    {
        return prefix + ".";
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }
}
