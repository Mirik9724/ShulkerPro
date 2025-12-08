package net.Mirik9724.ShulkerPro

import org.bukkit.Bukkit
import org.bukkit.block.ShulkerBox
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class ShulkerOpen(private var pluginInstance: JavaPlugin) : Listener {
    private val openedShulkers: MutableMap<UUID, OpenedShulker> = HashMap()

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
        val meta = item.itemMeta as? BlockStateMeta ?: return
        val shulkerState = meta.blockState as? ShulkerBox ?: return

        if (!player.isSneaking) return
        if (event.action != Action.RIGHT_CLICK_BLOCK && event.action != Action.RIGHT_CLICK_AIR) return

        // Отменяем стандартное открытие шёлкера, чтобы не конфликтовало с GUI
        event.isCancelled = true

        // Попытка поставить блок через ванильную логику
        // Используем runTaskLater, чтобы дать Minecraft шанс поставить блок
        Bukkit.getScheduler().runTaskLater(pluginInstance, Runnable {
            val handItem = if (item == player.inventory.itemInMainHand) player.inventory.itemInMainHand else player.inventory.itemInOffHand
            val handMeta = handItem.itemMeta as? BlockStateMeta ?: return@Runnable
            val handShulker = handMeta.blockState as? ShulkerBox ?: return@Runnable

            // Если предмет остался на месте — открыть GUI
            val displayName = if (handItem.hasItemMeta() && handItem.itemMeta!!.hasDisplayName()) {
                handItem.itemMeta!!.displayName
            } else {
                handItem.type.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
            }

            val inventory: Inventory = Bukkit.createInventory(null, 27, displayName)
            inventory.contents = handShulker.inventory.contents

            val offHand = handItem == player.inventory.itemInOffHand
            openedShulkers[player.uniqueId] = OpenedShulker(handItem.clone(), offHand)

            player.openInventory(inventory)
        }, 1L)
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return

        val openedShulker = openedShulkers.remove(player.uniqueId) ?: return

        val shulkerItem = if (openedShulker.offHand) {
            player.inventory.itemInOffHand
        } else {
            player.inventory.itemInMainHand
        }

        val meta = shulkerItem.itemMeta as? BlockStateMeta ?: return
        val shulkerState = meta.blockState as? ShulkerBox ?: return

        shulkerState.inventory.contents = event.inventory.contents
        meta.blockState = shulkerState
        shulkerItem.itemMeta = meta
    }

    fun clearOpenedShulkers() {
        openedShulkers.clear()
    }

    private data class OpenedShulker(val original: ItemStack, val offHand: Boolean)
}
