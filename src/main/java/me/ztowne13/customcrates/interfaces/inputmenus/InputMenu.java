package me.ztowne13.customcrates.interfaces.inputmenus;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 3/15/16.
 */
public class InputMenu
{
    CustomCrates cc;
    Player p;
    IGCMenu inputMenu;
    String value;
    String formatExp;
    String currentValue;
    boolean withoutColor;
    Object type;
    //SettingsValues sv;

	/*public InputMenu(CustomCrates cc, Player p, SettingsValues sv)
	{
		this.p = p;
		this.sv = sv;
		this.cc = cc;
	}*/

    public InputMenu(CustomCrates cc, Player p, String value, String currentValue, Object type, IGCMenu inputMenu)
    {
        this(cc, p, value, currentValue, "", type, inputMenu, false);
    }

    public InputMenu(CustomCrates cc, Player p, String value, String currentValue, Object type, IGCMenu inputMenu,
                     boolean withoutColor)
    {
        this(cc, p, value, currentValue, "", type, inputMenu, withoutColor);
    }

    public InputMenu(CustomCrates cc, Player p, String value, String currentValue, String formatExp, Object type,
                     IGCMenu inputMenu)
    {
        this(cc, p, value, currentValue, formatExp, type, inputMenu, false);
    }

    public InputMenu(CustomCrates cc, Player p, String value, String currentValue, String formatExp, Object type,
                     IGCMenu inputMenu, boolean withoutColor)
    {
        this.p = p;
        this.cc = cc;
        this.value = value;
        this.currentValue = currentValue;
        this.inputMenu = inputMenu;
        this.formatExp = formatExp;
        this.type = type;
        this.withoutColor = withoutColor;

        inputMenu.setInputMenu(this);

        initMsg();
    }

    private void initMsg()
    {
        p.closeInventory();
        for (int i = 0; i < 20; i++)
        {
            ChatUtils.msg(p, "");
        }
        ChatUtils.msg(p, "&7----------------------------------------");
        ChatUtils.msg(p, "&aYou are currently editing the &f" + value + " &avalue.");
        ChatUtils.msg(p, "&BCurrent Value: &f" + currentValue);
        if (!formatExp.equalsIgnoreCase(""))
        {
            ChatUtils.msg(p, "&d" + formatExp);
        }
        ChatUtils.msg(p, "&7----------------------------------------");
        ChatUtils.msg(p, "&6Please write the value you'd like to set it to below.");
        ChatUtils.msg(p, "&e&oType 'exit' to exit the current configuration session.");
    }

    public void runFor(IGCMenu igcm, String s)
    {
        if (type != String.class || withoutColor)
        {
            s = ChatUtils.removeColor(s);
        }

        if (s.equalsIgnoreCase("exit"))
        {
            igcm.open();
            igcm.setInputMenu(null);
            return;
        }
        else
        {
            if (inputMenu.handleInput(value, s))
            {
                igcm.open();
                igcm.setInputMenu(null);
            }
        }
    }

    public CustomCrates getCc()
    {
        return cc;
    }

    public void setCc(CustomCrates cc)
    {
        this.cc = cc;
    }

    public Player getP()
    {
        return p;
    }

    public void setP(Player p)
    {
        this.p = p;
    }

    public IGCMenu getInputMenu()
    {
        return inputMenu;
    }

    public void setInputMenu(IGCMenu inputMenu)
    {
        this.inputMenu = inputMenu;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getFormatExp()
    {
        return formatExp;
    }

    public void setFormatExp(String formatExp)
    {
        this.formatExp = formatExp;
    }

    public String getCurrentValue()
    {
        return currentValue;
    }

    public void setCurrentValue(String currentValue)
    {
        this.currentValue = currentValue;
    }

    public Object getType()
    {
        return type;
    }

    public void setType(Object type)
    {
        this.type = type;
    }
}
