package `in`.kyle.mcspring.economy

import org.bukkit.OfflinePlayer
import java.math.BigDecimal

interface EconomyService {
    fun deposit(player: OfflinePlayer, amount: BigDecimal)
    fun withdraw(player: OfflinePlayer, amount: BigDecimal)
    fun transfer(origin: OfflinePlayer, destination: OfflinePlayer, amount: BigDecimal)
    fun hasAmount(player: OfflinePlayer, amount: BigDecimal): Boolean
    fun createAccount(player: OfflinePlayer)
    fun format(amount: BigDecimal): String
    fun getBalance(player: OfflinePlayer): BigDecimal
    fun hasAccount(player: OfflinePlayer): Boolean
}
