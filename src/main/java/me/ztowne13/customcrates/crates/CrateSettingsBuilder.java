package me.ztowne13.customcrates.crates;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.RewardDisplayType;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.SimpleRewardDisplayer;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.SortedRewardDisplayer;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom.CustomRewardDisplayer;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.crates.types.display.CrateDisplayType;
import me.ztowne13.customcrates.crates.types.display.EntityType;
import me.ztowne13.customcrates.crates.types.display.MaterialPlaceholder;
import me.ztowne13.customcrates.crates.types.display.npcs.Citizens2NPCPlaceHolder;
import me.ztowne13.customcrates.crates.types.display.npcs.MobPlaceholder;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.NPCUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;

public class CrateSettingsBuilder {
    private final SpecializedCrates instance;
    private CrateSettings settings;
    private FileConfiguration fileConfiguration;

    public CrateSettingsBuilder(CrateSettings settings) {
        this.settings = settings;
        this.fileConfiguration = settings.getFileConfiguration();
        this.instance = settings.getCrate().getInstance();
    }

    public boolean hasValue(String path) {
        return getFileConfiguration().contains((path));
    }

    public void setupAutoClose() {
        if (hasValue("auto-close")) {
            getSettings().setAutoClose(Boolean.parseBoolean(getFileConfiguration().getString("auto-close")));
            StatusLoggerEvent.SETTINGS_AUTOCLOSE_SUCCESS.log(getStatusLogger());
        }
    }

    public void setupHologramOffset() {
        if (hasValue("hologram-offset")) {
            if (Utils.isDouble(fileConfiguration.getString("hologram-offset"))) {
                getSettings().setHologramOffset(fileConfiguration.getDouble("hologram-offset"));
                StatusLoggerEvent.SETTINGS_HOLOGRAMOFFSET_SUCCESS.log(getStatusLogger());
                return;
            }
            StatusLoggerEvent.SETTINGS_HOLOGRAMOFFSET_FAILURE.log(getStatusLogger());
        }
    }

    public void setupRequireKey() {
        if (hasValue("key.require")) {
            getSettings().setRequireKey(getFileConfiguration().getBoolean(("key.require")));
        } else {
            StatusLoggerEvent.SETTINGS_KEY_REQUIRE_NONEXISTENT.log(getStatusLogger());
        }
    }

    public void setupObtainMethod() {

        if (hasValue("obtain-method")) {
            try {
                ObtainType ot = ObtainType.valueOf(getFileConfiguration().getString("obtain-method").toUpperCase());
                getSettings().setObtainType(ot);
                StatusLoggerEvent.SETTINGS_OBTAINMETHOD_SUCCESS.log(getStatusLogger());
            } catch (Exception exc) {
                StatusLoggerEvent.SETTINGS_OBTAINMETHOD_INVALID
                        .log(getStatusLogger(), new String[]{getFileConfiguration().getString("obtain-method")});
            }
            return;
        }

        StatusLoggerEvent.SETTINGS_OBTAINMETHOD_NONEXISTENT.log(getStatusLogger());
    }

	/*public void setupOverrideDefaults()
	{
		if(hasV("open.tier-actions-override-defaults"))
		{
			try
			{
				getSettings().setTiersOverrideDefaults(getSettings().getFc().getBoolean("open.tier-actions-override-defaults"));
				getSl().addEvent(true, "Settings", "Loaded the 'open.tier-actions-override-defaults' value.", "NONE");
			}
			catch(Exception exc)
			{
				getSl().addEvent(false, "Settings", "Failed to load the 'open.tier-actions-override-defaults' value.", getFc().getString("open.tier-actions-override-defaults") + " is not true or false.");
			}
		}
		else
		{
			getSl().addEvent(false, "Settings", "The 'open.tier-actions-override-defaults' value does not exist.", "NONE");
		}
	}*/

    public void setupCrateAnimation() {
        if (hasValue("open.crate-animation")) {
            try {
                getSettings().setCrateType(CrateAnimationType.valueOf(getFileConfiguration().getString(("open.crate-animation"))));
                StatusLoggerEvent.SETTINGS_ANIMATION_SUCCESS.log(getStatusLogger());
            } catch (Exception exc) {
                getSettings().setCrateType(CrateAnimationType.BLOCK_CRATEOPEN);
                StatusLoggerEvent.SETTINGS_ANIMATION_INVALID
                        .log(getStatusLogger(), new String[]{getFileConfiguration().getString("open.crate-animation")});
            }
            return;
        }

        StatusLoggerEvent.SETTINGS_ANIMATION_NONEXISTENT.log(getStatusLogger());
    }

    public void setupCooldown() {
        if (hasValue("cooldown")) {
            try {
                getSettings().setCooldown(getFileConfiguration().getInt("cooldown"));
                StatusLoggerEvent.SETTINGS_COOLDOWN_SUCCESS.log(getStatusLogger());
            } catch (Exception exc) {
                StatusLoggerEvent.SETTINGS_COOLDOWN_INVALID.log(getStatusLogger());
            }
            return;
        }

        getFileConfiguration().set("cooldown", 0);
    }

    public void setupCost() {
        if (hasValue("cost")) {
            try {
                getSettings().setCost(getFileConfiguration().getInt("cost"));
                StatusLoggerEvent.SETTINGS_COST_SUCCESS.log(getStatusLogger());
            } catch (Exception exc) {
                StatusLoggerEvent.SETTINGS_COST_INVALID.log(getStatusLogger());
            }
            return;
        }

        getFileConfiguration().set("cost", -1);
    }

    public void setupAllowSkipAnimation() {
        if (hasValue("allow-skip-animation")) {
            try {
                getSettings().setCanFastTrack(getFileConfiguration().getBoolean("allow-skip-animation"));
                StatusLoggerEvent.SETTINGS_FASTTRACK_SUCCESS.log(getStatusLogger());
            } catch (Exception exc) {
                StatusLoggerEvent.SETTINGS_FASTTRACK_INVALID.log(getStatusLogger());
            }
            return;
        }

        getFileConfiguration().set("allow-skip-animation", false);
    }

    public void setupDisplay() {
        if (hasValue("display")) {
            CrateDisplayType cdt = CrateDisplayType.BLOCK;

            if (hasValue("display.type")) {
                try {
                    cdt = CrateDisplayType.valueOf(getFileConfiguration().getString("display.type").toUpperCase());
                    StatusLoggerEvent.SETTINGS_DISPLAYTYPE_SUCCESS.log(getStatusLogger());
                } catch (Exception exc) {
                    StatusLoggerEvent.SETTINGS_DISPLAYTYPE_INVALID
                            .log(getStatusLogger(), new String[]{getFileConfiguration().getString("display.type")});
                }
            }

            if (!NPCUtils.isCitizensInstalled() && !cdt.equals(CrateDisplayType.BLOCK)) {
                cdt = CrateDisplayType.BLOCK;
                StatusLoggerEvent.SETTINGS_DISPLAYTYPE_FAIL_NOCITIZENS.log(getStatusLogger());
            }

            getSettings().setCrateDisplayType(cdt);

            if (cdt == CrateDisplayType.MOB) {
                getSettings().setPlaceholder(new MobPlaceholder(instance));
                if (hasValue("display.creature")) {
                    try {
                        EntityType ent = EntityType.getEnum(getFileConfiguration().getString("display.creature").toUpperCase());
                        getSettings().getPlaceholder().setType(ent.toString());

                        StatusLoggerEvent.SETTINGS_DISPLAYTYPE_CREATURE_SUCCESS.log(getStatusLogger());
                        return;
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        StatusLoggerEvent.SETTINGS_DISPLAYTYPE_CREATURETYPE_INVALID
                                .log(getStatusLogger(), new String[]{getFileConfiguration().getString("display.creature")});
                    }
                } else {
                    StatusLoggerEvent.SETTINGS_DISPLAYTYPE_CREATURETYPE_NONEXISTENT.log(getStatusLogger());
                }
            } else if (cdt == CrateDisplayType.NPC) {
                getSettings().setPlaceholder(new Citizens2NPCPlaceHolder(instance));
                if (hasValue("display.name")) {
                    getSettings().getPlaceholder().setType(getFileConfiguration().getString("display.name"));

                    StatusLoggerEvent.SETTINGS_DISPLAYTYPE_DISPLAYNAME_SUCCESS.log(getStatusLogger());
                    return;
                }
                StatusLoggerEvent.SETTINGS_DISPLAYTYPE_DISPLAYNAME_NONEXISTENT.log(getStatusLogger());
            }

            getSettings().setPlaceholder(new MaterialPlaceholder(instance));
            return;
        }

        StatusLoggerEvent.SETTINGS_DISPLAYTYPE_NONEXISTENT.log(getStatusLogger());
        getFileConfiguration().set("display.type", "block");
        getSettings().getFileHandler().save();
        setupDisplay();
    }

    public void setupCrateInventoryName() {
        if (hasValue("inventory-name")) {
            String invName = getFileConfiguration().getString("inventory-name");
            if (invName.length() < 33) {
                getSettings().setCrateInventoryName(invName);
                StatusLoggerEvent.SETTINGS_INVENTORYNAME_SUCCESS.log(getStatusLogger());
                return;
            }

            getSettings().setCrateInventoryName(invName.substring(0, 33));
            StatusLoggerEvent.SETTINGS_INVENTORYNAME_INVALID.log(getStatusLogger());
            return;
        }
        StatusLoggerEvent.SETTINGS_INVENTORYNAME_NONEXISTENT.log(getStatusLogger());
    }

    public void setupPermission() {
        if (hasValue("permission")) {
            getSettings().setPermission(getFileConfiguration().getString("permission"));
            StatusLoggerEvent.SETTINGS_PERMISSION_SUCCESS.log(getStatusLogger());
            return;
        }

        getSettings().setPermission("no permission");
    }

    public void setupDisplayer() {
        if (hasValue("reward-display.type")) {
            String displayerString = getFileConfiguration().getString("reward-display.type").toUpperCase();

            try {
                RewardDisplayType rewardDisplayType = RewardDisplayType.valueOf(displayerString);
                Crate crate = getSettings().getCrate();
                getSettings().setRewardDisplayType(rewardDisplayType);

                switch (rewardDisplayType) {
                    case CUSTOM:
                        getSettings().setDisplayer(new CustomRewardDisplayer(crate));
                        break;
                    case IN_ORDER:
                        getSettings().setDisplayer(new SimpleRewardDisplayer(crate));
                        break;
                    case SORTED_HIGH_TO_LOW:
                        getSettings().setDisplayer(new SortedRewardDisplayer(crate, false));
                        break;
                    case SORTED_LOW_TO_HIGH:
                        getSettings().setDisplayer(new SortedRewardDisplayer(crate, true));
                        break;
                }

                StatusLoggerEvent.SETTINGS_REWARD_DISPLAYER_SUCCESS.log(getStatusLogger());
            } catch (Exception exc) {
                getSettings().setRewardDisplayType(RewardDisplayType.IN_ORDER);
                getSettings().setDisplayer(new SimpleRewardDisplayer(getSettings().getCrate()));
                StatusLoggerEvent.SETTINGS_REWARD_DISPLAYER_INVALID.log(getStatusLogger(), new String[]{displayerString});
            }

            getSettings().getDisplayer().load();

            return;
        }

        getSettings().setRewardDisplayType(RewardDisplayType.IN_ORDER);
        getSettings().setDisplayer(new SimpleRewardDisplayer(getSettings().getCrate()));
        //StatusLoggerEvent.SETTINGS_REWARD_DISPLAYER_NONEXISTENT.log(getSl());

        getSettings().getDisplayer().load();

    }


    public StatusLogger getStatusLogger() {
        return getSettings().getStatusLogger();
    }

    public CrateSettings getSettings() {
        return settings;
    }

    public void setSettings(CrateSettings settings) {
        this.settings = settings;
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public void setFileConfiguration(FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
    }
}
