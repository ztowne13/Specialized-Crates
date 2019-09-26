package me.ztowne13.customcrates;

import me.ztowne13.customcrates.commands.CommandCrate;
import me.ztowne13.customcrates.commands.CommandKey;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.CHolograms;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.crates.options.particles.ParticleData;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.animations.openchest.OpenChestAnimation;
import me.ztowne13.customcrates.crates.types.animations.openchestrolling.OpenChestRollingAnimation;
import me.ztowne13.customcrates.external.MetricsLite;
import me.ztowne13.customcrates.external.PlaceHolderAPIHandler;
import me.ztowne13.customcrates.external.holograms.HologramInteractListener;
import me.ztowne13.customcrates.external.holograms.HologramManager;
import me.ztowne13.customcrates.external.holograms.HologramManagerNMS;
import me.ztowne13.customcrates.listeners.*;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.players.data.events.CrateCooldownEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.DebugUtils;
import me.ztowne13.customcrates.utils.FileHandler;
import me.ztowne13.customcrates.utils.NPCUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class CustomCrates extends JavaPlugin
{
    FileHandler messageFile, rewardsFile, activecratesFile, crateconfigFile, dataFile;
    Settings settings;
    //UpdateChecker updateChecker;
    HologramManager hologramManager;
    DataHandler dataHandler;
    CommandCrate commandCrate;

    BukkitTask br;
    MetricsLite metricsLite = null;
    PlaceHolderAPIHandler placeHolderAPIHandler = null;

    DebugUtils du = null;

    int tick = 0;
    boolean allowTick = true;
    boolean onlyUseBuildInHolograms = true;

    ArrayList<ParticleData> alreadyUpdated = new ArrayList<>();


    public void onEnable()
    {
        onEnable(true);
    }

    public void onEnable(boolean register)
    {
        if(metricsLite == null)
            metricsLite = new MetricsLite(this);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && placeHolderAPIHandler == null){
            placeHolderAPIHandler = new PlaceHolderAPIHandler(this);
            placeHolderAPIHandler.register();
        }

        reloadConfig();
        saveDefaultConfig();
        loadFiles();

        this.hologramManager = new HologramManagerNMS(this);
        this.dataHandler = new DataHandler(this, dataFile);

        setSettings(new Settings(this));
        getSettings().load();

        //updateChecker = new UpdateChecker(this);

        if (du == null)
        {
            du = new DebugUtils(this);
        }

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
        OpenChestRollingAnimation.removeAllItems();

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
                crate.getCs().saveAll();
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

        getCommand("scrates").setExecutor(null);
        getCommand("scrates").setTabCompleter(null);
        getCommand("keys").setExecutor(null);

        setTick(0);

        FileHandler.clearLoaded();
        PlacedCrate.clearLoaded();
        PlayerManager.clearLoaded();
        Crate.clearLoaded();
        stopRun();

        setBr(null);

        CRewards.getAllRewards().clear();
        SettingsValues.valuesCache.clear();

        ChatUtils.log("Enabling, wait 1 second...");

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable()
        {
            public void run()
            {
                ChatUtils.log("Enabling SpecializedCrates");
                onEnable(false);
            }

        }, 20);

    }

    public void registerCommands()
    {
        commandCrate = new CommandCrate(this);
        getCommand("scrates").setExecutor(commandCrate);
        getCommand("scrates").setTabCompleter(new TabCompleteListener(this));

        getCommand("keys").setExecutor(new CommandKey(this));
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

        getMessageFile().saveDefaults();
        getRewardsFile().saveDefaults();
        getActivecratesFile().saveDefaults();
        getCrateconfigFile().saveDefaults();
        getDataFile().saveDefaults();
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

            setTick(getTick() + 1);

            if (getTick() == 10)
            {
                setTick(0);

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

    public void run()
    {
        setBr(Bukkit.getScheduler().runTaskTimer(this, new Runnable()
        {
            public void run()
            {
                tick();
            }

        }, 0, 2));
    }

    public void finishUpPlayers()
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            PlayerManager pm = PlayerManager.get(this, p);
            if (pm.isWaitingForClose())
            {
                pm.closeCrate();

                for (Reward r : pm.getWaitingForClose())
                {
                    r.runCommands(p);
                }

                pm.setWaitingForClose(null);
                p.closeInventory();
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

    public void setDataHandler(DataHandler dataHandler)
    {
        this.dataHandler = dataHandler;
    }

    public CommandCrate getCommandCrate()
    {
        return commandCrate;
    }
}
