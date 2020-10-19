package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.players.data.VirtualCrateData;
import org.bukkit.entity.Player;

public class Claim extends SubCommand {
    public Claim() {
        super("claim", 1, "");
    }

    @Override
    public boolean run(SpecializedCrates cc, Commands cmds, String[] args) {
        if (!(cmds.getCmdSender() instanceof Player)) {
            cmds.msgError("This command is only usable by a player.");
            return false;
        }

        Player player = (Player) cmds.getCmdSender();
        PlayerManager playerManager = PlayerManager.get(cc, player);
        PlayerDataManager dataManager = playerManager.getPlayerDataManager();

        int stacks = 0;
        for (VirtualCrateData vcd : dataManager.getVirtualCrateData().values()) {
            int stackSize = vcd.getCrate().getSettings().getKeyItemHandler().getItem(1).getMaxStackSize();
            stacks += (vcd.getKeys() / stackSize) + (vcd.getKeys() % stackSize == 0 ? 0 : 1);
            if (stacks >= 54) {
                break;
            }
        }

        int rowsNeeded = (stacks / 9) + (stacks % 9 == 0 ? 0 : 1) + 1;

        String invName = SettingsValue.CRATES_CLAIM_INVENTORY_NAME.getValue(cc).toString();
        InventoryBuilder builder = new InventoryBuilder(player, rowsNeeded > 6 ? 54 : (rowsNeeded * 9), invName);

        for (VirtualCrateData vcd : dataManager.getVirtualCrateData().values()) {
            int stackSize = vcd.getCrate().getSettings().getKeyItemHandler().getItem(1).getMaxStackSize();
            int keys = vcd.getKeys();
            while (keys > 0 && builder.getInv().firstEmpty() != -1) {
                int toAdd = Math.min(keys, stackSize);
                builder.getInv().addItem(vcd.getCrate().getSettings().getKeyItemHandler().getItem(toAdd));
                keys -= toAdd;

                dataManager.setVirtualCrateKeys(vcd.getCrate(), dataManager.getVCCrateData(vcd.getCrate()).getKeys() - toAdd);
            }
        }

        playerManager.setInCratesClaimMenu(true);
        builder.open();

        return false;
    }
}
