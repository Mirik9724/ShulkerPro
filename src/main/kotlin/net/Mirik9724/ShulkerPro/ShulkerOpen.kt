package net.Mirik9724.ShulkerPro

import org.bukkit.Bukkit
import org.bukkit.block.ShulkerBox
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent
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

        if (!player.isSneaking) return
        if (event.action != Action.RIGHT_CLICK_BLOCK && event.action != Action.RIGHT_CLICK_AIR) return

        val meta = item.itemMeta as? BlockStateMeta ?: return
        val shulkerState = meta.blockState as? ShulkerBox ?: return

        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            val clicked = event.clickedBlock ?: return
            val target = clicked.getRelative(event.blockFace)

            if (!target.type.isSolid) return
        }

        event.isCancelled = true

        val title = if (meta.hasDisplayName()) {
            meta.displayName
        } else {
            item.type.name.lowercase()
                .replace('_', ' ')
                .replaceFirstChar { it.uppercase() }
        }

        val size = shulkerState.inventory.size
        val inv = Bukkit.createInventory(null, size, title)
        inv.contents = shulkerState.inventory.contents

        val offHand = item == player.inventory.itemInOffHand
        openedShulkers[player.uniqueId] = OpenedShulker(item, offHand)

        player.openInventory(inv)
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val opened = openedShulkers[player.uniqueId] ?: return

        val cursor = event.cursor
        val current = event.currentItem

        if (cursor != null && cursor.isSimilar(opened.original)) {
            event.isCancelled = true
            return
        }

        if (current != null && current.isSimilar(opened.original)) {
            event.isCancelled = true
        }


        if(conf["shulkerInShulker"] == "true") { return }
        else{
            val item = cursor ?: current ?: return
            val meta = item.itemMeta as? BlockStateMeta ?: return

            if (meta.blockState is ShulkerBox) {
                event.isCancelled = true
            }
        }
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
