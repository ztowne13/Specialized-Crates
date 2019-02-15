package me.ztowne13.customcrates.gui.ingame.crates;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.crates.types.CrateType;
import me.ztowne13.customcrates.gui.DynamicMaterial;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.gui.dynamicmenus.InputMenu;
import me.ztowne13.customcrates.gui.ingame.IGCDefaultItems;
import me.ztowne13.customcrates.gui.ingame.IGCMenu;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import me.ztowne13.customcrates.visuals.EntityTypes;
import me.ztowne13.customcrates.visuals.MaterialPlaceholder;
import me.ztowne13.customcrates.visuals.npcs.Citizens2NPCPlaceHolder;
import me.ztowne13.customcrates.visuals.npcs.MobPlaceholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class IGCCratesBase extends IGCMenuCrate
{
	public IGCCratesBase(CustomCrates cc, Player p, IGCMenu lastMenu, Crate crates)
	{
		super(cc, p, lastMenu, "&7&l> &6&lThe Defaults", crates);
	}

	@Override
	public void open()
	{
		getP().closeInventory();
		putInMenu();

		InventoryBuilder ib = createDefault(18);

		ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());

		//9-17 (11-15)
		ib.setItem(9, new ItemBuilder(crates.isEnabled() ? DynamicMaterial.LIME_WOOL : DynamicMaterial.RED_WOOL, 1).setName(crates.isEnabled() ? "&aEnabled" : "&cDisabled").addLore("&7Click me to toggle the crate."));
		ib.setItem(2, new ItemBuilder(Material.BOOK, 1, 0).setName("&aSet the crate permission").setLore("&7Current value: ").addLore("&f" + cs.getPermission()));
		ib.setItem(3, new ItemBuilder(Material.BUCKET, 1, 0).setName("&aSet the obtain-method").setLore("&7Current value: ").addLore("&f" + cs.getOt().name()));
		ib.setItem(4, new ItemBuilder(Material.PAPER, 1, 0).setName("&aSet the inventory-name").setLore("&7Current value: ").addLore("&f" + cs.getCrateInventoryName()));
		ib.setItem(8, new ItemBuilder(DynamicMaterial.BIRCH_BUTTON, 1).setName("&aSet the display.type").setLore("&7Current value: ").addLore("&f" + cs.getDcp()));

		if(crates.getCs().getDcp().toString().equalsIgnoreCase("mob") || crates.getCs().getDcp().toString().equalsIgnoreCase("npc"))
		{
			ib.setItem(17, new ItemBuilder(Material.STONE_BUTTON, 1, 0).setName("&aSet the " + cs.getDcp() + " type").setLore("&7Current value: ").addLore("&f" + cs.getDcp().getType()));
		}

		ib.setItem(12, new ItemBuilder(DynamicMaterial.BIRCH_FENCE_GATE, 1).setName("&aSet auto-close").setLore("&7Current value: ").addLore("&f" + cs.isAutoClose()));
		ib.setItem(13, new ItemBuilder(Material.ARMOR_STAND, 1, 0).setName("&aSet hologram-offset").setLore("&7Current value: ").addLore("&f" + cs.getHologramOffset()));
		ib.setItem(11, new ItemBuilder(DynamicMaterial.SNOWBALL, 1).setName("&aSet the cooldown").setLore("&7Current value: ").addLore("&f" + cs.getCooldown()));
		ib.setItem(14, new ItemBuilder(cs.getCrate()).setName("&aSet the crate item.").setLore("&7By clicking this object you will").addLore("&7set the crate item to your").addLore("&7item you are currently holding."));
		if(!crates.isMultiCrate())
		{
			ib.setItem(7, new ItemBuilder(cs.getKey()).setName("&aSet the crate key.").setLore("&7By clicking this object you will").addLore("&7set the key item to your").addLore("&7item you are currently holding."));
			ib.setItem(5, new ItemBuilder(Material.ITEM_FRAME, 1, 0).setName("&aSet the crate animation").setLore("&7Current Value: ").addLore("&f" + cs.getCt().name()));
			ib.setItem(16, new ItemBuilder(Material.SLIME_BALL, 1, 0).setName("&aSet require key").setLore("&7Current value: ").addLore("&f" + cs.isRequireKey()));
		}

		getIb().open();
	}

	@Override
	public void manageClick(int slot)
	{
		if((slot == 5 || slot == 7 || slot == 16) && crates.isMultiCrate())
		{
			return;
		}

		switch(slot)
		{
			case 0:
				up();
				break;
			case 9:
				if(crates.isCanBeEnabled())
				{
					crates.setEnabled(!crates.isEnabled());
					getIb().setItem(9, new ItemBuilder(crates.isEnabled() ? DynamicMaterial.LIME_WOOL : DynamicMaterial.RED_WOOL, 1).setName(crates.isEnabled() ? "&aEnabled" : "&cDisabled").addLore("&7Click me to toggle the crate."));
				}
				else
				{
					getIb().setItem(9, new ItemBuilder(getIb().getInv().getItem(10)).setName("&cYou cannot do this").setLore("&7This crate cannot be enabled").addLore("&7for it failed to load,").addLore("due to a misconfiguration, on").addLore("&7startup. Please fix any errors,").addLore("&7reload the plugin, and try again."));
				}
				break;
			case 2:
				new InputMenu(getCc(), getP(), "permission", cs.getPermission(), "Type 'none' to remove the permission.", String.class, this);
				break;
			case 3:
				new InputMenu(getCc(), getP(), "obtain-method", cs.getOt().name(), "Available obtain methods: " + Arrays.toString(ObtainType.values()), String.class, this);
				break;
			case 4:
				new InputMenu(getCc(), getP(), "inventory-name", cs.getOt().name(), String.class, this);
				break;
			case 8:
				new InputMenu(getCc(), getP(), "display.type", cs.getOt().name(), "Available display types: block, mob, npc", String.class, this);
				break;
			case 18:
				if(cs.getDcp().toString().equalsIgnoreCase("mob") || cs.getDcp().toString().equalsIgnoreCase("npc"))
				{
					new InputMenu(getCc(), getP(), "display." + (cs.getDcp().toString().equalsIgnoreCase("mob") ? "creature" : "name"), cs.getDcp().getType(), cs.getDcp().toString().equalsIgnoreCase("mob") ? "Available mob types: " + Arrays.toString(EntityTypes.values()) : "Use a pkayer's name", String.class, this);
				}
				break;
			case 11:
				new InputMenu(getCc(), getP(), "cooldown", cs.getCooldown() + "", "Time is measured in seconds.", Integer.class, this);
				break;
			case 12:
				new InputMenu(getCc(), getP(), "autoclose", cs.isAutoClose() + "", "Set if they crate will close automatically when done", Integer.class, this);
				break;
			case 13:
				new InputMenu(getCc(), getP(), "hologramoffset", cs.getHologramOffset() + "", "Set how high, up or down, the hologram will be", Integer.class, this);
				break;
			case 14:
				if(getP().getItemInHand() != null && !getP().getItemInHand().getType().equals(Material.AIR))
				{
					if (getP().getItemInHand().hasItemMeta() && getP().getItemInHand().getItemMeta().hasDisplayName())
					{
						crates.getCs().setCrate(getP().getItemInHand());
						getIb().setItem(14, new ItemBuilder(cs.getCrate()).setName("&aSet the crate item.").setLore("&7By clicking this object you will").addLore("&7set the crate item to your").addLore("&7item you are currently holding."));
					}
					else
					{
						ChatUtils.msgError(getP(), "The crate in hand doesn't have the required displayname.");
					}
				}
				break;
			case 7:
				if(getP().getItemInHand() != null && !getP().getItemInHand().getType().equals(Material.AIR))
				{
					if (getP().getItemInHand().hasItemMeta() && getP().getItemInHand().getItemMeta().hasDisplayName())
					{
						crates.getCs().setKey(getP().getItemInHand());
						getIb().setItem(7, new ItemBuilder(cs.getKey()).setName("&aSet the crate key.").setLore("&7By clicking this object you will").addLore("&7set the key item to your").addLore("&7item you are currently holding."));
					}
					else
					{
						ChatUtils.msgError(getP(), "The key in hand doesn't have the required displayname.");
					}
				}
				break;
			case 5:
				new InputMenu(getCc(), getP(), "animation", cs.getCt().name(), "Available animations: " + Arrays.toString(CrateType.values()), String.class, this);
				break;
			case 16:
				new InputMenu(getCc(), getP(), "require key", cs.isRequireKey() + "", "Set whether or not this crate requires a key to be opened.", Boolean.class, this);
		}
	}

	@Override
	public boolean handleInput(String value, String input)
	{
		if(value.equalsIgnoreCase("obtain-method"))
		{
			try
			{
				ObtainType ot = ObtainType.valueOf(input.toUpperCase());
				cs.setOt(ot);
				ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
				return true;
			}
			catch(Exception exc)
			{
				ChatUtils.msgError(getP(), input + " is not one of the obtain types: " + Arrays.toString(ObtainType.values()));
			}
		}
		else if(value.equalsIgnoreCase("permission"))
		{
			if(input.equalsIgnoreCase("none"))
			{
				cs.setPermission("no permission");
				ChatUtils.msgSuccess(getP(), "Removed the permission.");
			}
			else
			{
				cs.setPermission(input);
				ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
			}
			return true;
		}
		else if(value.equalsIgnoreCase("require key"))
		{
			try
			{
				Boolean b = Boolean.valueOf(input);
				cs.setRequireKey(b);
				ChatUtils.msgSuccess(getP(), "Set " + input + " to " + input);
				return true;
			}
			catch(Exception exc)
			{
				ChatUtils.msgError(getP(), input + " is not a valid true / false response.");
			}
		}
		else if(value.equalsIgnoreCase("animation"))
		{
			try
			{
				CrateType ct = CrateType.valueOf(input.toUpperCase());
				cs.setCt(ct);
				ChatUtils.msgSuccess(getP(), "Set the " + value + " to " + input);
				return true;
			}
			catch(Exception exc)
			{
				ChatUtils.msgError(getP(), input + " is not valid in the list of crate animations: " + Arrays.toString(CrateType.values()));
			}
		}
		else if(value.equalsIgnoreCase("inventory-name"))
		{
			if(input.length() < 33)
			{
				cs.setCrateInventoryName(input);
				ChatUtils.msgSuccess(getP(), "Set the " + value + " to " + input);
				return true;
			}
			ChatUtils.msgError(getP(), input + " as an inventory-name cannot be longer than 32 characters.");
		}
		else if(value.equalsIgnoreCase("display.type"))
		{
			switch(input.toUpperCase())
			{
				case "BLOCK":
					cs.setDcp(new MaterialPlaceholder(getCc()));
					return true;
				case "NPC":
					cs.setDcp(new Citizens2NPCPlaceHolder(getCc()));
					ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
					manageClick(18);
					break;
				case "MOB":
					cs.setDcp(new MobPlaceholder(getCc()));
					ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
					manageClick(18);
					break;
				default:
					ChatUtils.msgError(getP(), input + " is not BLOCK, NPC, or MOB");
			}

			if(input.equalsIgnoreCase("mob") || input.equalsIgnoreCase("npc"))
			{
				getIb().setItem(17, new ItemBuilder(Material.STONE_BUTTON, 1, 0).setName("&aSet the " + cs.getDcp() + " type").setLore("&7Current value: ").addLore("&7" + cs.getDcp().getType()));
			}
		}
		else if(value.equalsIgnoreCase("display.creature"))
		{
			try
			{
				EntityTypes et = EntityTypes.valueOf(input.toUpperCase());
				cs.getDcp().setType(et.name());
				ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
				return true;
			}
			catch(Exception exc)
			{
				ChatUtils.msgError(getP(), input + " is not a valid entity type: " + Arrays.toString(EntityTypes.values()));
			}
		}
		else if(value.equalsIgnoreCase("display.name"))
		{
			cs.getDcp().setType(input);
			ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
			return true;
		}
		else if(value.equalsIgnoreCase("cooldown"))
		{
			if(Utils.isInt(input))
			{
				cs.setCooldown(Integer.parseInt(input));
				ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
				return true;
			}
			else
			{
				ChatUtils.msgError(getP(), input + " is not a valid number.");
			}
		}
		else if(value.equalsIgnoreCase("autoclose"))
		{
			if(Utils.isBoolean(input))
			{
				cs.setAutoClose(Boolean.valueOf(input));
				ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
				return true;
			}
			else
			{
				ChatUtils.msgError(getP(), input + " is not a valid true or false value.");
			}
		}
		else if(value.equalsIgnoreCase("hologramoffset"))
		{
			if(Utils.isDouble(input))
			{
				cs.setHologramOffset(Double.valueOf(input));
				ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
				return true;
			}
			else
			{
				ChatUtils.msgError(getP(), input + " is not a valid number.");
			}
		}
		return false;
	}
}
