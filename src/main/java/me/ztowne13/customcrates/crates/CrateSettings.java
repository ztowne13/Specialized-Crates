package me.ztowne13.customcrates.crates;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.crates.options.*;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.CrateHead;
import me.ztowne13.customcrates.crates.types.CrateType;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.SaveableItemBuilder;
import me.ztowne13.customcrates.logging.StatusLogger;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.CrateUtils;
import me.ztowne13.customcrates.utils.FileHandler;
import me.ztowne13.customcrates.visuals.CrateDisplayType;
import me.ztowne13.customcrates.visuals.DynamicCratePlaceholder;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.nio.file.Path;
import java.util.ArrayList;

public class CrateSettings
{
    CustomCrates cc;
    Crate crates;
    StatusLogger sl;

    String name, crateInventoryName = "", permission = "no permission";
    FileConfiguration fc;
    FileHandler fu;
    CrateSettingsBuilder csb;

    SaveableItemBuilder crate, key;
    boolean requireKey, tiersOverrideDefaults = true, autoClose = true;

    ObtainType ot;
    CrateType ct;
    int cooldown = 0;
    double hologramOffset = 0;

    CrateDisplayType cdt;
    DynamicCratePlaceholder dcp;

    CrateHead ch;
    CHolograms choloCopy;
    CParticles cp;
    CSounds cs;
    CActions ca;
    CFireworks cf;
    CLuckyChest clc;
    CRewards cr;
    CMultiCrateInventory cmci;

    public CrateSettings(CustomCrates cc, Crate crates, boolean newFile)
    {
        this.cc = cc;
        this.crates = crates;
        this.name = crates.getName();
        this.sl = new StatusLogger(cc);

        crate = new SaveableItemBuilder(DynamicMaterial.RED_WOOL, 1);
        crate.setDisplayName("&4Please set me!");
        key = new SaveableItemBuilder(DynamicMaterial.REDSTONE_TORCH, 1);
        key.setDisplayName("&4Please set me!");

        this.fu = new FileHandler(cc, crates.getName() + (crates.isMultiCrate() ? ".multicrate" : ".crate"), "/Crates", true,
                true, newFile);
        this.fc = fu.get();

        this.csb = new CrateSettingsBuilder(this);
    }

    public void saveAll()
    {
        saveIndividualValues();

        if (getOt().equals(ObtainType.LUCKYCHEST))
        {
            getClc().saveToFile();
        }
        getCholoCopy().saveToFile();
        getCp().saveToFile();

        if (!getCrates().isMultiCrate())
        {
            getCr().saveToFile();
            getCs().saveToFile();
            getCa().saveToFile();
            getCf().saveToFile();
        }
        else
        {
            getCmci().saveToFile();
        }


        getFu().save();
    }


    public void saveIndividualValues()
    {
        fc.set("enabled", crates.isEnabled());
        fc.set("cooldown", getCooldown());
        fc.set("obtain-method", getOt().name());
        fc.set("display.type", getDcp().toString());
        fc.set("hologram-offset", getHologramOffset());
        fc.set("auto-close", isAutoClose());
        fc.set("key.require", isRequireKey());

        fc.set("permission", getPermission().equalsIgnoreCase("no permission") ? null : getPermission());

        if (!getDcp().toString().equalsIgnoreCase("block"))
        {
            fc.set("display." + (getDcp().toString().equalsIgnoreCase("mob") ? "creature" : "name"), getDcp().getType());
        }

        if (!(getCrateInventoryName() == null))
        {
            fc.set("inventory-name", ChatUtils.fromChatColor(getCrateInventoryName()));
        }

        getCrate().saveItem(getFu(), "crate");

        if (!getCrates().isMultiCrate())
        {
            getKey().saveItem(getFu(), "key");

            fc.set("open.crate-animation", getCt().name());
        }

    }

//    public void saveCrate()
//    {
//        fc.set("crate.material", getCrate().getType() + ";" + getCrate().getDurability());
//        fc.set("crate.name", ChatUtils.fromChatColor(getCrate().getItemMeta().getDisplayName()));
//
//        if (getCrate().getEnchantments().keySet().iterator().hasNext())
//        {
//            Enchantment e = getCrate().getEnchantments().keySet().iterator().next();
//            fc.set("crate.enchantment", e.getName() + ";" + getCrate().getEnchantments().get(e));
//        }
//
//        if (getCrate().getItemMeta().hasLore())
//        {
//            fc.set("crate.lore", ChatUtils.removeColorFrom(getCrate().getItemMeta().getLore()));
//        }
//
//    }
//
//    public void saveKey()
//    {
//        fc.set("key.material", getKey().getType() + ";" + getKey().getDurability());
//        fc.set("key.name", ChatUtils.fromChatColor(getKey().getItemMeta().getDisplayName()));
//
//        if (getKey().getEnchantments().keySet().iterator().hasNext())
//        {
//            ArrayList<String> enchants = new ArrayList<String>();
//            for (Enchantment enchant : getKey().getEnchantments().keySet())
//            {
//                int lvl = getKey().getEnchantments().get(enchant);
//                enchants.add(enchant.getName() + ";" + lvl);
//            }
//            String updatedPath = fc.contains("key.enchantment") ? "key.enchantment" : "key.enchantments";
//            fc.set(updatedPath, enchants);
//        }
//
//        if (getKey().getItemMeta().hasLore())
//        {
//            fc.set("key.lore", ChatUtils.removeColorFrom(getKey().getItemMeta().getLore()));
//        }
//    }

    public void loadAll()
    {
        // Crate Loging
        String toLog = SettingsValues.LOG_SUCCESSES.getValue(getCrates().getCc()).toString();
        loadNotice(toLog);

        setCp(new CParticles(getCrates()));
        setCholoCopy(new CHolograms(getCrates()));

        if (!getCrates().isMultiCrate())
        {
            setCs(new CSounds(getCrates()));
            setCr(new CRewards(getCrates()));
            setCa(new CActions(getCrates()));
            setCf(new CFireworks(getCrates()));
            setClc(new CLuckyChest(getCrates()));
        }
        else
        {
            setCmci(new CMultiCrateInventory(getCrates()));
        }

        if (getFu().isProperLoad())
        {
            // Base Settings

            getCsb().setupCrate();
            getCsb().setupCooldowns();
            getCsb().setupDisplay();
            getCsb().setupObtainMethod();
            getCsb().setupCrateInventoryName();
            getCsb().setupPermission();
            getCsb().setupAutoClose();
            getCsb().setupHologramOffset();

            // Base Settings for non-MultiCrates
            if (!getCrates().isMultiCrate())
            {
                getCsb().setupKey();
                getCsb().setupCrateAnimation();
            }

            // Particles
            getCp().loadFor(getCsb(), CrateState.PLAY);
            getCp().loadFor(getCsb(), CrateState.OPEN);

            // Holograms
            getCholoCopy().loadFor(getCsb(), CrateState.PLAY);

            // Lucky Chest
            if (getOt().equals(ObtainType.LUCKYCHEST))
            {
                getClc().loadFor(getCsb(), null);
            }

            if (!getCrates().isMultiCrate())
            {
                // Sounds
                getCs().loadFor(getCsb(), CrateState.OPEN);

                // Rewards
                getCr().loadFor(getCsb(), CrateState.OPEN);

                // Actions
                getCa().loadFor(getCsb(), CrateState.OPEN);

                // Fireworks
                getCf().loadFor(getCsb(), CrateState.OPEN);

                getCrates().getCs().getCt().setupFor(crates);
                getCh().loadValueFromConfig(getSl());
            }
            else
            {
                getCmci().loadFor(getCsb(), CrateState.OPEN);
            }


            if (!toLog.equalsIgnoreCase("NOTHING"))
            {
                getSl().logAll();
                ChatUtils.log("");
                ChatUtils.log("-------------------------");
            }
        }
        else
        {
            crates.setEnabled(false);
            crates.setCanBeEnabled(false);
        }
    }

    public void loadNotice(String toLog)
    {
        if (!toLog.equalsIgnoreCase("NOTHING"))
        {
            ChatUtils.log("");
            ChatUtils.log("Loading the '" + getCrates().getName() + "' crate");
            ChatUtils.log("");
            if (!CrateUtils.isCrateUsable(getCrates()))
            {
                ChatUtils.log("NOTICE: This crate is disabled");
                ChatUtils.log("");
            }
        }
    }

    public String deleteCrate()
    {
        Path path = getFu().getDataFile().toPath();
        crates.deleteAllPlaced();
        try
        {
            org.apache.commons.io.FileUtils.forceDelete(getFu().getDataFile());
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
            return "File nonexistent, please try reloading or contacting the plugin author.";
        }
        cc.reload();
        return path.toString();
    }

	/*public void rename(final Player renamer, final String newName, String type)
	{

		ChatUtils.msgInfo(renamer, "Deleting all old placed instances.");
		crates.deleteAllPlaced();

		ChatUtils.msgInfo(renamer, "Renaming...");
		getFu().getDataFile().renameTo(new File(newName + "." + type));

		ChatUtils.msgInfo(renamer, "Reloading plugin, please wait.");

		Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
		{
			@Override
			public void run()
			{
				cc.reload();

				Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
				{
					@Override
					public void run()
					{
						ChatUtils.msgSuccess(renamer, "Crate successfully renamed.");
					}
				}, 20);
			}
		}, 20);
	}*/

    @Deprecated
    public void playAll(Location l, CrateState cs)
    {
        playAll(l, null, cs, null, null);
    }

    @Deprecated
    public void playAll(Location l, CrateState cstate, Player p, ArrayList<Reward> rewards)
    {
        playAll(l, null, cstate, p, rewards);
    }

    public void playAll(Location l, PlacedCrate placedCrate, CrateState cstate, Player p, ArrayList<Reward> rewards)
    {
        getCp().runAll(l, cstate, rewards);
        if (cstate.equals(CrateState.OPEN) && CrateUtils.isCrateUsable(getCrates()))
        {
            cs.runAll(p, l, rewards);
            cf.runAll(p, l, rewards);
            if (rewards != null && !rewards.isEmpty())
            {
                ca.playAll(p, placedCrate, rewards, false);
            }
        }
    }

    public ItemStack getKey(int amount)
    {
        ItemStack stack = key.get().clone();
        stack.setAmount(amount);
        return stack;
    }

    public ItemStack getCrate(int amount)
    {
        ItemStack stack = crate.get().clone();
        stack.setAmount(amount);
        return stack;
    }

    public boolean isRequireKey()
    {
        return requireKey;
    }

    public void setRequireKey(boolean requireKey)
    {
        this.requireKey = requireKey;
    }

    public Crate getCrates()
    {
        return crates;
    }

    public void setCrates(Crate crates)
    {
        this.crates = crates;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public FileConfiguration getFc()
    {
        return fc;
    }

    public void setFc(FileConfiguration fc)
    {
        this.fc = fc;
    }

    public void setKey(SaveableItemBuilder key)
    {
        this.key = key;
    }

    public CParticles getCp()
    {
        return cp;
    }

    public void setCp(CParticles cp)
    {
        this.cp = cp;
    }

    public CRewards getCr()
    {
        return cr;
    }

    public void setCr(CRewards cr)
    {
        this.cr = cr;
    }

    public CHolograms getCholoCopy()
    {
        return choloCopy;
    }

    public void setCholoCopy(CHolograms choloCopy)
    {
        this.choloCopy = choloCopy;
    }

    public void setCrate(SaveableItemBuilder crate)
    {
        this.crate = crate;
    }

    public ObtainType getOt()
    {
        return ot;
    }

    public void setOt(ObtainType ot)
    {
        this.ot = ot;
    }

    public CrateType getCt()
    {
        return ct;
    }

    public void setCt(CrateType ct)
    {
        this.ct = ct;
    }

    public StatusLogger getSl()
    {
        return sl;
    }

    public void setSl(StatusLogger sl)
    {
        this.sl = sl;
    }

    public boolean isTiersOverrideDefaults()
    {
        return tiersOverrideDefaults;
    }

    public void setTiersOverrideDefaults(boolean tiersOverrideDefaults)
    {
        this.tiersOverrideDefaults = tiersOverrideDefaults;
    }

    public CrateSettingsBuilder getCsb()
    {
        return csb;
    }

    public void setCsb(CrateSettingsBuilder csb)
    {
        this.csb = csb;
    }

    public CLuckyChest getClc()
    {
        return clc;
    }

    public void setClc(CLuckyChest clc)
    {
        this.clc = clc;
    }

    public boolean clcExists()
    {
        return clc != null;
    }

    public int getCooldown()
    {
        return cooldown;
    }

    public void setCooldown(int cooldown)
    {
        this.cooldown = cooldown;
    }

    public CustomCrates getCc()
    {
        return cc;
    }

    public void setCc(CustomCrates cc)
    {
        this.cc = cc;
    }

    public SaveableItemBuilder getCrate()
    {
        return crate;
    }

    public SaveableItemBuilder getKey()
    {
        return key;
    }

    public DynamicCratePlaceholder getDcp()
    {
        return dcp;
    }

    public void setDcp(DynamicCratePlaceholder dcp)
    {
        this.dcp = dcp;
    }

    public CSounds getCs()
    {
        return cs;
    }

    public void setCs(CSounds cs)
    {
        this.cs = cs;
    }

    public CActions getCa()
    {
        return ca;
    }

    public void setCa(CActions ca)
    {
        this.ca = ca;
    }

    public CFireworks getCf()
    {
        return cf;
    }

    public void setCf(CFireworks cf)
    {
        this.cf = cf;
    }

    public CrateDisplayType getCdt()
    {
        return cdt;
    }

    public void setCdt(CrateDisplayType cdt)
    {
        this.cdt = cdt;
    }

    public FileHandler getFu()
    {
        return fu;
    }

    public void setFu(FileHandler fu)
    {
        this.fu = fu;
    }

    public String getCrateInventoryName()
    {
        return crateInventoryName;
    }

    public void setCrateInventoryName(String crateInventoryName)
    {
        this.crateInventoryName = crateInventoryName;
    }

    public CrateHead getCh()
    {
        return ch;
    }

    public void setCh(CrateHead ch)
    {
        this.ch = ch;
    }

    public String getPermission()
    {
        return permission;
    }

    public void setPermission(String permission)
    {
        this.permission = permission;
    }

    public boolean isAutoClose()
    {
        return autoClose;
    }

    public void setAutoClose(boolean autoClose)
    {
        this.autoClose = autoClose;
    }

    public CMultiCrateInventory getCmci()
    {
        return cmci;
    }

    public void setCmci(CMultiCrateInventory cmci)
    {
        this.cmci = cmci;
    }

    public double getHologramOffset()
    {
        return hologramOffset;
    }

    public void setHologramOffset(double hologramOffset)
    {
        this.hologramOffset = hologramOffset;
    }

}
