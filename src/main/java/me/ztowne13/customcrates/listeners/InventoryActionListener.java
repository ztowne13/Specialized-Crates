package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.types.animations.AnimationDataHolder;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimation;
import me.ztowne13.customcrates.interfaces.igc.crates.IGCMultiCrateMain;
import me.ztowne13.customcrates.interfaces.igc.crates.previeweditor.IGCCratePreviewEditor;
import me.ztowne13.customcrates.interfaces.igc.fileconfigs.rewards.IGCDragAndDrop;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.CrateUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryActionListener implements Listener
{
    SpecializedCrates cc;

    public InventoryActionListener(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e)
    {
        cc.getDu().log("onInventoryDrag - CALL", getClass());

        Player player = (Player) e.getWhoClicked();
        PlayerManager playerManager = PlayerManager.get(cc, player);

        if (playerManager.isInCrate() || playerManager.isInRewardMenu())
        {
            cc.getDu().log("onInventoryDrag - CANCELLED");
            e.setCancelled(true);
        }
        else if (playerManager.isInOpenMenu())
        {
            if (e.getView().getTopInventory() != null)
            {
                if (playerManager.getOpenMenu() instanceof IGCCratePreviewEditor &&
                        e.getRawSlots().equals(e.getInventorySlots()))
                {
                    try
                    {
                        for (int slot : e.getInventorySlots())
                            ((IGCCratePreviewEditor) playerManager.getOpenMenu())
                                    .manageClick(slot, true, e.getNewItems().values().iterator().next());
                        e.setCancelled(true);
                    }
                    catch (Exception exc)
                    {
                        exc.printStackTrace();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        Player p = (Player) e.getWhoClicked();
        PlayerManager pm = PlayerManager.get(cc, p);

        int slot = e.getSlot();

        if (pm.isInCrate() || pm.isInRewardMenu())
        {
            e.setCancelled(true);

            if (isntPlayerInventory(e, pm))
            {

                if (pm.isInRewardMenu() && pm.getLastPage() != null)
                {
                    pm.getLastPage().handleInput(p, e.getSlot());
                }

                // Handle multicrate click
                if (pm.isInCrate() && pm.getOpenCrate().isMultiCrate())
                {
                    Crate crate = pm.getOpenCrate();
                    crate.getSettings().getMultiCrateSettings().checkClick(pm, slot, e.getClick());
                    e.setCancelled(true);
                }
                // Handle discover animation click
                else if (pm.isInCrate())
                {
                    pm.getOpenCrate().getSettings().getAnimation().handleClick(pm.getCurrentAnimation(), slot);
                }
            }
        }

        /*if (pm.isWaitingForClose())
        {
            e.setCancelled(true);
            pm.closeCrate();
            for (Reward r : pm.getWaitingForClose())
            {
                r.runCommands(p);
            }
            pm.setWaitingForClose(null);
            p.closeInventory();
        }*/
    }

    /**
     * Handles anvil crafting - mainly to prevent key/crate renaming.
     *
     * @param e The event passed by the server
     */
    @EventHandler
    public void onAnvilClick(InventoryClickEvent e)
    {
        if (e.getInventory().getType().equals(InventoryType.ANVIL))
        {
            if (e.getInventory().getItem(2) != null)
            {
                ItemBuilder builder = new ItemBuilder(e.getInventory().getItem(2));
                if (builder.hasDisplayName())
                {
                    if (CrateUtils.searchByCrate(builder.get()) != null || CrateUtils.searchByKey(builder.get()) != null)
                    {
                        e.setCancelled(true);
                        e.getWhoClicked().closeInventory();
                        Messages.CANT_CRAFT_KEYS.msgSpecified(cc, (Player) e.getWhoClicked());
                    }
                }
            }
        }
    }

    /**
     * Handles inventory clicks intended in the in-game config inventory editors.
     *
     * @param e The event passed by the server
     */
    @EventHandler
    public void onIGCClick(InventoryClickEvent e)
    {
        Player p = (Player) e.getWhoClicked();
        PlayerManager pm = PlayerManager.get(cc, p);

        if ((p.hasPermission("customcrates.admin") || p.hasPermission("specializedcrates.admin")) && pm.isInOpenMenu())
        {
            if (!(e.getClickedInventory() == null || e.getView().getTopInventory() == null))
            {
                if (e.getClickedInventory().equals(e.getView().getTopInventory()) &&
                        !e.getView().getTitle().equalsIgnoreCase(ChatUtils.toChatColor("&c&lClose to save")))
                {
                    if (!(pm.getOpenMenu() instanceof IGCDragAndDrop) || e.getSlot() == 52 || e.getSlot() == 53)
                        e.setCancelled(true);

                    try
                    {
                        pm.getOpenMenu().manageClick(e.getSlot());
                    }
                    catch (Exception exc)
                    {
                        exc.printStackTrace();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCratesClaimClick(InventoryClickEvent e)
    {
        if(e.getWhoClicked() instanceof Player)
        {
            Player player = (Player) e.getWhoClicked();
            PlayerManager playerManager = PlayerManager.get(cc, player);

            if(playerManager.isInCratesClaimMenu())
            {
                if(!((boolean) SettingsValue.CRATES_CLAIM_ALLOW_DEPOSIT.getValue(cc)))
                {
                    if(e.getClickedInventory() == player.getInventory() ||
                            (!e.getClick().equals(ClickType.LEFT) && !e.getClick().equals(ClickType.RIGHT)))
                    {
                        e.setCancelled(true);
                        Messages.CRATES_CLAIM_DENY_DEPOSIT_KEYS.msgSpecified(cc, player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCrateAnimationESCKey(InventoryCloseEvent e)
    {
        Player player = (Player) e.getPlayer();
        PlayerManager pm = PlayerManager.get(cc, player);

        if (pm.isInCrateAnimation())
        {
            AnimationDataHolder dataHolder = pm.getCurrentAnimation();
            dataHolder.getCrateAnimation().handleKeyPress(dataHolder, CrateAnimation.KeyType.ESC);
        }
    }

    @EventHandler
    public void onCratesClaimClose(InventoryCloseEvent e)
    {
        if(e.getPlayer() instanceof Player)
        {
            Player player = (Player) e.getPlayer();
            PlayerManager playerManager = PlayerManager.get(cc, player);
            PlayerDataManager dataManager = playerManager.getPdm();

            if(playerManager.isInCratesClaimMenu())
            {
                playerManager.setInCratesClaimMenu(false);
                for(ItemStack stack : e.getInventory().getContents())
                {
                    if(stack == null || stack.getType() == Material.AIR)
                    {
                        continue;
                    }

                    Crate result = CrateUtils.searchByKey(stack);
                    if(result != null)
                    {
                        dataManager.setVirtualCrateKeys(result, dataManager.getVCCrateData(result).getKeys() + stack.getAmount());
                    }
                    else
                    {
                        Utils.addItemAndDropRest(player, stack);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e)
    {
        final Player p = (Player) e.getPlayer();
        final PlayerManager pm = PlayerManager.get(cc, p);

        pm.setLastPage(null);

        if (pm.isInOpenMenu() && !pm.getOpenMenu().isInInputMenu())
        {

            if (e.getView().getTitle().equalsIgnoreCase(ChatUtils.toChatColor("&7&l> &6&lClose to save")))
            {

                pm.getOpenMenu().manageClick(-1);
                ChatUtils.msgSuccess(p,
                        "Successfully saved all rewards. Please go through and update all of their commands as well as their chance values.");
                Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        pm.getOpenMenu().up();
                    }
                }, 1);
            }
            // Closing a multicrate
            else if (e.getView().getTitle().equalsIgnoreCase(ChatUtils.toChatColor("&c&lClose to save")))
            {
                ChatUtils.msgInfo(p, "There are unsaved changes, please remember to save.");
                IGCMultiCrateMain openMenu = (IGCMultiCrateMain) pm.getOpenMenu();
                openMenu.getCrates().getSettings().getMultiCrateSettings().updateCrateSpots();
                Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        pm.getOpenMenu().open();
                    }
                }, 1);
            }
            else if (pm.getOpenMenu() instanceof IGCCratePreviewEditor)
            {
                ((IGCCratePreviewEditor) pm.getOpenMenu()).getCustomRewardDisplayer().saveAllPages();
                Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (pm.getOpenMenu() instanceof IGCCratePreviewEditor)
                            pm.getOpenMenu().up();
                    }
                }, 1);
            }
            else if (!(pm.getOpenMenu() instanceof IGCMultiCrateMain))
            {
                pm.setOpenMenu(null);
                Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (!pm.isInOpenMenu())
                        {
                            ChatUtils.msg(p, "&9&lNOTE: &bType &f'/scrates !' &bto reopen to your last config menu!");
                        }
                    }
                }, 1);
            }
        }

        // Close the multicrate
        if (pm.isInCrate() && !pm.isInRewardMenu())
        {
            if (pm.getOpenCrate() != null && pm.getOpenCrate().isMultiCrate())
            {
                pm.closeCrate();
                preventDupeReopen(p, e.getInventory(), true);
            }
        }

        // Handle closing an inventory while the reward preview menu is open
        if (pm.isInRewardMenu())
        {
            final Crate cachedOpenCrate = pm.getOpenCrate();
            final PlacedCrate cachedLastOpenedPlacedCrate = pm.getLastOpenedPlacedCrate();
            if (pm.getNextPageInventoryCloseGrace() <= cc.getTotalTicks())
            {
                // This is to reopen a multicrate if the reward preview menu was closed
                if (pm.isInCrate() && pm.getOpenCrate().isMultiCrate())
                {
                    pm.setInRewardMenu(false);
                    Bukkit.getScheduler().runTaskLater(cc, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            cachedOpenCrate.getSettings().getMultiCrateSettings().openFor(p, cachedLastOpenedPlacedCrate);
                        }
                    }, 1);
                }
                // This is to prevent duplication in the reward menu - it reopens the inventory very briefly and re-closes it.
                else
                {
                    pm.setInRewardMenu(false);
                    preventDupeReopen(p, e.getInventory(), false);
                }
            }
        }
    }

    public void preventDupeReopen(final Player player, final Inventory inventory, final boolean checkInRewardMenu)
    {
        Bukkit.getScheduler().runTaskLater(cc, new Runnable()
        {
            @Override
            public void run()
            {
                if(checkInRewardMenu)
                {
                    if(PlayerManager.get(cc, player).isInRewardMenu())
                    {
                        return;
                    }
                }

                if (inventory.getType().equals(InventoryType.CRAFTING))
                {
                    return;
                }

                player.openInventory(inventory);
                player.closeInventory();
            }
        }, 1);
    }

    public boolean isntPlayerInventory(InventoryClickEvent e, PlayerManager pm)
    {
        cc.getDu().log("onInventoryClick - In crate or reward menu (" + pm.isInCrate() + " : " + pm.isInRewardMenu() +
                ")", getClass());
        if (!(e.getClickedInventory() == null || e.getWhoClicked().getInventory() == null))
        {
            cc.getDu().log("onInventoryClick - Clicked inventory and clicker aren't null.");
            if (!e.getClickedInventory().equals(e.getWhoClicked().getInventory()))
            {
                return true;
            }
        }
        return false;
    }
}

