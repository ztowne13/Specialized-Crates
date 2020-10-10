package me.ztowne13.customcrates.crates.types.display.npcs;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.types.display.DynamicCratePlaceholder;
import me.ztowne13.customcrates.crates.types.display.EntityTypes;
import me.ztowne13.customcrates.utils.LocationUtils;
import me.ztowne13.customcrates.utils.NPCUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ztowne13 on 2/25/16.
 */
public class Citizens2NPCPlaceHolder extends DynamicCratePlaceholder {
    static Map<PlacedCrate, NPC> npcs = new HashMap<>();

    String name;

    public Citizens2NPCPlaceHolder(SpecializedCrates cc) {
        super(cc);
    }

    public static Map<PlacedCrate, NPC> getNpcs() {
        return npcs;
    }

    public static void setNpcs(Map<PlacedCrate, NPC> npcs) {
        Citizens2NPCPlaceHolder.npcs = npcs;
    }

    public void place(final PlacedCrate cm) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(getCc(), () -> {
            LocationUtils.removeDubBlocks(cm.getL());

            NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
            NPC npc;

            if (NPCUtils.npcExists(cm)) {
                npc = NPCUtils.getNpcForCrate(cm);
                if (npc.getEntity().getType().equals(EntityType.PLAYER)) {
                    getNpcs().put(cm, npc);
                    return;
                } else {
                    npc.destroy();
                }
            }

            npc = npcRegistry.createNPC(EntityType.PLAYER, "Specialized Crate - Crate");
            //npc.addTrait(new IdentifierTrait());

            npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, name);
            //npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST, false);

            npc.spawn(LocationUtils.getLocationCentered(cm.getL()));

            NPCUtils.applyDefaultInfo(npc);

            //applySkin(npc, getName());

            getNpcs().put(cm, npc);
        }, 20);

    }

    public void remove(PlacedCrate cm) {
        getNpcs().get(cm).destroy();
    }

    public String getType() {
        return getName();
    }

    public void setType(Object obj) {
        setName(obj.toString());
    }

    public void fixHologram(PlacedCrate cm) {
        Location l = cm.getL().clone();
        l.setY(l.getY() + EntityTypes.PLAYER.getHeight() - .8);
        cm.getHologram().getDh().teleport(l);
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
