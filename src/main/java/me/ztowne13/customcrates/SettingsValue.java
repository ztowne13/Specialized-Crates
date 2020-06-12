package me.ztowne13.customcrates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public enum SettingsValue
{
    STORE_DATA("store-data", Category.GENERAL_SETTINGS, String.class, false, "FLATFILE", "Data Storage Type",
            new String[]{
                    "Set how would you like to store",
                    "player data. Options: FLATFILE,",
                    "MYSQL, or PLAYERFILES."
            }),

    LOG_SUCCESSES("log-successes", Category.GENERAL_SETTINGS, String.class, false, "FAILURES", "Console Log Type",
            new String[]{
                    "Set how much would you like to",
                    "be logged to console.",
                    "Use EVERYTHING, FAILURES, or",
                    "NOTHING."
            }),

    PUSHBACK("push-back", Category.CRATE_SETTINGS, Boolean.class, false, true, "Pushback On Fail",
            new String[]{
                    "Players will be pushed back if",
                    "they fail to open a crate"
            }),

    AUTO_CLOSE("auto-close", Category.DEPRECATED, Boolean.class, false, true, "Auto Close Animations",
            new String[]{
                    "Crate animations should automatically",
                    "close when done. This is a default value",
                    "and will be overriden by the specific crate's",
                    "auto-close value.",
                    "THE RECOMMENDED SOLUTION",
                    "IS NOW THE 'auto-close' VALUE IN EACH .CRATE",
                    "FILE"
            }),

    REQUIRED_SLOTS("required-slots", Category.CRATE_SETTINGS, Integer.class, false, 1, "Required Slots To Open Crates",
            new String[]{
                    "Set amount of slots required",
                    "to open a crate?"
            }),

    OPEN_CREATIVE("open-creative", Category.CRATE_SETTINGS, Boolean.class, false, true, "Open Crates In Creative",
            new String[]{
                    "Players can open crates in",
                    "creative mode."
            }),

    PLACE_CREATIVE("place-creative", Category.CRATE_SETTINGS, Boolean.class, false, true, "Place Crates In Creative",
            new String[]{
                    "Players can place crates in",
                    "creative mode."
            }),

    LUCKYCHEST_CREATIVE("luckychest-creative", Category.LUCKY_CRATE_SETTINGS, Boolean.class, false, false,
            "Find Luckychests In Creative",
            new String[]{
                    "Players can find lucky chests",
                    "in creative mode."
            }),

    LUCKYCHEST_ALLOW_PLACED_BLOCKS("luckychest-allow-placed-blocks", Category.LUCKY_CRATE_SETTINGS, Boolean.class, false,
            false,
            "Luckychests For Placed Blocks",
            new String[]{
                    "Lucky chests (mine crates) WILL",
                    "spawn even when the block broken",
                    "was placed by a player.",
                    "NOTE: Placed block data IS NOT SAVED",
                    "on reload/restart as to not take too",
                    "many server resources. It is simply",
                    "meant to be a deterrent for placing",
                    "and breaking blocks over and over to",
                    "find lucky chests (mine crates)."
            }),

    REQUIRE_KEY_LORE("require-key-lore", Category.KEY_SETTINGS, Boolean.class, false, true, "Require Key Lore To Open",
            new String[]{
                    "A key's lore is required to",
                    "match the key."
            }),

    CA_FADE_IN("fade-in-time", Category.CRATE_SETTINGS, Integer.class, false, 0, "Title Fade In Time",
            new String[]{
                    "Set how long would you like titles",
                    "or subtitles to take to fade in.",
                    "(In seconds)"
            }),

    CA_STAY("stay-time", Category.CRATE_SETTINGS, Integer.class, false, 4, "Title Stay Time",
            new String[]{
                    "Set how long would you like titles",
                    "or subtitles to stay on the screen.",
                    "(In seconds)"
            }),

    CA_FADE_OUT("fade-out-time", Category.CRATE_SETTINGS, Integer.class, false, 1, "Title Fade Out Time",
            new String[]{
                    "Set how long would you like titles",
                    "or subtitles to take to fade out.",
                    "In seconds"
            }),

    LUCKYCHEST_DESPAWN("luckychest-despawn-after", Category.CRATE_SETTINGS, Integer.class, false, -1,
            "Luckychest Despawn Time",
            new String[]{
                    "Set how many MINUTES luckycrates will",
                    " take to despawn.",
                    "Set to -1 to never despawn."
            }),

    REWARD_DISPLAY_ENABLED("enabled", Category.REWARD_DISPLAY_SETTINGS, Boolean.class, false, true, "Use Reward Displayer",
            new String[]{
                    "Crate will display their rewards",
                    "when left clicked."
            }),

    REWARD_DISPLAY_NAME("inv-reward-display-name", Category.DEPRECATED, String.class, true, "&4%crate% &cRewards",
            "Default Preview Menu Name",
            new String[]{
                    "Set the reward display inventory's.",
                    "This is a default value and will be",
                    "overriden by the crate's own reward",
                    "displayer name value.",
                    "THE RECOMMENDED",
                    "SOLUTION NOW IS TO EDIT THE",
                    "'reward-display.name' VALUE IN EACH .CRATE",
                    "FILE"
            }),

    REWARD_ITEM_NAME("inv-reward-item-name", Category.GENERAL_SETTINGS, String.class, true, "&c%displayname%",
            "Default Reward Name Format",
            new String[]{
                    "Set the reward display item's name.",
                    "This is a default value and will be",
                    "overriden by the crate's own reward",
                    "displayer name value."
            }),

    REWARD_ITEM_LORE("inv-reward-item-lore", Category.GENERAL_SETTINGS, Collection.class, false,
            new String[]{"&7-", "&eChance: &6%writtenchance%%"},
            "Default Reward Lore",
            new String[]{
                    "Edit the reward display item's lore.",
                    "This is a default value and will be",
                    "overriden by the crate's own reward",
                    "displayer name value."
            }),

    EXPLODE_DYNAMIC("explosions-destroy-dynamic-crates", Category.CRATE_SETTINGS, Boolean.class, false, true,
            "Allow Explosions",
            new String[]{
                    "Explosions will destroy dynamic crates."
            }),

    HOLOGRAM_OFFSET("hologram-offset", Category.CRATE_SETTINGS, Double.class, false, 0, "Global Hologram Offset",
            new String[]{
                    "Set the global hologram location offset.",
                    "This offset will be applied IN ADDITION",
                    "to the hologram offset specified in each",
                    "crate's file."
            }),

    PLACE_EFFECT("place-effect", Category.CRATE_SETTINGS, Boolean.class, false, true, "Placing Effect",
            new String[]{
                    "A cool effect will be displayed when",
                    "a crate is placed."
            }),

    PRIORITIZE_PHYSICAL_KEY("prioritize-physical-key", Category.KEY_SETTINGS, Boolean.class, false, true,
            "Phyiscal/Virtual Key Priority",
            new String[]{
                    "Uses a physical key first if the",
                    "player has a physical & virtaul key."
            }),

    VIRTUAL_CRATE_LORE("virtual-crate-lore", Category.DEPRECATED, String.class, true,
            "&cCrates: &f(&7%crates%&f)", "Virtual Crate Lore",
            new String[]{
                    "This is no longer the recommended way",
                    "to put the quantity of keys for a crate",
                    "in an item's lore. Instead, change the",
                    "crate's 'crate item' that would normally",
                    "be placed down to have a lore that uses",
                    "the %crates% placeholder anywhere inside",
                    "of it.",
                    "THE RECOMMENDED SOLUTION IS NOW",
                    "ADDING %crate% TO THE CRATE ITEM'S",
                    "LORE."
            }),

    VIRTUAL_KEY_LORE("virtual-key-lore", Category.DEPRECATED, String.class, true, "&cKeys: &f(&7%keys%&f)",
            "Virtual Key Lore",
            new String[]{
                    "This is no longer the recommended way",
                    "to put the quantity of keys for a crate",
                    "in an item's lore. Instead, change the",
                    "crate's 'crate item' that would normally",
                    "be placed down to have a lore that uses",
                    "the %crates% placeholder anywhere inside",
                    "of it.",
                    "THE RECOMMENDED SOLUTION IS NOW",
                    "ADDING %key% TO THE CRATE ITEM'S",
                    "LORE."
            }),

    CRATES_COMMAND_MULTICRATE("crates-command-multicrate", Category.VIRTUAL_CRATE_SETTINGS, String.class, false, "AllCrates",
            "'/Crates' Crate Name",
            new String[]{
                    "The name of the crate that",
                    "is opened when non-OP players",
                    "type /crates."
            }),

    CRATES_COMMAND_NAME("crates-command-name", Category.VIRTUAL_CRATE_SETTINGS, String.class, true, "&b&lVirtual &7&lCrates",
            "'/Crates' Menu Name",
            new String[]{
                    "The name to be displayed on the",
                    "/crates virtual crates menu."
            }),

    MC_REWARD_DISPLAY_LEFTCLICK("mc-reward-display-leftclick", Category.REWARD_DISPLAY_SETTINGS, Boolean.class, false, true,
            "Multicrates Reward Preview",
            new String[]{
                    "MultiCrates will display rewards",
                    "on left click instead of",
                    "right click."
            }),

//    NOTIFY_UPDATES("notify-updates", Boolean.class, false, true,
//            new String[]{
//                    "The plugin will notify administrators",
//                    "when there is an update",
//                    "for the plugin."
//            }),

    DEBUG("debug", Category.GENERAL_SETTINGS, Boolean.class, false, false, "Debug Mode",
            new String[]{
                    "The plugin will log developer",
                    "information to console. ONLY ENABLE",
                    "IF ZTOWNE13 SAYS SO - it's information",
                    "will be useless without him."
            }),

    VIRTUAL_CRATE_KEYCOUNT("virtual-crate-keycount", Category.DEPRECATED, Boolean.class, false, false,
            "Show 'virtual-key-lore' Value",
            new String[]{
                    "Multicrates will show the",
                    "'virtual-key-lore' on crates",
                    "in a multicrate.",
                    "THE RECOMMENDED SOLUTION IS NOW",
                    "ADDING %key% TO THE CRATE ITEM'S",
                    "LORE."
            }),

    VIRTUAL_CRATE_CRATECOUNT("virtual-crate-cratecount", Category.DEPRECATED, Boolean.class, false, false,
            "Show 'virtual-crate-lore' Value",
            new String[]{
                    "Multicrates will show the",
                    "'virtual-crate-lore' on crates",
                    "in a multicrate.",
                    "THE RECOMMENDED SOLUTION IS NOW",
                    "ADDING %crates% TO THE CRATE ITEM'S",
                    "LORE."
            }),

    VIRTUAL_KEY_INSTEAD_OF_DROP("virtual-key-instead-of-drop", Category.CRATE_SETTINGS, Boolean.class, false, false,
            "Virtual Key Instead of Drop",
            new String[]{
                    "A player will be given a",
                    "virtual key instead of the key",
                    "dropping on the floor when",
                    "their inventory is full."
            }),

    SHIFT_CLICK_OPEN_ALL("shift-click-open-all", Category.CRATE_SETTINGS, Boolean.class, false, true, "Shift-Click Open All",
            new String[]{
                    "Shift clicking on a crate will",
                    "use EVERY key a player is holding",
                    "in their hand.",
                    "NOTE: Opening every key only gives",
                    "one reward per key - regardless of",
                    "animation type."
            }),

    SHIFT_CLICK_CONFIRM("shift-click-confirm", Category.CRATE_SETTINGS, Boolean.class, false, true, "Shift-Click Confirm",
            new String[]{
                    "The user has to shift-click again",
                    "to confirm opening with every",
                    "physical & virtual key."
            }),

    CONFIRM_OPEN("confirm-open", Category.CRATE_SETTINGS, Boolean.class, false, false, "Confirm Open",
            new String[]{
                    "The user will have to click the",
                    "crate again to confirm opening."
            }),

    CONFIRM_TIMEOUT("confirm-timeout", Category.CRATE_SETTINGS, Integer.class, false, 3, "Confirm Timeout Length",
            new String[]{
                    "The time, in seconds, the player",
                    "has to confirm opening a crate if",
                    "shift-click-confirm or",
                    "confirm-open is enabled."
            }),

    REQUIRE_VIRTUAL_CRATE_AND_KEY("require-virtual-crate-and-key", Category.VIRTUAL_CRATE_SETTINGS, Boolean.class, false,
            false, "Require Virtual Crate",
            new String[]{
                    "In MultiCrates (inventory crate), ",
                    "both a virtual key AND a virtual crate",
                    "is required to open the crate."
            }),

    KEEP_CRATE_BLOCK_CONSISTENT("keep-crate-block-consistent", Category.CRATE_SETTINGS, Boolean.class, false, true,
            "Keep Crate Block Consistent",
            new String[]{
                    "Every time the server is restarted/",
                    "reloaded, all of the crates will be",
                    "replaced to ensure no other blocks",
                    "or plugin commands have overridden",
                    "them."
            }),

    KEY_ALLOW_LEFT_CLICK_INTERACTION("key-allow-left-click-interaction", Category.KEY_SETTINGS, Boolean.class, false, false,
            "Allow Key Interaction",
            new String[]{
                    "When a player left clicks with a key,",
                    "the event won't be cancelled. The only",
                    "reason this would be set to true is if",
                    "keys need to interact with other plugins",
                    "like a sell-shop plugin where, to set up",
                    "the shop, a sign needs to be left-clicked",
                    "with a key."
            }),

    PARTICLE_VIEW_DISTANCE("particle-view-distance", Category.CRATE_SETTINGS, Integer.class, false, 25,
            "Particle View Distance",
            new String[]{
                    "The distance, in blocks, away that players",
                    "can be and still see crate particles"
            }),

    LEFT_CLICK_KEY_PREVIEW("left-click-key-preview", Category.REWARD_DISPLAY_SETTINGS, Boolean.class, false, false,
            "Left Click Key Preview",
            new String[]{
                    "When a player left-clicks with a key (anywhere)",
                    "it opens that specific crate's reward",
                    "preview menu."
            }),

    USE_CRATE_NAME_FOR_DISPLAY("use-crate-name-for-display", Category.GENERAL_SETTINGS, Boolean.class, false, false,
            "Use Crate Item Name",
            new String[]{
                    "Anywhere there is a %crate% placeholder",
                    "the crate item's display name will be used",
                    "instead of just the crate's name."
            }),

    CRATES_CLAIM_INVENTORY_NAME("crates-claim-inventory-name", Category.VIRTUAL_CRATE_SETTINGS, String.class, true,
            "&7&l> &6&lCrates Claim",
            "/Crates Claim Inv. Name",
            new String[]{
                    "The name of the inventory when a player",
                    "types /crates claim."
            }),

    CRATES_CLAIM_ALLOW_DEPOSIT("crates-claim-allow-deposit", Category.VIRTUAL_CRATE_SETTINGS, Boolean.class, false, true,
            "Allow deposit in /crate claim",
            new String[]{
                    "Player's can deposit keys into the /crates",
                    "claim inventory in addition to withdraw keys"
            });

    String easyName;
    String path;
    String[] descriptor;
    Object obj;
    Object defaultVal;
    boolean withColor;
    Category category;

    SettingsValue(String path, Category category, Object obj, boolean withColor, Object defaultVal, String easyName,
                  String[] descriptor)
    {
        this.category = category;
        this.easyName = easyName;
        this.path = path;
        this.descriptor = descriptor;
        this.obj = obj;
        this.defaultVal = defaultVal;
        this.withColor = withColor;
    }

    public static SettingsValue getByPath(String s)
    {
        for (SettingsValue sv : values())
        {
            if (sv.getPath().equalsIgnoreCase(s))
            {
                return sv;
            }
        }

        return null;
    }

    static HashMap<SettingsValue, Object> valuesCache = new HashMap<>();

    public Object getValue(SpecializedCrates cc)
    {
        if (valuesCache.containsKey(this))
        {
            return valuesCache.get(this);
        }
        else
        {
            Object val = cc.getSettings().getConfigValues().get(path);
            if (val == null)
                val = defaultVal;

            valuesCache.put(this, val);
            return getValue(cc);
        }
    }

    public void write(SpecializedCrates cc, Object obj)
    {

    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String[] getDescriptor()
    {
        return descriptor;
    }

    public void setDescriptor(String[] descriptor)
    {
        this.descriptor = descriptor;
    }

    public Object getObj()
    {
        return obj;
    }

    public void setObj(Object obj)
    {
        this.obj = obj;
    }

    public boolean isWithColor()
    {
        return withColor;
    }

    public void setWithColor(boolean withColor)
    {
        this.withColor = withColor;
    }

    public String getEasyName()
    {
        return easyName;
    }

    public enum Category
    {
        GENERAL_SETTINGS("General", "General Settings",
                "This includes general settings for the entire plugin.",
                "To edit a crate's specific settings, do /scrates edit (crate)"),

        KEY_SETTINGS("Key", "Key Settings",
                "General settings for how keys are handled and interact with crates, including shift-click to open all, etc.",
                "To edit key settings for a specific crate, type /scrates edit (crate), click 'The Essentials' and configurations are there."),

        CRATE_SETTINGS("Crate", "Crate Settings",
                "General settings for all crates, including creative settings, crate block consistencies, etc.",
                "To edit crate settings for a specific crate, type /scrates edit (crate)"),

        LUCKY_CRATE_SETTINGS("Lucky Crate", "Lucky Crate Settings",
                "General settings for all lucky crates including despawn time, etc.",
                "To make a chest a lucky chest, type /scratest edit (crate), click 'The Essentials' then change the obtain-type to LUCKYCHEST."),

        VIRTUAL_CRATE_SETTINGS("Virtual Crate", "Virtual Crate Settings",
                "General settings for anything virtual-crate related, including the /crates menu, and /crates claim settings.",
                "If you want a virtual key for a crate, type /scrates givekey (crate) (player) (amount) -v"),

        REWARD_DISPLAY_SETTINGS("Reward Preview", "Reward Preview Settings",
                "General settings for the reward displays, including whether or not they're enabled.",
                "To edit a crate's reward preview menu, do /scrates edit (crate), click 'Rewards', then click 'Reward Preview Editor'"),

        DEPRECATED("Deprecated", "Deprecated (No Longer Recommended)",
                "These settings still work but are no longer the recommended way to perform these functions",
                "Most of these functionalities have been moved to the /scrates edit (crate) menu and are editable per-crate instead of globally.");

        String title;
        String shortTitle;
        String[] description;

        Category(String shortTitle, String title, String... description)
        {
            this.shortTitle = shortTitle;
            this.title = title;
            this.description = description;
        }

        public String getShortTitle()
        {
            return shortTitle;
        }

        public String getTitle()
        {
            return title;
        }

        public String[] getDescription()
        {
            return description;
        }

        public List<SettingsValue> getAssociatedValues()
        {
            ArrayList<SettingsValue> vals = new ArrayList<>();
            for (SettingsValue value : SettingsValue.values())
            {
                if (value.category == this)
                {
                    vals.add(value);
                }
            }

            return vals;
        }
    }
}
