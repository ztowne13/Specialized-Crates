package me.ztowne13.customcrates.interfaces.externalhooks;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SpecializedCrates;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyHandler {
    private final SpecializedCrates instance;
    private final boolean enabled;
    private Economy economy;

    public EconomyHandler(SpecializedCrates instance) {
        this.instance = instance;
        this.enabled = instance.getServer().getPluginManager().isPluginEnabled("Vault") && setupEconomy();
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider =
                instance.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return economy != null;
    }

    public boolean handleCheck(Player p, int cost, boolean withMessage) {
        if (enabled && cost > 0) {
            double bal = economy.getBalance(p);

            if (bal < cost) {
                if (withMessage)
                    Messages.ECONOMY_NOT_ENOUGH_MONEY
                            .msgSpecified(instance, p, new String[]{"%amount%", "%short%"}, new String[]{cost + "", (cost - bal) + ""});
                return false;
            }

            economy.withdrawPlayer(p, cost);
        }

        return true;
    }

    public void failSoReturn(Player p, int cost) {
        if (enabled && cost > 0)
            economy.depositPlayer(p, cost);
    }
}
