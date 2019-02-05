package me.ztowne13.customcrates;

import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

public enum Messages 
{
	NO_PERMISSIONS,
	
	FAIL_OPEN,
	
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

	BYPASS_BREAK_RESTRICTIONS("&9&lNOTICE! &bThis crate typically isn't placeable, you have bypassed this restriction."),

	SUCCESS_DELETE("&2&lSUCCESS! &aDeleted the %crate% crate from this location."),

	CRATE_DISABLED_ADMIN("  &9&lNOTE: &bIf you did not disable this crate manually, something was misconfigured. Please view console to see why."),

	NEEDS_UPDATE("&9&lNOTICE: &bSpecialized Crates has an update available: v%version%"),
		
	HEADER("&3&l>> &7&m--------------- &6&lCrates &7&m---------------&3&l <<"),
	
	FOOTER("&3&l>> &7&m----------------------------------------------&3&l <<");
	
	String msg;
	
	Messages()
	{
		this("");
	}
	
	Messages(String msg)
	{
		this.msg = msg;
	}
	
	public String getFromConf(CustomCrates cc)
	{
		return ChatUtils.toChatColor(cc.getMessageFile().get().getString(name().toLowerCase().replace("_", "-")));
	}
	
	public void msgSpecified(CustomCrates cc, Player p)
	{
		msgSpecified(cc, p, new String[]{}, new String[]{});
	}

	public void msgSpecified(CustomCrates cc, Player p, String[] replaceValue, String[] setValue)
	{
		String correctMSG = getPropperMsg(cc);

		for(int i = 0; i < replaceValue.length; i++)
		{
			correctMSG = correctMSG.replace(replaceValue[i], setValue[i]);
		}

		p.sendMessage(correctMSG);
	}

	public void writeValue(CustomCrates cc, String value)
	{
		cc.getMessageFile().get().set(name().toLowerCase().replace("_", "-").toLowerCase(), value);
	}

	public String getPropperMsg(CustomCrates cc)
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
}
