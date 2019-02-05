package me.ztowne13.customcrates.utils;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.visuals.npcs.MobPlaceholder;
import me.ztowne13.customcrates.visuals.npcs.Citizens2NPCPlaceHolder;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Bukkit;

import java.util.Iterator;

/**
 * Created by ztowne13 on 2/26/16.
 */
public class NPCUtils
{
	public static void checkUncheckMobs(boolean b)
	{
		if(Utils.isPLInstalled("Citizens"))
		{
			for (NPCRegistry npcr : CitizensAPI.getNPCRegistries())
			{
				Iterator<NPC> npcs = npcr.iterator();

				for (Object obj : Utils.iteratorToList(npcs))
				{
					NPC npc = (NPC) obj;
					if (npc.getName().equals("Specialized Crate - Crate"))
					{
						if (!b)
						{
							b = true;
							for (NPC npck : MobPlaceholder.getMobs().values())
							{
								if (npck.getUniqueId().equals(npc.getUniqueId()))
								{
									b = false;
								}
							}

							for (NPC npck : Citizens2NPCPlaceHolder.getNpcs().values())
							{
								if (npck.getUniqueId().equals(npc.getUniqueId()))
								{
									b = false;
								}
							}
						}

						if (b)
						{
							npc.destroy();
						}
					}
				}
			}
		}
	}

	public static void checkUncheckMobs(CustomCrates cc, final boolean bVal, long l)
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
		{
			@Override
			public void run()
			{
				NPCUtils.checkUncheckMobs(bVal);
			}
		}, l);
	}

	public static void applyDefaultInfo(NPC npc)
	{
		npc.getTrait(LookClose.class).toggle();

		npc.data().setPersistent(NPC.NAMEPLATE_VISIBLE_METADATA, false);

		npc.data().set(NPC.AMBIENT_SOUND_METADATA, null);
		npc.data().set(NPC.DEFAULT_PROTECTED_METADATA, true);
	}
}
