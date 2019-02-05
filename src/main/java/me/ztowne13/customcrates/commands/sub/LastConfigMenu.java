package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 6/23/16.
 */
public class LastConfigMenu extends SubCommand
{
	public LastConfigMenu()
	{
		super("!", 1, "");
	}

	@Override
	public boolean run(CustomCrates cc, Commands cmds, String[] args)
	{
		Player p = (Player)cmds.getCmdSender();
		try
		{
			PlayerManager.get(cc, p).getLastOpenMenu().open();
			cmds.msgSuccess("Opened last crate config page.");
		}
		catch(Exception exc)
		{
			p.chat("/scrates config");
		}
		return true;
	}
}
