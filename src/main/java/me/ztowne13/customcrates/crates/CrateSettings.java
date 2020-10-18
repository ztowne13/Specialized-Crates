package me.ztowne13.customcrates.crates;

import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.*;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.RewardDisplayType;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.RewardDisplayer;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimation;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.crates.types.display.CrateDisplayType;
import me.ztowne13.customcrates.crates.types.display.DynamicCratePlaceholder;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.CrateUtils;
import org.bukkit.configuration.file.FileConfiguration;

public class CrateSettings {
    private final SpecializedCrates instance;
    private final CrateSettingsBuilder crateSettingsBuilder;
    private boolean requireKey;
    private boolean tiersOverrideDefaults = true;
    private ObtainType obtainType;
    private int cooldown = 0;
    private int cost = -1;
    private double hologramOffset = 0;
    private KeyItemHandler keyItemHandler;
    private CrateItemHandler crateItemHandler;
    private CrateDisplayType crateDisplayType = CrateDisplayType.BLOCK;
    private DynamicCratePlaceholder dynamicCratePlaceholder;
    private RewardDisplayer displayer;
    private RewardDisplayType rewardDisplayType;
    private CrateAnimation crateAnimation;
    private CrateAnimationType crateAnimationType;
    private boolean canFastTrack = false;
    private boolean autoClose = true;
    private CHologram hologram;
    private CParticle particle;
    private CSound sound;
    private CAction action;
    private CFirework firework;
    private CLuckyChest luckyChest;
    private CReward reward;
    private CMultiCrateInventory multiCrateInventory;
    private Crate crate;
    private StatusLogger statusLogger;
    private String name;
    private String crateInventoryName = "";
    private String permission = "no permission";
    private FileConfiguration fileConfiguration;
    private FileHandler fileHandler;

    public CrateSettings(SpecializedCrates instance, Crate crate, boolean newFile) {
        this.instance = instance;
        this.crate = crate;
        this.name = crate.getName();
        this.statusLogger = new StatusLogger(instance);

        this.fileHandler = new FileHandler(instance, crate.getName() + (crate.isMultiCrate() ? ".multicrate" : ".crate"), "/Crates", true,
                true, newFile);
        this.fileConfiguration = fileHandler.get();

        this.crateSettingsBuilder = new CrateSettingsBuilder(this);
    }

    public void saveAll() {
        saveIndividualValues();

        getKeyItemHandler().saveToFile();
        getCrateItemHandler().saveToFile();

        if (getObtainType().equals(ObtainType.LUCKYCHEST)) {
            getLuckyChestSettings().saveToFile();
        }
        getHologram().saveToFile();
        getParticle().saveToFile();
        getSound().saveToFile();

        if (!getCrate().isMultiCrate()) {
            getReward().saveToFile();
            getAction().saveToFile();
            getFirework().saveToFile();
            getDisplayer().saveToFile();
        } else {
            getMultiCrateSettings().saveToFile();
        }

        getFileHandler().save();
    }


    public void saveIndividualValues() {
        fileConfiguration.set("enabled", crate.isEnabled());
        fileConfiguration.set("cooldown", getCooldown());
        fileConfiguration.set("obtain-method", getObtainType().name());
        fileConfiguration.set("display.type", getPlaceholder().toString());
        fileConfiguration.set("hologram-offset", getHologramOffset());
        fileConfiguration.set("auto-close", isAutoClose());
        fileConfiguration.set("key.require", isRequireKey());
        fileConfiguration.set("cost", getCost());
        fileConfiguration.set("allow-skip-animation", isCanFastTrack());

        fileConfiguration.set("permission", getPermission().equalsIgnoreCase("no permission") ? null : getPermission());

        if (!getPlaceholder().toString().equalsIgnoreCase("block")) {
            fileConfiguration.set("display." + (getPlaceholder().toString().equalsIgnoreCase("mob") ? "creature" : "name"), getPlaceholder().getType());
        }

        if (getCrateInventoryName() != null) {
            fileConfiguration.set("inventory-name", ChatUtils.fromChatColor(getCrateInventoryName()));
        }

        if (!getCrate().isMultiCrate()) {
            fileConfiguration.set("open.crate-animation", getCrateType().name());
        }

    }

    public void loadAll() {
        instance.getDu().log("loadAll() - CALL");
        // Crate Loging
        String toLog = SettingsValue.LOG_SUCCESSES.getValue(getCrate().getInstance()).toString();
        instance.getDu().log("loadAll() - Preparing to load notice.");
        loadNotice(toLog);
        instance.getDu().log("loadAll() - Loaded notice.");

        setParticle(new CParticle(getCrate()));
        setHologram(new CHologram(getCrate()));
        setKeyItemHandler(new KeyItemHandler(getCrate()));
        setCrateItemHandler(new CrateItemHandler(getCrate()));
        setSound(new CSound(getCrate()));

        if (!getCrate().isMultiCrate()) {
            setReward(new CReward(getCrate()));
            setAction(new CAction(getCrate()));
            setFirework(new CFirework(getCrate()));
            setLuckyChestSettings(new CLuckyChest(getCrate()));
        } else {
            setMultiCrateSettings(new CMultiCrateInventory(getCrate()));
        }

        if (getFileHandler().isProperLoad()) {
            // Base Settings

            getCrateItemHandler().loadFor(getSettingsBuilder(), null);
            getSettingsBuilder().setupCooldown();
            getSettingsBuilder().setupDisplay();
            getSettingsBuilder().setupObtainMethod();
            getSettingsBuilder().setupCrateInventoryName();
            getSettingsBuilder().setupPermission();
            getSettingsBuilder().setupAutoClose();
            getSettingsBuilder().setupHologramOffset();
            getSettingsBuilder().setupDisplayer();
            getSettingsBuilder().setupCost();
            getSettingsBuilder().setupAllowSkipAnimation();

            // Base Settings for non-MultiCrates
            if (!getCrate().isMultiCrate()) {
                getKeyItemHandler().loadFor(getSettingsBuilder(), null);
                getSettingsBuilder().setupCrateAnimation();
            }

            // Particles
            getParticle().loadFor(getSettingsBuilder(), CrateState.PLAY);
            getParticle().loadFor(getSettingsBuilder(), CrateState.OPEN);

            // Holograms
            getHologram().loadFor(getSettingsBuilder(), CrateState.PLAY);

            // Sounds
            getSound().loadFor(getSettingsBuilder(), CrateState.OPEN);

            // Lucky Chest
            if (getObtainType().equals(ObtainType.LUCKYCHEST)) {
                getLuckyChestSettings().loadFor(getSettingsBuilder(), null);
            }

            if (!getCrate().isMultiCrate()) {
                // Rewards
                getReward().loadFor(getSettingsBuilder(), CrateState.OPEN);

                // Actions
                getAction().loadFor(getSettingsBuilder(), CrateState.OPEN);

                // Fireworks
                getFirework().loadFor(getSettingsBuilder(), CrateState.OPEN);

                getCrate().getSettings().getCrateType().setupFor(crate);
                getCrateAnimation().loadDataValues(getStatusLogger());
            } else {
                getMultiCrateSettings().loadFor(getSettingsBuilder(), CrateState.OPEN);
            }


            if (!toLog.equalsIgnoreCase("NOTHING")) {
                getStatusLogger().logAll();
            }
        } else {
            crate.setEnabled(false);
            crate.setCanBeEnabled(false);
        }
    }

    public void loadNotice(String toLog) {
        if (!toLog.equalsIgnoreCase("NOTHING")) {
            ChatUtils.log("");
            if (!CrateUtils.isCrateUsable(getCrate())) {
                ChatUtils.log("&b" + getCrate().getName() + " &c(Disabled)");
            } else {
                ChatUtils.log("&b" + getCrate().getName());
            }
        }
    }

    public boolean isRequireKey() {
        return requireKey;
    }

    public void setRequireKey(boolean requireKey) {
        this.requireKey = requireKey;
    }

    public Crate getCrate() {
        return crate;
    }

    public void setCrate(Crate crate) {
        this.crate = crate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public void setFileConfiguration(FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
    }

    public CParticle getParticle() {
        return particle;
    }

    public void setParticle(CParticle particle) {
        this.particle = particle;
    }

    public CReward getReward() {
        return reward;
    }

    public void setReward(CReward reward) {
        this.reward = reward;
    }

    public CHologram getHologram() {
        return hologram;
    }

    public void setHologram(CHologram holograms) {
        this.hologram = holograms;
    }

    public ObtainType getObtainType() {
        return obtainType;
    }

    public void setObtainType(ObtainType obtainType) {
        this.obtainType = obtainType;
    }

    public CrateAnimationType getCrateType() {
        return crateAnimationType;
    }

    public void setCrateType(CrateAnimationType crateType) {
        this.crateAnimationType = crateType;
    }

    public StatusLogger getStatusLogger() {
        return statusLogger;
    }

    public void setStatusLogger(StatusLogger statusLogger) {
        this.statusLogger = statusLogger;
    }

    public boolean isTiersOverrideDefaults() {
        return tiersOverrideDefaults;
    }

    public void setTiersOverrideDefaults(boolean tiersOverrideDefaults) {
        this.tiersOverrideDefaults = tiersOverrideDefaults;
    }

    public CrateSettingsBuilder getSettingsBuilder() {
        return crateSettingsBuilder;
    }

    public CLuckyChest getLuckyChestSettings() {
        return luckyChest;
    }

    public void setLuckyChestSettings(CLuckyChest luckyChest) {
        this.luckyChest = luckyChest;
    }

    public boolean luckyChestSettingsExists() {
        return luckyChest != null;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public SpecializedCrates getInstance() {
        return instance;
    }

    public KeyItemHandler getKeyItemHandler() {
        return keyItemHandler;
    }

    public void setKeyItemHandler(KeyItemHandler keyItemHandler) {
        this.keyItemHandler = keyItemHandler;
    }

    public CrateItemHandler getCrateItemHandler() {
        return crateItemHandler;
    }

    public void setCrateItemHandler(CrateItemHandler crateItemHandler) {
        this.crateItemHandler = crateItemHandler;
    }

    public DynamicCratePlaceholder getPlaceholder() {
        return dynamicCratePlaceholder;
    }

    public void setPlaceholder(DynamicCratePlaceholder dynamicCratePlaceholder) {
        this.dynamicCratePlaceholder = dynamicCratePlaceholder;
    }

    public CSound getSound() {
        return sound;
    }

    public void setSound(CSound sound) {
        this.sound = sound;
    }

    public CAction getAction() {
        return action;
    }

    public void setAction(CAction action) {
        this.action = action;
    }

    public CFirework getFirework() {
        return firework;
    }

    public void setFirework(CFirework firework) {
        this.firework = firework;
    }

    public CrateDisplayType getCrateDisplayType() {
        return crateDisplayType;
    }

    public void setCrateDisplayType(CrateDisplayType crateDisplayType) {
        this.crateDisplayType = crateDisplayType;
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }

    public void setFileHandler(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    public String getCrateInventoryName() {
        return crateInventoryName;
    }

    public void setCrateInventoryName(String crateInventoryName) {
        this.crateInventoryName = crateInventoryName;
    }

    public CrateAnimation getCrateAnimation() {
        return crateAnimation;
    }

    public void setCrateAnimation(CrateAnimation crateAnimation) {
        this.crateAnimation = crateAnimation;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isAutoClose() {
        return autoClose;
    }

    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }

    public CMultiCrateInventory getMultiCrateSettings() {
        return multiCrateInventory;
    }

    public void setMultiCrateSettings(CMultiCrateInventory multiCrateInventory) {
        this.multiCrateInventory = multiCrateInventory;
    }

    public double getHologramOffset() {
        return hologramOffset;
    }

    public void setHologramOffset(double hologramOffset) {
        this.hologramOffset = hologramOffset;
    }

    public RewardDisplayer getDisplayer() {
        return displayer;
    }

    public void setDisplayer(RewardDisplayer displayer) {
        this.displayer = displayer;
    }

    public RewardDisplayType getRewardDisplayType() {
        return rewardDisplayType;
    }

    public void setRewardDisplayType(RewardDisplayType rewardDisplayType) {
        this.rewardDisplayType = rewardDisplayType;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public boolean isCanFastTrack() {
        return canFastTrack;
    }

    public void setCanFastTrack(boolean canFastTrack) {
        this.canFastTrack = canFastTrack;
    }
}
