package `in`.kyle.mcspring.chat

import `in`.kyle.mcspring.chat.TextComponentSupport.color
import `in`.kyle.mcspring.chat.TextComponentSupport.command
import `in`.kyle.mcspring.chat.TextComponentSupport.hover
import `in`.kyle.mcspring.chat.TextComponentSupport.suggest
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object PlayerSupport {
    fun Player.sendMessage(vararg components: TextComponent) {
        spigot().sendMessage(*components)
    }

    fun Player.sendMessage(lambda: () -> TextComponent) {
        spigot().sendMessage(lambda())
    }
}

object StringSupport {

    fun String.toTextComponent(): TextComponent {
        val player: Player
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

    operator fun String.plus(textComponent: TextComponent): TextComponent {
        return TextComponent(toTextComponent(), textComponent)
    }
}

object TextComponentSupport {
    infix fun TextComponent.color(color: ChatColor): TextComponent {
        this.color = color.asBungee()
        return this
    }

    infix fun TextComponent.hover(hover: TextComponent): TextComponent {
        this.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(hover))
        return this
    }

    infix fun TextComponent.hover(lambda: () -> TextComponent): TextComponent {
        return hover(lambda())
    }

    infix fun TextComponent.hover(hover: String): TextComponent {
        val hoverComponent = TextComponent(hover)
        this.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(hoverComponent))
        return this
    }

    infix fun TextComponent.command(command: String): TextComponent {
        this.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
        return this
    }

    infix fun TextComponent.suggest(command: String): TextComponent {
        this.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)
        return this
    }

    operator fun TextComponent.plus(text: String): TextComponent {
        return TextComponent(this, TextComponent(text))
    }

    operator fun TextComponent.plus(text: TextComponent): TextComponent {
        return TextComponent(this, text)
    }
}
