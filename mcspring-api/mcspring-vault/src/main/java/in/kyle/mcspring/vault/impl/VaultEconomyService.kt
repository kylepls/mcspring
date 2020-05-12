package `in`.kyle.mcspring.vault.impl

import `in`.kyle.mcspring.annotation.PluginDepend
import `in`.kyle.mcspring.vault.api.EconomyException
import `in`.kyle.mcspring.vault.api.EconomyService
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Suppress("unused")
@Lazy
@Service
@ConditionalOnClass(Economy::class)
@PluginDepend("Vault")
internal class VaultEconomyService(server: Server) : EconomyService {

    private val economy: Economy = server.servicesManager.getRegistration(Economy::class.java)!!.provider

    override fun deposit(player: OfflinePlayer, amount: BigDecimal) {
        requirePositive(amount)
        assertEconomyResponse(economy.depositPlayer(player, amount.toDouble()))
    }

    override fun withdraw(player: OfflinePlayer, amount: BigDecimal) {
        requirePositive(amount)
        assertEconomyResponse(economy.withdrawPlayer(player, amount.toDouble()))
    }

    override fun transfer(origin: OfflinePlayer, destination: OfflinePlayer, amount: BigDecimal) {
        requirePositive(amount)
        require(hasAmount(origin, amount)) { "Player ${origin.name} does not have the required amount $amount" }
        withdraw(origin, amount)
        if (!hasAccount(destination)) {
            createAccount(destination)
        }
        deposit(destination, amount)
    }

    override fun hasAmount(player: OfflinePlayer, amount: BigDecimal): Boolean {
        requirePositive(amount)
        return economy.has(player, amount.toDouble())
    }

    override fun createAccount(player: OfflinePlayer) {
        assert(economy.createPlayerAccount(player)) { "Failed to create account for ${player.name}" }
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

    private fun requirePositive(decimal: BigDecimal) {
        require(decimal.toDouble() > 0) { "The decimal amount must be positive, got: $decimal" }
    }
}
