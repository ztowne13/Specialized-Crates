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
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;

/**
 * Created by ztowne13 on 2/24/16.
 */
public class MobPlaceholder extends DynamicCratePlaceholder
{
    static HashMap<PlacedCrate, NPC> mobs = new HashMap<PlacedCrate, NPC>();

    EntityTypes ent;

    public MobPlaceholder(SpecializedCrates cc)
    {
        super(cc);
    }

    public void place(final PlacedCrate cm)
    {
        Bukkit.getScheduler().runTaskLater(getCc(), new Runnable()
        {
            @Override
            public void run()
            {
                LocationUtils.removeDubBlocks(cm.getL());

                NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
                NPC npc;

                if(NPCUtils.npcExists(cm))
                {
                    npc = NPCUtils.getNpcForCrate(cm);

                    if(npc.getEntity().getType().equals(getEnt().getEt()))
                    {
                        getMobs().put(cm, npc);
                        return;
                    }
                    else
                    {
                        npc.destroy();
                    }
                }

                npc = npcRegistry.createNPC(ent.getEt(), "Specialized Crate - Crate");

                //npc.addTrait(new IdentifierTrait());

                NPCUtils.applyDefaultInfo(npc);

                npc.spawn(LocationUtils.getLocationCentered(cm.getL()).add(0, -1, 0));

                getMobs().put(cm, npc);
            }
        }, 20);

    }

    public void remove(PlacedCrate cm)
    {
        getMobs().get(cm).destroy();
    }

    public boolean existsAt(Location l)
    {
        return true;
    }

    public void setType(Object obj)
    {
        setEnt(EntityTypes.getEnum(obj.toString()));
    }

    public String getType()
    {
        return ent == null ? "null" : ent.name();
    }

    public void fixHologram(PlacedCrate cm)
    {
        Location l = cm.getL().clone();
        l.setY(l.getY() + getEnt().getHeight() - .5);
        cm.getHologram().getDh().teleport(l);

    }


    public static HashMap<PlacedCrate, NPC> getMobs()
    {
        return mobs;
    }

    public static void setMobs(HashMap<PlacedCrate, NPC> mobs)
    {
        MobPlaceholder.mobs = mobs;
    }

    public EntityTypes getEnt()
    {
        return ent;
    }

    public void setEnt(EntityTypes ent)
    {
        this.ent = ent;
    }

    public String toString()
    {
        return "Mob";
    }
}
