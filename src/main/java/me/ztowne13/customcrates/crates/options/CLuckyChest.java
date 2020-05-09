package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ztowne13 on 8/3/15.
 */
public class CLuckyChest extends CSetting
{

    double chance, outOfChance;
    boolean isBLWL;
    boolean requirePermission = true;
    ArrayList<Material> whiteList = new ArrayList<>();
    ArrayList<World> worlds = new ArrayList<World>();

    List<String> worldsRaw = new ArrayList<>();
    boolean allWorlds = true;

    public CLuckyChest(Crate crates)
    {
        super(crates, crates.getCc());
    }

    @Override
    public void loadFor(CrateSettingsBuilder csb, CrateState cs)
    {
        FileConfiguration fc = up().getFc();
        StatusLogger sl = up().getStatusLogger();

        if (csb.hasV("lucky-chest"))
        {
            if (csb.hasV("lucky-chest.chance"))
            {
                String unParsedChance = fc.getString("lucky-chest.chance");
                String[] args = unParsedChance.split("/");
                try
                {
                    setChance(Integer.parseInt(args[0]));
                    setOutOfChance(Integer.parseInt(args[1]));
                }
                catch (Exception exc)
                {
                    StatusLoggerEvent.LUCKYCHEST_CHANCE_MISFORMATTED.log(getCrate());
                }
            }
            else
            {
                chance = 1;
                outOfChance = 100;
                StatusLoggerEvent.LUCKYCHEST_CHANCE_NONEXISTENT.log(getCrate());
            }

            if (csb.hasV("lucky-chest.is-block-list-whitelist"))
            {
                try
                {
                    setBLWL(fc.getBoolean("lucky-chest.is-block-list-whitelist"));
                }
                catch (Exception exc)
                {
                    StatusLoggerEvent.LUCKYCHEST_BLWL_INVALID.log(getCrate());
                }
            }
            else
            {
                setBLWL(true);
                StatusLoggerEvent.LUCKYCHEST_BLWL_NONEXISTENT.log(getCrate());
            }

            if (csb.hasV("lucky-chest.require-permission"))
            {
                try
                {
                    requirePermission = fc.getBoolean("lucky-chest.require-permission");
                }
                catch (Exception exc)
                {
                    StatusLoggerEvent.LUCKYCHEST_REQUIRE_PERMISSION_INVALID.log(getCrate());
                }
            }
            else
            {
                requirePermission = true;
                StatusLoggerEvent.LUCKYCHEST_REQUIRE_PERMISSION_NONEXISTENT.log(getCrate());
            }

            if (csb.hasV("lucky-chest.worlds"))
            {
                worldsRaw = fc.getStringList("lucky-chest.worlds");

                for (String s : fc.getStringList("lucky-chest.worlds"))
                {
                    setAllWorlds(false);
                    World w = Bukkit.getWorld(s);
                    if (w != null)
                    {
                        getWorlds().add(w);
                    }
                    else
                    {
                        StatusLoggerEvent.LUCKYCHEST_WORLD_INVALID.log(getCrate(), new String[]{s});
                    }
                }
            }

            if (csb.hasV("lucky-chest.block-list"))
            {
                try
                {
                    for (String mat : fc.getStringList("lucky-chest.block-list"))
                    {
                        try
                        {
                            DynamicMaterial m = DynamicMaterial.fromString(mat.toUpperCase());
                            getWhiteList().add(m.parseMaterial());
                        }
                        catch (Exception exc)
                        {
                            StatusLoggerEvent.LUCKYCHEST_BLOCKLIST_INVALIDBLOCK.log(getCrate(), new String[]{mat});
                        }
                    }
                }
                catch (Exception exc)
                {
                    StatusLoggerEvent.LUCKYCHEST_BLOCKLIST_INVALID.log(getCrate());
                }
            }
            else
            {
                StatusLoggerEvent.LUCKYCHEST_BLOCKLIST_NONEXISTENT.log(getCrate());
            }
        }
        else
        {
            StatusLoggerEvent.LUCKYCHEST_NOVALUES.log(getCrate());
        }
    }

    public void saveToFile()
    {
        getFu().get().set("lucky-chest.chance", ((int) chance) + "/" + ((int) outOfChance));
        getFu().get().set("lucky-chest.require-permission", isRequirePermission());
        if (!whiteList.isEmpty())
        {
            getFu().get().set("lucky-chest.is-block-list-whitelist", isBLWL);

            ArrayList<String> whiteListRaw = new ArrayList<>();
            for(Material mat : getWhiteList())
                whiteListRaw.add(mat.name());

            getFu().get().set("lucky-chest.block-list", whiteListRaw);
        }
        else
        {
            getFu().get().set("lucky-chest.is-block-list-whitelist", null);
            getFu().get().set("lucky-chest.block-list", null);
        }
        if (!worldsRaw.isEmpty())
        {
            getFu().get().set("lucky-chest.worlds", worldsRaw);
        }
        else
        {
            getFu().get().set("lucky-chest.worlds", null);
        }
    }

    public boolean canRunForBlock(Block b)
    {
        if (isAllWorlds() || getWorlds().contains(b.getWorld()))
        {
            if (getWhiteList().isEmpty())
            {
                return true;
            }

            return isBLWL() ? getWhiteList().contains(b.getType()) : !getWhiteList().contains(b.getType());
        }
        return false;
    }

    public boolean runChance()
    {
        Random r = new Random();
        return (r.nextInt(((int) getOutOfChance())) + 1) <= getChance();
    }

    public boolean checkRun(Block b)
    {
        return canRunForBlock(b) && runChance();
    }

    public double getChance()
    {
        return chance;
    }

    public void setChance(double chance)
    {
        this.chance = chance;
    }

    public double getOutOfChance()
    {
        return outOfChance;
    }

    public void setOutOfChance(double outOfChance)
    {
        this.outOfChance = outOfChance;
    }

    public boolean isBLWL()
    {
        return isBLWL;
    }

    public void setBLWL(boolean BLWL)
    {
        isBLWL = BLWL;
    }

    public ArrayList<Material> getWhiteList()
    {
        return whiteList;
    }

    public void setWhiteList(ArrayList<Material> whiteList)
    {
        this.whiteList = whiteList;
    }

    public ArrayList<World> getWorlds()
    {
        return worlds;
    }

    public void setWorlds(ArrayList<World> worlds)
    {
        this.worlds = worlds;
    }

    public boolean isAllWorlds()
    {
        return allWorlds;
    }

    public void setAllWorlds(boolean allWorlds)
    {
        this.allWorlds = allWorlds;
    }

    public List<String> getWorldsRaw()
    {
        return worldsRaw;
    }

    public void setWorldsRaw(List<String> worldsRaw)
    {
        this.worldsRaw = worldsRaw;
    }

    public boolean isRequirePermission()
    {
        return requirePermission;
    }

    public void setRequirePermission(boolean requirePermission)
    {
        this.requirePermission = requirePermission;
    }
}

