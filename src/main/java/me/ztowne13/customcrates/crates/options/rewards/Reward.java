package me.ztowne13.customcrates.crates.options.rewards;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.items.SaveableItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Reward implements Comparable<Reward> {
    private final SpecializedCrates instance;
    private final Random random;
    private FileConfiguration fileConfiguration;
    private CRewards rewards;
    private String rewardName;
    private String rarity = "default";
    private boolean giveDisplayItem;
    private boolean giveDisplayItemLore = true;
    private boolean giveDisplayItemName = true;

    private ItemBuilder displayBuilder;
    private SaveableItemBuilder saveBuilder;

    private double chance;
    private List<String> commands;
    private int totalUses;
    private boolean needsMoreConfig;

    private String fallbackRewardName = "";
    private String fallbackPermission = "";

    private boolean toLog;

    public Reward(SpecializedCrates instance, String rewardName) {
        init();
        needsMoreConfig = true;
        this.instance = instance;
        setRewardName(rewardName);
        saveBuilder = new SaveableItemBuilder(XMaterial.STONE, 1);
        saveBuilder.setDisplayName(rewardName);
        displayBuilder = new ItemBuilder(saveBuilder);
        giveDisplayItem = true;
        this.random = new Random();
    }

    public Reward(SpecializedCrates instance, CRewards rewards, String rewardName) {
        this(instance, rewardName);
        init();
        this.rewards = rewards;
        toLog = true;
        loadChance();
    }

    @Override
    public int compareTo(Reward otherReward) {
        return (int) (getChance() * 10000 - otherReward.getChance() * 10000);
    }

    public void init() {
        commands = new ArrayList<>();
        needsMoreConfig = false;
        toLog = false;
        chance = -1;
    }

    public void giveRewardToPlayer(Player p) {
        // Fallback reward
        if (!fallbackRewardName.equalsIgnoreCase("") && !fallbackPermission.equalsIgnoreCase("") &&
                p.hasPermission(fallbackPermission)) {
            if (!p.hasPermission("customcrates.admin") && !p.hasPermission("specializedcrates.admin")) {
                Reward fallbackReward = CRewards.getAllRewards().get(fallbackRewardName);
                if (fallbackReward == null) {
                    ChatUtils.msgError(p, "The reward " + rewardName + " has the fallback reward " + fallbackRewardName +
                            ", but that reward does not exist. This message is not configurable. If you would like there to be no reward" +
                            " as a fallback reward, please set the fallback reward to a new reward that has no commands and does not give" +
                            " the player any items. A reward must have a fallback reward IF it has a fallback permission.");
                } else {
                    fallbackReward.giveRewardToPlayer(p);
                    Messages.GIVEN_FALLBACK_REWARD.msgSpecified(instance, p, new String[]{"%reward%", "%fallbackreward%"},
                            new String[]{getDisplayBuilder().getDisplayName(true),
                                    fallbackReward.getDisplayBuilder().getDisplayName(true)});
                }

                return;
            } else {
                ChatUtils.msgInfo(p, "Normally, you would have won the fallback reward " + rewardName +
                        " instead, but since you have the customcrates.admin permission, you've bypassed that.");
            }
        }

        if (isGiveDisplayItem()) {
            ItemBuilder stack = new ItemBuilder(displayBuilder);

            try {
                if (!isGiveDisplayItemLore()) {
                    ItemMeta im = stack.getItemMeta();
                    im.setLore(null);
                    stack.setItemMeta(im);
                }
            } catch (Exception exc) {
            }

            if (!isGiveDisplayItemName()) {
                stack.removeDisplayName();
            }

            Utils.addItemAndDropRest(p, stack.getStack());
        }

        for (String command : getCommands()) {
            try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), applyCommandPlaceHolders(p, command));
            } catch (Exception exc) {
                ChatUtils
                        .log("PLEASE READ THIS: Specialized Crates has attempted to run a command for a reward that has produced an error. " +
                                "Please contact the author of the plugin who's command is run to fix the issue because THIS IS NOT A SPECIALIZED" +
                                "CRATES ISSUE, it is the issue of the plugin who's command was run. Command: " + command);
            }
        }

        //new RewardLimitEvent(this, PlayerManager.get(cc, p).getPdm().getCurrentRewardLimitUses(this), 1).addTo(PlayerManager.get(cc, p).getPdm());
    }

    public String applyCommandPlaceHolders(Player p, String cmd) {
        cmd = cmd.replace("%player%", p.getName());
        cmd = cmd.replace("%name%", p.getName());
        cmd = cmd.replace("%playername%", p.getName());
        cmd = cmd.replace("{name}", p.getName());

        if (cmd.contains("%amount")) {
            String[] args = cmd.split("%amount");

            for (int i = 1; i < args.length; i++) {
                boolean firstVal = true;
                StringBuilder firstNum = new StringBuilder();
                StringBuilder secondNum = new StringBuilder();

                for (String letter : args[i].split("")) {
                    if (letter.equalsIgnoreCase("-")) {
                        firstVal = false;
                    } else if (letter.equalsIgnoreCase("%")) {
                        break;
                    } else if (Utils.isInt(letter)) {
                        if (firstVal)
                            firstNum.append(letter);
                        else
                            secondNum.append(letter);
                    }
                }

                int first = Integer.parseInt(firstNum.toString());
                int second = Integer.parseInt(secondNum.toString());

                try {
                    if (second - first == 0) {
                        second = second + 1;
                    } else if (second - first < 0) {
                        int temp = second;
                        second = first;
                        first = temp;
                    }
                    int random = this.random.nextInt(second - first) + first;
                    String toReplace = "%amount" + firstNum + "-" + secondNum + "%";
                    cmd = cmd.replaceAll(toReplace, random + "");
                } catch (Exception exc) {
                    ChatUtils
                            .log("The %amountX-X% placeholder is improperly formatted. Please use %amountX-Y where X is the starting value and Y is the ending (X is LESS THAN Y)");
                }
            }
        }

        return cmd;
    }

    public void writeToFile() {
        FileHandler fu = getInstance().getRewardsFile();
        FileConfiguration fc = fu.get();
        fc.set(getPath("commands"), getCommands());
        fc.set(getPath("chance"), getChance());
        fc.set(getPath("rarity"), getRarity());
        fc.set(getPath("receive-limit"), /*getTotalUses()*/null);
        fc.set(getPath("give-display-item.value"), giveDisplayItem);
        fc.set(getPath("give-display-item.with-lore"), giveDisplayItemLore);
        fc.set(getPath("give-display-item.with-name"), giveDisplayItemName);
        fc.set(getPath("fallback-reward.reward-name"), fallbackRewardName);
        fc.set(getPath("fallback-reward.permission"), fallbackPermission);

        saveBuilder.saveItem(getInstance().getRewardsFile(), getPath("display-item"), false);

        fu.save();
    }

    public String delete(boolean forSure) {
        if (!forSure) {
            ArrayList<String> cratesThatUse = new ArrayList<>();
            for (Crate crate : Crate.getLoadedCrates().values()) {
                if (!crate.isMultiCrate() && crate.isLoadedProperly()) {
                    for (Reward r : crate.getSettings().getRewards().getCrateRewards()) {
                        if (r.equals(this)) {
                            cratesThatUse.add(crate.getName());
                            break;
                        }
                    }
                }
            }

            return cratesThatUse.toString();
        } else {
            for (Crate crate : Crate.getLoadedCrates().values()) {
                if (!crate.isMultiCrate() && crate.isLoadedProperly()) {
                    for (Reward r : crate.getSettings().getRewards().getCrateRewards()) {
                        if (r.equals(this)) {
                            crate.getSettings().getRewards().removeReward(r.getRewardName());
                            crate.getSettings().getRewards().saveToFile();
                            crate.getSettings().getFileHandler().save();
                            break;
                        }
                    }
                }
            }

            getInstance().getRewardsFile().get().set(getRewardName(), null);
            getInstance().getRewardsFile().save();
            CRewards.getAllRewards().remove(getRewardName());
        }
        return "";
    }

    public String applyVariablesTo(String s) {
        return ChatUtils.toChatColor(s.replace("%rewardname%", getRewardName()).
                replace("%displayname%", saveBuilder.getDisplayName(true)).
                replace("%writtenchance%", getChance() + "").
                replace("%rarity%", rarity)).
                replace("%chance%", getFormattedChance());
    }

    public String getFormattedChance() {
        if (toLog) {
            double ch = getChance() / rewards.getTotalOdds();
            ch = ch * 100;

            // If a chance is really small, this is so it doesn't show as 0.
            if (ch < 1) {
                String chanceAsDub = BigDecimal.valueOf(ch).toPlainString();
                String littleNumberFormat = "#.";
                boolean foundDot = false;
                for (int i = 0; i < chanceAsDub.length(); i++) {
                    char val = chanceAsDub.charAt(i);

                    if (foundDot) {
                        littleNumberFormat += "#";
                        if (val != '0') {
                            break;
                        }
                    }

                    if (val == '.') {
                        foundDot = true;
                    }
                }
                return new DecimalFormat(littleNumberFormat + "#").format(ch);
            }

            return new DecimalFormat("#.##").format(ch);
        } else {
            return "-1";
        }
    }

    public void loadChance() {
        try {
            setChance(getInstance().getRewardsFile().get().getDouble(getPath("chance")));
        } catch (Exception exc) {
            needsMoreConfig = true;
            if (toLog) {
                setChance(-1);
                StatusLoggerEvent.REWARD_CHANCE_NONEXISTENT.log(getRewards().getCrate(), new String[]{this.toString()});
            }
        }
    }

    @Deprecated
    public boolean loadFromConfig() {
        setFileConfiguration(getInstance().getRewardsFile().get());
        boolean success = true;
        needsMoreConfig = false;

        if (fileConfiguration.contains(getPath("item"))) {
            ChatUtils.log("Converting " + getRewardName() + " to new reward format.");
            RewardConverter rewardConverter = new RewardConverter(this);
            rewardConverter.loadFromConfig();
            rewardConverter.saveAllAsNull();

            saveBuilder.saveItem(instance.getRewardsFile(), getPath("display-item"), false);
            instance.getRewardsFile().save();
        } else {
            if (toLog)
                saveBuilder.loadItem(getInstance().getRewardsFile(), getRewardName() + ".display-item",
                        getRewards().getCrate().getSettings().getStatusLogger(),
                        StatusLoggerEvent.REWARD_ITEM_FAILURE, StatusLoggerEvent.REWARD_ENCHANT_INVALID,
                        StatusLoggerEvent.REWARD_POTION_INVALID, StatusLoggerEvent.REWARD_GLOW_FAILURE,
                        StatusLoggerEvent.REWARD_AMOUNT_INVALID, StatusLoggerEvent.REWARD_FLAG_FAILURE);
            else
                saveBuilder.loadItem(getInstance().getRewardsFile(), getRewardName() + ".display-item");
        }

        if (!loadNonItemValsFromConfig())
            success = false;

        if (getRarity() == null)
            rarity = "default";

        displayBuilder = new ItemBuilder(saveBuilder);
        if (displayBuilder.hasDisplayName()) {
            displayBuilder.setDisplayName(applyVariablesTo(saveBuilder.getDisplayName(true)));
        }

        displayBuilder.clearLore();
        for (String loreLine : saveBuilder.getLore())
            displayBuilder.addLore(applyVariablesTo(loreLine));

        return success;
    }

    public boolean loadNonItemValsFromConfig() {
        boolean success = true;
        try {
            setRarity(getFileConfiguration().getString(getPath("rarity")));
        } catch (Exception exc) {
            //needsMoreConfig = true;
            if (toLog) {
                StatusLoggerEvent.REWARD_RARITY_NONEXISTENT.log(getRewards().getCrate(), new String[]{this.toString()});
                success = false;
            }
        }

        try {
            setGiveDisplayItem(getFileConfiguration().getBoolean(getPath("give-display-item.value")));
        } catch (Exception exc) {
            setGiveDisplayItem(false);
        }

        try {
            setGiveDisplayItemLore(getFileConfiguration().getBoolean(getPath("give-display-item.with-lore")));
        } catch (Exception exc) {
            setGiveDisplayItemLore(true);
        }

        try {
            if (getFileConfiguration().contains(getPath("give-display-item.with-name")))
                setGiveDisplayItemName(getFileConfiguration().getBoolean(getPath("give-display-item.with-name")));
        } catch (Exception exc) {
            setGiveDisplayItemName(true);
        }

        try {
            setCommands(getFileConfiguration().getStringList(getPath("commands")));
        } catch (Exception exc) {
            if (toLog) {
                StatusLoggerEvent.REWARD_COMMAND_INVALID.log(getRewards().getCrate(), new String[]{this.toString()});
                success = false;
            }
        }

        if (getFileConfiguration().contains(getPath("fallback-reward.reward-name"))) {
            fallbackRewardName = getFileConfiguration().getString(getPath("fallback-reward.reward-name"));
        } else {
            fallbackRewardName = "";
        }

        if (getFileConfiguration().contains(getPath("fallback-reward.permission"))) {
            fallbackPermission = getFileConfiguration().getString(getPath("fallback-reward.permission"));
        } else {
            fallbackPermission = "";
        }

        try {
            setTotalUses(getFileConfiguration().getInt(getPath("receive-limit")));
        } catch (Exception exc) {
            setTotalUses(-1);
        }

        return success;
    }

    public String getDisplayName(boolean useMaterialIfNull) {
        if (!useMaterialIfNull && (displayBuilder == null || !displayBuilder.hasDisplayName())) {
            return rewardName;
        }

        String displayName = displayBuilder.getDisplayName(true);
        if (displayName.equalsIgnoreCase("")) {
            displayBuilder.setDisplayName(null);
            return displayBuilder.getDisplayName(true);
        }
        return displayName;
    }

    public void checkIsNeedMoreConfig() {
        needsMoreConfig =
                !(chance != -1 && /*saveBuilder.getDisplayName(false) != null &&*/ rarity != null && saveBuilder != null);
    }

    public boolean equals(Reward r) {
        return r.getRewardName().equalsIgnoreCase(getRewardName());
    }

    public String toString() {
        return getRewardName();
    }

    public String getPath(String s) {
        return getRewardName() + "." + s;
    }

    public boolean isGiveDisplayItemName() {
        return giveDisplayItemName;
    }

    public void setGiveDisplayItemName(boolean giveDisplayItemName) {
        this.giveDisplayItemName = giveDisplayItemName;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> list) {
        this.commands = list;
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public Double getChance() {
        return chance;
    }

    public void setChance(Integer chance) {
        this.chance = chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public SpecializedCrates getInstance() {
        return instance;
    }

    public int getTotalUses() {
        return totalUses;
    }

    public void setTotalUses(int totalUses) {
        this.totalUses = totalUses;
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public void setFileConfiguration(FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
    }

    public CRewards getRewards() {
        return rewards;
    }

    public void setRewards(CRewards rewards) {
        this.rewards = rewards;
    }

    public boolean isNeedsMoreConfig() {
        return needsMoreConfig;
    }

    public void setNeedsMoreConfig(boolean needsMoreConfig) {
        this.needsMoreConfig = needsMoreConfig;
    }

    public boolean isGiveDisplayItem() {
        return giveDisplayItem;
    }

    public void setGiveDisplayItem(boolean giveDisplayItem) {
        this.giveDisplayItem = giveDisplayItem;
    }

    public boolean isGiveDisplayItemLore() {
        return giveDisplayItemLore;
    }

    public void setGiveDisplayItemLore(boolean giveDisplayItemLore) {
        this.giveDisplayItemLore = giveDisplayItemLore;
    }

    public ItemBuilder getDisplayBuilder() {
        return displayBuilder;
    }

    public void setDisplayBuilder(ItemBuilder displayBuilder) {
        this.displayBuilder = displayBuilder;
    }

    public ItemBuilder getSaveBuilder() {
        return saveBuilder;
    }

    public void setSaveBuilder(SaveableItemBuilder saveBuilder) {
        this.saveBuilder = saveBuilder;
    }

    public void setBuilder(ItemBuilder setBuilder) {
        this.saveBuilder = new SaveableItemBuilder(setBuilder);
        this.displayBuilder = new ItemBuilder(setBuilder);
    }

    public String getFallbackRewardName() {
        return fallbackRewardName;
    }

    public void setFallbackRewardName(String fallbackRewardName) {
        this.fallbackRewardName = fallbackRewardName;
    }

    public String getFallbackPermission() {
        return fallbackPermission;
    }

    public void setFallbackPermission(String fallbackPermission) {
        this.fallbackPermission = fallbackPermission;
    }

    public boolean isToLog() {
        return toLog;
    }
}
