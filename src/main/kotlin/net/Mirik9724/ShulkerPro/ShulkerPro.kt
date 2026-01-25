package net.Mirik9724.ShulkerPro

import net.Mirik9724.api.isAvailableNewVersion
import org.bukkit.plugin.java.JavaPlugin
import net.Mirik9724.api.bstats.bukkit.Metrics
import net.Mirik9724.api.copyFileFromJar
import net.Mirik9724.api.loadYmlFile
import net.Mirik9724.api.tryCreatePath
import net.Mirik9724.api.updateYmlFromJar
import java.io.File

lateinit var conf: Map<String,String>
class ShulkerPro : JavaPlugin() {
    private lateinit var shulkerOpen: ShulkerOpen

    override fun onEnable() {
        shulkerOpen = ShulkerOpen(this)
        server.pluginManager.registerEvents(shulkerOpen, this)

        tryCreatePath(File("plugins/ShulkerPro"))

        copyFileFromJar("config.yml", "plugins/ShulkerPro", this.javaClass.classLoader)
        updateYmlFromJar("config.yml", "plugins/ShulkerPro/config.yml", this.javaClass.classLoader)

        conf = loadYmlFile("plugins/ShulkerPro/config.yml")

        if(conf["useMetrics"] == "true") {
            Metrics(this, 28845)
        }
        if(conf["checkUpdates"] == "true") {
            if(isAvailableNewVersion("https://raw.githubusercontent.com/Mirik9724/ShulkerPro/refs/heads/master/V", this.description.version)){
                logger.info {"New version available"}
            }
        }

        logger.info("Plugin ON")
    }

    override fun onDisable() {
        shulkerOpen.clearOpenedShulkers()
        logger.info("Plugin OFF")
    }
}
