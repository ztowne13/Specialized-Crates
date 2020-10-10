package me.ztowne13.customcrates.commands;

import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Commands {
    String commandName;
    CommandSender cmdSender;

    public Commands(String commandName) {
        this.commandName = commandName;
    }

    public abstract void msgPage(int page);

    public boolean canExecute(boolean console, boolean reqperm, String... perms) {
        for (String perm : perms) {
            if (cmdSender instanceof Player ? !reqperm || cmdSender.hasPermission(perm) : console) {
                return true;
            }
        }
        return false;
    }

    public void msg(String s) {
        if (getCmdSender() instanceof Player) {
            ChatUtils.msg((Player) getCmdSender(), s);
        } else {
//            ChatUtils.log(ChatUtils.removeColor(s));
            ChatUtils.log(s);
        }
    }

    public void msgError(String s) {
        msg("&4&lERROR! &c" + s);
    }

    public void msgSuccess(String s) {
        msg("&2&lSUCCESS! &a" + s);
    }

    public CommandSender getCmdSender() {
        return cmdSender;
    }

    public void setCmdSender(CommandSender cmdSender) {
        this.cmdSender = cmdSender;
    }
}
