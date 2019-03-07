package me.ztowne13.customcrates.gui.ingame.crates;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.particles.BukkitParticleEffect;
import me.ztowne13.customcrates.crates.options.particles.NMSParticleEffect;
import me.ztowne13.customcrates.crates.options.particles.ParticleData;
import me.ztowne13.customcrates.crates.options.particles.ParticleEffect;
import me.ztowne13.customcrates.crates.options.particles.effects.PEAnimationType;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.gui.dynamicmenus.InputMenu;
import me.ztowne13.customcrates.gui.ingame.IGCDefaultItems;
import me.ztowne13.customcrates.gui.ingame.IGCMenu;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.InventoryUtils;
import me.ztowne13.customcrates.utils.NMSUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by ztowne13 on 4/2/16.
 */
public class IGCCrateParticles extends IGCTierMenu
{
	HashMap<Integer,ParticleData> slots = new HashMap<Integer,ParticleData>();
	public IGCCrateParticles(CustomCrates cc, Player p, IGCMenu lastMenu, Crate crates, String tier)
	{
		super(cc, p, lastMenu, "&7&l> &6&lParticles", crates, tier);
	}

	@Override
	public void open()
	{
		getP().closeInventory();
		putInMenu();

		InventoryBuilder ib = createDefault(InventoryUtils.getRowsFor(4, (crates.getCs().getCp().getParticles().containsKey(tier) ? crates.getCs().getCp().getParticles().get(tier).size() : 0) + 9));

		ib.setItem(9, IGCDefaultItems.EXIT_BUTTON.getIb());
		ib.setItem(8, new ItemBuilder(Material.PAPER, 1, 0).setName("&aCreate a new particle").setLore("&7Make sure you save").addLore("&7when you're done."));

		if(crates.getCs().getCp().getParticles().containsKey(tier))
		{
			int i = 2;
			for (ParticleData pd : crates.getCs().getCp().getParticles().get(tier))
			{
				if (i % 9 == 7)
				{
					i += 4;
				}

				slots.put(i, pd);
				ib.setItem(i, new ItemBuilder(Material.NETHER_STAR, 1, 0).setName("&a" + pd.getParticleName()).
						setLore("&7X: &f" + pd.getOffX()).addLore("&7Y:&f " + pd.getOffY()).addLore("&7Z:&f " + pd.getOffZ()).
						addLore("&7Speed: &f" + pd.getSpeed()).addLore("&7Amount:&f " + pd.getAmount()).
						addLore("&7Animation: &f" + (pd.getParticleAnimationEffect() == null ? "none" : PEAnimationType.getFromParticleAnimationEffect(pd.getParticleAnimationEffect()).name())));
				i++;
			}
		}

		ib.open();
	}

	@Override
	public void manageClick(int slot)
	{
		if(slot == 9)
		{
			up();
		}
		else if(slot == 8)
		{
			new InputMenu(getCc(), getP(), "particle type", "null", "Available particles: " + Arrays.toString(ParticleEffect.values()), String.class, this);
		}
		else if(getIb().getInv().getItem(slot) != null && getIb().getInv().getItem(slot).getType().equals(Material.NETHER_STAR))
		{
			ParticleData pd = slots.get(slot);
			getP().closeInventory();
			new IGCCrateParticle(getCc(), getP(), this, crates, pd, tier).open();
		}
	}

	ParticleData pd;

	@Override
	public boolean handleInput(String value, String input)
	{
		if(value.equalsIgnoreCase("particle type"))
		{
			try
			{
				if(NMSUtils.Version.v1_7.isServerVersionOrLater() && NMSUtils.Version.v1_8.isServerVersionOrEarlier())
				{
					ParticleEffect pe = ParticleEffect.valueOf(input.toUpperCase());
					pd = new NMSParticleEffect(pe, false);
				}
				else
				{
					pd = new BukkitParticleEffect(input.toUpperCase(), false);
				}


				/*ParticleEffect pe = ParticleEffect.valueOf(input.toUpperCase());
				pd = new ParticleData(pe);*/
				new InputMenu(getCc(), getP(), "X offset", "null", "How far the particles can spawn, in the X direction, from the crate.", Double.class, this);
			}
			catch(Exception exc)
			{
				ChatUtils.msgError(getP(), input + " is not valid from the list of particles: " + Arrays.toString(ParticleEffect.values()));
			}
		}
		else if(value.equalsIgnoreCase("x offset"))
		{
			if(Utils.isDouble(input))
			{
				pd.setOffX(Float.valueOf(input));
				new InputMenu(getCc(), getP(), "Y offset", "null", "How far the particles can spawn, in the Y direction, from the crate.", Double.class, this);
			}
			else
			{
				ChatUtils.msgError(getP(), input + " is not a valid double (number) value.");
			}
		}
		else if(value.equalsIgnoreCase("y offset"))
		{
			if(Utils.isDouble(input))
			{
				pd.setOffY(Float.valueOf(input));
				new InputMenu(getCc(), getP(), "Z offset", "null", "How far the particles can spawn, in the Z direction, from the crate.", Double.class, this);
			}
			else
			{
				ChatUtils.msgError(getP(), input + " is not a valid double (number) value.");
			}
		}
		else if(value.equalsIgnoreCase("z offset"))
		{
			if(Utils.isDouble(input))
			{
				pd.setOffZ(Float.valueOf(input));
				new InputMenu(getCc(), getP(), "speed", "null", "Changes the speed of most of the particles. For some, like music notes, it changes the color.", Double.class, this);
			}
			else
			{
				ChatUtils.msgError(getP(), input + " is not a valid double (number) value.");
			}
		}
		else if(value.equalsIgnoreCase("speed"))
		{
			if(Utils.isDouble(input))
			{
				pd.setSpeed(Float.valueOf(input));
				new InputMenu(getCc(), getP(), "amount", "null", "How many particles spawn every tick (1/20th of a second).", Integer.class, this);
			}
			else
			{
				ChatUtils.msgError(getP(), input + " is not a valid double (number) value.");
			}
		}
		else if(value.equalsIgnoreCase("amount"))
		{
			if(Utils.isInt(input))
			{
				pd.setAmount(Integer.valueOf(input));

				cs.getCp().addParticle(pd, tier);

				ChatUtils.msgSuccess(getP(), "Successfully set all particle values.");
				new IGCCrateParticle(getCc(), getP(), this, crates, pd, tier).open();
			}
			else
			{
				ChatUtils.msgError(getP(), input + " is not a valid integer (number) value.");
			}
		}
		return false;
	}
}
