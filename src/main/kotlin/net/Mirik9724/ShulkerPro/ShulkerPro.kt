package net.Mirik9724.ShulkerPro

import org.bukkit.plugin.java.JavaPlugin
import net.mirik9724.api.bstats.bukkit.Metrics

class ShulkerPro : JavaPlugin() {
    private lateinit var shulkerOpen: ShulkerOpen

    override fun onEnable() {
        shulkerOpen = ShulkerOpen(this)
        server.pluginManager.registerEvents(shulkerOpen, this)

        val metrics: Metrics = Metrics(this, 28845)
        logger.info("Plugin ON")
    }

    override fun onDisable() {
        shulkerOpen.clearOpenedShulkers()
        logger.info("Plugin OFF")
    }
}
