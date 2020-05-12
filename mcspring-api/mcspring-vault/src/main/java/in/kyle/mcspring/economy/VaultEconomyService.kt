package `in`.kyle.mcspring.economy

import `in`.kyle.mcspring.annotation.PluginDepend
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Lazy
@Service
@ConditionalOnClass(Economy::class)
@PluginDepend("Vault")
internal class VaultEconomyService(server: Server) : EconomyService {

    private val economy: Economy = server.servicesManager.getRegistration(Economy::class.java)!!.provider

    override fun deposit(player: OfflinePlayer, amount: BigDecimal) {
        assertEconomyResponse(economy.depositPlayer(player, amount.toDouble()))
    }

    override fun withdraw(player: OfflinePlayer, amount: BigDecimal) {
        assertEconomyResponse(economy.withdrawPlayer(player, amount.toDouble()))
    }

    override fun transfer(origin: OfflinePlayer, destination: OfflinePlayer, amount: BigDecimal) {
        withdraw(origin, amount)
        deposit(destination, amount)
    }

    override fun hasAmount(player: OfflinePlayer, amount: BigDecimal): Boolean {
        return economy.has(player, amount.toDouble())
    }

    override fun createAccount(player: OfflinePlayer) {
        economy.createPlayerAccount(player)
    }

    override fun format(amount: BigDecimal): String {
        return economy.format(amount.toDouble())
    }

    override fun getBalance(player: OfflinePlayer): BigDecimal {
        return BigDecimal.valueOf(economy.getBalance(player))
    }

    override fun hasAccount(player: OfflinePlayer): Boolean {
        return economy.hasAccount(player)
    }

    private fun assertEconomyResponse(response: EconomyResponse) {
        if (response.type != EconomyResponse.ResponseType.SUCCESS) {
            throw EconomyException(response.errorMessage)
        }
    }
}
