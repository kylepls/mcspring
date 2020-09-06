package `in`.kyle.mcspring.chat

import `in`.kyle.mcspring.chat.CommandSenderSupport.sendMessage
import `in`.kyle.mcspring.chat.StringSupport.toTextComponent
import `in`.kyle.mcspring.chat.TextComponentSupport.color
import `in`.kyle.mcspring.chat.TextComponentSupport.command
import `in`.kyle.mcspring.chat.TextComponentSupport.hover
import `in`.kyle.mcspring.chat.TextComponentSupport.suggest
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PlayerSupport {
    fun Player.sendMessage(vararg components: Any) = (this as CommandSender).sendMessage(*components)
    fun Player.sendMessage(vararg components: TextComponent) = (this as CommandSender).sendMessage(*components)
    fun Player.sendMessage(lambda: () -> TextComponent) = (this as CommandSender).sendMessage(lambda)
}

object CommandSenderSupport {
    fun CommandSender.sendMessage(vararg components: Any) {
        sendMessage(*components.map(::convertComponent).toTypedArray())
    }

    private fun convertComponent(component: Any) = when (component) {
        is TextComponent -> component
        is String -> component.toTextComponent()
        else -> error("Invalid component for message: ${component::class.simpleName}:$component. Only strings and TextComponents are supported")
    }

    fun CommandSender.sendMessage(vararg components: TextComponent) {
        spigot().sendMessage(*components)
    }

    fun CommandSender.sendMessage(lambda: () -> TextComponent) {
        spigot().sendMessage(lambda())
    }
}

object StringSupport {

    fun String.toTextComponent(): TextComponent {
        return TextComponent(translateColorCodes())
    }

    infix fun String.color(color: ChatColor): TextComponent {
        return toTextComponent() color color
    }

    infix fun String.hover(hover: String): TextComponent {
        return toTextComponent() hover hover
    }

    infix fun String.command(command: String): TextComponent {
        return toTextComponent() command command
    }

    infix fun String.suggest(command: String): TextComponent {
        return toTextComponent() suggest command
    }
}

object TextComponentSupport {
    infix fun TextComponent.color(color: ChatColor): TextComponent {
        this.color = color.asBungee()
        return TextComponent(this).apply { this.color = color.asBungee() }
    }

    infix fun TextComponent.hover(hover: TextComponent): TextComponent {
        return TextComponent(this).apply {
            this.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(hover))
        }
    }

    infix fun TextComponent.hover(lambda: () -> TextComponent): TextComponent {
        return hover(lambda())
    }

    infix fun TextComponent.hover(hover: String): TextComponent {
        return hover(TextComponent(hover))
    }

    infix fun TextComponent.command(command: String): TextComponent {
        return TextComponent(this).apply {
            this.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
        }
    }

    infix fun TextComponent.suggest(command: String): TextComponent {
        return TextComponent(this).apply {
            this.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)
        }
    }

    operator fun TextComponent.plus(text: String): TextComponent {
        return plus(TextComponent(text))
    }

    operator fun TextComponent.plus(text: TextComponent): TextComponent {
        return TextComponent(this, text)
    }

    operator fun TextComponent.plus(texts: Collection<TextComponent>): TextComponent {
        return TextComponent(this, *texts.toTypedArray())
    }
}
