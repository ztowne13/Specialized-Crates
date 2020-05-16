package me.ztowne13.customcrates.interfaces.igc.fileconfigs.rewards;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 3/22/16.
 */
public class IGCMenuFallbackReward extends IGCMenu
{
    Reward reward;

    public IGCMenuFallbackReward(SpecializedCrates cc, Player p, IGCMenu lastMenu, Reward reward)
    {
        super(cc, p, lastMenu, "&7&l> &6&lFallback Reward");
        this.reward = reward;
    }

    @Override
    public void openMenu()
    {

        InventoryBuilder ib = createDefault(9);

        getIb().setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());

        ItemBuilder fallbackPerm = new ItemBuilder(DynamicMaterial.BOOK);
        fallbackPerm.setDisplayName("&aPermission");
        fallbackPerm.addLore("&7Current Value:");
        fallbackPerm.addLore("&7" + reward.getFallbackPermission());
        fallbackPerm.addLore("");
        fallbackPerm.addAutomaticLore("&f", 30, "Set to 'none' to remove.");
        fallbackPerm.addLore("");
        fallbackPerm.addAutomaticLore("&f", 30, "If a player has this permission and wins this reward," +
                " they will be given the specified fallback reward instead of this reward.");

        ItemBuilder fallbackReward = new ItemBuilder(DynamicMaterial.DIAMOND);
        fallbackReward.setDisplayName("&aReward");
        fallbackReward.addLore("&7Current Value:");
        fallbackReward.addLore("&7" + reward.getFallbackRewardName());
        fallbackReward.addLore("");
        fallbackReward.addAutomaticLore("&f", 30, "if a player has the specified permission and wins this reward," +
                " they will receive this specified reward instead.");

        ib.setItem(2, fallbackPerm);
        ib.setItem(3, fallbackReward);

        getIb().open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot)
    {
        switch (slot)
        {
            case 0:
                up();
                break;
            case 2:
                new InputMenu(getCc(), getP(), "fallback-reward.permission", reward.getFallbackPermission(), "Set to 'none' to remove", String.class, this, true);
                break;
            case 3:
                new IGCMenuFallbackRewardSelector(getCc(), getP(), this, reward, 1).open();
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        if(value.equalsIgnoreCase("fallback-reward.permission"))
        {
            if(input.equalsIgnoreCase("none"))
            {
                reward.setFallbackPermission("");
                ChatUtils.msgSuccess(getP(), "Removed the fallback reward.");
                return true;
            }
            reward.setFallbackPermission(input);
            ChatUtils.msgSuccess(getP(), "Set the fallback reward to " + input);
        }
        return true;
    }

}
