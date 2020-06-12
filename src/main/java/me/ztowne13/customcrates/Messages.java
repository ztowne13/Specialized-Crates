package me.ztowne13.customcrates;

import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;

public enum Messages
{
    NO_PERMISSIONS,

    FAIL_OPEN,

    ALREADY_OPENING_CRATE,

    DENY_CREATIVE_MODE,

    DENY_PLACE_KEY,

    PLACED_CRATE,

    FOUND_LUCKY_CHEST,

    WAIT_ONE_SECOND,

    BROKEN_CRATE,

    FAILED_BREAK_CRATE,

    NO_KEY_USE,

    DENIED_USE_CRATE,

    INVENTORY_TOO_FULL,

    COOLDOWN_START,

    COOLDOWN_END,

    CRATE_ON_COOLDOWN,

    CRATE_DISABLED,

    DENIED_PLACE_LOCATION,

    TOGGLE_LUCKYCRATE,

    NO_PERMISSION_CRATE,

    OPENING_VIRTUALCRATES,

    INSUFFICIENT_VIRTUAL_CRATES,

    CONFIRM_OPEN,

    CONFIRM_OPEN_ALL,

    LOADING_FROM_DATABASE,

    PLACEHOLDER_SECONDS("","seconds"),

    PLACEHOLDER_MINUTES("","minutes"),

    PLACEHOLDER_HOURS("","hours"),

    PLACEHOLDER_DAYS("","days"),

    ECONOMY_NOT_ENOUGH_MONEY("", "&4&lERROR: &cYou do not have enough money, you need %amount% and are %short% short."),

    COMMAND_REWARDS_USAGE("", "&4&lERROR: &cUsage: /rewards [crate name]"),

    COMMAND_REWARDS_OPENING("", "&6&lINFO: &eOpening reward preview for %crate% crate."),

    COMMAND_REWARDS_INVALID_CRATE("", "&4&lERROR: &c%crate% is not a valid crate."),

    CANT_CRAFT_KEYS("", "&4&lERROR: &cYou are not allowed to craft keys."),

    RECEIVED_KEY("", "&2&lSUCCESS! &aYou just received %amount% %crate% key(s)!"),

    RECEIVED_VIRTUAL_KEY("", "&2&lSUCCESS! &aYou just received %amount% virtual %crate% key(s)!"),

    GIVEN_FALLBACK_REWARD("", "&6&lINFO: &eYou already have the %reward% &ereward, so you have been given the %fallbackreward% &ereward instead."),

    CRATES_CLAIM_DENY_DEPOSIT_KEYS("", "&4&lHey! &cYou can not deposit keys into /crates claim."),

    BLACKLISTED_PLUGIN("&cIMPORTANT: THIS COPY OF THE SPECIALIZED CRATES HAS BEEN BLACKLISTED BECAUSE THE USER WHO PURCHASED IT" +
            " IS NOT THE ONLY PERSON USING IT, OR THIS PERSON HAS REFUNDED IT. IF YOU BELIEVE THIS IS AN ERROR, PLEASE RE-DOWNLOAD THE PLUGIN (NO" +
            " NEED TO REGENERATE CONFIG) AND TRY AGAIN. IF IT'S STILL NOT WORKING, PLEASE CONTACT ZTOWNE13."),

    BYPASS_BREAK_RESTRICTIONS("&9&lNOTICE! &bThis crate typically isn't placeable, you have bypassed this restriction."),

    SUCCESS_DELETE("&2&lSUCCESS! &aDeleted the %crate% crate from this location."),

    CRATE_DISABLED_ADMIN(
            "  &9&lNOTE: &bIf you did not disable this crate manually, something was misconfigured. Please view console to see why."),

    NEEDS_UPDATE("&9&lNOTICE: &bSpecialized Crates has an update available: v%version%"),

    HEADER("&3&l>> &7&m--------------- &6&lCrates &7&m---------------&3&l <<"),

    FOOTER("&3&l>> &7&m----------------------------------------------&3&l <<");

    String msg;
    String defaultMsg;

    Messages()
    {
        this("");
    }

    Messages(String msg)
    {
        this(msg, "");
    }

    Messages(String msg, String defaultMsg)
    {
        this.msg = msg;
        this.defaultMsg = defaultMsg;
    }

    static HashMap<Messages, String> cachedMessages = new HashMap<Messages, String>();

    public String getFromConf(SpecializedCrates cc)
    {
        if(cachedMessages.containsKey(this))
            return cachedMessages.get(this);

        try
        {
            String val = cc.getMessageFile().get().getString(nameFormatted());

            if (val == null)
                throw new Exception();

            cachedMessages.put(this, ChatUtils.toChatColor(val));
            return ChatUtils.toChatColor(val);
        }
        catch (Exception exc)
        {
            if(defaultMsg.equalsIgnoreCase(""))
            {
                cachedMessages.put(this, ChatUtils.toChatColor(
                        "&eThis value isn't set, please tell the server operator to configure the " + name() + " value."));
            }
            else
            {
                cachedMessages.put(this, ChatUtils.toChatColor(defaultMsg));
                cc.getMessageFile().get().set(nameFormatted(), defaultMsg);
                cc.getMessageFile().save();
            }
            return cachedMessages.get(this);
        }
    }

    public String nameFormatted()
    {
        return name().toLowerCase().replace("_", "-");
    }

    public void msgSpecified(SpecializedCrates cc, Player p)
    {
        msgSpecified(cc, p, new String[]{}, new String[]{});
    }

    public void msgSpecified(SpecializedCrates cc, Player p, String[] replaceValue, String[] setValue)
    {
        String correctMSG = getPropperMsg(cc);
        if(correctMSG.equalsIgnoreCase("none")
                || correctMSG.equalsIgnoreCase("")
                || correctMSG.equalsIgnoreCase("&f")) {
            return;
        }

        for (int i = 0; i < replaceValue.length; i++)
        {
            correctMSG = correctMSG.replace(replaceValue[i], setValue[i]);
        }

        p.sendMessage(correctMSG);
    }

    public void writeValue(SpecializedCrates cc, String value)
    {
        cc.getMessageFile().get().set(name().toLowerCase().replace("_", "-").toLowerCase(), value);
    }

    public String getPropperMsg(SpecializedCrates cc)
    {
        return ChatUtils.toChatColor(getMsg().equalsIgnoreCase("") ? getFromConf(cc) : getMsg());
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public static void clearCache()
    {
        cachedMessages.clear();
    }
}
