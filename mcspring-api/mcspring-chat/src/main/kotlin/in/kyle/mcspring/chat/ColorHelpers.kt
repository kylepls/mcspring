package `in`.kyle.mcspring.chat

import `in`.kyle.mcspring.chat.StringSupport.color
import `in`.kyle.mcspring.chat.TextComponentSupport.color
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor.*

fun String.translateColorCodes() = translateAlternateColorCodes('&', this)

fun String.black() = this color BLACK
fun String.darkBlue() = this color DARK_BLUE
fun String.darkGreen() = this color DARK_GREEN
fun String.darkAqua() = this color DARK_AQUA
fun String.darkRed() = this color DARK_RED
fun String.darkPurple() = this color DARK_PURPLE
fun String.gold() = this color GOLD
fun String.gray() = this color GRAY
fun String.darkGray() = this color DARK_GRAY
fun String.blue() = this color BLUE
fun String.green() = this color GREEN
fun String.aqua() = this color AQUA
fun String.red() = this color RED
fun String.lightPurple() = this color LIGHT_PURPLE
fun String.yellow() = this color YELLOW
fun String.white() = this color WHITE
fun String.magic() = this color MAGIC
fun String.bold() = this color BOLD
fun String.strikethrough() = this color STRIKETHROUGH
fun String.underline() = this color UNDERLINE
fun String.italic() = this color ITALIC
fun String.reset() = this color RESET

fun TextComponent.black() = this color BLACK
fun TextComponent.darkBlue() = this color DARK_BLUE
fun TextComponent.darkGreen() = this color DARK_GREEN
fun TextComponent.darkAqua() = this color DARK_AQUA
fun TextComponent.darkRed() = this color DARK_RED
fun TextComponent.darkPurple() = this color DARK_PURPLE
fun TextComponent.gold() = this color GOLD
fun TextComponent.gray() = this color GRAY
fun TextComponent.darkGray() = this color DARK_GRAY
fun TextComponent.blue() = this color BLUE
fun TextComponent.green() = this color GREEN
fun TextComponent.aqua() = this color AQUA
fun TextComponent.red() = this color RED
fun TextComponent.lightPurple() = this color LIGHT_PURPLE
fun TextComponent.yellow() = this color YELLOW
fun TextComponent.white() = this color WHITE
fun TextComponent.magic() = this color MAGIC
fun TextComponent.bold() = this color BOLD
fun TextComponent.strikethrough() = this color STRIKETHROUGH
fun TextComponent.underline() = this color UNDERLINE
fun TextComponent.italic() = this color ITALIC
fun TextComponent.reset() = this color RESET
