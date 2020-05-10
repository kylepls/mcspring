package `in`.kyle.mcspring.subcommands.plugincommand

import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommandBase.State
import `in`.kyle.mcspring.subcommands.plugincommand.api.Err1
import `in`.kyle.mcspring.subcommands.plugincommand.api.PluginCommand
import org.bukkit.Bukkit

interface PluginCommandWith : PluginCommandBase, PluginCommand {

    override fun withString() = with({ it }, stageName = "string")

    override fun withSentence() {
        addCompletionStage("sentence", "with")
        if (nextPart() != null) {
            injections.add(parts.joinToString())
            parts.clear()
        }
    }

    override fun withInt(errorMessage: Err1) = with({ it.toIntOrNull() }, errorMessage, "int")

    override fun withDouble(errorMessage: Err1) = with({ it.toDoubleOrNull() },
            errorMessage, "double")

    override fun withOfflinePlayer(errorMessage: Err1) {
        with({ Bukkit.getOfflinePlayer(it) }, errorMessage, "offline player")
    }

    override fun withPlayer(errorMessage: Err1) {
        withMap(Bukkit.getOnlinePlayers().associateBy { it.name }, errorMessage, "player")
    }

    override fun withWorld(errorMessage: Err1) {
        withMap(Bukkit.getWorlds().associateBy { it.name }, errorMessage, "world")
    }

    override fun withXYZInt(errorMessage: Err1) = repeat(3) { withInt(errorMessage) }

    override fun <T> withMap(options: Map<String, T>, errorMessage: Err1, stageName: String) {
        with({ options[it] }, errorMessage, stageName)
        options.keys.forEach { addCompletion(it, "with") }
    }

    override fun withAny(options: Collection<String>, errorMessage: Err1, stageName: String) {
        withMap(options.associateBy { it }, errorMessage, stageName)
    }

    override fun with(processor: (String) -> Any?, errorMessage: Err1, stageName: String) {
        addCompletionStage(stageName, "with")
        val part = nextPart()
        if (part != null) {
            val parsed = processor(part)
            if (parsed != null) {
                parts.removeAt(0)
                injections.add(parsed)
            } else {
                dirtiesState { sendMessage(errorMessage(part)) }
            }
        } else {
            dirtiesState(resultingState = State.MISSING_ARG) {}
        }
    }
}
