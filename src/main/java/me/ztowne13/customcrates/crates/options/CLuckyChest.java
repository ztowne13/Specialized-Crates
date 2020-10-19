package me.ztowne13.customcrates.crates.options;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Created by ztowne13 on 8/3/15.
 */
public class CLuckyChest extends CSetting {

    private final Random random = new Random();
    private double chance;
    private double outOfChance;
    private boolean isBLWL;
    private boolean requirePermission = true;
    private List<Material> whiteList = new ArrayList<>();
    private List<World> worlds = new ArrayList<>();
    private List<String> worldsRaw = new ArrayList<>();
    private boolean allWorlds = true;

    public CLuckyChest(Crate crate) {
        super(crate, crate.getInstance());
    }

    @Override
    public void loadFor(CrateSettingsBuilder crateSettingsBuilder, CrateState crateState) {
        FileConfiguration fc = getSettings().getFileConfiguration();

        if (!crateSettingsBuilder.hasValue("lucky-chest")) {
            StatusLoggerEvent.LUCKYCHEST_NOVALUES.log(getCrate());
            return;
        }

        if (crateSettingsBuilder.hasValue("lucky-chest.chance")) {
            String unParsedChance = fc.getString("lucky-chest.chance");
            String[] args = unParsedChance.split("/");
            try {
                setChance(Integer.parseInt(args[0]));
                setOutOfChance(Integer.parseInt(args[1]));
            } catch (Exception exc) {
                StatusLoggerEvent.LUCKYCHEST_CHANCE_MISFORMATTED.log(getCrate());
            }
        } else {
            chance = 1;
            outOfChance = 100;
            StatusLoggerEvent.LUCKYCHEST_CHANCE_NONEXISTENT.log(getCrate());
        }

        if (crateSettingsBuilder.hasValue("lucky-chest.is-block-list-whitelist")) {
            try {
                setBLWL(fc.getBoolean("lucky-chest.is-block-list-whitelist"));
            } catch (Exception exc) {
                StatusLoggerEvent.LUCKYCHEST_BLWL_INVALID.log(getCrate());
            }
        } else {
            setBLWL(true);
            StatusLoggerEvent.LUCKYCHEST_BLWL_NONEXISTENT.log(getCrate());
        }

        if (crateSettingsBuilder.hasValue("lucky-chest.require-permission")) {
            try {
                requirePermission = fc.getBoolean("lucky-chest.require-permission");
            } catch (Exception exc) {
                StatusLoggerEvent.LUCKYCHEST_REQUIRE_PERMISSION_INVALID.log(getCrate());
            }
        } else {
            requirePermission = true;
            StatusLoggerEvent.LUCKYCHEST_REQUIRE_PERMISSION_NONEXISTENT.log(getCrate());
        }

        if (crateSettingsBuilder.hasValue("lucky-chest.worlds")) {
            worldsRaw = fc.getStringList("lucky-chest.worlds");

            for (String s : fc.getStringList("lucky-chest.worlds")) {
                setAllWorlds(false);
                World w = Bukkit.getWorld(s);
                if (w != null) {
                    getWorlds().add(w);
                } else {
                    StatusLoggerEvent.LUCKYCHEST_WORLD_INVALID.log(getCrate(), s);
                }
            }
        }

        if (crateSettingsBuilder.hasValue("lucky-chest.block-list")) {
            try {
                for (String mat : fc.getStringList("lucky-chest.block-list")) {
                    Optional<XMaterial> optional = XMaterial.matchXMaterial(mat);
                    if (optional.isPresent()) {
                        getWhiteList().add(optional.get().parseMaterial());
                    } else {
                        StatusLoggerEvent.LUCKYCHEST_BLOCKLIST_INVALIDBLOCK.log(getCrate(), mat);
                    }
                }
            } catch (Exception exc) {
                StatusLoggerEvent.LUCKYCHEST_BLOCKLIST_INVALID.log(getCrate());
            }
        } else {
            StatusLoggerEvent.LUCKYCHEST_BLOCKLIST_NONEXISTENT.log(getCrate());
        }
    }

    public void saveToFile() {
        getFileHandler().get().set("lucky-chest.chance", ((int) chance) + "/" + ((int) outOfChance));
        getFileHandler().get().set("lucky-chest.require-permission", isRequirePermission());
        if (!whiteList.isEmpty()) {
            getFileHandler().get().set("lucky-chest.is-block-list-whitelist", isBLWL);

            ArrayList<String> whiteListRaw = new ArrayList<>();
            for (Material mat : getWhiteList())
                whiteListRaw.add(mat.name());

            getFileHandler().get().set("lucky-chest.block-list", whiteListRaw);
        } else {
            getFileHandler().get().set("lucky-chest.is-block-list-whitelist", null);
            getFileHandler().get().set("lucky-chest.block-list", null);
        }
        if (!worldsRaw.isEmpty()) {
            getFileHandler().get().set("lucky-chest.worlds", worldsRaw);
        } else {
            getFileHandler().get().set("lucky-chest.worlds", null);
        }
    }

    public boolean canRunForBlock(Block block) {
        if (!isAllWorlds() && !getWorlds().contains(block.getWorld())) {
            return false;
        }

        if (getWhiteList().isEmpty()) {
            return true;
        }

        return isBLWL() == getWhiteList().contains(block.getType());
    }

    public boolean runChance() {
        return (random.nextInt(((int) getOutOfChance())) + 1) <= getChance();
    }

    public boolean checkRun(Block b) {
        return canRunForBlock(b) && runChance();
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public double getOutOfChance() {
        return outOfChance;
    }

    public void setOutOfChance(double outOfChance) {
        this.outOfChance = outOfChance;
    }

    public boolean isBLWL() {
        return isBLWL;
    }

    public void setBLWL(boolean BLWL) {
        isBLWL = BLWL;
    }

    public List<Material> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<Material> whiteList) {
        this.whiteList = whiteList;
    }

    public List<World> getWorlds() {
        return worlds;
    }

    public void setWorlds(List<World> worlds) {
        this.worlds = worlds;
    }

    public boolean isAllWorlds() {
        return allWorlds;
    }

    public void setAllWorlds(boolean allWorlds) {
        this.allWorlds = allWorlds;
    }

    public List<String> getWorldsRaw() {
        return worldsRaw;
    }

    public void setWorldsRaw(List<String> worldsRaw) {
        this.worldsRaw = worldsRaw;
    }

    public boolean isRequirePermission() {
        return requirePermission;
    }

    public void setRequirePermission(boolean requirePermission) {
        this.requirePermission = requirePermission;
    }
}

