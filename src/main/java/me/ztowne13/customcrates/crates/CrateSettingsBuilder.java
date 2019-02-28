package me.ztowne13.customcrates.crates;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.crates.types.CrateType;
import me.ztowne13.customcrates.gui.DynamicMaterial;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.logging.StatusLogger;
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import me.ztowne13.customcrates.visuals.CrateDisplayType;
import me.ztowne13.customcrates.visuals.EntityTypes;
import me.ztowne13.customcrates.visuals.MaterialPlaceholder;
import me.ztowne13.customcrates.visuals.npcs.Citizens2NPCPlaceHolder;
import me.ztowne13.customcrates.visuals.npcs.MobPlaceholder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class CrateSettingsBuilder
{	
	CrateSettings settings;
	FileConfiguration fc;
	CustomCrates cc;
	
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
		if(hasV("auto-close"))
		{
			getSettings().setAutoClose(Boolean.valueOf(getFc().getString("auto-close")));
			StatusLoggerEvent.SETTINGS_AUTOCLOSE_SUCCESS.log(getSl());
			return;
		}
	}

	public void setupHologramOffset()
	{
		if(hasV("hologram-offset"))
		{
			if(Utils.isDouble(fc.getString("hologram-offset")))
			{
				getSettings().setHologramOffset(fc.getDouble("hologram-offset"));
				StatusLoggerEvent.SETTINGS_HOLOGRAMOFFSET_SUCCESS.log(getSl());
				return;
			}
			StatusLoggerEvent.SETTINGS_HOLOGRAMOFFSET_FAILURE.log(getSl());
		}
	}
	
	public void setupObtainMethod()
	{

		if(hasV("obtain-method"))
		{
			try
			{
				ObtainType ot = ObtainType.valueOf(getFc().getString("obtain-method").toUpperCase());
				getSettings().setOt(ot);
				StatusLoggerEvent.SETTINGS_OBTAINMETHOD_SUCCESS.log(getSl());
			}
			catch(Exception exc)
			{
				StatusLoggerEvent.SETTINGS_OBTAINMETHOD_INVALID.log(getSl(), new String[]{getFc().getString("obtain-method")});
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
		if(hasV("open.crate-animation"))
		{
			try
			{
				getSettings().setCt(CrateType.valueOf(getFc().getString(("open.crate-animation"))));
				StatusLoggerEvent.SETTINGS_ANIMATION_SUCCESS.log(getSl());
			}
			catch(Exception exc)
			{
				StatusLoggerEvent.SETTINGS_ANIMATION_INVALID.log(getSl(), new String[]{getFc().getString("open.crate-animation")});
			}
			return;
		}

		StatusLoggerEvent.SETTINGS_ANIMATION_NONEXISTENT.log(getSl());
	}

	public void setupCooldowns()
	{
		if(hasV("cooldown"))
		{
			try
			{
				getSettings().setCooldown(getFc().getInt("cooldown"));
				StatusLoggerEvent.SETTINGS_COOLDOWN_SUCCESS.log(getSl());
			}
			catch(Exception exc)
			{
				StatusLoggerEvent.SETTINGS_COOLDOWN_INVALID.log(getSl());
			}
			return;
		}

		getFc().set("cooldown", 0);
	}

	public void setupDisplay()
	{
		if(hasV("display"))
		{
			CrateDisplayType cdt = CrateDisplayType.BLOCK;

			if(hasV("display.type"))
			{
				try
				{
					cdt = CrateDisplayType.valueOf(getFc().getString("display.type").toUpperCase());
					StatusLoggerEvent.SETTINGS_DISPLAYTYPE_SUCCESS.log(getSl());
				}
				catch(Exception exc)
				{
					StatusLoggerEvent.SETTINGS_DISPLAYTYPE_INVALID.log(getSl(), new String[]{getFc().getString("display.type")});
				}
			}

			if(!Utils.isPLInstalled("Citizens") && !cdt.equals(CrateDisplayType.BLOCK))
			{
				cdt = CrateDisplayType.BLOCK;
				StatusLoggerEvent.SETTINGS_DISPLAYTYPE_FAIL_NOCITIZENS.log(getSl());
			}

			getSettings().setCdt(cdt);

			if(cdt == CrateDisplayType.MOB)
			{
				getSettings().setDcp(new MobPlaceholder(getCc()));
				if(hasV("display.creature"))
				{
					try
					{
						EntityTypes ent = EntityTypes.valueOf(getFc().getString("display.creature").toUpperCase());
						getSettings().getDcp().setType(ent.toString());

						StatusLoggerEvent.SETTINGS_DISPLAYTYPE_CREATURE_SUCCESS.log(getSl());
						return;
					}
					catch(Exception exc)
					{
						exc.printStackTrace();
						StatusLoggerEvent.SETTINGS_DISPLAYTYPE_CREATURETYPE_INVALID.log(getSl(), new String[]{getFc().getString("display.creature")});
					}
				}
				else
				{
					StatusLoggerEvent.SETTINGS_DISPLAYTYPE_CREATURETYPE_NONEXISTENT.log(getSl());
				}
			}
			else if(cdt == CrateDisplayType.NPC)
			{
				getSettings().setDcp(new Citizens2NPCPlaceHolder(getCc()));
				if(hasV("display.name"))
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
		if(hasV("inventory-name"))
		{
			String invName = getFc().getString("inventory-name");
			if(invName.length() < 33)
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
		if(hasV("permission"))
		{
			getSettings().setPermission(getFc().getString("permission"));
			StatusLoggerEvent.SETTINGS_PERMISSION_SUCCESS.log(getSl());
			return;
		}

		getSettings().setPermission("no permission");
	}

	public void setupCrate()
	{
		String cause = "The 'crate.material' value does not exist.";
		if(hasV("crate.material"))
		{
			cause = "The 'crate.name' value does not exist.";
			if(hasV("crate.name"))
			{
				cause = "NONE";
				try
				{
					String unsplitMaterial = getFc().getString("crate.material");
					String[] args = unsplitMaterial.split(";");
					cause = "The material '" + args[0] + "' does not exist.";

					DynamicMaterial m = DynamicMaterial.fromString(unsplitMaterial);

					cause = "The crate.name value does not exist.";
					String name = ChatUtils.toChatColor(getFc().getString(("crate.name")));
					ItemBuilder ib = new ItemBuilder(m, 1).setName(name);
					cause = "NONE";

					if (hasV("crate.lore"))
					{
						for (String value : getFc().getStringList(("crate.lore")))
						{
							ib.addLore(value);
							StatusLoggerEvent.SETTINGS_CRATE_LORE_ADDLINE_SUCCESS.log(getSl(), new String[]{value});
						}
					}


					if (hasV("crate.enchantment"))
					{
						String enchantCause = "crate.enchantment value is improperly set up.";
						try
						{
							String[] enchantArgs = getFc().getString("crate.enchantment").split(";");
							enchantCause = "Enchantment " + enchantArgs[0] + " doesn't exist";
							Enchantment ench = Enchantment.getByName(enchantArgs[0].toUpperCase());

							enchantCause = "The enchantment is improperly formatted. Use ENCHANTMENT;LEVEL";
							enchantCause = enchantArgs[1] + " is not a valid enchantment level number.";
							int level = Integer.parseInt(enchantArgs[1]);

							ib.addEnchantment(ench, level);

							StatusLoggerEvent.SETTINGS_CRATE_ENCHANTMENT_ADD_SUCCESS.log(getSl(), new String[]{enchantArgs[0]});
						}
						catch (Exception exc)
						{
							StatusLoggerEvent.SETTINGS_CRATE_ENCHANTMENT_ADD_FAILURE.log(getSl(), new String[]{enchantCause});
						}
					}

					StatusLoggerEvent.SETTINGS_CRATE_SUCCESS.log(getSl());
					getSettings().setCrate(ib.get());
					return;
				}
				catch (Exception exc)
				{
					getSettings().getCrates().setEnabled(false);
					getSettings().getCrates().setCanBeEnabled(false);

					StatusLoggerEvent.SETTINGS_CRATE_FAILURE_DISABLE.log(getSl(), new String[]{cause});
				}
			}
		}

		StatusLoggerEvent.SETTINGS_CRATE_FAILURE.log(getSl(), new String[]{cause});
	}


	public void setupKey()
	{
		String cause = "The 'key.material' value does not exist.";
		if(hasV("key.material"))
		{
			cause = "The 'key.name' value does not exist.";
			if(hasV("key.name"))
			{
				cause = "NONE";
				try
				{
					String unsplitMaterial = getFc().getString("key.material");
					String[] args = unsplitMaterial.split(";");
					cause = "The material '" + args[0] + "' does not exist.";

					DynamicMaterial m = DynamicMaterial.fromString(unsplitMaterial);

					cause = "NONE";
					String name = ChatUtils.toChatColor(getFc().getString(("key.name")));
					ItemBuilder ib = new ItemBuilder(m, 1).setName(name);

					if(hasV("key.lore"))
					{
						for(String value: getFc().getStringList(("key.lore")))
						{
							ib.addLore(value);
							StatusLoggerEvent.SETTINGS_KEY_LORE_ADDLINE.log(getSl(), new String[]{value});
						}
					}

					if(hasV("key.enchantment"))
					{
						String enchantCause = "Key.enchantment value is improperly set up.";
						try
						{
							String[] enchantArgs = getFc().getString("key.enchantment").split(";");
							enchantCause = "Enchantment " + enchantArgs[0] + " doesn't exist";
							Enchantment ench = Enchantment.getByName(enchantArgs[0].toUpperCase());

							enchantCause = "The enchantment is improperly formatted. Use ENCHANTMENT;LEVEL";
							enchantCause = enchantArgs[1] + " is not a valid enchantment level number.";
							int level = Integer.parseInt(enchantArgs[1]);

							ib.addEnchantment(ench, level);
							StatusLoggerEvent.SETTINGS_KEY_ENCHANTMENT_ADD_SUCCESS.log(getSl(), new String[]{enchantArgs[0]});
						}
						catch(Exception exc)
						{
							StatusLoggerEvent.SETTINGS_KEY_ENCHANTMENT_ADD_FAILURE.log(getSl(), new String[]{enchantCause});
						}
					}

					StatusLoggerEvent.SETTINGS_KEY_SUCCESS.log(getSl());
					getSettings().setKey(ib.get());

					if(hasV("key.require"))
					{
						cause = "The 'key.require' value is not true or false";
						getSettings().setRequireKey(getFc().getBoolean(("key.require")));
					}
					else
					{
						StatusLoggerEvent.SETTINGS_KEY_REQUIRE_NONEXISTENT.log(getSl());
					}

					return;
				}
				catch(Exception exc)
				{
					getSettings().getCrates().setEnabled(false);
					getSettings().getCrates().setCanBeEnabled(false);
					StatusLoggerEvent.SETTINGS_KEY_FAILURE_DISABLE.log(getSl(), new String[]{cause});
				}
			}
		}
		StatusLoggerEvent.SETTINGS_KEY_FAILURE.log(getSl(), new String[]{cause});
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

	public CustomCrates getCc()
	{
		return cc;
	}

	public void setCc(CustomCrates cc)
	{
		this.cc = cc;
	}
}
