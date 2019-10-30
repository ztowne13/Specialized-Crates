package me.ztowne13.customcrates.crates.options.rewards;

import me.ztowne13.customcrates.interfaces.items.CompressedPotionEffect;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.items.SaveableItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.FileHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;

public class RewardConverter
{
    Reward reward;

    public RewardConverter(Reward reward)
    {
        this.reward = reward;
    }

    public void saveAllAsNull()
    {
        FileHandler fu = reward.getCc().getRewardsFile();
        FileConfiguration fc = fu.get();

        fc.set(reward.getPath("name"), null);
        fc.set(reward.getPath("item"), null);
        fc.set(reward.getPath("glow"), null);
        fc.set(reward.getPath("amount"), null);
        fc.set(reward.getPath("head-player-name"), null);
        fc.set(reward.getPath("enchantments"), null);
        fc.set(reward.getPath("potion-effects"), null);
        fc.set(reward.getPath("lore"), null);
        fc.set(reward.getPath("nbt-tags"), null);
    }

    @Deprecated
    public boolean loadFromConfig()
    {
        reward.setFc(reward.getCc().getRewardsFile().get());
        boolean success = true;
        reward.needsMoreConfig = false;

        try
        {
            String unsplitMat = reward.getFc().getString(reward.getPath("item"));
            DynamicMaterial m = DynamicMaterial.fromString(unsplitMat);

            if(m.equals(DynamicMaterial.AIR))
            {
                StatusLoggerEvent.REWARD_ITEM_AIR.log(reward.getCr().getCrates(), new String[]{this.toString()});
                return false;
            }

            reward.saveBuilder = new SaveableItemBuilder(m, 1);
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
            return false;
        }

        try
        {
            reward.saveBuilder.setDisplayName(reward.getFc().getString(reward.getPath("name")));
        }
        catch (Exception exc)
        {
            reward.saveBuilder.setDisplayName(reward.saveBuilder.getStack().getType().name().toLowerCase());
            reward.needsMoreConfig = true;
            if (reward.toLog)
            {
                StatusLoggerEvent.REWARD_NAME_NONEXISTENT.log(reward.getCr().getCrates(), new String[]{this.toString()});
                success = false;
            }
        }

        if(!reward.loadNonItemValsFromConfig())
            success = false;

        try
        {
            reward.saveBuilder.setGlowing(reward.getFc().getBoolean(reward.getPath("glow")));
        }
        catch (Exception exc)
        {
            reward.saveBuilder.setGlowing(false);
        }

        try
        {
            buildDisplayItemFromConfig();
        }
        catch (Exception exc)
        {
            reward.needsMoreConfig = true;
            if (reward.toLog)
            {
                StatusLoggerEvent.REWARD_ITEM_NONEXISTENT.log(reward.getCr().getCrates(), new String[]{this.toString()});
                success = false;
            }
        }

        if (reward.saveBuilder.getDisplayName() == null)
            reward.saveBuilder.setDisplayName(reward.rewardName);

        if (reward.getRarity() == null)
            reward.rarity = "default";

        reward.displayBuilder = new ItemBuilder(reward.saveBuilder.getStack());
        reward.displayBuilder.setDisplayName(reward.applyVariablesTo(reward.saveBuilder.getDisplayName()));

        reward.displayBuilder.clearLore();
        for(String loreLine : reward.saveBuilder.getLore())
        {
            reward.displayBuilder.addLore(reward.applyVariablesTo(loreLine));
        }

        return success;
    }

    @Deprecated
    public void buildDisplayItemFromConfig()
    {
        reward.saveBuilder.setDisplayName(reward.applyVariablesTo(reward.cc.getSettings().getConfigValues().get("inv-reward-item-name").toString()));

        // If an item has a custom lore, apply that. Otherwise apply the general lore.
        if (reward.getFc().contains(reward.getPath("lore")))
        {

            for (String s : reward.getFc().getStringList(reward.getPath("lore")))
            {
                reward.saveBuilder.addLore(s);
            }
        }
        else
        {
            for (Object s : (ArrayList<String>) reward.cc.getSettings().getConfigValues().get("inv-reward-item-lore"))
            {
                reward.saveBuilder.addLore(s.toString());
            }
        }

        if (reward.getFc().contains(reward.getPath("head-player-name")))
        {
            reward.saveBuilder.setPlayerHeadName(reward.getFc().getString(reward.getPath("head-player-name")));
        }

        if (reward.getFc().contains(reward.getPath("amount")))
        {
            try
            {
                int amnt = Integer.parseInt(reward.getFc().getString(reward.getPath("amount")));
                reward.saveBuilder.getStack().setAmount(amnt);
            }
            catch (Exception exc)
            {
                StatusLoggerEvent.REWARD_AMOUNT_INVALID.log(reward.getCr().getCrates(), new String[]{reward.saveBuilder.getDisplayName()});
            }
        }


        if (reward.getFc().contains(reward.getPath("nbt-tags")))
        {
            for (String s : reward.fc.getStringList(reward.getPath("nbt-tags")))
            {
                reward.saveBuilder.addNBTTag(s);
            }
        }

        if (reward.getFc().contains(reward.getPath("potion-effects")))
        {
            for (String unparsedPot : reward.getFc().getStringList(reward.getPath("potion-effects")))
            {
                try
                {
                    CompressedPotionEffect compressedPotionEffect = CompressedPotionEffect.fromString(unparsedPot);

                    if (compressedPotionEffect == null)
                        throw new Exception();
                    else
                        reward.saveBuilder.addPotionEffect(compressedPotionEffect);
                }
                catch (Exception exc)
                {
                    StatusLoggerEvent.REWARD_POTION_INVALID
                            .log(reward.getCr().getCrates(), new String[]{reward.saveBuilder.getDisplayName(), unparsedPot});
                }
            }
        }

        if (reward.getFc().contains(reward.getPath("enchantments")))
        {
            String cause = reward.getPath("enchantments") + " value is not a valid list of enchantments.";
            try
            {
                for (String s : reward.getFc().getStringList(reward.getPath("enchantments")))
                {
                    cause = "Enchantment " + s + " is not formatted ENCHANTMENT;LEVEL";
                    String[] args = s.split(";");

                    cause = args[0] + " is not a valid enchantment.";
                    Enchantment ench = Enchantment.getByName(args[0].toUpperCase());

                    if (ench == null)
                    {
                        throw new NullPointerException(cause);
                    }

                    cause = "Enchantment " + s + " is not formatted ENCHANTMENT;LEVEL";
                    cause = args[1] + " is not a valid Integer.";
                    int level = Integer.parseInt(args[1]);

                    reward.saveBuilder.addEnchantment(ench, level);
                    continue;
                }
            }
            catch (Exception exc)
            {
                StatusLoggerEvent.REWARD_ENCHANT_INVALID
                        .log(reward.getCr().getCrates(), new String[]{reward.saveBuilder.getDisplayName(), cause});
            }
        }
    }
}
