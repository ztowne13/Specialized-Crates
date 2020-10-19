package me.ztowne13.customcrates.crates.options.rewards;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.interfaces.items.CompressedPotionEffect;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.items.SaveableItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.util.Collection;
import java.util.Optional;

public class RewardConverter {
    private final Reward reward;

    public RewardConverter(Reward reward) {
        this.reward = reward;
    }

    public void saveAllAsNull() {
        FileHandler rewardsFile = reward.getInstance().getRewardsFile();
        FileConfiguration fc = rewardsFile.get();

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
    public boolean loadFromConfig() {
        reward.setFileConfiguration(reward.getInstance().getRewardsFile().get());
        boolean success = true;
        reward.setNeedsMoreConfig(false);

        String unsplitMat = reward.getFileConfiguration().getString(reward.getPath("item"));
        Optional<XMaterial> optional = unsplitMat != null ? XMaterial.matchXMaterial(unsplitMat) : Optional.empty();

        if (optional.isPresent()) {
            XMaterial m = optional.get();

            if (m.equals(XMaterial.AIR)) {
                StatusLoggerEvent.REWARD_ITEM_AIR.log(reward.getRewards().getCrate(), this.toString());
                return false;
            }

            reward.setSaveBuilder(new SaveableItemBuilder(m, 1));
        } else {
            // TODO: Something here to indicate that the material is invalid
            return false;
        }

        try {
            reward.getSaveBuilder().setDisplayName(reward.getFileConfiguration().getString(reward.getPath("name")));
        } catch (Exception exc) {
            reward.getSaveBuilder().setDisplayName(reward.getSaveBuilder().getStack().getType().name().toLowerCase());
            reward.setNeedsMoreConfig(true);
            if (reward.isToLog()) {
                StatusLoggerEvent.REWARD_NAME_NONEXISTENT.log(reward.getRewards().getCrate(), this.toString());
                success = false;
            }
        }

        if (!reward.loadNonItemValsFromConfig())
            success = false;

        try {
            reward.getSaveBuilder().setGlowing(reward.getFileConfiguration().getBoolean(reward.getPath("glow")));
        } catch (Exception exc) {
            reward.getSaveBuilder().setGlowing(false);
        }

        try {
            buildDisplayItemFromConfig();
        } catch (Exception exc) {
            reward.setNeedsMoreConfig(true);
            if (reward.isToLog()) {
                StatusLoggerEvent.REWARD_ITEM_NONEXISTENT.log(reward.getRewards().getCrate(), this.toString());
                success = false;
            }
        }

        if (!reward.getSaveBuilder().hasDisplayName())
            reward.getSaveBuilder().setDisplayName(reward.getRewardName());

        if (reward.getRarity() == null)
            reward.setRarity("default");

        reward.setDisplayBuilder(new ItemBuilder(reward.getSaveBuilder().getStack()));
        reward.getDisplayBuilder().setDisplayName(reward.applyVariablesTo(reward.getSaveBuilder().getDisplayName(false)));

        reward.getDisplayBuilder().clearLore();
        for (String loreLine : reward.getSaveBuilder().getLore()) {
            reward.getDisplayBuilder().addLore(reward.applyVariablesTo(loreLine));
        }

        return success;
    }

    @Deprecated
    public void buildDisplayItemFromConfig() {
        reward.getSaveBuilder().setDisplayName(reward.applyVariablesTo(reward.getInstance().getSettings().getConfigValues().get("inv-reward-item-name").toString()));

        // If an item has a custom lore, apply that. Otherwise apply the general lore.
        if (reward.getFileConfiguration().contains(reward.getPath("lore"))) {

            for (String s : reward.getFileConfiguration().getStringList(reward.getPath("lore"))) {
                reward.getSaveBuilder().addLore(s);
            }
        } else {
            for (Object s : (Collection<?>) reward.getInstance().getSettings().getConfigValues().get("inv-reward-item-lore")) {
                reward.getSaveBuilder().addLore(s.toString());
            }
        }

        if (reward.getFileConfiguration().contains(reward.getPath("head-player-name"))) {
            reward.getSaveBuilder().setPlayerHeadName(reward.getFileConfiguration().getString(reward.getPath("head-player-name")));
        }

        if (reward.getFileConfiguration().contains(reward.getPath("amount"))) {
            try {
                int amnt = Integer.parseInt(reward.getFileConfiguration().getString(reward.getPath("amount")));
                reward.getSaveBuilder().getStack().setAmount(amnt);
            } catch (Exception exc) {
                StatusLoggerEvent.REWARD_AMOUNT_INVALID.log(reward.getRewards().getCrate(), reward.getSaveBuilder().getDisplayName(true));
            }
        }


        if (reward.getFileConfiguration().contains(reward.getPath("nbt-tags"))) {
            for (String s : reward.getFileConfiguration().getStringList(reward.getPath("nbt-tags"))) {
                reward.getSaveBuilder().addNBTTag(s);
            }
        }

        if (reward.getFileConfiguration().contains(reward.getPath("potion-effects"))) {
            for (String unparsedPot : reward.getFileConfiguration().getStringList(reward.getPath("potion-effects"))) {
                try {
                    CompressedPotionEffect compressedPotionEffect = CompressedPotionEffect.fromString(unparsedPot);

                    reward.getSaveBuilder().addPotionEffect(compressedPotionEffect);
                } catch (Exception exc) {
                    StatusLoggerEvent.REWARD_POTION_INVALID
                            .log(reward.getRewards().getCrate(), reward.getSaveBuilder().getDisplayName(true), unparsedPot);
                }
            }
        }

        if (reward.getFileConfiguration().contains(reward.getPath("enchantments"))) {
            String cause = reward.getPath("enchantments") + " value is not a valid list of enchantments.";
            try {
                for (String s : reward.getFileConfiguration().getStringList(reward.getPath("enchantments"))) {
                    cause = "Enchantment " + s + " is not formatted ENCHANTMENT;LEVEL";
                    String[] args = s.split(";");

                    cause = args[0] + " is not a valid enchantment.";
                    Enchantment ench = Enchantment.getByName(args[0].toUpperCase());

                    if (ench == null) {
                        throw new NullPointerException(cause);
                    }

                    cause = "Enchantment " + s + " is not formatted ENCHANTMENT;LEVEL";
                    cause = args[1] + " is not a valid Integer.";
                    int level = Integer.parseInt(args[1]);

                    reward.getSaveBuilder().addEnchantment(ench, level);
                }
            } catch (Exception exc) {
                StatusLoggerEvent.REWARD_ENCHANT_INVALID
                        .log(reward.getRewards().getCrate(), reward.getSaveBuilder().getDisplayName(true), cause);
            }
        }
    }
}
