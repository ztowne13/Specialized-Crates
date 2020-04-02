package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.players.data.VirtualCrateData;
import me.ztowne13.customcrates.players.data.events.CrateCooldownEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.CrateUtils;
import me.ztowne13.customcrates.utils.Utils;
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

/**
 * Created by ztowne13 on 6/14/16.
 */
public class CMultiCrateInventory extends CSetting
{
    static String availableSymbols = "abcdefghijklmnopqrstuvwxyz123456789 ";

    HashMap<Integer, Crate> crateSpots = new HashMap<>();
    HashMap<Integer, ItemStack> items = new HashMap<>();
    HashMap<String, ItemStack> materialsWithID = new HashMap<>();
    HashMap<String, Crate> cratesWithID = new HashMap<>();

    InventoryBuilder ib;

    public CMultiCrateInventory(Crate crates)
    {
        super(crates, crates.getCc());
    }

    @Override
    public void loadFor(CrateSettingsBuilder csb, CrateState cs)
    {
        FileConfiguration fc = getCrates().getCs().getFc();
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
                        String[] args = fc.getString("gui.objects." + s).split(";");
                        if (Crate.crateAlreadyExist(args[0]))
                        {
                            cause = "The crate name to set the object to is invalid";
                            cratesWithID.put(s, Crate.getCrate(cc, args[0]));
                        }
                        else
                        {
                            cause = args[0] + " is not a valid Material";
                            Material m = DynamicMaterial.fromString(args[0].toUpperCase()).parseMaterial();
                            int byt = 0;

                            if (Utils.isInt(args[1]))
                            {
                                byt = Integer.parseInt(args[1]);
                            }

                            materialsWithID.put(s, new ItemStack(m, 1, (short) byt));
                        }
                    }
                    catch (Exception exc)
                    {
                        //exc.printStackTrace();
                        StatusLoggerEvent.MULTICRATEINVENTORY_OBJECTS_INVALID.log(getCrates(), new String[]{s, cause});
                    }
                }
            }
            catch (Exception exc)
            {
                StatusLoggerEvent.MULTICRATEINVENTORY_OBJECTS_MISCONFIGURED.log(getCrates());
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
                StatusLoggerEvent.MULTICRATEINVENTORY_ROW_MISCONFIGURED.log(getCrates());
            }

            return;
        }
        StatusLoggerEvent.MULTICRATEINVENTORY_NONEXISTENT.log(getCrates());
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
                ItemBuilder crateIb = new ItemBuilder(crate.getCs().getCrate(1));
                crateIb.addLore("");

                if (cc.getSettings().getConfigValAsBoolean("virtual-crate-cratecount"))
                {
                    crateIb.addLore(cc.getSettings().getConfigValues().get("virtual-crate-lore").toString()
                            .replaceAll("%crates%", vcd.getCrates() + ""));
                }
                if (cc.getSettings().getConfigValAsBoolean("virtual-crate-keycount"))
                {
                    crateIb.addLore(cc.getSettings().getConfigValues().get("virtual-key-lore").toString()
                            .replaceAll("%keys%", vcd.getKeys() + ""));
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

    @Override
    public void saveToFile()
    {
        if(ib == null || ib.getInv() == null)
            return;

        Inventory inv = ib.getInv();

        for (int i = 0; i < inv.getSize(); i++)
        {
            ItemStack stack;
            if (inv.getItem(i) == null)
            {
                stack = new ItemStack(Material.AIR);
            }
            else
            {
                stack = inv.getItem(i);
            }
            if (CrateUtils.searchByCrate(stack) == null)
            {
                boolean b = true;
                for (ItemStack alreadyExisting : materialsWithID.values())
                {
                    if (alreadyExisting.getType() == stack.getType() &&
                            alreadyExisting.getDurability() == stack.getDurability())
                    {
                        b = false;
                        break;
                    }
                }

                if (b)
                {
                    materialsWithID.put(getNextSymbol(), stack);
                }
            }
            else
            {
                Crate crate = CrateUtils.searchByCrate(stack);
                boolean b = true;
                for (Crate alreadyExisting : cratesWithID.values())
                {
                    if (alreadyExisting.equals(crate))
                    {
                        b = false;
                        break;
                    }
                }

                if (b)
                {
                    cratesWithID.put(getNextSymbol(), crate);
                }
            }
        }

        ArrayList<String> lines = new ArrayList<>();
        String line = "";

        for (int i = 0; i < inv.getSize(); i++)
        {
            ItemStack stack;
            if (inv.getItem(i) == null)
            {
                stack = new ItemStack(Material.AIR);
            }
            else
            {
                stack = inv.getItem(i);
            }

            if (CrateUtils.searchByCrate(stack) == null)
            {
                for (String s : materialsWithID.keySet())
                {
                    if (s.equalsIgnoreCase(""))
                    {
                        s = "-";
                    }
                    try
                    {
                        ItemStack s2 = materialsWithID.get(s);
                        if (s2.getType() == stack.getType() && s2.getDurability() == stack.getDurability())
                        {
                            line = line + s;
                            getFu().get().set("gui.objects." + s, s2.getType() + ";" + s2.getDurability());
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
                                        "Failed to save multicrate " + stack.getType().name() + " with value " + s);
                            }
                        }
                        exc.printStackTrace();
                    }
                }
            }
            else
            {
                Crate cs = CrateUtils.searchByCrate(stack);
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
                            getFu().get().set("gui.objects." + s, crate.getName());
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

        getFu().get().set("gui.rows", lines);
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

    public void checkClick(PlayerManager pm, int slot, ClickType clickType)
    {
        Player p = pm.getP();
        Crate crate = pm.getOpenCrate();

        if (crate.getCs().getCmci().getCrateSpots().keySet().contains(slot))
        {
            Crate clickedCrate = crate.getCs().getCmci().getCrateSpots().get(slot);

            if (clickType.equals(Boolean.valueOf(
                    cc.getSettings().getConfigValues().get("mc-reward-display-leftclick").toString().toUpperCase()) ?
                    ClickType.LEFT : ClickType.RIGHT) && (Boolean) SettingsValues.REWARD_DISPLAY_ENABLED.getValue(cc))
            {
                clickedCrate.getCs().getDisplayer().openFor(p);
            }
            else if (!((Boolean)SettingsValues.REQUIRE_VIRTUAL_CRATE_AND_KEY.getValue(cc))
                    || pm.getPdm().getVCCrateData(clickedCrate).getCrates() > 0)
            {
                if (!p.getGameMode().equals(GameMode.CREATIVE) ||
                        (Boolean) cc.getSettings().getConfigValues().get("open-creative"))
                {
                    CrateCooldownEvent cce = pm.getPdm().getCrateCooldownEventByCrates(clickedCrate);
                    if (cce == null || cce.isCooldownOverAsBoolean())
                    {
                        if(cc.getEconomyHandler().handleCheck(p, clickedCrate.getCs().getCost(), true))
                        {
                            if (clickedCrate.getCs().getCh().tick(p, pm.getLastOpenCrate(), CrateState.OPEN, false))
                            {
                                new CrateCooldownEvent(clickedCrate, System.currentTimeMillis(), true).addTo(pm.getPdm());
                                // Post Conditions
                                if (pm.isUseVirtualCrate())
                                {
                                    pm.getPdm().setVirtualCrateCrates(clickedCrate,
                                            pm.getPdm().getVCCrateData(clickedCrate).getCrates() - 1);
                                }
                                else if (!crates.getCs().getOt().equals(ObtainType.STATIC))
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
                                invCheck(p, pm);
                                cc.getEconomyHandler().failSoReturn(p, clickedCrate.getCs().getCost());
                            }
                        }
                        else
                        {
                            crate.getCs().getCh().playFailToOpen(p, false);
                            invCheck(p, pm);
                        }
                    }
                    else
                    {
                        invCheck(p, pm);
                        cce.playFailure(pm.getPdm());
                    }
                }
                else
                {
                    Messages.DENY_CREATIVE_MODE.msgSpecified(cc, p);
                }
            }
            else
            {
                Messages.INSUFFICIENT_VIRTUAL_CRATES.msgSpecified(pm.getCc(), p);
            }
        }
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

    public void setCrateSpots(HashMap<Integer, Crate> crateSpots)
    {
        this.crateSpots = crateSpots;
    }

    public HashMap<Integer, ItemStack> getItems()
    {
        return items;
    }

    public void setItems(HashMap<Integer, ItemStack> items)
    {
        this.items = items;
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
