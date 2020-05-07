package `in`.kyle.mcspring.subcommands.plugincommand

import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommandBase.State
import org.bukkit.Bukkit

interface PluginCommandWith : PluginCommandBase {

    fun withString() = with({ it })

    fun withSentence() {
        if (nextPart() != null) {
            val out = injections.add(parts.joinToString())
            parts.clear()
        }
    }

    fun withInt(errorMessage: (String) -> String) = with({ it.toIntOrNull() }, errorMessage)

    fun withDouble(errorMessage: (String) -> String) = with({ it.toDoubleOrNull() }, errorMessage)

    fun withOfflinePlayer(errorMessage: (String) -> String) {
        with({ Bukkit.getOfflinePlayer(it) }, errorMessage)
    }

    fun withPlayer(errorMessage: (String) -> String) {
        with({ Bukkit.getPlayer(it) }, errorMessage)
    }

    fun withWorld(errorMessage: (String) -> String) {
        with({ worldName: String ->
            Bukkit.getWorlds().find { it.name === worldName }
        }, errorMessage)
    }

    fun withXYZInt(errorMessage: (String) -> String) {
        repeat(3) { withInt(errorMessage) }
    }

    fun <T> withMap(options: Map<String, T>, errorMessage: (String) -> String) {
        with({ options[it] }, errorMessage)
    }

    fun withAny(options: Collection<String>, errorMessage: (String) -> String) {
        withMap(options.associateBy { it }, errorMessage)
    }

    fun with(processor: (String) -> Any?, errorMessage: (String) -> String = { "" }) {
        val part = nextPart()
        if (part != null) {
            val parsed = processor(part)
            if (parsed != null) {
                consumePart()
                injections.add(parsed)
            } else {
                dirtiesState { sendMessage(errorMessage(part)) }
            }
        } else {
            dirtiesState(resultingState = State.MISSING_ARG) {}
        }
    }
}
