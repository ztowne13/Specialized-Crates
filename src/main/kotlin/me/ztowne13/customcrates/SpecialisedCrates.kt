package me.ztowne13.customcrates

import ch.jalu.configme.SettingsManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.okkero.skedule.CoroutineTask
import com.okkero.skedule.SynchronizationContext
import com.okkero.skedule.schedule
import me.ztowne13.customcrates.api.SpecialisedCratesAPI
import me.ztowne13.customcrates.crates.Crate
import me.ztowne13.customcrates.handlers.CrateHandler
import me.ztowne13.customcrates.handlers.EconomyHandler
import me.ztowne13.customcrates.holograms.HologramHandler
import me.ztowne13.customcrates.listeners.*
import me.ztowne13.customcrates.logic.Loop
import me.ztowne13.customcrates.particles.ParticleHandler
import me.ztowne13.customcrates.storage.StorageProvider
import me.ztowne13.customcrates.utils.*
import me.ztowne13.oldcustomcrates.interfaces.externalhooks.EconomyHandler
import me.ztowne13.oldcustomcrates.interfaces.externalhooks.holograms.HologramManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitScheduler
import org.jetbrains.annotations.Async
import java.lang.Exception
import java.util.ArrayList

class SpecialisedCrates : JavaPlugin() {

    //TODO
    //  Rework command system to be cleaner (ACF)
    //  Rework config system
    //  Rework saving system
    //  Reorganise everything to work - IN PROGRESS
    //  Use Coroutines everywhere because they are cool

    companion object {
        private var api = SpecialisedCratesAPI()
        private val gson = GsonBuilder().setPrettyPrinting().create()

        fun getApi(): SpecialisedCratesAPI { return api }
        fun getGson(): Gson { return gson }
    }

    private lateinit var settingsManager: SettingsManager

    private lateinit var hologramHandler: HologramHandler
    private lateinit var crateHandler: CrateHandler
    private lateinit var economyHandler: EconomyHandler
    private lateinit var particleHandler: ParticleHandler

    private lateinit var storageProvider: StorageProvider


    fun getSchedule(): BukkitScheduler {
        return Bukkit.getScheduler()
    }
    fun getInstance(): SpecialisedCrates { return this }

    override fun onEnable() {
        onEnable(true)
    }

    private fun onEnable(register: Boolean) {
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            Logging.warn("It appears that you don't have Vault! Stopping the plugin..")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        if (!Bukkit.getPluginManager())
    }

//        scheduler.scheduleSyncRepeatingTask(this, schedule)
//
//        if (metricsLite == null) {
//            metricsLite = metricsLite(this, 5642)
//        }
//
//        if (debugUtils == null) {
//            debugUtils = DebugUtils(this)
//        }
//
//        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && !isUsingPlaceholderAPI()) {
//            placeHolderAPIHandler = PlaceHolderAPIHandler(this)
//            placeHolderAPIHandler!!.register()
//        }
//
//        reloadConfig()
//        saveDefaultConfig()
//        loadFiles()
//
//        dataHandler = me.ztowne13.oldcustomcrates.DataHandler(this, dataFile)
//
//        setSettings(Settings(this))
//        hologramManager = when {
//            Utils.isPLInstalled("HolographicDisplays") -> {
//                Utils.addToInfoLog(this, "Hologram Plugin", "HolographicDisplays")
//                HDHologramManager(this)
//            }
//            Utils.isPLInstalled("Holograms") -> {
//                Utils.addToInfoLog(this, "Hologram Plugin", "Holograms")
//                SainttXHologramManager(this)
//            }
//            Utils.isPLInstalled("CMI") -> {
//                Utils.addToInfoLog(this, "Hologram Plugin", "CMI")
//                ZripsHologramManager(this)
//            }
//            else -> {
//                Utils.addToInfoLog(this, "Hologram Plugin", "None")
//                NoHologramManager(this)
//            }
//        }
//
//        getSettings()!!.load()
//        registerCommands()
//
//        if (register) {
//            registerAll()
//        }
//
//        for (p: Player in Bukkit.getOnlinePlayers()) {
//            PlayerManager[this, p]
//        }
//
//        NPCUtils.load(register)
//        NPCUtils.checkUncheckMobs(this, false, 20)
//        loadRewards()
//        run()
//        dataHandler!!.loadFromFile()
//        allowTick = true
//        getSettings()!!.getInfoToLog().put("Metrics", if (metricsLite == null) "&cdisabled" else "&aenabled")
//
//        // Check to see if the plugin needs a reload to find the hologram plugin
//        if (!hasAttemptedReload) {
//            Bukkit.getScheduler().runTaskLater(this, Runnable {
//                if (getSettings()!!.getInfoToLog().containsKey("Hologram Plugin") &&
//                    getSettings()!!.getInfoToLog()["Hologram Plugin"].equals("None", ignoreCase = true)
//                ) {
//                    hasAttemptedReload = true
//                    ChatUtils.log(
//                        ("&e[SpecialisedCrates] No hologram plugin was found. In the off-chance that this is because the hologram plugin" +
//                                " opted to ignore the softdepend and loaded after SpecialisedCrates, the plugin is reloading once to " +
//                                "try again.")
//                    )
//                    reload()
//                }
//            }, 1)
//        }
//        if (Bukkit.getServer().getSpawnRadius() != 0) {
//            ChatUtils.log(
//                ("&4WARNING: &cThe value 'spawn-protection' is set to " + Bukkit.getServer().getSpawnRadius() +
//                        " in the server.properties file. This WILL cause issues with SpecialisedCrates - any crates near spawn will " +
//                        "only be openable for OP players. Please go to your server.properties file in the main directory of your server" +
//                        " and change spawn-protection: 0.")
//            )
//        }
//    }
//        override fun onDisable() {
//        allowTick = false
//        try {
//            finishUpPlayers()
//        } catch (exc: Exception) {
//            // IGNORED
//        }
//        dataHandler!!.saveToFile()
//        CHologram.Companion.deleteAll()
//        NPCUtils.checkUncheckMobs(true)
//        OpenChestAnimation.Companion.removeAllItems()
//        Messages.Companion.clearCache()
//        SQLQueryThread.Companion.stopRun()
//        SQLQueryThread.Companion.clearQuery()
//        saveFilesTick(false)
//    }
//
//    fun saveEverything() {
//        messageFile!!.save()
//        rewardsFile!!.save()
//        crateConfigFile!!.save()
//        settings!!.writeSettingsValues()
//        for (crate: Crate in Crate.Companion.getLoadedCrates().values) {
//            try {
//                crate.getSettings().saveAll()
//            } catch (exc: Exception) {
//                exc.printStackTrace()
//            }
//        }
//    }
//
//    fun reload() {
//        ChatUtils.log("Disabling...")
//        onDisable()
//        setMessageFile(null)
//        setRewardsFile(null)
//        setActiveCratesFile(null)
//        setCrateConfigFile(null)
//        setSettings(null)
//        setDataFile(null)
//        setSqlFile(null)
//        getCommand("scrates").setExecutor(null)
//        getCommand("scrates").setTabCompleter(null)
//        getCommand("keys").setExecutor(null)
//        try {
//            getCommand("rewards").setExecutor(null)
//            getCommand("rewards").setTabCompleter(null)
//        } catch (exc: Exception) {
//            // IGNORED
//        }
//        setTick(0)
//        FileHandler.Companion.clearLoaded()
//        PlacedCrate.Companion.clearLoaded()
//        PlayerManager.Companion.clearLoaded()
//        Crate.Companion.clearLoaded()
//        ReflectionUtilities.clearLoaded()
//        Utils.setCachedParticleDistance(-1)
//        stopRun()
//        setBukkitTask(null)
//        CReward.Companion.getAllRewards().clear()
//        SettingsValue.Companion.clearCache()
//        ChatUtils.log("Enabling SpecialisedCrates")
//        onEnable(false)
//    }
//
//    fun registerCommands() {
//        val tabCompleteListener: TabCompleteListener = TabCompleteListener(this)
//        commandCrate = CommandCrate(this)
//        getCommand("scrates").setExecutor(commandCrate)
//        getCommand("scrates").setTabCompleter(tabCompleteListener)
//        getCommand("keys").setExecutor(CommandKey(this))
//        getCommand("rewards").setExecutor(CommandRewards(this))
//        getCommand("rewards").setTabCompleter(tabCompleteListener)
//    }
//
//    fun registerAll() {
//        registerListener(InteractListener(this))
//        registerListener(BlockBreakListener(this))
//        registerListener(BlockPlaceListener(this))
//        registerListener(BlockRemoveListener(this))
//        registerListener(InventoryActionListener(this))
//        registerListener(PlayerConnectionListener(this))
//        registerListener(CommandPreprocessListener(this))
//        registerListener(ChatListener(this))
//        registerListener(PluginEnableListener(this))
//        registerListener(DamageListener(this))
//        if (NPCUtils.isCitizensInstalled()) {
//            registerListener(NPCEventListener(this))
//        }
//    }
//
//    fun loadRewards() {
//        var newValues: Boolean = false
//        for (rName: String? in getRewardsFile()!!.get()!!.getKeys(false)) {
//            if (!CReward.Companion.getAllRewards().containsKey(rName)) {
//                if (!newValues) {
//                    newValues = true
//                }
//                val reward: Reward = Reward(this, rName)
//                reward.loadFromConfig()
//                reward.loadChance()
//                CReward.Companion.getAllRewards().put(rName, reward)
//            }
//        }
//    }
//
//    fun registerListener(listener: Listener?) {
//        Bukkit.getPluginManager().registerEvents(listener, this)
//    }
//
//    fun loadFiles() {
//        setRewardsFile(FileHandler(this, "Rewards.yml", true, false))
//        setActiveCratesFile(FileHandler(this, "ActiveCrates.db", false, false))
//        setCrateConfigFile(FileHandler(this, "CrateConfig.yml", true, false))
//        setMessageFile(FileHandler(this, "Messages.yml", true, false))
//        setDataFile(FileHandler(this, "PluginData.db", false, false))
//        setSqlFile(FileHandler(this, "SQL.yml", true, false))
//        getMessageFile()!!.saveDefaults()
//        getRewardsFile()!!.saveDefaults()
//        getActiveCratesFile()!!.saveDefaults()
//        getCrateConfigFile()!!.saveDefaults()
//        getDataFile()!!.saveDefaults()
//        getSqlFile()!!.saveDefaults()
//    }
//
//    fun firstLoadFiles() {
//        val firstFiles: Array<String> =
//            arrayOf("BasicCrate", "BeginnerCrate", "MiddleCrate", "MasterCrate", "MineChestExample", "ExpertCrate")
//        for (newFile: String in firstFiles) {
//            FileHandler(this, newFile + ".crate", "Crates/", true, true, false).saveDefaults()
//        }
//        FileHandler(this, "AllCrates.multicrate", "Crates/", true, true, false).saveDefaults()
//    }
//
//    fun tick() {
//        if (!allowTick) {
//            return
//        }
//        for (placedCrate: PlacedCrate in ArrayList<PlacedCrate>(PlacedCrate.Companion.getPlacedCrates().values)) {
//            if (placedCrate.isCratesEnabled()) {
//                try {
//                    placedCrate.tick(CrateState.PLAY)
//                } catch (exc: Exception) {
//                    exc.printStackTrace()
//                }
//            }
//        }
//        getAlreadyUpdated().clear()
//        setTotalTicks(getTotalTicks() + 1)
//        setTick(getTick() + 1)
//        if (getTick() == 10) {
//            setTick(0)
//            saveFilesTick(true)
//            for (p: Player? in Bukkit.getOnlinePlayers()) {
//                val pdm: PlayerDataManager = PlayerManager.Companion.get(this, p)!!
//                    .getPlayerDataManager()
//                val list: ArrayList<CrateCooldownEvent?> = ArrayList(pdm.getCrateCooldownEvents())
//                for (cce: CrateCooldownEvent? in list) {
//                    cce!!.tickSecond(pdm)
//                }
//            }
//        }
//    }
//
//    fun saveFilesTick(isInterval: Boolean) {
//        saveFilesTick++
//        val dataSaveType: String = SettingsValue.DATA_SAVE_METHOD.getValue(this).toString()
//        val saveInterVal: Int = SettingsValue.DATA_SAVE_INTERVAL.getValue(this) as Int
//        if (isInterval && (dataSaveType.equals("DISABLE", ignoreCase = true) || saveFilesTick % saveInterVal != 0)) {
//            return
//        }
//        if (FlatFileDataHandler.Companion.isToSave()) {
//            getDebugUtils()!!.log("saveFilesTick() - Saving playerdata.db flat file")
//            FlatFileDataHandler.Companion.getFileHandler()!!.save()
//            FlatFileDataHandler.Companion.resetToSave()
//        }
//        for (file: IndividualFileDataHandler in IndividualFileDataHandler.Companion.getToSave()) {
//            file.getFileHandler().save()
//        }
//        IndividualFileDataHandler.Companion.getToSave().clear()
//    }
//
//    fun run() {
//        setBukkitTask(Bukkit.getScheduler().runTaskTimer(this, Runnable({
//            if (DebugUtils.Companion.OUTPUT_AVERAGE_TICK) {
//                val curTimeMillis: Long = System.currentTimeMillis()
//                tick()
//                val dif: Long = (System.currentTimeMillis() - curTimeMillis)
//                total += dif
//                count++
//                val solved: Long = total / count
//                ChatUtils.log("Average: " + solved)
//            } else {
//                tick()
//            }
//        }), 0, 2))
//    }
//
//    fun finishUpPlayers() {
//        for (p: Player in Bukkit.getOnlinePlayers()) {
//            val pm: PlayerManager? = PlayerManager.Companion.get(this, p)
//            if (pm!!.isInCrateAnimation()) {
//                pm.getCurrentAnimation()!!.setFastTrack(true, true)
//            }
//            if (pm.isInCratesClaimMenu()) {
//                p.closeInventory()
//            }
//        }
//    }

    fun particleCoroutine() {
        getSchedule().schedule(this) {
            repeating(2)


        }
    }

    fun saveCoroutine() {
        getSchedule().schedule(this) {
            repeating(600)



        }
    }



    fun getSettingsManager(): SettingsManager {
        return settingsManager
    }

    fun getHologramHandler(): Any {
        return hologramHandler
    }

    fun getCrateHandler(): CrateHandler {
        return crateHandler
    }

    fun getEconomyHandler(): EconomyHandler {
        return economyHandler
    }

    fun getParticleHandler(): ParticleHandler {
        return particleHandler
    }

    fun getLoop(): Loop {
        return loop
    }

    fun getStorageProvider(): StorageProvider {
        return storageProvider
    }
}