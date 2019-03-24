package me.ztowne13.customcrates.visuals.npcs;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.utils.LocationUtils;
import me.ztowne13.customcrates.utils.NPCUtils;
import me.ztowne13.customcrates.visuals.DynamicCratePlaceholder;
import me.ztowne13.customcrates.visuals.EntityTypes;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.npc.skin.Skin;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.util.NMS;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

/**
 * Created by ztowne13 on 2/25/16.
 */
public class Citizens2NPCPlaceHolder extends DynamicCratePlaceholder
{
	static HashMap<PlacedCrate, NPC> npcs = new HashMap<PlacedCrate, NPC>();

	String name;

	public Citizens2NPCPlaceHolder(CustomCrates cc)
	{
		super(cc);
	}

	public void place(PlacedCrate cm)
	{
		LocationUtils.removeDubBlocks(cm.getL());

		NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
		final NPC npc = npcRegistry.createNPC(EntityType.PLAYER, name);

		NPCUtils.applyDefaultInfo(npc);
		npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, name);
		npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST, true);

		npc.addTrait(new IdentifierTrait());

		npc.spawn(LocationUtils.getLocationCentered(cm.getL()));

		//applySkin(npc, getName());

		getNpcs().put(cm, npc);

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
		if (npc.isSpawned()) {

			SkinnableEntity skinnable = (SkinnableEntity) npc.getEntity();
			if (skinnable != null) {
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
