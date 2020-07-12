package `in`.kyle.mcspring.chat

import `in`.kyle.mcspring.chat.StringSupport.toTextComponent
import `in`.kyle.mcspring.chat.TextComponentSupport.command
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player

infix fun TextComponent.onClick(lambda: (Player) -> Unit): TextComponent {
    val (command, subject) = CommandActionListener.createCommand()
    subject.subscribe { lambda(it) }
    return this command "/$command"
}

infix fun TextComponent.onClickOnce(lambda: (Player) -> Unit): TextComponent {
    val (command, subject) = CommandActionListener.createCommand()
    subject.subscribe {
        lambda(it)
        CommandActionListener.unregisterCommand(command)
    }
    return this command "/$command"
}

infix fun String.onClick(lambda: (Player) -> Unit): TextComponent {
    return toTextComponent() onClick lambda
}

infix fun String.onClickOnce(lambda: (Player) -> Unit): TextComponent {
    return toTextComponent() onClickOnce lambda
}

