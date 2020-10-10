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
    SpecializedCrates cc;
    Crate crates;
    StatusLogger sl;

    String name;
    String crateInventoryName = "";
    String permission = "no permission";
    FileConfiguration fc;
    FileHandler fu;
    CrateSettingsBuilder csb;

    boolean requireKey;
    boolean tiersOverrideDefaults = true;

    ObtainType ot;
    int cooldown = 0;
    int cost = -1;
    double hologramOffset = 0;

    KeyItemHandler keyItemHandler;
    CrateItemHandler crateItemHandler;

    CrateDisplayType cdt = CrateDisplayType.BLOCK;
    DynamicCratePlaceholder dcp;
    RewardDisplayer displayer;
    RewardDisplayType rewardDisplayType;

    CrateAnimation ch;
    CrateAnimationType ct;
    boolean canFastTrack = false;
    boolean autoClose = true;

    CHolograms choloCopy;
    CParticles cp;
    CSounds cs;
    CActions ca;
    CFireworks cf;
    CLuckyChest clc;
    CRewards cr;
    CMultiCrateInventory cmci;

    public CrateSettings(SpecializedCrates cc, Crate crates, boolean newFile) {
        this.cc = cc;
        this.crates = crates;
        this.name = crates.getName();
        this.sl = new StatusLogger(cc);

        this.fu = new FileHandler(cc, crates.getName() + (crates.isMultiCrate() ? ".multicrate" : ".crate"), "/Crates", true,
                true, newFile);
        this.fc = fu.get();

        this.csb = new CrateSettingsBuilder(this);
    }

    public void saveAll() {
        saveIndividualValues();

        getKeyItemHandler().saveToFile();
        getCrateItemHandler().saveToFile();

        if (getObtainType().equals(ObtainType.LUCKYCHEST)) {
            getLuckyChestSettings().saveToFile();
        }
        getHologram().saveToFile();
        getParticles().saveToFile();
        getSounds().saveToFile();

        if (!getCrate().isMultiCrate()) {
            getRewards().saveToFile();
            getActions().saveToFile();
            getFireworks().saveToFile();
            getDisplayer().saveToFile();
        } else {
            getMultiCrateSettings().saveToFile();
        }

        getFileHandler().save();
    }


    public void saveIndividualValues() {
        fc.set("enabled", crates.isEnabled());
        fc.set("cooldown", getCooldown());
        fc.set("obtain-method", getObtainType().name());
        fc.set("display.type", getPlaceholder().toString());
        fc.set("hologram-offset", getHologramOffset());
        fc.set("auto-close", isAutoClose());
        fc.set("key.require", isRequireKey());
        fc.set("cost", getCost());
        fc.set("allow-skip-animation", isCanFastTrack());

        fc.set("permission", getPermission().equalsIgnoreCase("no permission") ? null : getPermission());

        if (!getPlaceholder().toString().equalsIgnoreCase("block")) {
            fc.set("display." + (getPlaceholder().toString().equalsIgnoreCase("mob") ? "creature" : "name"), getPlaceholder().getType());
        }

        if (getCrateInventoryName() != null) {
            fc.set("inventory-name", ChatUtils.fromChatColor(getCrateInventoryName()));
        }

        if (!getCrate().isMultiCrate()) {
            fc.set("open.crate-animation", getCrateType().name());
        }

    }

    public void loadAll() {
        cc.getDu().log("loadAll() - CALL");
        // Crate Loging
        String toLog = SettingsValue.LOG_SUCCESSES.getValue(getCrate().getCc()).toString();
        cc.getDu().log("loadAll() - Preparing to load notice.");
        loadNotice(toLog);
        cc.getDu().log("loadAll() - Loaded notice.");

        setParticles(new CParticles(getCrate()));
        setHologram(new CHolograms(getCrate()));
        setKeyItemHandler(new KeyItemHandler(getCrate(), getSc()));
        setCrateItemHandler(new CrateItemHandler(getCrate(), getSc()));
        setSounds(new CSounds(getCrate()));

        if (!getCrate().isMultiCrate()) {
            setRewards(new CRewards(getCrate()));
            setActions(new CActions(getCrate()));
            setFireworks(new CFireworks(getCrate()));
            setLuckyChestSettings(new CLuckyChest(getCrate()));
        } else {
            setMultiCrateSettings(new CMultiCrateInventory(getCrate()));
        }

        if (getFileHandler().isProperLoad()) {
            // Base Settings

            getCrateItemHandler().loadFor(getSettingsBuilder(), null);
            getSettingsBuilder().setupCooldowns();
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
            getParticles().loadFor(getSettingsBuilder(), CrateState.PLAY);
            getParticles().loadFor(getSettingsBuilder(), CrateState.OPEN);

            // Holograms
            getHologram().loadFor(getSettingsBuilder(), CrateState.PLAY);

            // Sounds
            getSounds().loadFor(getSettingsBuilder(), CrateState.OPEN);

            // Lucky Chest
            if (getObtainType().equals(ObtainType.LUCKYCHEST)) {
                getLuckyChestSettings().loadFor(getSettingsBuilder(), null);
            }

            if (!getCrate().isMultiCrate()) {
                // Rewards
                getRewards().loadFor(getSettingsBuilder(), CrateState.OPEN);

                // Actions
                getActions().loadFor(getSettingsBuilder(), CrateState.OPEN);

                // Fireworks
                getFireworks().loadFor(getSettingsBuilder(), CrateState.OPEN);

                getCrate().getSettings().getCrateType().setupFor(crates);
                getAnimation().loadDataValues(getStatusLogger());
            } else {
                getMultiCrateSettings().loadFor(getSettingsBuilder(), CrateState.OPEN);
            }


            if (!toLog.equalsIgnoreCase("NOTHING")) {
                getStatusLogger().logAll();
            }
        } else {
            crates.setEnabled(false);
            crates.setCanBeEnabled(false);
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
        return crates;
    }

    public void setCrates(Crate crates) {
        this.crates = crates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileConfiguration getFc() {
        return fc;
    }

    public void setFc(FileConfiguration fc) {
        this.fc = fc;
    }

    public CParticles getParticles() {
        return cp;
    }

    public void setParticles(CParticles cp) {
        this.cp = cp;
    }

    public CRewards getRewards() {
        return cr;
    }

    public void setRewards(CRewards cr) {
        this.cr = cr;
    }

    public CHolograms getHologram() {
        return choloCopy;
    }

    public void setHologram(CHolograms choloCopy) {
        this.choloCopy = choloCopy;
    }

    public ObtainType getObtainType() {
        return ot;
    }

    public void setObtainType(ObtainType ot) {
        this.ot = ot;
    }

    public CrateAnimationType getCrateType() {
        return ct;
    }

    public void setCrateType(CrateAnimationType ct) {
        this.ct = ct;
    }

    public StatusLogger getStatusLogger() {
        return sl;
    }

    public void setStatusLogger(StatusLogger sl) {
        this.sl = sl;
    }

    public boolean isTiersOverrideDefaults() {
        return tiersOverrideDefaults;
    }

    public void setTiersOverrideDefaults(boolean tiersOverrideDefaults) {
        this.tiersOverrideDefaults = tiersOverrideDefaults;
    }

    public CrateSettingsBuilder getSettingsBuilder() {
        return csb;
    }

    public CLuckyChest getLuckyChestSettings() {
        return clc;
    }

    public void setLuckyChestSettings(CLuckyChest clc) {
        this.clc = clc;
    }

    public boolean luckyChestSettingsExists() {
        return clc != null;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public SpecializedCrates getSc() {
        return cc;
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
        return dcp;
    }

    public void setPlaceholder(DynamicCratePlaceholder dcp) {
        this.dcp = dcp;
    }

    public CSounds getSounds() {
        return cs;
    }

    public void setSounds(CSounds cs) {
        this.cs = cs;
    }

    public CActions getActions() {
        return ca;
    }

    public void setActions(CActions ca) {
        this.ca = ca;
    }

    public CFireworks getFireworks() {
        return cf;
    }

    public void setFireworks(CFireworks cf) {
        this.cf = cf;
    }

    public CrateDisplayType getCrateDisplayType() {
        return cdt;
    }

    public void setCrateDisplayType(CrateDisplayType cdt) {
        this.cdt = cdt;
    }

    public FileHandler getFileHandler() {
        return fu;
    }

    public void setFileHandler(FileHandler fu) {
        this.fu = fu;
    }

    public String getCrateInventoryName() {
        return crateInventoryName;
    }

    public void setCrateInventoryName(String crateInventoryName) {
        this.crateInventoryName = crateInventoryName;
    }

    public CrateAnimation getAnimation() {
        return ch;
    }

    public void setAnimation(CrateAnimation ch) {
        this.ch = ch;
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
        return cmci;
    }

    public void setMultiCrateSettings(CMultiCrateInventory cmci) {
        this.cmci = cmci;
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
