package com.joshb.mcspringboot.economy;

import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;

public interface EconomyService {
    
    void deposit(OfflinePlayer player, BigDecimal amount);
    
    void withdraw(OfflinePlayer player, BigDecimal amount);
    
    void transfer(OfflinePlayer origin, OfflinePlayer destination, BigDecimal amount);
    
    boolean hasAmount(OfflinePlayer player, BigDecimal amount);
    
    void createAccount(OfflinePlayer player);
    
    String format(BigDecimal amount);
    
    BigDecimal getBalance(OfflinePlayer player);
    
    boolean hasAccount(OfflinePlayer player);
    
}
