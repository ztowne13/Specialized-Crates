package me.ztowne13.customcrates.interfaces.verification;

import org.bukkit.Bukkit;

public class AntiFraudPlaceholders
{
    static String USER = "%%__USER__%%";

    static String RESOURCE = "%%__RESOURCE__%%";

    static String NONCE = "%%__NONCE__%%";

    static String USER_BETA = "TEST-" + Bukkit.getPluginManager().getPlugin("SpecializedCrates").getDescription().getVersion();

    static String RESOURCE_BETA = "TEST-" + Bukkit.getPluginManager().getPlugin("SpecializedCrates").getDescription().getVersion();

    static String NONCE_BETA = "TEST-" + Bukkit.getPluginManager().getPlugin("SpecializedCrates").getDescription().getVersion();
}
