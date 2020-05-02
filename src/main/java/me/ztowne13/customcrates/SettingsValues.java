package me.ztowne13.customcrates;

import java.util.Collection;
import java.util.HashMap;

public enum SettingsValues
{
    STORE_DATA("store-data", String.class, false, "FLATFILE",
            new String[]{
                    "Set how would you like to store",
                    "player data. Options: FLATFILE,",
                    "MYSQL, or PLAYERFILES."
            }),

    LOG_SUCCESSES("log-successes", String.class, false, "FAILURES",
            new String[]{
                    "Set how much would you like to",
                    "be logged to console.",
                    "Use EVERYTHING, FAILURES, or",
                    "NOTHING."
            }),

    PUSHBACK("push-back", Boolean.class, false, true,
            new String[]{
                    "Players will be pushed back if",
                    "they fail to open a crate"
            }),

    AUTO_CLOSE("auto-close", Boolean.class, false, true,
            new String[]{
                    "Crate animations should automatically",
                    "close when done."
            }),

    REQUIRED_SLOTS("required-slots", Integer.class, false, 1,
            new String[]{
                    "Set amount of slots required",
                    "to open a crate?"
            }),

    OPEN_CREATIVE("open-creative", Boolean.class, false, true,
            new String[]{
                    "Players can open crates in",
                    "creative mode."
            }),

    PLACE_CREATIVE("place-creative", Boolean.class, false, true,
            new String[]{
                    "Players can place crates in",
                    "creative mode."
            }),

    LUCKYCHEST_CREATIVE("luckychest-creative", Boolean.class, false, false,
            new String[]{
                    "Players can find lucky chests",
                    "in creative mode."
            }),

    LUCKYCHEST_ALLOW_PLACED_BLOCKS("luckychest-allow-placed-blocks", Boolean.class, false, false,
            new String[]{
                    "Lucky chests (mine crates) WILL",
                    "spawn even when the block broken",
                    "was placed by a player.",
                    "",
                    "NOTE: Placed block data IS NOT SAVED",
                    "on reload/restart as to not take too",
                    "many server resources. It is simply",
                    "meant to be a deterrent for placing",
                    "and breaking blocks over and over to",
                    "find lucky chests (mine crates)."
            }),

    REQUIRE_KEY_LORE("require-key-lore", Boolean.class, false, true,
            new String[]{
                    "A key's lore is required to",
                    "match the key."
            }),

    CA_FADE_IN("fade-in-time", Integer.class, false, 0,
            new String[]{
                    "Set how long would you like titles",
                    "or subtitles to take to fade in.",
                    "(In seconds)"
            }),

    CA_STAY("stay-time", Integer.class, false, 4,
            new String[]{
                    "Set how long would you like titles",
                    "or subtitles to stay on the screen.",
                    "(In seconds)"
            }),

    CA_FADE_OUT("fade-out-time", Integer.class, false, 1,
            new String[]{
                    "Set how long would you like titles",
                    "or subtitles to take to fade out.",
                    "In seconds"
            }),

    LUCKYCHEST_DESPAWN("luckychest-despawn-after", Integer.class, false, -1,
            new String[]{
                    "Set how many MINUTES luckycrates will",
                    " take to despawn.",
                    "Set to -1 to never despawn."
            }),

    REWARD_DISPLAY_ENABLED("enabled", Boolean.class, false, true,
            new String[]{
                    "Crate will display their rewards",
                    "when left clicked."
            }),

    REWARD_DISPLAY_NAME("inv-reward-display-name", String.class, true, "&4%crate% &cRewards",
            new String[]{
                    "Set the reward display inventory's."
            }),

    REWARD_ITEM_NAME("inv-reward-item-name", String.class, true, "&c%displayname%",
            new String[]{
                    "Set the reward display item's name."
            }),

    REWARD_ITEM_LORE("inv-reward-item-lore", Collection.class, false, new String[]{"&7-", "&eChance: &6%writtenchance%%"},
            new String[]{
                    "Edit the reward display item's lore."
            }),

    EXPLODE_DYNAMIC("explosions-destroy-dynamic-crates", Boolean.class, false, true,
            new String[]{
                    "Explosions will destroy dynamic crates."
            }),

    HOLOGRAM_OFFSET("hologram-offset", Double.class, false, 0,
            new String[]{
                    "Set the global hologram location offset."
            }),

    PLACE_EFFECT("place-effect", Boolean.class, false, true,
            new String[]{
                    "A cool effect will be displayed when",
                    "a crate is placed."
            }),

    PRIORITIZE_PHYSICAL_KEY("prioritize-physical-key", Boolean.class, false, true,
            new String[]{
                    "Uses a physical key first if the",
                    "player has a physical & virtaul key."
            }),

    VIRTUAL_CRATE_LORE("virtual-crate-lore", String.class, true,"&cCrates: &f(&7%crates%&f)",
            new String[]{
                    "Change the lore to display the",
                    "virtual crates."
            }),

    VIRTUAL_KEY_LORE("virtual-key-lore", String.class, true,"&cKeys: &f(&7%keys%&f)",
            new String[]{
                    "Change the lore to display the",
                    "virtual keys."
            }),

    CRATES_COMMAND_MULTICRATE("crates-command-multicrate", String.class, false, "AllCrates",
            new String[]{
                    "The name of the crate that",
                    "is run when /crates is run."
            }),

    CRATES_COMMAND_NAME("crates-command-name", String.class, true,"&b&lVirtual &7&lCrates",
            new String[]{
                    "The name of the /crates virtual",
                    "crates menu."
            }),

    MC_REWARD_DISPLAY_LEFTCLICK("mc-reward-display-leftclick", Boolean.class, false, true,
            new String[]{
                    "MultiCrates will display rewards",
                    "on left click instead of",
                    "right click."
            }),

    NOTIFY_UPDATES("notify-updates", Boolean.class, false, true,
            new String[]{
                    "The plugin will notify administrators",
                    "when there is an update",
                    "for the plugin."
            }),

    DEBUG("debug", Boolean.class, false, false,
            new String[]{
                    "The plugin will log developer",
                    "information to console."
            }),

    VIRTUAL_CRATE_KEYCOUNT("virtual-crate-keycount", Boolean.class, false, true,
            new String[]{
                    "Multicrates will show the",
                    "player's virtual keys",
                    "amount."
            }),

    VIRTUAL_CRATE_CRATECOUNT("virtual-crate-cratecount", Boolean.class, false, true,
            new String[]{
                    "Multicrates will show the",
                    "player's virtual crates",
                    "amount."
            }),

    VIRTUAL_KEY_INSTEAD_OF_DROP("virtual-key-instead-of-drop", Boolean.class, false, false,
            new String[]{
                    "A player will be given a",
                    "virtual key instead of the key",
                    "dropping on the floor when",
                    "their inventory is full."
            }),

    SHIFT_CLICK_OPEN_ALL("shift-click-open-all", Boolean.class, false, true,
            new String[]{
                    "Shift clicking on a crate will",
                    "use EVERY key a player is holding",
                    "in their hand.",
                    "",
                    "NOTE: Opening every key only gives",
                    "one reward per key - regardless of",
                    "animation type."
            }),

    SHIFT_CLICK_CONFIRM("shift-click-confirm", Boolean.class, false, true,
            new String[]{
                    "The user has to shift-click again",
                    "to confirm opening with every",
                    "physical & virtual key."
            }),

    CONFIRM_OPEN("confirm-open", Boolean.class, false, false,
            new String[]{
                    "The user will have to click the",
                    "crate again to confirm opening."
            }),

    CONFIRM_TIMEOUT("confirm-timeout", Integer.class, false, 3,
            new String[]{
                    "The time, in seconds, the player",
                    "has to confirm opening a crate if",
                    "shift-click-confirm or",
                    "confirm-open is enabled."
            }),

    REQUIRE_VIRTUAL_CRATE_AND_KEY("require-virtual-crate-and-key", Boolean.class, false, false,
            new String[]{
                    "In MultiCrates (inventory crate), ",
                    "both a virtual key AND a virtual crate",
                    "is required to open the crate."
            }),

    KEEP_CRATE_BLOCK_CONSISTENT("keep-crate-block-consistent", Boolean.class, false, true,
            new String[]{
                    "Every time the server is restarted/",
                    "reloaded, all of the crates will be",
                    "replaced to ensure no other blocks",
                    "or plugin commands have overridden",
                    "them."
            }),

    KEY_ALLOW_LEFT_CLICK_INTERACTION("key-allow-left-click-interaction", Boolean.class, false, false,
            new String[]{
                    "When a player left clicks with a key,",
                    "the event won't be cancelled. The only",
                    "reason this would be set to true is if",
                    "keys need to interact with other plugins",
                    "like a sell-shop plugin where, to set up",
                    "the shop, a sign needs to be left-clicked",
                    "with a key."
            }),

    PARTICLE_VIEW_DISTANCE("particle-view-distance", Integer.class, false, 25,
            new String[]{
                    "The distance, in blocks, away that players",
                    "can be and still see crate particles"
            });

    String path;
    String[] descriptor;
    Object obj;
    Object defaultVal;
    boolean withColor;

    SettingsValues(String path, Object obj, boolean withColor, Object defaultVal, String[] descriptor)
    {
        this.path = path;
        this.descriptor = descriptor;
        this.obj = obj;
        this.defaultVal = defaultVal;
        this.withColor = withColor;
    }

    public static SettingsValues getByPath(String s)
    {
        for (SettingsValues sv : values())
        {
            if (sv.getPath().equalsIgnoreCase(s))
            {
                return sv;
            }
        }

        return null;
    }

    static HashMap<SettingsValues, Object> valuesCache = new HashMap<>();

    public Object getValue(SpecializedCrates cc)
    {
        if(valuesCache.containsKey(this))
        {
            return valuesCache.get(this);
        }
        else
        {
            Object val = cc.getSettings().getConfigValues().get(path);
            if(val == null)
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

    enum Category
    {
        CRATE_SETTINGS,

        CRATE_ACTION_SETTINGS,

        LUCKY_CRATE_SETTINGS,

        VIRTUAL_CRATE_SETTINGS,

        REWARDS_DISPLAY_INVENTORY
    }
}
