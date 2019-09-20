package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.particles.FireworkData;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CFireworks extends CSetting
{
    HashMap<String, ArrayList<FireworkData>> fireworks = new HashMap<String, ArrayList<FireworkData>>();

    public CFireworks(Crate crates)
    {
        super(crates, crates.getCc());
    }

    @Override
    public void loadFor(CrateSettingsBuilder csb, CrateState cs)
    {
        if (csb.hasV("open.fireworks"))
        {
            addFireworks("OPEN", csb.getSettings().getFc().getStringList("open.fireworks"));
        }
        if (csb.hasV("open.crate-tiers"))
        {
            for (String id : up().getFc().getConfigurationSection("open.crate-tiers").getKeys(false))
            {
                if (csb.hasV("open.crate-tiers." + id + ".fireworks"))
                {
                    addFireworks(id, up().getFc().getStringList("open.crate-tiers." + id + ".fireworks"));
                }
            }
        }
    }

    public void saveToFile()
    {
        if (!fireworks.isEmpty())
        {
            for (String tier : fireworks.keySet())
            {
                ArrayList<String> toSetList = new ArrayList<>();
                for (FireworkData fd : fireworks.get(tier))
                {
                    String serializedFw = "";
                    for (String color : fd.getColors())
                    {
                        serializedFw += color + ";";
                    }

                    serializedFw = serializedFw.substring(0, serializedFw.length() - 1) + ", ";

                    for (String color : fd.getFadeColors())
                    {
                        serializedFw += color + ";";
                    }

                    serializedFw = serializedFw.substring(0, serializedFw.length() - 1);
                    serializedFw += ", " + fd.isTrail();
                    serializedFw += ", " + fd.isFlicker();
                    serializedFw += ", " + fd.getFeType().name();
                    serializedFw += ", " + fd.getPower();
                    toSetList.add(serializedFw);
                }

                String path = "open." + (tier.equalsIgnoreCase("OPEN") ? "" : "crate-tiers." + tier + ".") + ".fireworks";
                getFu().get().set(path, toSetList);
            }
        }
    }

    public void addFireworks(String id, List<String> list)
    {
        for (String firework : list)
        {
            FireworkData fd = new FireworkData(cc, up());
            fd.load(firework);
            addFirework(id, fd);
        }
    }

    public void removeFireworks(String tier, FireworkData fd)
    {
        getFireworks().get(tier).remove(fd);
    }

    public FireworkData getByItemStack(String tier, ItemStack stack)
    {
        ItemBuilder ib = new ItemBuilder(stack);

        String lastLine = "";
        for (String line : ib.im().getLore())
            lastLine = line;

        for (FireworkData fd : getFireworks().get(tier))
        {
            if (fd.getId().equalsIgnoreCase(lastLine))
            {
                return fd;
            }
        }
        return null;
    }

    public void addFirework(String id, FireworkData s)
    {
        if (getFireworks().containsKey(id))
        {
            ArrayList<FireworkData> list = getFireworks().get(id);
            list.add(s);
            getFireworks().put(id, list);
            return;
        }

        ArrayList<FireworkData> list = new ArrayList<FireworkData>();
        list.add(s);

        StatusLoggerEvent.FIREWORK_ADD.log(getCrates(), new String[]{s.getFeType().name(), id});
        getFireworks().put(id, list);
    }

    public void runAll(Player p, Location l, ArrayList<Reward> rewards)
    {
        for (String tier : getFireworks().keySet())
        {
            if ((tier.equalsIgnoreCase("OPEN") && (!up().isTiersOverrideDefaults() || rewards.isEmpty() ||
                    !getFireworks().containsKey(rewards.get(0).getRarity().toUpperCase()))) ||
                    (!rewards.isEmpty() && rewards.get(0).getRarity().equalsIgnoreCase(tier)))
            {
                for (FireworkData fd : getFireworks().get(tier))
                {
                    fd.play(LocationUtils.getLocationCentered(l));
                }
            }
        }
    }

    public HashMap<String, ArrayList<FireworkData>> getFireworks()
    {
        return fireworks;
    }

    public void setFireworks(HashMap<String, ArrayList<FireworkData>> fireworks)
    {
        this.fireworks = fireworks;
    }
}
