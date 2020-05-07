package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.command.Command
import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommand
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.springframework.stereotype.Component

@Component
internal class CommandGamemode {
    @Command(value = "gamemode", aliases = ["gm"], description = "Set your game mode", usage = "/gamemode <creative|survival...>")
    fun gamemode(command: PluginCommand) {
        command.requiresPlayerSender { "Only players can run this command." }

        val gamemodes = mutableMapOf<String, GameMode>()
        GameMode.values().forEach {
            gamemodes[it.value.toString()] = it
            gamemodes[it.name.toLowerCase()] = it
        }

        command.withMap(gamemodes) { "$it is not a valid game mode" }
        command.then(::gamemodeExecutor)
        command.otherwise("Usage: /gamemode <game mode>")
    }

    @Command(value = "gmc", description = "Set your game mode to creative")
    fun gmc(sender: Player): String = gamemodeExecutor(sender, GameMode.CREATIVE)

    @Command(value = "gms", description = "Set your game mode to survival")
    fun gms(sender: Player): String = gamemodeExecutor(sender, GameMode.SURVIVAL)

    private fun gamemodeExecutor(sender: Player, gameMode: GameMode): String {
        sender.gameMode = gameMode
        return "Game mode set to ${gameMode.name.toLowerCase()}"
    }
}
