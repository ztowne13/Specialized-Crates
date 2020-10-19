package me.ztowne13.customcrates.crates.options;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.interfaces.items.SaveableItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KeyItemHandler extends CSetting {

    private SaveableItemBuilder keyItem;

    public KeyItemHandler(Crate crate) {
        super(crate, crate.getInstance());

        keyItem = new SaveableItemBuilder(XMaterial.REDSTONE_TORCH, 1);
        keyItem.setDisplayName("&4Please set me!");
    }

    @Override
    public void loadFor(CrateSettingsBuilder crateSettingsBuilder, CrateState crateState) {
        //itemfail, improperenchamnt, improperpotion, improperglow
        crateSettingsBuilder.setupRequireKey();
        boolean result = keyItem.
                loadItem(getCrate().getSettings().getFileHandler(), "key", crateSettingsBuilder.getStatusLogger(),
                        StatusLoggerEvent.SETTINGS_KEY_FAILURE,
                        StatusLoggerEvent.SETTINGS_KEY_ENCHANTMENT_ADD_FAILURE,
                        StatusLoggerEvent.SETTINGS_KEY_POTION_ADD_FAILURE, StatusLoggerEvent.SETTINGS_KEY_GLOW_FAILURE,
                        StatusLoggerEvent.SETTINGS_KEY_AMOUNT_FAILURE, StatusLoggerEvent.SETTINGS_KEY_FLAG_FAILURE);

        if (!result) {
            StatusLoggerEvent.SETTINGS_KEY_FAILURE_DISABLE.log(crateSettingsBuilder.getStatusLogger());
        }
    }

    @Override
    public void saveToFile() {
        if (!getCrate().isMultiCrate()) {
            keyItem.saveItem(getCrate().getSettings().getFileHandler(), "key", false);
        }
    }

    public ItemStack getItem(int amount) {
        ItemStack stack = keyItem.getStack().clone();
        stack.setAmount(amount);
        return stack;
    }

    public boolean keyMatchesToStack(ItemStack stack, boolean checkLore) {
        ItemStack crate = getItem(1);
        if (stack != null && Utils.itemHasName(stack)) {
            boolean matchesNormal = crate.getType().equals(stack.getType()) &&
                    crate.getItemMeta().getDisplayName().equals(stack.getItemMeta().getDisplayName());

            boolean matchesLore = !checkLore || keyLoreMatches(stack);

            return matchesNormal && matchesLore;
        }
        return false;
    }

    public boolean hasKeyInInventory(Player player) {
        for (ItemStack stack : player.getInventory().getContents()) {
            if (keyMatchesToStack(stack, true)) {
                instance.getDu().log("hasKeyInInventory() - return true", getClass());
                return true;
            }
        }
        return false;
    }

    private boolean keyLoreMatches(ItemStack stack) {
        if (!getItem(1).hasItemMeta() || !getItem(1).getItemMeta().hasLore()) {
            return true;
        }

        if (SettingsValue.REQUIRE_KEY_LORE.getValue(instance).equals(Boolean.FALSE)) {
            return true;
        }

        if (!stack.hasItemMeta() || !stack.getItemMeta().hasLore()) {
            return false;
        }

        for (int i = 0; i < getItem(1).getItemMeta().getLore().size(); i++) {
            try {
                String stackLore = stack.getItemMeta().getLore().get(i);
                String keyLore = getItem(1).getItemMeta().getLore().get(i);
                if (!stackLore.equalsIgnoreCase(keyLore)) {
                    return false;
                }
            } catch (Exception exc) {
                return false;
            }
        }

        return true;
    }

    public boolean playerPassesKeyTest(Player player, boolean requireKeyInHand) {
        if (player.getItemInHand() == null) {
            return false;
        }

        CrateSettings settings = getCrate().getSettings();
        boolean passesKeyTest = !settings.isRequireKey() ||
                (requireKeyInHand ? keyMatchesToStack(player.getItemInHand(), true) : hasKeyInInventory(player)) ||
                PlayerManager.get(instance, player).getPlayerDataManager().getVCCrateData(getCrate()).getKeys() > 0;

        instance.getDu().log("playerPasseysKeyTest() - return " + passesKeyTest);
        return passesKeyTest;
    }

    public void takeKeyFromPlayer(Player player, boolean fromInv) {
        boolean prioritzePhysical = (boolean) instance.getSettings().getConfigValues().get("prioritize-physical-key");
        if (!takeKeyFromPlayer(player, fromInv, prioritzePhysical)) {
            takeKeyFromPlayer(player, fromInv, !prioritzePhysical);
        }
    }

    public boolean takeKeyFromPlayer(Player player, boolean fromInv, boolean checkPhysical) {
        if (!checkPhysical) {
            PlayerDataManager pdm = PlayerManager.get(instance, player).getPlayerDataManager();
            if (pdm.getVCCrateData(getCrate()).getKeys() > 0) {
                pdm.setVirtualCrateKeys(getCrate(), pdm.getVCCrateData(getCrate()).getKeys() - 1);
                return true;
            }
            return false;
        }

        if (fromInv) {
            for (int i = 0; i < 36; i++) {
                try {
                    if (player.getInventory().getItem(i) != null) {
                        ItemStack stack = player.getInventory().getItem(i);
                        if (getCrate().getSettings().getKeyItemHandler().keyMatchesToStack(stack, true)) {
                            if (stack.getAmount() == 1) {
                                player.getInventory().setItem(i, null);
                            } else {
                                stack.setAmount(stack.getAmount() - 1);
                            }
                            return true;
                        }
                    }
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        } else {
            ItemStack stack = player.getItemInHand();
            if (getCrate().getSettings().getKeyItemHandler().keyMatchesToStack(stack, true)) {
                if (player.getItemInHand().getAmount() == 1) {
                    player.setItemInHand(null);
                } else {
                    ItemStack st = player.getItemInHand();
                    st.setAmount(st.getAmount() - 1);
                    player.setItemInHand(st);
                }
                return true;
            }
        }
        return false;
    }

    public SaveableItemBuilder getItem() {
        return keyItem;
    }

    public void setItem(SaveableItemBuilder keyItem) {
        this.keyItem = keyItem;
    }
}
