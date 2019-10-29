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

/**
 * Created by ztowne13 on 2/25/16.
 */
public class Citizens2NPCPlaceHolder extends DynamicCratePlaceholder
{
    static HashMap<PlacedCrate, NPC> npcs = new HashMap<PlacedCrate, NPC>();

    String name;

    public Citizens2NPCPlaceHolder(SpecializedCrates cc)
    {
        super(cc);
    }

    public void place(final PlacedCrate cm)
    {
        Bukkit.getScheduler().scheduleSyncDelayedTask(getCc(), new Runnable()
        {
            @Override
            public void run()
            {
                LocationUtils.removeDubBlocks(cm.getL());

                NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
                final NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "");
                npc.addTrait(new IdentifierTrait());

                npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, name);
                //npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST, false);

                npc.spawn(LocationUtils.getLocationCentered(cm.getL()));

                NPCUtils.applyDefaultInfo(npc);

                //applySkin(npc, getName());

                getNpcs().put(cm, npc);
            }
        }, 60);

    }

    public void remove(PlacedCrate cm)
    {
        getNpcs().get(cm).destroy();
    }

    public void setType(Object obj)
    {
        setName(obj.toString());
    }

    public String getType()
    {
        return getName();
    }

    public void fixHologram(PlacedCrate cm)
    {
        Location l = cm.getL().clone();
        l.setY(l.getY() + EntityTypes.PLAYER.getHeight() - .8);
        cm.getCholo().getDh().teleport(l);
    }

    public void applySkin(NPC npc, String name)
    {
        if (npc.isSpawned())
        {

            SkinnableEntity skinnable = (SkinnableEntity) npc.getEntity();
            if (skinnable != null)
            {
                skinnable.setSkinName(name);
            }
        }
    }

    public static HashMap<PlacedCrate, NPC> getNpcs()
    {
        return npcs;
    }

    public static void setNpcs(HashMap<PlacedCrate, NPC> npcs)
    {
        Citizens2NPCPlaceHolder.npcs = npcs;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return "NPC";
    }
}
