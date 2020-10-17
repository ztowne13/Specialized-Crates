package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.players.data.events.HistoryEvent;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 6/23/16.
 */
public class ListHistory extends SubCommand {
    public ListHistory() {
        super("listhistory", 3, "Usage: /SCrates listHistory (Player) (Amount of shown entries)", new String[]{"lhistory", "history"});
    }

    @Override
    public boolean run(SpecializedCrates cc, Commands cmds, String[] args) {
        Player p = Bukkit.getPlayer(args[1]);
        if (p != null) {
            if (Utils.isInt(args[2])) {
                int showEntries = Integer.parseInt(args[2]);
                HistoryEvent.listFor(cc, cmds.getCmdSender(), p, showEntries);
            } else {
                cmds.msgError(args[2] + " is not a valid ");
            }
        } else {
            cmds.msgError(args[1] + " is not an online player.");
        }
        return true;
    }
}
