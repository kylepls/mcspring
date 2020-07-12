package `in`.kyle.mcspring.manager.controller

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.WatchKey
import java.nio.file.WatchService
import javax.annotation.PostConstruct

@Service
class PluginFileWatcher(
        private val pluginController: PluginController
) {

    lateinit var watcher: WatchService
    lateinit var key: WatchKey

    @PostConstruct
    fun setup() {
        startWatcher()
    }

    private fun startWatcher() {
        watcher = FileSystems.getDefault().newWatchService()
        key = pluginController.pluginsFolder.register(watcher, ENTRY_CREATE, ENTRY_MODIFY)
    }

    @Scheduled(fixedRate = 100)
    fun pollWatcher() {
        for (event in key.pollEvents()) {
            val context = event.context()
            require(context is Path) { "Context must be a path" }

            if (context.endsWith(".jar") && context == pluginController.pluginsFolder.resolve(context.fileName)) {
                if (pluginController.isPluginJar(context)) {
                    when (event.kind()) {
                        ENTRY_CREATE -> pluginController.load(context)
                        ENTRY_MODIFY -> pluginController.reload(context)
                    }
                }
            }
        }
    }
}
