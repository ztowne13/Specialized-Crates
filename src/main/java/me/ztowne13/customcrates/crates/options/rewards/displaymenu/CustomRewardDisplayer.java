package me.ztowne13.customcrates.crates.options.rewards.displaymenu;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.entity.Player;

public class CustomRewardDisplayer extends RewardDisplayer
{
    public CustomRewardDisplayer(Crate crate)
    {
        super(crate);
    }

    @Override
    public void openFor(Player p)
    {
        p.openInventory(createInventory(p).getInv());
        PlayerManager.get(getCrates().getCc(), p).setInRewardMenu(true);
    }

    @Override
    public InventoryBuilder createInventory(Player p)
    {

        return null;
    }

    @Override
    public void load()
    {
//        loadDefaults();
//
//        FileConfiguration fileConfiguration = getFileHandler().get();
//
//        FileConfiguration fc = getCrates().getCs().getFc();
//        if (csb.hasV("gui"))
//        {
//            try
//            {
//                for (String s : fc.getConfigurationSection("gui.objects").getValues(false).keySet())
//                {
//                    s = s.toLowerCase();
//                    String cause = "The crate name is invalid";
//                    try
//                    {
//                        String[] args = fc.getString("gui.objects." + s).split(";");
//                        if (Crate.crateAlreadyExist(args[0]))
//                        {
//                            cause = "The crate name to set the object to is invalid";
//                            cratesWithID.put(s, Crate.getCrate(cc, args[0]));
//                        }
//                        else
//                        {
//                            cause = args[0] + " is not a valid Material";
//                            Material m = DynamicMaterial.fromString(args[0].toUpperCase()).parseMaterial();
//                            int byt = 0;
//
//                            if (Utils.isInt(args[1]))
//                            {
//                                byt = Integer.parseInt(args[1]);
//                            }
//
//                            materialsWithID.put(s, new ItemStack(m, 1, (short) byt));
//                        }
//                    }
//                    catch (Exception exc)
//                    {
//                        //exc.printStackTrace();
//                        StatusLoggerEvent.MULTICRATEINVENTORY_OBJECTS_INVALID.log(getCrates(), new String[]{s, cause});
//                    }
//                }
//            }
//            catch (Exception exc)
//            {
//                StatusLoggerEvent.MULTICRATEINVENTORY_OBJECTS_MISCONFIGURED.log(getCrates());
//            }
//
//
//            try
//            {
//                int row = 0;
//                int slot = 0;
//                for (String s : fc.getStringList("gui.rows"))
//                {
//                    s = s.toLowerCase();
//                    for (String character : s.split(""))
//                    {
//                        if (character == null || character.replaceAll("\\s+", "").equalsIgnoreCase(""))
//                        {
//                            continue;
//                        }
//                        for (String identifier : materialsWithID.keySet())
//                        {
//                            if (identifier.equalsIgnoreCase(character))
//                            {
//                                items.put((row * 9) + slot, materialsWithID.get(character));
//                                break;
//                            }
//                        }
//
//
//                        for (String identifier : cratesWithID.keySet())
//                        {
//                            if (identifier.equalsIgnoreCase(character))
//                            {
//                                crateSpots.put((row * 9) + slot, cratesWithID.get(character));
//                                break;
//                            }
//                        }
//                        slot++;
//                    }
//                    row++;
//                    slot = 0;
//                }
//
//            }
//            catch (Exception exc)
//            {
//                StatusLoggerEvent.MULTICRATEINVENTORY_ROW_MISCONFIGURED.log(getCrates());
//            }
//
//            return;
//        }
//        StatusLoggerEvent.MULTICRATEINVENTORY_NONEXISTENT.log(getCrates());
    }

}
