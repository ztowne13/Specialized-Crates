package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimation;
import me.ztowne13.customcrates.crates.types.animations.CrateType;
import me.ztowne13.customcrates.crates.types.animations.discover.DiscoverAnimation;
import me.ztowne13.customcrates.crates.types.animations.discover.DiscoverDataHolder;
import me.ztowne13.customcrates.crates.types.animations.menu.MenuAnimation;
import me.ztowne13.customcrates.interfaces.igc.crates.IGCMultiCrateMain;
import me.ztowne13.customcrates.interfaces.igc.crates.previeweditor.IGCCratePreviewEditor;
import me.ztowne13.customcrates.interfaces.igc.fileconfigs.rewards.IGCDragAndDrop;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

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
                if (playerManager.getOpenMenu() instanceof IGCCratePreviewEditor && e.getRawSlots().equals(e.getInventorySlots()))
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
        cc.getDu().log("onInventoryClick - CALL", getClass());
        Player p = (Player) e.getWhoClicked();
        PlayerManager pm = PlayerManager.get(cc, p);

        if (pm.isInCrate() || pm.isInRewardMenu())
        {
            if(e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT))
                e.setCancelled(true);

            cc.getDu().log("onInventoryClick - In crate or reward menu (" + pm.isInCrate() + " : " + pm.isInRewardMenu() +
                    ")", getClass());
            if (!(e.getClickedInventory() == null || e.getWhoClicked().getInventory() == null))
            {
                cc.getDu().log("onInventoryClick - Clicked inventory and clicker aren't null.");
                if (!e.getClickedInventory().equals(e.getWhoClicked().getInventory()))
                {
                    cc.getDu().log("onInventoryClick - CANCELLED");
                    e.setCancelled(true);

                    cc.getDu().log("onInventoryClick - inRewardMenu: " + pm.isInRewardMenu() + " lastPage: " +
                            pm.getLastPage());
                    if (pm.isInRewardMenu() && pm.getLastPage() != null)
                    {
                        pm.getLastPage().handleInput(p, e.getSlot());
                    }
                }
            }

            if (pm.isInCrate() && pm.getOpenCrate().isMultiCrate())
            {
                Crate crate = pm.getOpenCrate();
                int slot = e.getSlot();

                crate.getCs().getCmci().checkClick(pm, slot, e.getClick());
            }
            else if (pm.isInCrate())
            {
                if (pm.getOpenCrate().getCs().getCt().equals(CrateType.INV_DISCOVER) &&
                        DiscoverDataHolder.getHolders().containsKey(p))
                {
                    ((DiscoverAnimation) pm.getOpenCrate().getCs().getCh())
                            .handleClick(DiscoverDataHolder.getHolders().get(p), e.getSlot());
                }
            }
        }

        if (pm.isWaitingForClose())
        {
            e.setCancelled(true);
            pm.closeCrate();
            for (Reward r : pm.getWaitingForClose())
            {
                r.runCommands(p);
            }
            pm.setWaitingForClose(null);
            p.closeInventory();
        }

        if (pm.isInOpenMenu())
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
    public void onInventoryClose(InventoryCloseEvent e)
    {
        final Player p = (Player) e.getPlayer();
        final PlayerManager pm = PlayerManager.get(cc, p);

        pm.setInRewardMenu(false);
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
            else if (e.getView().getTitle().equalsIgnoreCase(ChatUtils.toChatColor("&c&lClose to save")))
            {
                ChatUtils.msgInfo(p, "There are unsaved changes, please remember to save.");

                Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        pm.getOpenMenu().open();
                    }
                }, 1);
            }
            else if(pm.getOpenMenu() instanceof IGCCratePreviewEditor)
            {
                ((IGCCratePreviewEditor) pm.getOpenMenu()).getCustomRewardDisplayer().saveAllPages();
                Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(pm.getOpenMenu() instanceof IGCCratePreviewEditor)
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
                            ChatUtils.msg(p, "&9&lNOTE: &bType &f'/sc !' &bto reopen to your last open config menu!");
                        }
                    }
                }, 1);
            }
        }

        if (pm.isWaitingForClose())
        {
            pm.closeCrate();
            for (Reward r : pm.getWaitingForClose())
            {
                r.runCommands(p);
            }
            pm.setWaitingForClose(null);
        }


        if (pm.isInCrate() || pm.isInRewardMenu())
        {
            CrateAnimation ch = pm.getOpenCrate().getCs().getCh();
            if (ch instanceof MenuAnimation)
            {
                ch.completeCrateRun(p);
                return;
            }
            else if (pm.getOpenCrate().isMultiCrate())
            {
                pm.closeCrate();
            }
        }

        // Prevents the player from opening the inventory
        if (!pm.isCanClose())
        {
            //e.getPlayer().openInventory(e.getView().getTopInventory());
            pm.setCanClose(true);

            try
            {
                e.getPlayer().openInventory(e.getView().getTopInventory());
            }
            catch (Exception exc)
            {

            }

            pm.setCanClose(false);
        }
    }
}
