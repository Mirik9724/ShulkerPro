package net.Mirik9724.ShulkerPro

import org.bukkit.plugin.java.JavaPlugin

class ShulkerPro : JavaPlugin() {
    private lateinit var shulkerOpen: ShulkerOpen

    override fun onEnable() {
        shulkerOpen = ShulkerOpen(this)
        server.pluginManager.registerEvents(shulkerOpen, this)
        logger.info("Plugin ON.")
    }

    override fun onDisable() {
        shulkerOpen.clearOpenedShulkers()
        logger.info("Plugin OFF.")
    }
}
