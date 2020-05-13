package `in`.kyle.mcspring.vault.economy

import org.bukkit.OfflinePlayer
import java.math.BigDecimal

// https://github.com/MilkBowl/VaultAPI/blob/master/src/main/java/net/milkbowl/vault/economy/Economy.java
/**
 * A wrapper for the [VaultAPI](http://milkbowl.github.io/VaultAPI/)
 */
interface EconomyService {

    /**
     * Attempts to create a player account for the given player
     * @return if the account creation was successful
     */
    fun createAccount(player: OfflinePlayer)

    /**
     * Deposit a **non-negative** balance to a player
     * @throws EconomyException if the transaction fails
     */
    fun deposit(player: OfflinePlayer, amount: BigDecimal)

    /**
     * Format amount into a human readable String This provides translation into
     * economy specific formatting to improve consistency between plugins.
     * @return Human-readable string describing amount
     */
    fun format(amount: BigDecimal): String

    /**
     * Gets balance of a player
     * @return Amount currently held in players account
     */
    fun getBalance(player: OfflinePlayer): BigDecimal

    /**
     * Checks if the player account has the **non-negative** amount
     * @return True if [player] has [amount], False else wise
     */
    fun hasAmount(player: OfflinePlayer, amount: BigDecimal): Boolean

    /**
     * Checks if this player has an account on the server yet
     * This will always return true if the player has joined the server at least once
     * as all major economy plugins auto-generate a player account when the player joins the server
     * @return if the player has an account
     */
    fun hasAccount(player: OfflinePlayer): Boolean

    /**
     * Transfer an amount between two players
     */
    fun transfer(origin: OfflinePlayer, destination: OfflinePlayer, amount: BigDecimal)

    /**
     * Withdraw a **non-negative** amount from a player
     */
    fun withdraw(player: OfflinePlayer, amount: BigDecimal)
}
