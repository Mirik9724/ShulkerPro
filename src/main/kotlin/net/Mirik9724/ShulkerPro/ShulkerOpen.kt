package net.Mirik9724.ShulkerPro

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.ShulkerBox
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
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

    fun isShulker(item: ItemStack?): Boolean {
        if (item == null) return false
        return when (item.type) {
            Material.SHULKER_BOX,
            Material.WHITE_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX,
            Material.MAGENTA_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX,
            Material.YELLOW_SHULKER_BOX,
            Material.LIME_SHULKER_BOX,
            Material.PINK_SHULKER_BOX,
            Material.GRAY_SHULKER_BOX,
            Material.LIGHT_GRAY_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX,
            Material.PURPLE_SHULKER_BOX,
            Material.BLUE_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX,
            Material.GREEN_SHULKER_BOX,
            Material.RED_SHULKER_BOX,
            Material.BLACK_SHULKER_BOX -> true
            else -> false
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val opened = openedShulkers[player.uniqueId] ?: return

        val cursor = event.cursor
        val current = event.currentItem

        if ((cursor?.isSimilar(opened.original) == true) || (current?.isSimilar(opened.original) == true)) {
            event.isCancelled = true
            return
        }


        if (conf["shulkerInShulker"] == "true") {
        }
        else{
            val clickedInv = event.clickedInventory ?: return
            val topInv = event.view.topInventory

            if (clickedInv == topInv) {
                if (isShulker(cursor)) {
                    event.isCancelled = true
                    return
                }
            }

            if (clickedInv != topInv && event.isShiftClick) {
                if (isShulker(current)) {
                    event.isCancelled = true
                    return
                }
            }

            if (event.click == ClickType.NUMBER_KEY && clickedInv == topInv) {
                val hotbarItem = player.inventory.getItem(event.hotbarButton)
                if (isShulker(hotbarItem)) {
                    event.isCancelled = true
                    return
                }
            }
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return

        val openedShulker = openedShulkers.remove(player.uniqueId) ?: return

        val inventoryContents = player.inventory.contents
        val shulkerItem = inventoryContents.find { it != null && it.isSimilar(openedShulker.original) }

        if (shulkerItem == null) {
//            player.sendMessage("§cОшибка: Шалкер не найден в инвентаре. Изменения не сохранены!")
            return
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
