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
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ztowne13 on 2/25/16.
 */
public class Citizens2NPCPlaceHolder extends DynamicCratePlaceholder {
    private static Map<PlacedCrate, NPC> npcMap = new HashMap<>();

    private String name;

    public Citizens2NPCPlaceHolder(SpecializedCrates instance) {
        super(instance);
    }

    public static Map<PlacedCrate, NPC> getNpcMap() {
        return npcMap;
    }

    public static void setNpcMap(Map<PlacedCrate, NPC> npcMap) {
        Citizens2NPCPlaceHolder.npcMap = npcMap;
    }

    public void place(final PlacedCrate placedCrate) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
            LocationUtils.removeDubBlocks(placedCrate.getLocation());

            NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
            NPC npc;

            if (NPCUtils.npcExists(placedCrate)) {
                npc = NPCUtils.getNpcForCrate(placedCrate);
                if (npc.getEntity().getType().equals(org.bukkit.entity.EntityType.PLAYER)) {
                    getNpcMap().put(placedCrate, npc);
                    return;
                } else {
                    npc.destroy();
                }
            }

            npc = npcRegistry.createNPC(org.bukkit.entity.EntityType.PLAYER, "Specialized Crate - Crate");
            //npc.addTrait(new IdentifierTrait());

            npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, name);
            //npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST, false);

            npc.spawn(LocationUtils.getLocationCentered(placedCrate.getLocation()));

            NPCUtils.applyDefaultInfo(npc);

            //applySkin(npc, getName());

            getNpcMap().put(placedCrate, npc);
        }, 20);

    }

    public void remove(PlacedCrate placedCrate) {
        getNpcMap().get(placedCrate).destroy();
    }

    public String getType() {
        return getName();
    }

    public void setType(Object obj) {
        setName(obj.toString());
    }

    public void fixHologram(PlacedCrate placedCrate) {
        Location l = placedCrate.getLocation().clone();
        l.setY(l.getY() + EntityType.PLAYER.getHeight() - .8);
        placedCrate.getHologram().getDynamicHologram().teleport(l);
    }

    public void applySkin(NPC npc, String name) {
        if (npc.isSpawned()) {

            SkinnableEntity skinnable = (SkinnableEntity) npc.getEntity();
            if (skinnable != null) {
                skinnable.setSkinName(name);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "NPC";
    }
}
