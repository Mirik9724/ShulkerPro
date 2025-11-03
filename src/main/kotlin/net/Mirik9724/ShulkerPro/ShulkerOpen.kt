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
import java.util.*

class ShulkerOpen : Listener {
    private val openedShulkers: MutableMap<UUID, OpenedShulker> = HashMap()

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return

        val player: Player = event.player
        if (!player.isSneaking) return

        val item = event.item ?: return
        val meta = item.itemMeta as? BlockStateMeta ?: return
        val shulkerState = meta.blockState as? ShulkerBox ?: return

        event.isCancelled = true

        // Используем имя предмета в GUI, если оно есть
        val displayName = if (item.hasItemMeta() && item.itemMeta!!.hasDisplayName()) {
            item.itemMeta!!.displayName
        } else {
            item.type.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
        }

        val inventory: Inventory = Bukkit.createInventory(null, 27, displayName)
        inventory.contents = shulkerState.inventory.contents

        val offHand = item == player.inventory.itemInOffHand
        openedShulkers[player.uniqueId] = OpenedShulker(item.clone(), offHand)

        player.openInventory(inventory)
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
