package me.ztowne13.customcrates.crates.options;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.crateaction.CrateAction;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.items.SaveableItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.players.data.VirtualCrateData;
import me.ztowne13.customcrates.players.data.events.CrateCooldownEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.CrateUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ztowne13 on 6/14/16.
 */
public class CMultiCrateInventory extends CSetting {
    private static final String AVAILABLE_SYMBOLS = "abcdefghijklmnopqrstuvwxyz123456789 ";

    private final HashMap<Integer, Crate> crateSpots = new HashMap<>();
    private final HashMap<Integer, ItemBuilder> items = new HashMap<>();
    private final HashMap<String, SaveableItemBuilder> materialsWithID = new HashMap<>();
    private final HashMap<String, Crate> cratesWithID = new HashMap<>();

    private InventoryBuilder inventoryBuilder;

    public CMultiCrateInventory(Crate crate) {
        super(crate, crate.getInstance());
    }

    public void updateCrateSpots() {
        for (int i = 0; i < getInventoryBuilder().getSize(); i++) {
            if (crateSpots.containsKey(i)) {
                Crate crateAtSpot = crateSpots.get(i);
                ItemStack itemAtSlot = getInventoryBuilder().getInv().getItem(i);
                if (itemAtSlot == null || itemAtSlot.getType().equals(Material.AIR) ||
                        !(new ItemBuilder(itemAtSlot).equals(crateAtSpot.getSettings().getCrateItemHandler().getItem()))) {
                    crateSpots.remove(i);
                }
            }
        }
    }

    @Override
    public void loadFor(CrateSettingsBuilder crateSettingsBuilder, CrateState crateState) {
        FileConfiguration fc = getCrate().getSettings().getFileConfiguration();
        if (crateSettingsBuilder.hasValue("gui")) {
            try {
                for (String s : fc.getConfigurationSection("gui.objects").getValues(false).keySet()) {
                    s = s.toLowerCase();
                    String cause = "The crate name is invalid";
                    try {
                        String value = fc.getString("gui.objects." + s);
                        if (Crate.existsNotCaseSensitive(value)) {
                            cause = "The crate name to set the object to is invalid";
                            cratesWithID.put(s, Crate.getCrate(instance, value));
                        } else {
                            SaveableItemBuilder item = new SaveableItemBuilder(XMaterial.STONE, 1);
                            item.setDisplayName("&cThis item was configured improperly");
                            item.loadItem(getFileHandler(), "gui.objects." + s, crateSettingsBuilder.getStatusLogger(),
                                    StatusLoggerEvent.MULTICRATE_ITEM_FAILURE,
                                    StatusLoggerEvent.MULTICRATE_ENCHANTMENT_ADD_FAILURE,
                                    StatusLoggerEvent.MULTICRATE_POTION_ADD_FAILURE,
                                    StatusLoggerEvent.MULTICRATE_GLOW_FAILURE,
                                    StatusLoggerEvent.MULTICRATE_AMOUNT_FAILURE,
                                    StatusLoggerEvent.MULTICRATE_FLAG_FAILURE);
                            materialsWithID.put(s, item);
                        }
                    } catch (Exception exc) {
                        StatusLoggerEvent.MULTICRATEINVENTORY_OBJECTS_INVALID.log(getCrate(), new String[]{s, cause});
                    }
                }
            } catch (Exception exc) {
                StatusLoggerEvent.MULTICRATEINVENTORY_OBJECTS_MISCONFIGURED.log(getCrate());
            }


            try {
                int row = 0;
                int slot = 0;
                for (String s : fc.getStringList("gui.rows")) {
                    s = s.toLowerCase();
                    for (String character : s.split("")) {
                        if (character == null || character.replaceAll("\\s+", "").equalsIgnoreCase("")) {
                            continue;
                        }
                        for (String identifier : materialsWithID.keySet()) {
                            if (identifier.equalsIgnoreCase(character)) {
                                items.put((row * 9) + slot, materialsWithID.get(character));
                                break;
                            }
                        }


                        for (String identifier : cratesWithID.keySet()) {
                            if (identifier.equalsIgnoreCase(character)) {
                                crateSpots.put((row * 9) + slot, cratesWithID.get(character));
                                break;
                            }
                        }
                        slot++;
                    }
                    row++;
                    slot = 0;
                }

            } catch (Exception exc) {
                StatusLoggerEvent.MULTICRATEINVENTORY_ROW_MISCONFIGURED.log(getCrate());
            }

            return;
        }
        StatusLoggerEvent.MULTICRATEINVENTORY_NONEXISTENT.log(getCrate());
    }

    @Override
    public void saveToFile() {
        if (inventoryBuilder == null || inventoryBuilder.getInv() == null)
            return;

        Inventory inv = inventoryBuilder.getInv();

        // It looks like this updates the arrays that have the items in them
        for (int i = 0; i < inv.getSize(); i++) {
            SaveableItemBuilder stack = inv.getItem(i) == null
                    ? new SaveableItemBuilder(XMaterial.AIR)
                    : new SaveableItemBuilder(inv.getItem(i));

            // If the item is not a crate
            if (CrateUtils.searchByCrate(stack.getStack()) == null) {
                boolean itemDoesntAlreadyExist = true;
                for (ItemBuilder alreadyExistingItem : materialsWithID.values()) {
                    if (alreadyExistingItem.equals(stack)) {
                        itemDoesntAlreadyExist = false;
                        break;
                    }
                }

                if (itemDoesntAlreadyExist) {
                    materialsWithID.put(getNextSymbol(), stack);
                }
            }
            // The item is a crate
            else {
                Crate crate = CrateUtils.searchByCrate(stack.getStack());
                boolean createDoesntAlreadyExist = true;
                for (Crate alreadyExistingCrate : cratesWithID.values()) {
                    if (alreadyExistingCrate.equals(crate)) {
                        createDoesntAlreadyExist = false;
                        break;
                    }
                }

                if (createDoesntAlreadyExist) {
                    cratesWithID.put(getNextSymbol(), crate);
                }
            }
        }

        ArrayList<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();

        for (int i = 0; i < inv.getSize(); i++) {
            ItemBuilder stack;
            if (inv.getItem(i) == null) {
                stack = new ItemBuilder(XMaterial.AIR);
            } else {
                stack = new ItemBuilder(inv.getItem(i));
            }

            // The item isn't a crate
            if (CrateUtils.searchByCrate(stack.getStack()) == null) {

                for (Map.Entry<String, SaveableItemBuilder> entry : materialsWithID.entrySet()) {
                    String s = entry.getKey();
                    if (s.equalsIgnoreCase("")) {
                        s = "-";
                    }
                    try {
                        SaveableItemBuilder s2 = entry.getValue();

                        if (s2.equals(stack)) {
                            line.append(s);
                            s2.saveItem(getFileHandler(), "gui.objects." + s, true);
                            break;
                        }
                    } catch (Exception exc) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.isOp()) {
                                ChatUtils.msgError(p,
                                        "Failed to save multicrate " + getCrate().getName() + " with value " + s);
                            }
                        }
                        exc.printStackTrace();
                    }
                }
            } else {
                Crate cs = CrateUtils.searchByCrate(stack.getStack());
                for (Map.Entry<String, Crate> entry : cratesWithID.entrySet()) {
                    String s = entry.getKey();
                    try {
                        if (s.equalsIgnoreCase("")) {
                            s = "-";
                        }
                        Crate crate = entry.getValue();
                        if (crate.equals(cs)) {
                            line.append(s);
                            getFileHandler().get().set("gui.objects." + s, crate.getName());
                            break;
                        }
                    } catch (Exception exc) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.isOp()) {
                                ChatUtils.msgError(p, "Failed to save multicrate " + crate.getName() + " with value " + s);
                            }
                        }
                        exc.printStackTrace();
                    }
                }
            }

            if (line.length() == 9) {
                lines.add(line.toString());
                line = new StringBuilder();
            }
        }

        getFileHandler().get().set("gui.rows", lines);
    }

    public InventoryBuilder getInventory(Player player, String invName, boolean toEdit) {
        if (inventoryBuilder == null) {
            int slots = items.size() + crateSpots.size() - 2;
            int rows = (slots / 9) + (slots % 9 == 0 ? 0 : 1);
            inventoryBuilder = new InventoryBuilder(player, rows * 9, invName);

            for (Map.Entry<Integer, ItemBuilder> entry : items.entrySet()) {
                try {
                    inventoryBuilder.setItem(entry.getKey(), entry.getValue());
                } catch (Exception exc) {
                    ChatUtils.msgError(player, "There are to many lines in the MultiCrate inventory");
                }
            }
            instance.getDu().log("CMultiCrateInventory.getInventory.if(ib == null)");

        } else if (toEdit) {
            InventoryBuilder newIb = new InventoryBuilder(player, inventoryBuilder.getInv().getSize(), invName);
            for (int i = 0; i < newIb.getInv().getSize(); i++) {
                Inventory oldInv = inventoryBuilder.getInv();
                if (oldInv.getItem(i) != null && !oldInv.getItem(i).getType().equals((Material.AIR))) {
                    newIb.getInv().setItem(i, oldInv.getItem(i));
                }
            }

            inventoryBuilder = newIb;
            instance.getDu().log("CMultiCrateInventory.getInventory.else");
        }

        for (Map.Entry<Integer, Crate> entry : crateSpots.entrySet()) {
            try {
                Crate crate = entry.getValue();
                instance.getDu().log(crate.getName());
                VirtualCrateData vcd = PlayerManager.get(instance, player).getPdm().getVCCrateData(crate);
                instance.getDu().log(vcd.toString());
                ItemBuilder crateIb = new ItemBuilder(crate.getSettings().getCrateItemHandler().getItem(1));

                if (instance.getSettings().getConfigValAsBoolean("virtual-crate-cratecount").equals(Boolean.TRUE)) {
                    String toAddLore = (String) SettingsValue.VIRTUAL_CRATE_LORE.getValue(instance);
                    if (!toAddLore.equalsIgnoreCase("") && !toAddLore.equalsIgnoreCase("none")) {
                        crateIb.addLore("");
                        crateIb.addLore(toAddLore);
                    }
                }
                if (instance.getSettings().getConfigValAsBoolean("virtual-crate-keycount").equals(Boolean.TRUE)) {
                    String toAddLore = (String) SettingsValue.VIRTUAL_KEY_LORE.getValue(instance);
                    if (!toAddLore.equalsIgnoreCase("") && !toAddLore.equalsIgnoreCase("none")) {
                        crateIb.addLore("");
                        crateIb.addLore(toAddLore);
                    }
                }

                List<String> lore = new ArrayList<>(crateIb.getLore());
                crateIb.clearLore();

                for (String line : lore) {
                    line = line.replace("%keys%", vcd.getKeys() + "");
                    line = line.replace("%crates%", vcd.getCrates() + "");

                    crateIb.addLore(line);
                }

                inventoryBuilder.setItem(entry.getKey(), crateIb);
            } catch (Exception exc) {
                ChatUtils.msgError(player, "There are to many lines in the MultiCrate inventory");
            }
        }

        inventoryBuilder.setPlayer(player);
        return inventoryBuilder;
    }

    public String getNextSymbol() {
        ArrayList<String> combined = new ArrayList<>();
        combined.addAll(materialsWithID.keySet());
        combined.addAll(cratesWithID.keySet());
        for (String s : AVAILABLE_SYMBOLS.split("")) {
            if (!combined.contains(s) && !s.replaceAll("\\s+", "").equalsIgnoreCase("")) {
                return s;
            }
        }
        return "TOMANYSYMBOLS";
    }

    public void checkClick(PlayerManager playerManager, int slot, ClickType clickType) {
        final Player player = playerManager.getP();
        Crate crate = playerManager.getOpenCrate();

        if (!crate.getSettings().getMultiCrateSettings().getCrateSpots().containsKey(slot) || playerManager.isInRewardMenu()) {
            return;
        }

        final Crate clickedCrate = crate.getSettings().getMultiCrateSettings().getCrateSpots().get(slot);

        // Usability check
        if (!CrateUtils.isCrateUsable(clickedCrate)) {
            Messages.CRATE_DISABLED.msgSpecified(instance, player);
            if (player.hasPermission("customcrates.admin") || player.isOp()) {
                Messages.CRATE_DISABLED_ADMIN.msgSpecified(instance, player);
            }
            return;
        }

        if (clickType != ClickType.LEFT && clickType != ClickType.RIGHT) {
            return;
        }

        // Is preview menu
        if (clickType.equals(Boolean
                .parseBoolean(SettingsValue.MC_REWARD_DISPLAY_LEFTCLICK.getValue(instance).toString().toUpperCase()) ?
                ClickType.LEFT : ClickType.RIGHT) && SettingsValue.REWARD_DISPLAY_ENABLED.getValue(instance).equals(Boolean.TRUE)) {
            clickedCrate.getSettings().getDisplayer().openFor(player);
            // Set last open crate back to multicrate so that closing the reward previewer reopens the multicrate
            final Crate cachedMulticrate = this.crate;
            Bukkit.getScheduler().runTaskLater(instance, () -> playerManager.openCrate(cachedMulticrate), 2);
            return;
        }

        // Virtual crates check
        if (SettingsValue.REQUIRE_VIRTUAL_CRATE_AND_KEY.getValue(instance).equals(Boolean.TRUE)
                && playerManager.getPdm().getVCCrateData(clickedCrate).getCrates() <= 0) {
            Messages.INSUFFICIENT_VIRTUAL_CRATES.msgSpecified(playerManager.getCc(), player);
            return;
        }

        // Inventory check
        if (!CrateAction.isInventoryTooEmpty(instance, player)) {
            Messages.INVENTORY_TOO_FULL.msgSpecified(instance, player);
            clickedCrate.getSettings().getCrateAnimation().playFailToOpen(player, false, true);

            invCheck(player, playerManager);
            return;
        }

        // Permission check
        if (!player.hasPermission(clickedCrate.getSettings().getPermission()) &&
                !clickedCrate.getSettings().getPermission().equalsIgnoreCase("no permission")) {
            Messages.NO_PERMISSION_CRATE.msgSpecified(instance, player);
            clickedCrate.getSettings().getCrateAnimation().playFailToOpen(player, false, true);
            invCheck(player, playerManager);
            return;
        }

        // Gamemode check
        if (player.getGameMode().equals(GameMode.CREATIVE) &&
                instance.getSettings().getConfigValues().get("open-creative").equals(Boolean.FALSE)) {
            Messages.DENY_CREATIVE_MODE.msgSpecified(instance, player);
            return;
        }

        // Cooldown check
        CrateCooldownEvent cce = playerManager.getPdm().getCrateCooldownEventByCrates(clickedCrate);
        if (cce != null && !cce.isCooldownOverAsBoolean()) {
            invCheck(player, playerManager);
            cce.playFailure(playerManager.getPdm());
            return;
        }

        // Economy check
        if (!instance.getEconomyHandler().handleCheck(player, clickedCrate.getSettings().getCost(), true)) {
            crate.getSettings().getCrateAnimation().playFailToOpen(player, false, true);
            invCheck(player, playerManager);
            return;
        }

        // Open crate
        player.closeInventory();

        if (clickedCrate.getSettings().isCanFastTrack()) {
            clickedCrate.getSettings().setCanFastTrack(false);
            Bukkit.getScheduler().runTaskLater(instance, () -> clickedCrate.getSettings().setCanFastTrack(true), 2L);
        }

        if (!clickedCrate.getSettings().getCrateAnimation()
                .startAnimation(player, playerManager.getLastOpenCrate(), false, false)) {
            invCheck(player, playerManager);
            instance.getEconomyHandler().failSoReturn(player, clickedCrate.getSettings().getCost());
            return;
        }

        new CrateCooldownEvent(clickedCrate, System.currentTimeMillis(), true).addTo(playerManager.getPdm());
        // Post Conditions
        if (playerManager.isUseVirtualCrate()) {
            playerManager.getPdm().setVirtualCrateCrates(clickedCrate,
                    playerManager.getPdm().getVCCrateData(clickedCrate).getCrates() - 1);
        } else if (!this.crate.getSettings().getObtainType().equals(ObtainType.STATIC)) {
            try {
                PlacedCrate pc = PlacedCrate.get(instance, playerManager.getLastOpenCrate());
                pc.delete();
            } catch (Exception exc) {
                // IGNORED
            }
        }
    }

    public void openFor(Player player, PlacedCrate placedCrate) {
        PlayerManager pm = PlayerManager.get(instance, player);
        crate.getSettings().getMultiCrateSettings()
                .getInventory(player, crate.getSettings().getCrateInventoryName() == null ? crate.getName() :
                        crate.getSettings().getCrateInventoryName(), true).open();
        pm.setLastOpenCrate(placedCrate.getLocation());
        pm.setLastOpenedPlacedCrate(placedCrate);
        pm.openCrate(crate);
        getCrate().getSettings().getSound().runAll(player, placedCrate.getLocation(), new ArrayList<>());
    }

    public void invCheck(Player player, PlayerManager playerManager) {
        if (player.getLocation().distance(playerManager.getLastOpenCrate()) > 10) {
            player.closeInventory();
        }
    }

    public Map<Integer, Crate> getCrateSpots() {
        return crateSpots;
    }

    public Map<Integer, ItemBuilder> getItems() {
        return items;
    }

    public InventoryBuilder getInventoryBuilder() {
        return inventoryBuilder;
    }

    public void setInventoryBuilder(InventoryBuilder inventoryBuilder) {
        this.inventoryBuilder = inventoryBuilder;
    }
}
