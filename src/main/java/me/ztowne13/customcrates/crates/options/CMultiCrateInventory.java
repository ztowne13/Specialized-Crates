package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.crateaction.CrateAction;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
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

/**
 * Created by ztowne13 on 6/14/16.
 */
public class CMultiCrateInventory extends CSetting
{
    static String availableSymbols = "abcdefghijklmnopqrstuvwxyz123456789 ";

    HashMap<Integer, Crate> crateSpots = new HashMap<>();
    HashMap<Integer, ItemBuilder> items = new HashMap<>();
    HashMap<String, SaveableItemBuilder> materialsWithID = new HashMap<>();
    HashMap<String, Crate> cratesWithID = new HashMap<>();

    InventoryBuilder ib;

    public CMultiCrateInventory(Crate crates)
    {
        super(crates, crates.getCc());
    }

    public void updateCrateSpots()
    {
        for (int i = 0; i < getIb().getSize(); i++)
        {
            if (crateSpots.keySet().contains(i))
            {
                Crate crateAtSpot = crateSpots.get(i);
                ItemStack itemAtSlot = getIb().getInv().getItem(i);
                if (itemAtSlot == null || itemAtSlot.getType().equals(Material.AIR) ||
                        !(new ItemBuilder(itemAtSlot).equals(crateAtSpot.getSettings().getCrateItemHandler().getItem())))
                {
                    crateSpots.remove(i);
                }
            }
        }
    }

    @Override
    public void loadFor(CrateSettingsBuilder csb, CrateState cs)
    {
        FileConfiguration fc = getCrate().getSettings().getFc();
        if (csb.hasV("gui"))
        {
            try
            {
                for (String s : fc.getConfigurationSection("gui.objects").getValues(false).keySet())
                {
                    s = s.toLowerCase();
                    String cause = "The crate name is invalid";
                    try
                    {
                        String value = fc.getString("gui.objects." + s);
                        if (Crate.existsNotCaseSensitive(value))
                        {
                            cause = "The crate name to set the object to is invalid";
                            cratesWithID.put(s, Crate.getCrate(cc, value));
                        }
                        else
                        {
                            SaveableItemBuilder item = new SaveableItemBuilder(DynamicMaterial.STONE, 1);
                            item.setDisplayName("&cThis item was configured improperly");
                            item.loadItem(getFileHandler(), "gui.objects." + s, csb.getStatusLogger(),
                                    StatusLoggerEvent.MULTICRATE_ITEM_FAILURE,
                                    StatusLoggerEvent.MULTICRATE_ENCHANTMENT_ADD_FAILURE,
                                    StatusLoggerEvent.MULTICRATE_POTION_ADD_FAILURE,
                                    StatusLoggerEvent.MULTICRATE_GLOW_FAILURE,
                                    StatusLoggerEvent.MULTICRATE_AMOUNT_FAILURE,
                                    StatusLoggerEvent.MULTICRATE_FLAG_FAILURE);
                            materialsWithID.put(s, item);
                        }
                    }
                    catch (Exception exc)
                    {
//                        exc.printStackTrace();
                        StatusLoggerEvent.MULTICRATEINVENTORY_OBJECTS_INVALID.log(getCrate(), new String[]{s, cause});
                    }
                }
            }
            catch (Exception exc)
            {
                StatusLoggerEvent.MULTICRATEINVENTORY_OBJECTS_MISCONFIGURED.log(getCrate());
            }


            try
            {
                int row = 0;
                int slot = 0;
                for (String s : fc.getStringList("gui.rows"))
                {
                    s = s.toLowerCase();
                    for (String character : s.split(""))
                    {
                        if (character == null || character.replaceAll("\\s+", "").equalsIgnoreCase(""))
                        {
                            continue;
                        }
                        for (String identifier : materialsWithID.keySet())
                        {
                            if (identifier.equalsIgnoreCase(character))
                            {
                                items.put((row * 9) + slot, materialsWithID.get(character));
                                break;
                            }
                        }


                        for (String identifier : cratesWithID.keySet())
                        {
                            if (identifier.equalsIgnoreCase(character))
                            {
                                crateSpots.put((row * 9) + slot, cratesWithID.get(character));
                                break;
                            }
                        }
                        slot++;
                    }
                    row++;
                    slot = 0;
                }

            }
            catch (Exception exc)
            {
                StatusLoggerEvent.MULTICRATEINVENTORY_ROW_MISCONFIGURED.log(getCrate());
            }

            return;
        }
        StatusLoggerEvent.MULTICRATEINVENTORY_NONEXISTENT.log(getCrate());
    }

    @Override
    public void saveToFile()
    {
        if (ib == null || ib.getInv() == null)
            return;

        Inventory inv = ib.getInv();

        // It looks like this updates the arrays that have the items in them
        for (int i = 0; i < inv.getSize(); i++)
        {
            SaveableItemBuilder stack;
            if (inv.getItem(i) == null)
            {
                stack = new SaveableItemBuilder(DynamicMaterial.AIR, 1);
            }
            else
            {
                stack = new SaveableItemBuilder(inv.getItem(i));
            }

            // If the item is not a crate
            if (CrateUtils.searchByCrate(stack.get()) == null)
            {
                boolean itemDoesntAlreadyExist = true;
                for (ItemBuilder alreadyExistingItem : materialsWithID.values())
                {
                    if (alreadyExistingItem.equals(stack))
                    {
                        itemDoesntAlreadyExist = false;
                        break;
                    }
                }

                if (itemDoesntAlreadyExist)
                {
                    materialsWithID.put(getNextSymbol(), stack);
                }
            }
            // The item is a crate
            else
            {
                Crate crate = CrateUtils.searchByCrate(stack.get());
                boolean createDoesntAlreadyExist = true;
                for (Crate alreadyExistingCrate : cratesWithID.values())
                {
                    if (alreadyExistingCrate.equals(crate))
                    {
                        createDoesntAlreadyExist = false;
                        break;
                    }
                }

                if (createDoesntAlreadyExist)
                {
                    cratesWithID.put(getNextSymbol(), crate);
                }
            }
        }

        ArrayList<String> lines = new ArrayList<>();
        String line = "";

        for (int i = 0; i < inv.getSize(); i++)
        {
            ItemBuilder stack;
            if (inv.getItem(i) == null)
            {
                stack = new ItemBuilder(DynamicMaterial.AIR);
            }
            else
            {
                stack = new ItemBuilder(inv.getItem(i));
            }

            // The item isn't a crate
            if (CrateUtils.searchByCrate(stack.get()) == null)
            {

                for (String s : materialsWithID.keySet())
                {
                    if (s.equalsIgnoreCase(""))
                    {
                        s = "-";
                    }
                    try
                    {
                        SaveableItemBuilder s2 = materialsWithID.get(s);

                        if (s2.equals(stack))
                        {
                            line = line + s;
                            s2.saveItem(getFileHandler(), "gui.objects." + s, true);
//                            getFu().get().set("gui.objects." + s, s2.getType() + ";" + s2.getDurability());
                            break;
                        }
                    }
                    catch (Exception exc)
                    {
                        for (Player p : Bukkit.getOnlinePlayers())
                        {
                            if (p.isOp())
                            {
                                ChatUtils.msgError(p,
                                        "Failed to save multicrate " + getCrate().getName() + " with value " + s);
                            }
                        }
                        exc.printStackTrace();
                    }
                }
            }
            else
            {
                Crate cs = CrateUtils.searchByCrate(stack.get());
                for (String s : cratesWithID.keySet())
                {
                    try
                    {
                        if (s.equalsIgnoreCase(""))
                        {
                            s = "-";
                        }
                        Crate crate = cratesWithID.get(s);
                        if (crate.equals(cs))
                        {
                            line = line + s;
                            getFileHandler().get().set("gui.objects." + s, crate.getName());
                            break;
                        }
                    }
                    catch (Exception exc)
                    {
                        for (Player p : Bukkit.getOnlinePlayers())
                        {
                            if (p.isOp())
                            {
                                ChatUtils.msgError(p, "Failed to save multicrate " + crates.getName() + " with value " + s);
                            }
                        }
                        exc.printStackTrace();
                    }
                }
            }

            if (line.length() == 9)
            {
                lines.add(line);
                line = "";
            }
        }

        getFileHandler().get().set("gui.rows", lines);
    }

    public InventoryBuilder getInventory(Player p, String invName, boolean toEdit)
    {
        if (ib == null)
        {
            int slots = items.size() + crateSpots.size() - 2;
            int rows = (slots / 9) + (slots % 9 == 0 ? 0 : 1);
            ib = new InventoryBuilder(p, rows * 9, invName);

            for (int i : items.keySet())
            {
                try
                {
                    ib.setItem(i, items.get(i));
                }
                catch (Exception exc)
                {
                    ChatUtils.msgError(p, "There are to many lines in the MultiCrate inventory");
                }
            }
            cc.getDu().log("CMultiCrateInventory.getInventory.if(ib == null)");

        }
        else if (toEdit)
        {
            InventoryBuilder newIb = new InventoryBuilder(p, ib.getInv().getSize(), invName);
            for (int i = 0; i < newIb.getInv().getSize(); i++)
            {
                Inventory oldInv = ib.getInv();
                if (oldInv.getItem(i) != null && !oldInv.getItem(i).getType().equals((Material.AIR)))
                {
                    newIb.getInv().setItem(i, oldInv.getItem(i));
                }
            }

            ib = newIb;
            cc.getDu().log("CMultiCrateInventory.getInventory.else");
        }

        for (int i : crateSpots.keySet())
        {
            try
            {
                Crate crate = crateSpots.get(i);
                cc.getDu().log(crate.getName());
                VirtualCrateData vcd = PlayerManager.get(cc, p).getPdm().getVCCrateData(crate);
                cc.getDu().log(vcd.toString());
                ItemBuilder crateIb = new ItemBuilder(crate.getSettings().getCrateItemHandler().getItem(1));

                if (cc.getSettings().getConfigValAsBoolean("virtual-crate-cratecount"))
                {
                    String toAddLore = (String) SettingsValue.VIRTUAL_CRATE_LORE.getValue(cc);
                    if (!toAddLore.equalsIgnoreCase("") && !toAddLore.equalsIgnoreCase("none"))
                    {
                        crateIb.addLore("");
                        crateIb.addLore(toAddLore);
                    }
                }
                if (cc.getSettings().getConfigValAsBoolean("virtual-crate-keycount"))
                {
                    String toAddLore = (String) SettingsValue.VIRTUAL_KEY_LORE.getValue(cc);
                    if (!toAddLore.equalsIgnoreCase("") && !toAddLore.equalsIgnoreCase("none"))
                    {
                        crateIb.addLore("");
                        crateIb.addLore(toAddLore);
                    }
                }

                List<String> lore = new ArrayList<String>(crateIb.getLore());
                crateIb.clearLore();

                for (int loreLineNum = 0; loreLineNum < lore.size(); loreLineNum++)
                {
                    String line = lore.get(loreLineNum);
                    line = line.replaceAll("%keys%", vcd.getKeys() + "");
                    line = line.replaceAll("%crates%", vcd.getCrates() + "");

                    crateIb.addLore(line);
                }

                ib.setItem(i, crateIb);
            }
            catch (Exception exc)
            {
                ChatUtils.msgError(p, "There are to many lines in the MultiCrate inventory");
            }
        }

        ib.setP(p);
        return ib;
    }

    public String getNextSymbol()
    {
        ArrayList<String> combined = new ArrayList<>();
        combined.addAll(materialsWithID.keySet());
        combined.addAll(cratesWithID.keySet());
        for (String s : availableSymbols.split(""))
        {
            if (!combined.contains(s) && !s.replaceAll("\\s+", "").equalsIgnoreCase(""))
            {
                return s;
            }
        }
        return "TOMANYSYMBOLS";
    }

    public void checkClick(final PlayerManager pm, int slot, ClickType clickType)
    {
        final Player player = pm.getP();
        Crate crate = pm.getOpenCrate();

        if (crate.getSettings().getMultiCrateSettings().getCrateSpots().keySet().contains(slot) && !pm.isInRewardMenu())
        {
            final Crate clickedCrate = crate.getSettings().getMultiCrateSettings().getCrateSpots().get(slot);

            // Usability check
            if(!CrateUtils.isCrateUsable(clickedCrate))
            {
                Messages.CRATE_DISABLED.msgSpecified(cc, player);
                if (player.hasPermission("customcrates.admin") || player.isOp())
                {
                    Messages.CRATE_DISABLED_ADMIN.msgSpecified(cc, player);
                }
                return;
            }

            if (clickType != ClickType.LEFT && clickType != ClickType.RIGHT)
            {
                return;
            }

            // Is preview menu
            if (clickType.equals(Boolean
                    .parseBoolean(SettingsValue.MC_REWARD_DISPLAY_LEFTCLICK.getValue(getSc()).toString().toUpperCase()) ?
                    ClickType.LEFT : ClickType.RIGHT) && (Boolean) SettingsValue.REWARD_DISPLAY_ENABLED.getValue(cc))
            {
                clickedCrate.getSettings().getDisplayer().openFor(player);
                // Set last open crate back to multicrate so that closing the reward previewer reopens the multicrate
                final Crate cachedMulticrate = this.crates;
                Bukkit.getScheduler().runTaskLater(cc, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        pm.openCrate(cachedMulticrate);
                    }
                }, 2);
            }
            // Virtual crates check
            else if (!((Boolean) SettingsValue.REQUIRE_VIRTUAL_CRATE_AND_KEY.getValue(cc))
                    || pm.getPdm().getVCCrateData(clickedCrate).getCrates() > 0)
            {

                // Inventory check
                if (!CrateAction.isInventoryTooEmpty(cc, player))
                {
                    Messages.INVENTORY_TOO_FULL.msgSpecified(cc, player);
                    clickedCrate.getSettings().getAnimation().playFailToOpen(player, false, true);

                    invCheck(player, pm);
                    return;
                }

                // Permission check
                if (!player.hasPermission(clickedCrate.getSettings().getPermission()) &&
                        !clickedCrate.getSettings().getPermission().equalsIgnoreCase("no permission"))
                {
                    Messages.NO_PERMISSION_CRATE.msgSpecified(cc, player);
                    clickedCrate.getSettings().getAnimation().playFailToOpen(player, false, true);
                    invCheck(player, pm);
                    return;
                }

                // Gamemode check
                if (!player.getGameMode().equals(GameMode.CREATIVE) ||
                        (Boolean) cc.getSettings().getConfigValues().get("open-creative"))
                {
                    // Cooldown check
                    CrateCooldownEvent cce = pm.getPdm().getCrateCooldownEventByCrates(clickedCrate);
                    if (cce == null || cce.isCooldownOverAsBoolean())
                    {
                        // Economy check
                        if (cc.getEconomyHandler().handleCheck(player, clickedCrate.getSettings().getCost(), true))
                        {
                            // Open crate
                            player.closeInventory();

                            if (clickedCrate.getSettings().isCanFastTrack())
                            {
                                clickedCrate.getSettings().setCanFastTrack(false);
                                Bukkit.getScheduler().runTaskLater(cc, new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        clickedCrate.getSettings().setCanFastTrack(true);
                                    }
                                }, 2L);
                            }
                            if (clickedCrate.getSettings().getAnimation()
                                    .startAnimation(player, pm.getLastOpenCrate(), false, false))
                            {
                                new CrateCooldownEvent(clickedCrate, System.currentTimeMillis(), true)
                                        .addTo(pm.getPdm());
                                // Post Conditions
                                if (pm.isUseVirtualCrate())
                                {
                                    pm.getPdm().setVirtualCrateCrates(clickedCrate,
                                            pm.getPdm().getVCCrateData(clickedCrate).getCrates() - 1);
                                }
                                else if (!crates.getSettings().getObtainType().equals(ObtainType.STATIC))
                                {
                                    try
                                    {
                                        PlacedCrate pc = PlacedCrate.get(cc, pm.getLastOpenCrate());
                                        pc.delete();
                                    }
                                    catch (Exception exc)
                                    {

                                    }
                                }
                            }
                            else
                            {
                                invCheck(player, pm);
                                cc.getEconomyHandler().failSoReturn(player, clickedCrate.getSettings().getCost());
                            }
                        }
                        else
                        {
                            crate.getSettings().getAnimation().playFailToOpen(player, false, true);
                            invCheck(player, pm);
                        }
                    }
                    else
                    {
                        invCheck(player, pm);
                        cce.playFailure(pm.getPdm());
                    }
                }
                else
                {
                    Messages.DENY_CREATIVE_MODE.msgSpecified(cc, player);
                }
            }
            else
            {
                Messages.INSUFFICIENT_VIRTUAL_CRATES.msgSpecified(pm.getCc(), player);
            }
        }
    }

    public void openFor(Player player, PlacedCrate cm)
    {
        PlayerManager pm = PlayerManager.get(cc, player);
        crates.getSettings().getMultiCrateSettings()
                .getInventory(player, crates.getSettings().getCrateInventoryName() == null ? crates.getName() :
                        crates.getSettings().getCrateInventoryName(), true).open();
        pm.setLastOpenCrate(cm.getL());
        pm.setLastOpenedPlacedCrate(cm);
        pm.openCrate(crates);
        getCrate().getSettings().getSounds().runAll(player, cm.getL(), new ArrayList<Reward>());
    }

    public void invCheck(Player p, PlayerManager pm)
    {
        if (p.getLocation().distance(pm.getLastOpenCrate()) > 10)
        {
            p.closeInventory();
        }
    }

    public HashMap<Integer, Crate> getCrateSpots()
    {
        return crateSpots;
    }

    public HashMap<Integer, ItemBuilder> getItems()
    {
        return items;
    }

    public InventoryBuilder getIb()
    {
        return ib;
    }

    public void setIb(InventoryBuilder ib)
    {
        this.ib = ib;
    }
}
