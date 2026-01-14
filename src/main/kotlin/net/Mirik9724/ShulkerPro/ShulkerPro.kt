package net.Mirik9724.ShulkerPro

import net.Mirik9724.api.isAvailableNewVersion
import org.bukkit.plugin.java.JavaPlugin
import net.mirik9724.api.bstats.bukkit.Metrics

class ShulkerPro : JavaPlugin() {
    private lateinit var shulkerOpen: ShulkerOpen

    override fun onEnable() {
        shulkerOpen = ShulkerOpen(this)
        server.pluginManager.registerEvents(shulkerOpen, this)

        val metrics: Metrics = Metrics(this, 28845)
        if(isAvailableNewVersion("https://raw.githubusercontent.com/Mirik9724/ShulkerPro/refs/heads/master/V", this.description.version)){
            logger.info {"New version available"}
        }

        logger.info("Plugin ON")
    }

    override fun onDisable() {
        shulkerOpen.clearOpenedShulkers()
        logger.info("Plugin OFF")
    }
}
