package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by ztowne13 on 6/23/16.
 */
public class GiveCrate extends SubCommand
{
	public GiveCrate()
	{
		super("givecrate", 2, "Usage: /SCrates GiveCrate (Crate) (Player/ALL) [Amount] [-v : for a virtual crate]");
	}

	@Override
	public boolean run(CustomCrates cc, Commands cmds, String[] args)
	{
		if (Crate.exists(args[1]))
		{
			int amount = 1;
			if (args.length >= 4)
			{
				if(Utils.isInt(args[3]))
				{
					amount = Integer.parseInt(args[3]);
				}
				else
				{
					cmds.msgError(args[3] + " is not a valid number.");
					return true;
				}
			}

			Crate crate = Crate.getCrate(cc, args[1]);
			ItemStack toAdd = crate.getCs().getCrate(amount);

			if(args.length < 3)
			{
				if(cmds.getCmdSender() instanceof Player)
				{
					Player p = (Player) cmds.getCmdSender();
					cmds.msgSuccess("Given crate for crate: " + args[1]);
					p.getInventory().addItem(toAdd);
				}
				return true;
			}

			if (args[2].equalsIgnoreCase("ALL"))
			{
				Utils.giveAllItem(toAdd);
				return true;
			}

			Player op = Bukkit.getPlayer(args[2]);
			Player op2 = null;
			try
			{
				op2 = Bukkit.getPlayer(UUID.fromString(args[2]));
			}
			catch(Exception exc)
			{
				//exc.printStackTrace();
			}

			boolean foundPlayer = true;

			if (op == null && op2 == null)
			{
				if (!args[2].equalsIgnoreCase("ALL"))
				{
					foundPlayer = false;
				}
			}

			if(!foundPlayer)
			{
				cmds.msgError(args[2] + " is not an online player / online player's UUID.");
				return false;
			}

			Player toGive = op == null ? op2 : op;
			PlayerDataManager pdm = PlayerManager.get(cc, toGive).getPdm();
			String end = args[args.length - 1];
			if(end.toLowerCase().startsWith("-v"))
			{
				pdm.setVirtualCrateCrates(crate, pdm.getVCCrateData(crate).getCrates() + amount);
				cmds.msgSuccess("Given virtual crate for crate: " + args[1]);
			}
			else
			{
				toGive.getInventory().addItem(toAdd);
				cmds.msgSuccess("Given physical crate for crate: " + args[1]);
			}
			return true;
		}
		cmds.msgError(args[1] + " crate does NOT exist.");
		return false;
	}
}
