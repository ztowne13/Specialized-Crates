package me.ztowne13.customcrates.crates;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.RewardDisplayType;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.SimpleRewardDisplayer;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.SortedRewardDisplayer;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom.CustomRewardDisplayer;
import me.ztowne13.customcrates.crates.types.animations.CrateType;
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
        this.cc = settings.getCrates().getCc();
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
            StatusLoggerEvent.SETTINGS_AUTOCLOSE_SUCCESS.log(getSl());
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
                StatusLoggerEvent.SETTINGS_HOLOGRAMOFFSET_SUCCESS.log(getSl());
                return;
            }
            StatusLoggerEvent.SETTINGS_HOLOGRAMOFFSET_FAILURE.log(getSl());
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
            StatusLoggerEvent.SETTINGS_KEY_REQUIRE_NONEXISTENT.log(getSl());
        }
    }

    public void setupObtainMethod()
    {

        if (hasV("obtain-method"))
        {
            try
            {
                ObtainType ot = ObtainType.valueOf(getFc().getString("obtain-method").toUpperCase());
                getSettings().setOt(ot);
                StatusLoggerEvent.SETTINGS_OBTAINMETHOD_SUCCESS.log(getSl());
            }
            catch (Exception exc)
            {
                StatusLoggerEvent.SETTINGS_OBTAINMETHOD_INVALID
                        .log(getSl(), new String[]{getFc().getString("obtain-method")});
            }
            return;
        }

        StatusLoggerEvent.SETTINGS_OBTAINMETHOD_NONEXISTENT.log(getSl());
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
                getSettings().setCt(CrateType.valueOf(getFc().getString(("open.crate-animation"))));
                StatusLoggerEvent.SETTINGS_ANIMATION_SUCCESS.log(getSl());
            }
            catch (Exception exc)
            {
                getSettings().setCt(CrateType.BLOCK_CRATEOPEN);
                StatusLoggerEvent.SETTINGS_ANIMATION_INVALID
                        .log(getSl(), new String[]{getFc().getString("open.crate-animation")});
            }
            return;
        }

        StatusLoggerEvent.SETTINGS_ANIMATION_NONEXISTENT.log(getSl());
    }

    public void setupCooldowns()
    {
        if (hasV("cooldown"))
        {
            try
            {
                getSettings().setCooldown(getFc().getInt("cooldown"));
                StatusLoggerEvent.SETTINGS_COOLDOWN_SUCCESS.log(getSl());
            }
            catch (Exception exc)
            {
                StatusLoggerEvent.SETTINGS_COOLDOWN_INVALID.log(getSl());
            }
            return;
        }

        getFc().set("cooldown", 0);
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
                    StatusLoggerEvent.SETTINGS_DISPLAYTYPE_SUCCESS.log(getSl());
                }
                catch (Exception exc)
                {
                    StatusLoggerEvent.SETTINGS_DISPLAYTYPE_INVALID
                            .log(getSl(), new String[]{getFc().getString("display.type")});
                }
            }

            if (!NPCUtils.isCitizensInstalled() && !cdt.equals(CrateDisplayType.BLOCK))
            {
                cdt = CrateDisplayType.BLOCK;
                StatusLoggerEvent.SETTINGS_DISPLAYTYPE_FAIL_NOCITIZENS.log(getSl());
            }

            getSettings().setCdt(cdt);

            if (cdt == CrateDisplayType.MOB)
            {
                getSettings().setDcp(new MobPlaceholder(getCc()));
                if (hasV("display.creature"))
                {
                    try
                    {
                        EntityTypes ent = EntityTypes.valueOf(getFc().getString("display.creature").toUpperCase());
                        getSettings().getDcp().setType(ent.toString());

                        StatusLoggerEvent.SETTINGS_DISPLAYTYPE_CREATURE_SUCCESS.log(getSl());
                        return;
                    }
                    catch (Exception exc)
                    {
                        exc.printStackTrace();
                        StatusLoggerEvent.SETTINGS_DISPLAYTYPE_CREATURETYPE_INVALID
                                .log(getSl(), new String[]{getFc().getString("display.creature")});
                    }
                }
                else
                {
                    StatusLoggerEvent.SETTINGS_DISPLAYTYPE_CREATURETYPE_NONEXISTENT.log(getSl());
                }
            }
            else if (cdt == CrateDisplayType.NPC)
            {
                getSettings().setDcp(new Citizens2NPCPlaceHolder(getCc()));
                if (hasV("display.name"))
                {
                    getSettings().getDcp().setType(getFc().getString("display.name"));

                    StatusLoggerEvent.SETTINGS_DISPLAYTYPE_DISPLAYNAME_SUCCESS.log(getSl());
                    return;
                }
                StatusLoggerEvent.SETTINGS_DISPLAYTYPE_DISPLAYNAME_NONEXISTENT.log(getSl());
            }

            getSettings().setDcp(new MaterialPlaceholder(getCc()));
            return;
        }

        StatusLoggerEvent.SETTINGS_DISPLAYTYPE_NONEXISTENT.log(getSl());
        getFc().set("display.type", "block");
        getSettings().getFu().save();
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
                StatusLoggerEvent.SETTINGS_INVENTORYNAME_SUCCESS.log(getSl());
                return;
            }

            getSettings().setCrateInventoryName(invName.substring(0, 33));
            StatusLoggerEvent.SETTINGS_INVENTORYNAME_INVALID.log(getSl());
            return;
        }
        StatusLoggerEvent.SETTINGS_INVENTORYNAME_NONEXISTENT.log(getSl());
    }

    public void setupPermission()
    {
        if (hasV("permission"))
        {
            getSettings().setPermission(getFc().getString("permission"));
            StatusLoggerEvent.SETTINGS_PERMISSION_SUCCESS.log(getSl());
            return;
        }

        getSettings().setPermission("no permission");
    }

    public void setupCrate()
    {
        boolean result = getSettings().getCrate()
                .loadItem(getSettings().getFu(), "crate", getSl(), StatusLoggerEvent.SETTINGS_CRATE_FAILURE,
                        StatusLoggerEvent.SETTINGS_CRATE_ENCHANTMENT_ADD_FAILURE,
                        StatusLoggerEvent.SETTINGS_CRATE_POTION_ADD_FAILURE, StatusLoggerEvent.SETTINGS_CRATE_GLOW_FAILURE,
                        StatusLoggerEvent.SETTINGS_CRATE_AMOUNT_FAILURE, StatusLoggerEvent.SETTINGS_CRATE_FLAG_FAILURE);
        if (!result)
        {
            StatusLoggerEvent.SETTINGS_CRATE_FAILURE_DISABLE.log(getSl());
        }
    }

    public void setupKey()
    {
        //itemfail, improperenchamnt, improperpotion, improperglow
        setupRequireKey();
        boolean result = getSettings().getKey()
                .loadItem(getSettings().getFu(), "key", getSl(), StatusLoggerEvent.SETTINGS_KEY_FAILURE,
                        StatusLoggerEvent.SETTINGS_KEY_ENCHANTMENT_ADD_FAILURE,
                        StatusLoggerEvent.SETTINGS_KEY_POTION_ADD_FAILURE, StatusLoggerEvent.SETTINGS_KEY_GLOW_FAILURE,
                        StatusLoggerEvent.SETTINGS_KEY_AMOUNT_FAILURE, StatusLoggerEvent.SETTINGS_KEY_FLAG_FAILURE);

        if (!result)
        {
            StatusLoggerEvent.SETTINGS_KEY_FAILURE_DISABLE.log(getSl());
        }
    }

    public void setupDisplayer()
    {
        if (hasV("reward-display.type"))
        {
            String displayerString = getFc().getString("reward-display.type").toUpperCase();

            try
            {
                RewardDisplayType rewardDisplayType = RewardDisplayType.valueOf(displayerString);
                Crate crate = getSettings().getCrates();
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

                getSettings().getDisplayer().load();

                StatusLoggerEvent.SETTINGS_REWARD_DISPLAYER_SUCCESS.log(getSl());
            }
            catch(Exception exc)
            {
                getSettings().setRewardDisplayType(RewardDisplayType.IN_ORDER);
                getSettings().setDisplayer(new SimpleRewardDisplayer(getSettings().getCrates()));
                StatusLoggerEvent.SETTINGS_REWARD_DISPLAYER_INVALID.log(getSl(), new String[]{displayerString});
            }
            return;
        }

        getSettings().setRewardDisplayType(RewardDisplayType.IN_ORDER);
        getSettings().setDisplayer(new SimpleRewardDisplayer(getSettings().getCrates()));
        StatusLoggerEvent.SETTINGS_REWARD_DISPLAYER_NONEXISTENT.log(getSl());
    }


    public StatusLogger getSl()
    {
        return getSettings().getSl();
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
