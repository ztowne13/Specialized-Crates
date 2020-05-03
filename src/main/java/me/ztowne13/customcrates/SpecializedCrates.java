package me.ztowne13.customcrates;

import me.ztowne13.customcrates.commands.CommandCrate;
import me.ztowne13.customcrates.commands.CommandKey;
import me.ztowne13.customcrates.commands.CommandRewards;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.CHolograms;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.crates.options.particles.ParticleData;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.animations.block.OpenChestAnimation;
import me.ztowne13.customcrates.interfaces.externalhooks.EconomyHandler;
import me.ztowne13.customcrates.interfaces.externalhooks.MetricsLite;
import me.ztowne13.customcrates.interfaces.externalhooks.PlaceHolderAPIHandler;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.HologramInteractListener;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.HologramManager;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.HologramManagerNMS;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.interfaces.sql.SQLQueryThread;
import me.ztowne13.customcrates.interfaces.verification.AntiFraudSQLHandler;
import me.ztowne13.customcrates.listeners.*;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.players.data.FlatFileDataHandler;
import me.ztowne13.customcrates.players.data.IndividualFileDataHandler;
import me.ztowne13.customcrates.players.data.events.CrateCooldownEvent;
import me.ztowne13.customcrates.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class SpecializedCrates extends JavaPlugin
{

    FileHandler messageFile, rewardsFile, activecratesFile, crateconfigFile, dataFile, sqlFile;
    Settings settings;
    //UpdateChecker updateChecker;
    HologramManager hologramManager;
    DataHandler dataHandler;
    EconomyHandler economyHandler;
    CommandCrate commandCrate;
    AntiFraudSQLHandler antiFraudSQLHandler;

    BukkitTask br;
    MetricsLite metricsLite = null;
    PlaceHolderAPIHandler placeHolderAPIHandler = null;

    DebugUtils du = null;

    int tick = 0;
    int totalTicks = 0;
    boolean allowTick = true;
    boolean onlyUseBuildInHolograms = true;
    boolean hasAttemptedReload = false;
    boolean particlesEnabled = true;

    ArrayList<ParticleData> alreadyUpdated = new ArrayList<>();


    public void onEnable()
    {
        onEnable(true);
    }

    public void onEnable(boolean register)
    {
        if(metricsLite == null)
            metricsLite = new MetricsLite(this);

        if (du == null)
            du = new DebugUtils(this);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && !isUsingPlaceholderAPI()){
            placeHolderAPIHandler = new PlaceHolderAPIHandler(this);
            placeHolderAPIHandler.register();
        }

        reloadConfig();
        saveDefaultConfig();
        loadFiles();

        this.hologramManager = new HologramManagerNMS(this);
        this.dataHandler = new DataHandler(this, dataFile);
        this.economyHandler = new EconomyHandler(this);

        setSettings(new Settings(this));
        getSettings().load();

        //updateChecker = new UpdateChecker(this);

        registerCommands();
        if (register)
        {
            registerAll();
        }

        for (Player p : Bukkit.getOnlinePlayers())
        {
            PlayerManager.get(this, p);
        }

        NPCUtils.load(register);
        NPCUtils.checkUncheckMobs(this, false, 20);

        loadRewards();

        run();

        dataHandler.loadFromFile();
        allowTick = true;

        antiFraudSQLHandler = new AntiFraudSQLHandler(this);

        // Check to see if the plugin needs a reload to find the hologram plugin
        if(!hasAttemptedReload)
        {
            Bukkit.getScheduler().runTaskLater(this, new Runnable()
            {
                @Override
                public void run()
                {
                    if(getSettings().getInfoToLog().containsKey("Hologram Plugin") &&
                        getSettings().getInfoToLog().get("Hologram Plugin").equalsIgnoreCase("None"))
                    {
                        hasAttemptedReload = true;
                        ChatUtils
                                .log("&e[SpecializedCrates] No hologram plugin was found. In the off-chance that this is because the hologram plugin" +
                                        " opted to ignore the softdepend and loaded after SpecializedCrates, the plugin is reloading once to " +
                                        "try again.");
                        reload();
                    }
                }
            }, 1);
        }

        if(Bukkit.getServer().getSpawnRadius() != 0)
        {
            ChatUtils.log("&4WARNING: &cThe value 'spawn-protection' is set to " + Bukkit.getServer().getSpawnRadius() +
                    " in the server.properties file. This WILL cause issues with SpecializedCrates - any crates near spawn will " +
                    "only be openable for OP players. Please go to your server.properties file in the main directory of your server" +
                    " and change spawn-protection: 0.");
        }
    }

    public void onDisable()
    {
        allowTick = false;

        try
        {
            finishUpPlayers();
        }
        catch (Exception exc)
        {

        }

        dataHandler.saveToFile();

        CHolograms.deleteAll();
        NPCUtils.checkUncheckMobs(true);
        OpenChestAnimation.removeAllItems();
        Messages.clearCache();
        SQLQueryThread.sql_query.clear();
        SQLQueryThread.task_query.clear();
    }

    public void saveEverything()
    {
        messageFile.save();
        rewardsFile.save();
        crateconfigFile.save();
        settings.writeSettingsValues();
        for(Crate crate : Crate.getLoadedCrates().values())
        {
            try
            {
                crate.getSettings().saveAll();
            }
            catch(Exception exc)
            {
                exc.printStackTrace();
            }
        }
    }

    public void reload()
    {
        ChatUtils.log("Disabling...");

        onDisable();

        setMessageFile(null);
        setRewardsFile(null);
        setActivecratesFile(null);
        setCrateconfigFile(null);
        setSettings(null);
        setDataFile(null);
        setSqlFile(null);

        getCommand("scrates").setExecutor(null);
        getCommand("scrates").setTabCompleter(null);
        getCommand("keys").setExecutor(null);
        getCommand("rewards").setExecutor(null);
        getCommand("rewards").setTabCompleter(null);

        setTick(0);

        FileHandler.clearLoaded();
        PlacedCrate.clearLoaded();
        PlayerManager.clearLoaded();
        Crate.clearLoaded();
        ReflectionUtilities.clearLoaded();
        Utils.cachedParticleDistance = -1;
        stopRun();

        setBr(null);

        CRewards.getAllRewards().clear();
        SettingsValues.valuesCache.clear();

//        ChatUtils.log("Enabling, wait 1 second...");

        ChatUtils.log("Enabling SpecializedCrates");
        onEnable(false);

    }

    public void registerCommands()
    {
        TabCompleteListener tabCompleteListener = new TabCompleteListener(this);

        commandCrate = new CommandCrate(this);
        getCommand("scrates").setExecutor(commandCrate);
        getCommand("scrates").setTabCompleter(tabCompleteListener);

        getCommand("keys").setExecutor(new CommandKey(this));

        getCommand("rewards").setExecutor(new CommandRewards(this));
        getCommand("rewards").setTabCompleter(tabCompleteListener);
    }


    public void registerAll()
    {
        rl(new InteractListener(this));
        rl(new BlockBreakListener(this));
        rl(new BlockPlaceListener(this));
        rl(new BlockRemoveListener(this));
        rl(new InventoryActionListener(this));
        rl(new PlayerConnectionListener(this));
        rl(new CommandPreprocessListener(this));
        rl(new ChatListener(this));
        rl(new HologramInteractListener(this));
        rl(new PluginEnableListener(this));
        rl(new DamageListener(this));

        if (NPCUtils.isCitizensInstalled())
        {
            rl(new NPCEventListener(this));
        }
    }

    public void loadRewards()
    {
        boolean newValues = false;

        for(String rName : getRewardsFile().get().getKeys(false))
        {
            if (!CRewards.getAllRewards().keySet().contains(rName))
            {
                if(!newValues)
                {
                    newValues = true;
                }
                Reward r = new Reward(this, rName);
                r.loadFromConfig();
                r.loadChance();
                CRewards.allRewards.put(rName, r);
            }
        }
    }

    public void rl(Listener listener)
    {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }


    public void loadFiles()
    {
        setRewardsFile(new FileHandler(this, "Rewards.yml", true, false));
        setActivecratesFile(new FileHandler(this, "ActiveCrates.db", false, false));
        setCrateconfigFile(new FileHandler(this, "CrateConfig.yml", true, false));
        setMessageFile(new FileHandler(this, "Messages.yml", true, false));
        setDataFile(new FileHandler(this, "PluginData.db", false, false));
        setSqlFile(new FileHandler(this, "SQL.yml", true, false));

        getMessageFile().saveDefaults();
        getRewardsFile().saveDefaults();
        getActivecratesFile().saveDefaults();
        getCrateconfigFile().saveDefaults();
        getDataFile().saveDefaults();
        getSqlFile().saveDefaults();
    }

    public void firstLoadFiles()
    {
        String[] firstFiles =
                new String[]{"BasicCrate", "BeginnerCrate", "MiddleCrate", "MasterCrate", "MineChestExample", "ExpertCrate"};
        for (String newFile : firstFiles)
        {
            new FileHandler(this, newFile + ".crate", "Crates/", true, true, false).saveDefaults();
        }

        new FileHandler(this, "AllCrates.multicrate", "Crates/", true, true, false).saveDefaults();
    }

    public void tick()
    {
        if(!antiFraudSQLHandler.isAuthenticated())
            return;

        if (allowTick)
        {
            for (PlacedCrate cm : new ArrayList<PlacedCrate>(PlacedCrate.getPlacedCrates().values()))
            {
                if (cm.isCratesEnabled())
                {
                    try
                    {
                        cm.tick(CrateState.PLAY);
                    }
                    catch (Exception exc)
                    {
                        exc.printStackTrace();
                    }
                }
            }

            getAlreadyUpdated().clear();

            setTotalTicks(getTotalTicks() + 1);
            setTick(getTick() + 1);

            if (getTick() == 10)
            {
                setTick(0);
                saveFilesTick();

                for (Player p : Bukkit.getOnlinePlayers())
                {
                    PlayerDataManager pdm = PlayerManager.get(this, p).getPdm();
                    ArrayList<CrateCooldownEvent> list =
                            ((ArrayList<CrateCooldownEvent>) pdm.getCrateCooldownEvents().clone());

                    for (CrateCooldownEvent cce : list)
                    {
                        cce.tickSecond(pdm);
                    }
                }
            }
        }
    }

    public void saveFilesTick()
    {
        for(FlatFileDataHandler file : FlatFileDataHandler.toSave)
        {
            file.getFu().save();
        }
        FlatFileDataHandler.toSave.clear();

        for(IndividualFileDataHandler file : IndividualFileDataHandler.toSave)
        {
            file.getFu().save();
        }
        IndividualFileDataHandler.toSave.clear();
    }

    public static double total = 0;
    public static double count = 0;

    public void run()
    {
        setBr(Bukkit.getScheduler().runTaskTimer(this, new Runnable()
        {
            public void run()
            {
                if(DebugUtils.OUTPUT_AVERAGE_TICK)
                {
                    double curTimeMillis = System.currentTimeMillis();
                    tick();
                    double dif = (System.currentTimeMillis() - curTimeMillis);
                    total += dif;
                    count++;
                    double solved = total / count;

                    ChatUtils.log("Average: " + solved);
                }
                else
                {
                    tick();
                }
            }

        }, 0, 2));
    }

    public void finishUpPlayers()
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            PlayerManager pm = PlayerManager.get(this, p);

            if(pm.isInCrateAnimation())
            {
                pm.getCurrentAnimation().setFastTrack(true, true);
            }
        }
    }

    public void stopRun()
    {
        br.cancel();
    }

    public Settings getSettings()
    {
        return settings;
    }

    public void setSettings(Settings settings)
    {
        this.settings = settings;
    }

    public DebugUtils getDu()
    {
        return du;
    }

    public void setBr(BukkitTask br)
    {
        this.br = br;
    }

    public int getTick()
    {
        return tick;
    }

    public void setTick(int tick)
    {
        this.tick = tick;
    }

    public FileHandler getMessageFile()
    {
        return messageFile;
    }

    public void setMessageFile(FileHandler messageFile)
    {
        this.messageFile = messageFile;
    }

    public FileHandler getRewardsFile()
    {
        return rewardsFile;
    }

    public void setRewardsFile(FileHandler rewardsFile)
    {
        this.rewardsFile = rewardsFile;
    }

    public FileHandler getActivecratesFile()
    {
        return activecratesFile;
    }

    public void setActivecratesFile(FileHandler activecratesFile)
    {
        this.activecratesFile = activecratesFile;
    }

    public FileHandler getCrateconfigFile()
    {
        return crateconfigFile;
    }

    public void setCrateconfigFile(FileHandler crateconfigFile)
    {
        this.crateconfigFile = crateconfigFile;
    }


    public ArrayList<ParticleData> getAlreadyUpdated()
    {
        return alreadyUpdated;
    }

//    public UpdateChecker getUpdateChecker()
//    {
//        return updateChecker;
//    }

    public HologramManager getHologramManager()
    {
        return hologramManager;
    }

    public boolean isOnlyUseBuildInHolograms()
    {
        return onlyUseBuildInHolograms;
    }

    public boolean isAllowTick()
    {
        return allowTick;
    }

    public FileHandler getDataFile()
    {
        return dataFile;
    }

    public void setDataFile(FileHandler dataFile)
    {
        this.dataFile = dataFile;
    }

    public DataHandler getDataHandler()
    {
        return dataHandler;
    }

    public CommandCrate getCommandCrate()
    {
        return commandCrate;
    }

    public FileHandler getSqlFile()
    {
        return sqlFile;
    }

    public void setSqlFile(FileHandler sqlFile)
    {
        this.sqlFile = sqlFile;
    }

    public AntiFraudSQLHandler getAntiFraudSQLHandler()
    {
        return antiFraudSQLHandler;
    }

    public EconomyHandler getEconomyHandler()
    {
        return economyHandler;
    }

    public boolean isUsingPlaceholderAPI() {
        return placeHolderAPIHandler != null;
    }

    public boolean isHasAttemptedReload()
    {
        return hasAttemptedReload;
    }

    public boolean isParticlesEnabled()
    {
        return particlesEnabled;
    }

    public void setParticlesEnabled(boolean particlesEnabled)
    {
        this.particlesEnabled = particlesEnabled;
    }

    public int getTotalTicks()
    {
        return totalTicks;
    }

    public void setTotalTicks(int totalTicks)
    {
        this.totalTicks = totalTicks;
    }
}
