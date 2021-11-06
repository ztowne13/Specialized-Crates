package me.ztowne13.customcrates.listeners

import org.bukkit.entity.Player
import org.bukkit.Material
import org.bukkit.Bukkit
import java.lang.Runnable
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import me.ztowne13.customcrates.SpecialisedCrates
import me.ztowne13.customcrates.crates.types.animations.CrateAnimation
import me.ztowne13.customcrates.interfaces.igc.crates.IGCMultiCrateMain
import me.ztowne13.customcrates.interfaces.igc.crates.previeweditor.IGCCratePreviewEditor
import me.ztowne13.customcrates.interfaces.igc.fileconfigs.rewards.IGCDragAndDrop
import me.ztowne13.customcrates.interfaces.items.ItemBuilder
import me.ztowne13.customcrates.players.PlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.Inventory
import java.lang.Exception

class InventoryActionListener(var cc: SpecialisedCrates) : Listener {
    @EventHandler
    fun onInventoryDrag(e: InventoryDragEvent) {
        cc.debugUtils!!.log("onInventoryDrag - CALL", javaClass)
        val player = e.whoClicked as Player
        val playerManager: PlayerManager = PlayerManager.Companion.get(cc, player)
        if (playerManager.isInCrate || playerManager.isInRewardMenu) {
            cc.debugUtils!!.log("onInventoryDrag - CANCELLED")
            e.isCancelled = true
        } else if (playerManager.isInOpenMenu) {
            if (e.view.topInventory != null) {
                if (playerManager.openMenu is IGCCratePreviewEditor && e.rawSlots == e.inventorySlots) {
                    try {
                        for (slot in e.inventorySlots) (playerManager.openMenu as IGCCratePreviewEditor)
                            .manageClick(slot, true, e.newItems.values.iterator().next())
                        e.isCancelled = true
                    } catch (exc: Exception) {
                        exc.printStackTrace()
                    }
                }
            }
        }
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val p = e.whoClicked as Player
        val pm: PlayerManager = PlayerManager.Companion.get(cc, p)
        val slot = e.slot
        if (pm.isInCrate || pm.isInRewardMenu) {
            e.isCancelled = true
            if (isntPlayerInventory(e, pm)) {
                if (pm.isInRewardMenu && pm.lastPage != null) {
                    pm.lastPage!!.handleInput(p, e.slot)
                }

                // Handle multicrate click
                if (pm.isInCrate && pm.openCrate!!.isMultiCrate) {
                    val crate = pm.openCrate
                    crate!!.settings.multiCrateSettings!!.checkClick(pm, slot, e.click)
                    e.isCancelled = true
                } else if (pm.isInCrate) {
                    pm.openCrate!!.settings.crateAnimation!!
                        .handleClick(pm.currentAnimation, slot)
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
    fun onAnvilClick(e: InventoryClickEvent) {
        if (e.inventory.type == InventoryType.ANVIL) {
            if (e.inventory.getItem(2) != null) {
                val builder = ItemBuilder(e.inventory.getItem(2))
                if (builder.hasDisplayName()) {
                    if (searchByCrate(builder.stack) != null || CrateUtils.searchByKey(builder.stack) != null) {
                        e.isCancelled = true
                        e.whoClicked.closeInventory()
                        Messages.CANT_CRAFT_KEYS.msgSpecified(cc, e.whoClicked as Player)
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
    fun onIGCClick(e: InventoryClickEvent) {
        val p = e.whoClicked as Player
        val pm: PlayerManager = PlayerManager.Companion.get(cc, p)
        if ((p.hasPermission("me.ztowne13.customcrates.admin") || p.hasPermission("specializedcrates.admin")) && pm.isInOpenMenu) {
            if (!(e.clickedInventory == null || e.view.topInventory == null)) {
                if (e.clickedInventory == e.view.topInventory &&
                    !e.view.title.equals(ChatUtils.toChatColor("&c&lClose to save"), ignoreCase = true)
                ) {
                    if (pm.openMenu !is IGCDragAndDrop || e.slot == 52 || e.slot == 53) e.isCancelled = true
                    try {
                        pm.openMenu!!.manageClick(e.slot)
                    } catch (exc: Exception) {
                        exc.printStackTrace()
                    }
                }
            }
        }
    }

    @EventHandler
    fun onCratesClaimClick(e: InventoryClickEvent) {
        if (e.whoClicked is Player) {
            val player = e.whoClicked as Player
            val playerManager: PlayerManager = PlayerManager.Companion.get(
                cc, player
            )
            if (playerManager.isInCratesClaimMenu) {
                if (!(SettingsValue.CRATES_CLAIM_ALLOW_DEPOSIT.getValue(cc) as Boolean)) {
                    if (e.clickedInventory !== player.inventory && player.itemOnCursor != null && player.itemOnCursor.type != Material.AIR
                        || e.click != ClickType.LEFT && e.click != ClickType.RIGHT
                    ) {
                        e.isCancelled = true
                        Messages.CRATES_CLAIM_DENY_DEPOSIT_KEYS.msgSpecified(cc, player)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onCrateAnimationESCKey(e: InventoryCloseEvent) {
        val player = e.player as Player
        val pm: PlayerManager = PlayerManager.Companion.get(cc, player)
        if (pm.isInCrateAnimation) {
            val dataHolder = pm.currentAnimation
            dataHolder.crateAnimation.handleKeyPress(dataHolder, CrateAnimation.KeyType.ESC)
        }
    }

    @EventHandler
    fun onCratesClaimClose(e: InventoryCloseEvent) {
        if (e.player is Player) {
            val player = e.player as Player
            val playerManager: PlayerManager = PlayerManager.Companion.get(
                cc, player
            )
            val dataManager = playerManager.playerDataManager
            if (playerManager.isInCratesClaimMenu) {
                preventDupeReopen(player, e.inventory, false)
                playerManager.isInCratesClaimMenu = false
                for (stack in e.inventory.contents) {
                    if (stack == null || stack.type == Material.AIR) {
                        continue
                    }
                    val result = CrateUtils.searchByKey(stack)
                    if (result != null) {
                        dataManager!!.setVirtualCrateKeys(
                            result,
                            dataManager.getVCCrateData(result)!!.keys + stack.amount
                        )
                    } else {
                        Utils.addItemAndDropRest(player, stack)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val p = e.player as Player
        val pm: PlayerManager = PlayerManager.Companion.get(cc, p)
        pm.lastPage = null
        if (pm.isInOpenMenu && !pm.openMenu!!.isInInputMenu) {
            if (e.view.title.equals(ChatUtils.toChatColor("&7&l> &6&lClose to save"), ignoreCase = true)) {
                pm.openMenu!!.manageClick(-1)
                ChatUtils.msgSuccess(
                    p,
                    "Successfully saved all rewards. Please go through and update all of their commands as well as their chance values."
                )
                Bukkit.getScheduler().scheduleSyncDelayedTask(cc, { pm.openMenu!!.up() }, 1)
            } else if (e.view.title.equals(ChatUtils.toChatColor("&c&lClose to save"), ignoreCase = true)) {
                ChatUtils.msgInfo(p, "There are unsaved changes, please remember to save.")
                val openMenu = pm.openMenu as IGCMultiCrateMain
                openMenu.crates!!.settings.multiCrateSettings!!.updateCrateSpots()
                Bukkit.getScheduler().scheduleSyncDelayedTask(cc, { pm.openMenu!!.open() }, 1)
            } else if (pm.openMenu is IGCCratePreviewEditor) {
                (pm.openMenu as IGCCratePreviewEditor).customRewardDisplayer!!
                    .saveAllPages()
                Bukkit.getScheduler()
                    .scheduleSyncDelayedTask(cc, { if (pm.openMenu is IGCCratePreviewEditor) pm.openMenu.up() }, 1)
            } else if (pm.openMenu !is IGCMultiCrateMain) {
                pm.openMenu = null
                Bukkit.getScheduler().scheduleSyncDelayedTask(cc, {
                    if (!pm.isInOpenMenu) {
                        ChatUtils.msg(p, "&9&lNOTE: &bType &f'/scrates !' &bto reopen to your last config menu!")
                    }
                }, 1)
            }
        }

        // Close the multicrate
        if (pm.isInCrate && !pm.isInRewardMenu) {
            if (pm.openCrate != null && pm.openCrate!!.isMultiCrate) {
                pm.closeCrate()
                preventDupeReopen(p, e.inventory, true)
            }
        }

        // Handle closing an inventory while the reward preview menu is open
        if (pm.isInRewardMenu) {
            val cachedOpenCrate = pm.openCrate
            val cachedLastOpenedPlacedCrate = pm.lastOpenedPlacedCrate
            if (pm.nextPageInventoryCloseGrace <= cc.totalTicks) {
                // This is to reopen a multicrate if the reward preview menu was closed
                if (pm.isInCrate && pm.openCrate!!.isMultiCrate) {
                    pm.isInRewardMenu = false
                    Bukkit.getScheduler().runTaskLater(cc, Runnable {
                        cachedOpenCrate!!.settings.multiCrateSettings!!.openFor(p, cachedLastOpenedPlacedCrate)
                    }, 1)
                } else {
                    pm.isInRewardMenu = false
                    preventDupeReopen(p, e.inventory, false)
                }
            }
        }
    }

    fun preventDupeReopen(player: Player, inventory: Inventory, checkInRewardMenu: Boolean) {
        Bukkit.getScheduler().runTaskLater(cc, Runnable {
            if (checkInRewardMenu) {
                if (PlayerManager.Companion.get(cc, player)!!.isInRewardMenu()) {
                    return@runTaskLater
                }
            }
            if (inventory.type == InventoryType.CRAFTING) {
                return@runTaskLater
            }
            player.openInventory(inventory)
            player.closeInventory()
        }, 1)
    }

    fun isntPlayerInventory(e: InventoryClickEvent, pm: PlayerManager?): Boolean {
        cc.debugUtils!!.log(
            "onInventoryClick - In crate or reward menu (" + pm!!.isInCrate + " : " + pm.isInRewardMenu +
                    ")", javaClass
        )
        if (!(e.clickedInventory == null || e.whoClicked.inventory == null)) {
            cc.debugUtils!!.log("onInventoryClick - Clicked inventory and clicker aren't null.")
            return e.clickedInventory != e.whoClicked.inventory
        }
        return false
    }
}