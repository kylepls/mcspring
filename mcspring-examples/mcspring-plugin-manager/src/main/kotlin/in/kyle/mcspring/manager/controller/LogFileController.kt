package `in`.kyle.mcspring.manager.controller

import org.bukkit.plugin.Plugin
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import java.nio.file.Files
import java.nio.file.Paths
import javax.annotation.PostConstruct

@Controller
internal class LogFileController {

    private val logsFolder = Paths.get("logs")

    @PostConstruct
    fun setup() {
        Files.list(logsFolder)
                .filter { it.toString().endsWith(".log.gz") }
                .forEach { Files.delete(it) }
    }
}
