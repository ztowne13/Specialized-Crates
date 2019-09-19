package me.ztowne13.customcrates;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.entity.Player;

public class PlaceHolderAPIHandler extends PlaceholderExpansion
{
    CustomCrates cc;

    public PlaceHolderAPIHandler(CustomCrates cc)
    {
        this.cc = cc;
    }

    @Override
    public boolean persist()
    {
        return true;
    }

    @Override
    public boolean canRegister()
    {
        return true;
    }

    @Override
    public String getIdentifier()
    {
        return "specializedcrates";
    }

    @Override
    public String getAuthor()
    {
        return cc.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion()
    {
        return cc.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        // %specializedcrates_virtual_keys_[cratename]%
        // %specializedcrates_virtual_crates_[cratename]%
        // %specializedcrates_cooldown_[cratename]%

        if(player == null){
            return "";
        }

        String[] args = identifier.split("_");

        PlayerManager playerManager = PlayerManager.get(cc, player);
        PlayerDataManager playerDataManager = playerManager.getPdm();

        if(args.length == 2)
        {
            // specializedcrates_cooldown
            if(args[0].equalsIgnoreCase("cooldown"))
            {
                // specializedcrates_cooldown_[cratename]
                if(!Crate.exists(args[1]))
                    return "[" + args[1] + " is an invalid crate]";

                Crate crate = Crate.getCrate(cc, args[1]);

                if(playerDataManager.getCrateCooldownEventByCrates(crate) == null)
                    return "0";

                int seconds = Math.round(playerDataManager.getCrateCooldownEventByCrates(crate).isCooldownOver());
                String[] values = Utils.ConvertSecondToHHMMString(seconds);
                String formatted = "";
                if(!values[0].equalsIgnoreCase("0"))
                    formatted = formatted + values[0] + " days, ";
                if(!values[1].equalsIgnoreCase("00"))
                    formatted = formatted + values[1] + " hours, ";
                if(!values[2].equalsIgnoreCase("00"))
                    formatted = formatted + values[2] + " minutes, ";
                if(!values[3].equalsIgnoreCase("00"))
                    formatted = formatted + values[3] + " seconds";
                else
                    formatted = formatted.substring(0, formatted.length() - 2);

                return formatted;
            }
        }
        else if(args.length == 3)
        {
            // specializedcrates_virtual
            if (args[0].equalsIgnoreCase("virtual"))
            {
                // specialized_virtual_..._[cratename]
                if(!Crate.exists(args[2]))
                    return "[" + args[2] + " is an invalid crate]";

                Crate crate = Crate.getCrate(cc, args[2]);

                // specializedcrates_virtual_keys_[cratename]
                if(args[1].equalsIgnoreCase("keys"))
                {
                    return "" + playerDataManager.getVirtualCrateData().get(crate).getKeys();
                }
                // specializedcrates_virtual_crates_[cratename]
                else if(args[1].equalsIgnoreCase("crates"))
                {
                    return "" + playerDataManager.getVirtualCrateData().get(crate).getCrates();
                }
            }
        }

        return null;
    }
}
