package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.commands.dsl.command
import `in`.kyle.mcspring.commands.dsl.commandExecutor
import `in`.kyle.mcspring.commands.dsl.mcspring.Command
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.springframework.stereotype.Component
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
@Component
internal class CommandGamemode {

    @Command(
            value = "gamemode",
            aliases = ["gm"],
            description = "Set your game mode",
            usage = "/gamemode <creative|survival...>"
    )
    fun gamemode() = command {
        requirePlayer { message("Only players can run this command.") }

        val gameMode = mapArg<GameMode> {
            parser {
                map(GameMode.values().associateBy { it.name.toLowerCase() })
                map(GameMode.values().associateBy { it.value.toString() })
            }
            missing { message("Usage: /$label <game mode>") }
            invalid { message("Invalid game mode $it") }
        }

        then { message(gamemodeExecutor(sender as Player, gameMode)) }
    }

    @Command(value = "gmc", description = "Set your game mode to creative")
    fun gmc() = command { then { message(gamemodeExecutor(sender as Player, GameMode.CREATIVE)) } }

    @Command(value = "gms", description = "Set your game mode to survival")
    fun gms() = command { then { message(gamemodeExecutor(sender as Player, GameMode.SURVIVAL)) } }

    private fun gamemodeExecutor(target: Player, gameMode: GameMode): String {
        target.gameMode = gameMode
        return "Game mode set to ${gameMode.name.toLowerCase()}"
    }
}
