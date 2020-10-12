package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.particles.FireworkData;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFireworks extends CSetting {
    Map<String, List<FireworkData>> fireworks = new HashMap<>();

    public CFireworks(Crate crates) {
        super(crates, crates.getCc());
    }

    @Override
    public void loadFor(CrateSettingsBuilder csb, CrateState cs) {
        if (csb.hasV("open.fireworks")) {
            addFireworks("OPEN", csb.getSettings().getFc().getStringList("open.fireworks"));
        }
        if (csb.hasV("open.crate-tiers")) {
            for (String id : getSettings().getFc().getConfigurationSection("open.crate-tiers").getKeys(false)) {
                if (csb.hasV("open.crate-tiers." + id + ".fireworks")) {
                    addFireworks(id, getSettings().getFc().getStringList("open.crate-tiers." + id + ".fireworks"));
                }
            }
        }
    }

    public void saveToFile() {
        if (!fireworks.isEmpty()) {
            for (Map.Entry<String, List<FireworkData>> entry : fireworks.entrySet()) {
                String tier = entry.getKey();
                ArrayList<String> toSetList = new ArrayList<>();
                for (FireworkData fd : entry.getValue()) {
//                    String serializedFw = "";
//                    for (String color : fd.getColors())
//                    {
//                        serializedFw += color + ";";
//                    }
//
//                    serializedFw = serializedFw.substring(0, serializedFw.length() - 1) + ", ";
//
//                    for (String color : fd.getFadeColors())
//                    {
//                        serializedFw += color + ";";
//                    }
//
//                    serializedFw = serializedFw.substring(0, serializedFw.length() - 1);
//                    serializedFw += ", " + fd.isTrail();
//                    serializedFw += ", " + fd.isFlicker();
//                    serializedFw += ", " + fd.getFeType().name();
//                    serializedFw += ", " + fd.getPower();
                    toSetList.add(fd.toString());
                }

                String path = "open." + (tier.equalsIgnoreCase("OPEN") ? "" : "crate-tiers." + tier + ".") + ".fireworks";
                getFileHandler().get().set(path, toSetList);
            }
        }
    }

    public void addFireworks(String id, List<String> list) {
        for (String firework : list) {
            FireworkData fd = new FireworkData(cc, getSettings());
            fd.load(firework);
            addFirework(id, fd);
        }
    }

    public void removeFireworks(String tier, FireworkData fd) {
        getFireworks().get(tier).remove(fd);
    }

    public FireworkData getByItemStack(String tier, ItemStack stack) {
        ItemBuilder ib = new ItemBuilder(stack);

        String lastLine = "";
        for (String line : ib.getItemMeta().getLore())
            lastLine = line;

        for (FireworkData fd : getFireworks().get(tier)) {
            if (fd.getId().equalsIgnoreCase(lastLine)) {
                return fd;
            }
        }
        return null;
    }

    public void addFirework(String id, FireworkData s) {
        if (getFireworks().containsKey(id)) {
            List<FireworkData> list = getFireworks().get(id);
            list.add(s);
            getFireworks().put(id, list);
            return;
        }

        ArrayList<FireworkData> list = new ArrayList<>();
        list.add(s);

        StatusLoggerEvent.FIREWORK_ADD.log(getCrate(), new String[]{s.getFeType().name(), id});
        getFireworks().put(id, list);
    }

    public void runAll(Player p, Location l, List<Reward> rewards) {
        for (String tier : getFireworks().keySet()) {
            if ((tier.equalsIgnoreCase("OPEN") && (!getSettings().isTiersOverrideDefaults() || rewards.isEmpty() ||
                    !getFireworks().containsKey(rewards.get(0).getRarity().toUpperCase()))) ||
                    (!rewards.isEmpty() && rewards.get(0).getRarity().equalsIgnoreCase(tier))) {
                for (FireworkData fd : getFireworks().get(tier)) {
                    fd.play(LocationUtils.getLocationCentered(l));
                }
            }
        }
    }

    public Map<String, List<FireworkData>> getFireworks() {
        return fireworks;
    }

    public void setFireworks(Map<String, List<FireworkData>> fireworks) {
        this.fireworks = fireworks;
    }
}
