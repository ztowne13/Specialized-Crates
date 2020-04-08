package me.ztowne13.customcrates.crates;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.RewardDisplayType;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.SimpleRewardDisplayer;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.SortedRewardDisplayer;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom.CustomRewardDisplayer;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.crates.types.display.CrateDisplayType;
import me.ztowne13.customcrates.crates.types.display.EntityTypes;
import me.ztowne13.customcrates.crates.types.display.MaterialPlaceholder;
import me.ztowne13.customcrates.crates.types.display.npcs.Citizens2NPCPlaceHolder;
import me.ztowne13.customcrates.crates.types.display.npcs.MobPlaceholder;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.NPCUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;

public class CrateSettingsBuilder
{
    CrateSettings settings;
    FileConfiguration fc;
    SpecializedCrates cc;

    public CrateSettingsBuilder(CrateSettings settings)
    {
        this.settings = settings;
        this.fc = settings.getFc();
        this.cc = settings.getCrate().getCc();
    }

    public boolean hasV(String path)
    {
        return getFc().contains((path));
    }

    public void setupAutoClose()
    {
        if (hasV("auto-close"))
        {
            getSettings().setAutoClose(Boolean.valueOf(getFc().getString("auto-close")));
            StatusLoggerEvent.SETTINGS_AUTOCLOSE_SUCCESS.log(getStatusLogger());
            return;
        }
    }

    public void setupHologramOffset()
    {
        if (hasV("hologram-offset"))
        {
            if (Utils.isDouble(fc.getString("hologram-offset")))
            {
                getSettings().setHologramOffset(fc.getDouble("hologram-offset"));
                StatusLoggerEvent.SETTINGS_HOLOGRAMOFFSET_SUCCESS.log(getStatusLogger());
                return;
            }
            StatusLoggerEvent.SETTINGS_HOLOGRAMOFFSET_FAILURE.log(getStatusLogger());
        }
    }

    public void setupRequireKey()
    {
        if (hasV("key.require"))
        {
            getSettings().setRequireKey(getFc().getBoolean(("key.require")));
        }
        else
        {
            StatusLoggerEvent.SETTINGS_KEY_REQUIRE_NONEXISTENT.log(getStatusLogger());
        }
    }

    public void setupObtainMethod()
    {

        if (hasV("obtain-method"))
        {
            try
            {
                ObtainType ot = ObtainType.valueOf(getFc().getString("obtain-method").toUpperCase());
                getSettings().setObtainType(ot);
                StatusLoggerEvent.SETTINGS_OBTAINMETHOD_SUCCESS.log(getStatusLogger());
            }
            catch (Exception exc)
            {
                StatusLoggerEvent.SETTINGS_OBTAINMETHOD_INVALID
                        .log(getStatusLogger(), new String[]{getFc().getString("obtain-method")});
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

    public void setupCrateAnimation()
    {
        if (hasV("open.crate-animation"))
        {
            try
            {
                getSettings().setCrateType(CrateAnimationType.valueOf(getFc().getString(("open.crate-animation"))));
                StatusLoggerEvent.SETTINGS_ANIMATION_SUCCESS.log(getStatusLogger());
            }
            catch (Exception exc)
            {
                getSettings().setCrateType(CrateAnimationType.BLOCK_CRATEOPEN);
                StatusLoggerEvent.SETTINGS_ANIMATION_INVALID
                        .log(getStatusLogger(), new String[]{getFc().getString("open.crate-animation")});
            }
            return;
        }

        StatusLoggerEvent.SETTINGS_ANIMATION_NONEXISTENT.log(getStatusLogger());
    }

    public void setupCooldowns()
    {
        if (hasV("cooldown"))
        {
            try
            {
                getSettings().setCooldown(getFc().getInt("cooldown"));
                StatusLoggerEvent.SETTINGS_COOLDOWN_SUCCESS.log(getStatusLogger());
            }
            catch (Exception exc)
            {
                StatusLoggerEvent.SETTINGS_COOLDOWN_INVALID.log(getStatusLogger());
            }
            return;
        }

        getFc().set("cooldown", 0);
    }

    public void setupCost()
    {
        if (hasV("cost"))
        {
            try
            {
                getSettings().setCost(getFc().getInt("cost"));
                StatusLoggerEvent.SETTINGS_COST_SUCCESS.log(getStatusLogger());
            }
            catch (Exception exc)
            {
                StatusLoggerEvent.SETTINGS_COST_INVALID.log(getStatusLogger());
            }
            return;
        }

        getFc().set("cost", -1);
    }

    public void setupDisplay()
    {
        if (hasV("display"))
        {
            CrateDisplayType cdt = CrateDisplayType.BLOCK;

            if (hasV("display.type"))
            {
                try
                {
                    cdt = CrateDisplayType.valueOf(getFc().getString("display.type").toUpperCase());
                    StatusLoggerEvent.SETTINGS_DISPLAYTYPE_SUCCESS.log(getStatusLogger());
                }
                catch (Exception exc)
                {
                    StatusLoggerEvent.SETTINGS_DISPLAYTYPE_INVALID
                            .log(getStatusLogger(), new String[]{getFc().getString("display.type")});
                }
            }

            if (!NPCUtils.isCitizensInstalled() && !cdt.equals(CrateDisplayType.BLOCK))
            {
                cdt = CrateDisplayType.BLOCK;
                StatusLoggerEvent.SETTINGS_DISPLAYTYPE_FAIL_NOCITIZENS.log(getStatusLogger());
            }

            getSettings().setCrateDisplayType(cdt);

            if (cdt == CrateDisplayType.MOB)
            {
                getSettings().setPlaceholder(new MobPlaceholder(getCc()));
                if (hasV("display.creature"))
                {
                    try
                    {
                        EntityTypes ent = EntityTypes.valueOf(getFc().getString("display.creature").toUpperCase());
                        getSettings().getPlaceholder().setType(ent.toString());

                        StatusLoggerEvent.SETTINGS_DISPLAYTYPE_CREATURE_SUCCESS.log(getStatusLogger());
                        return;
                    }
                    catch (Exception exc)
                    {
                        exc.printStackTrace();
                        StatusLoggerEvent.SETTINGS_DISPLAYTYPE_CREATURETYPE_INVALID
                                .log(getStatusLogger(), new String[]{getFc().getString("display.creature")});
                    }
                }
                else
                {
                    StatusLoggerEvent.SETTINGS_DISPLAYTYPE_CREATURETYPE_NONEXISTENT.log(getStatusLogger());
                }
            }
            else if (cdt == CrateDisplayType.NPC)
            {
                getSettings().setPlaceholder(new Citizens2NPCPlaceHolder(getCc()));
                if (hasV("display.name"))
                {
                    getSettings().getPlaceholder().setType(getFc().getString("display.name"));

                    StatusLoggerEvent.SETTINGS_DISPLAYTYPE_DISPLAYNAME_SUCCESS.log(getStatusLogger());
                    return;
                }
                StatusLoggerEvent.SETTINGS_DISPLAYTYPE_DISPLAYNAME_NONEXISTENT.log(getStatusLogger());
            }

            getSettings().setPlaceholder(new MaterialPlaceholder(getCc()));
            return;
        }

        StatusLoggerEvent.SETTINGS_DISPLAYTYPE_NONEXISTENT.log(getStatusLogger());
        getFc().set("display.type", "block");
        getSettings().getFileHandler().save();
        setupDisplay();
    }

    public void setupCrateInventoryName()
    {
        if (hasV("inventory-name"))
        {
            String invName = getFc().getString("inventory-name");
            if (invName.length() < 33)
            {
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

    public void setupPermission()
    {
        if (hasV("permission"))
        {
            getSettings().setPermission(getFc().getString("permission"));
            StatusLoggerEvent.SETTINGS_PERMISSION_SUCCESS.log(getStatusLogger());
            return;
        }

        getSettings().setPermission("no permission");
    }

    public void setupDisplayer()
    {
        if (hasV("reward-display.type"))
        {
            String displayerString = getFc().getString("reward-display.type").toUpperCase();

            try
            {
                RewardDisplayType rewardDisplayType = RewardDisplayType.valueOf(displayerString);
                Crate crate = getSettings().getCrate();
                getSettings().setRewardDisplayType(rewardDisplayType);

                switch(rewardDisplayType)
                {
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
            }
            catch(Exception exc)
            {
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


    public StatusLogger getStatusLogger()
    {
        return getSettings().getStatusLogger();
    }

    public CrateSettings getSettings()
    {
        return settings;
    }

    public void setSettings(CrateSettings settings)
    {
        this.settings = settings;
    }

    public FileConfiguration getFc()
    {
        return fc;
    }

    public void setFc(FileConfiguration fc)
    {
        this.fc = fc;
    }

    public SpecializedCrates getCc()
    {
        return cc;
    }

    public void setCc(SpecializedCrates cc)
    {
        this.cc = cc;
    }
}
