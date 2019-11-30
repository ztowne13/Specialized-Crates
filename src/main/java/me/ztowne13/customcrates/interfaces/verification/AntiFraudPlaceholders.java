package me.ztowne13.customcrates.interfaces.verification;

import org.bukkit.Bukkit;

public class AntiFraudPlaceholders
{
//    static String USER = "%%__USER__%%-DEMO-" + Bukkit.ge;
//
//    static String RESOURCE = "%%__RESOURCE__%%";
//
//    static String NONCE = "%%__NONCE__%%";

    static String USER = "DEMO1-" + Bukkit.getPluginManager().getPlugin("SpecializedCrates-Demo").getDescription().getVersion();

    static String RESOURCE = "DEMO1-" + Bukkit.getPluginManager().getPlugin("SpecializedCrates-Demo").getDescription().getVersion();

    static String NONCE = "DEMO1-" + Bukkit.getPluginManager().getPlugin("SpecializedCrates-Demo").getDescription().getVersion();
}
