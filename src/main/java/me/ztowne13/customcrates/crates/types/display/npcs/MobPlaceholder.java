package me.ztowne13.customcrates.crates.types.display.npcs;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.types.display.DynamicCratePlaceholder;
import me.ztowne13.customcrates.crates.types.display.EntityType;
import me.ztowne13.customcrates.utils.LocationUtils;
import me.ztowne13.customcrates.utils.NPCUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ztowne13 on 2/24/16.
 */
public class MobPlaceholder extends DynamicCratePlaceholder {
    private static Map<PlacedCrate, NPC> npcMap = new HashMap<>();

    private EntityType entityType;

    public MobPlaceholder(SpecializedCrates instance) {
        super(instance);
    }

    public static Map<PlacedCrate, NPC> getNpcMap() {
        return npcMap;
    }

    public static void setNpcMap(Map<PlacedCrate, NPC> npcMap) {
        MobPlaceholder.npcMap = npcMap;
    }

    public void place(final PlacedCrate placedCrate) {
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            LocationUtils.removeDubBlocks(placedCrate.getLocation());

            NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
            NPC npc;

            if (NPCUtils.npcExists(placedCrate)) {
                npc = NPCUtils.getNpcForCrate(placedCrate);

                if (npc.getEntity().getType().equals(getEntityType().getBukkitEntityType())) {
                    getNpcMap().put(placedCrate, npc);
                    return;
                } else {
                    npc.destroy();
                }
            }

            npc = npcRegistry.createNPC(entityType.getBukkitEntityType(), "Specialized Crate - Crate");

            //npc.addTrait(new IdentifierTrait());

            NPCUtils.applyDefaultInfo(npc);

            npc.spawn(LocationUtils.getLocationCentered(placedCrate.getLocation()).add(0, -1, 0));

            getNpcMap().put(placedCrate, npc);
        }, 20);

    }

    public void remove(PlacedCrate placedCrate) {
        getNpcMap().get(placedCrate).destroy();
    }

    public boolean existsAt(Location location) {
        return true;
    }

    public String getType() {
        return entityType == null ? "null" : entityType.name();
    }

    public void setType(Object obj) {
        setEntityType(EntityType.getEnum(obj.toString()));
    }

    public void fixHologram(PlacedCrate placedCrate) {
        Location l = placedCrate.getLocation().clone();
        l.setY(l.getY() + getEntityType().getHeight() - .5);
        placedCrate.getHologram().getDynamicHologram().teleport(l);

    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public String toString() {
        return "Mob";
    }
}
