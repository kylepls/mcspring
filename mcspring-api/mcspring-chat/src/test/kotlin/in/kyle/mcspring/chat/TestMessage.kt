package `in`.kyle.mcspring.chat

import `in`.kyle.mcspring.chat.StringSupport.color
import `in`.kyle.mcspring.chat.StringSupport.command
import `in`.kyle.mcspring.chat.StringSupport.hover
import `in`.kyle.mcspring.chat.StringSupport.suggest
import `in`.kyle.mcspring.chat.StringSupport.toTextComponent
import `in`.kyle.mcspring.chat.TextComponentSupport.plus
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ClickEvent.Action.*
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT
import net.md_5.bungee.api.chat.TextComponent
import org.amshove.kluent.shouldBeEqualTo
import org.bukkit.ChatColor.RED
import org.junit.jupiter.api.Test

class TestMessage {

    @Test
    fun testHover() {
        "test" hover "hover" shouldBeEqualTo
                TextComponent("test").apply {
                    hoverEvent = HoverEvent(SHOW_TEXT, arrayOf(TextComponent("hover")))
                }
    }

    @Test
    fun testColor() {
        "test" color RED shouldBeEqualTo
                ComponentBuilder("test").color(ChatColor.RED).create()[0]
    }

    @Test
    fun testPlus() {
        val component = "hello ".toTextComponent() + "world"
        // ofc the equality checking doesn't work...
        component.toString() shouldBeEqualTo
                TextComponent(TextComponent("hello "), TextComponent("world")).toString()
    }

    @Test
    fun testCommand() {
        "test" command "/test" shouldBeEqualTo
                TextComponent("test").apply {
                    clickEvent = ClickEvent(RUN_COMMAND, "/test")
                }
    }

    @Test
    fun testSuggest() {
        "test" suggest "/test" shouldBeEqualTo
                TextComponent("test").apply {
                    clickEvent = ClickEvent(SUGGEST_COMMAND, "/test")
                }
    }
}
