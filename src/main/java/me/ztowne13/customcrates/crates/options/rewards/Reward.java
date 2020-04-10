package me.ztowne13.customcrates.crates.options.rewards;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.items.SaveableItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.FileHandler;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Reward implements Comparable<Reward>
{
    SpecializedCrates cc;
    FileConfiguration fc;
    Random r;

    CRewards cr;
    String rewardName;
    String rarity = "default";
    boolean giveDisplayItem = true;
    boolean giveDisplayItemLore = true;
    boolean giveDisplayItemName = true;

    ItemBuilder displayBuilder;
    SaveableItemBuilder saveBuilder;

    double chance;
    List<String> commands;
    int totalUses;
    boolean needsMoreConfig;

    boolean toLog;

    public Reward(SpecializedCrates cc, String rewardName)
    {
        init();
        needsMoreConfig = true;
        this.cc = cc;
        setRewardName(rewardName);
        saveBuilder = new SaveableItemBuilder(DynamicMaterial.STONE, 1);
        saveBuilder.setDisplayName(rewardName);
        displayBuilder = new ItemBuilder(saveBuilder);
        giveDisplayItem = true;
        this.r = new Random();
    }

    public Reward(SpecializedCrates cc, CRewards cr, String rewardName)
    {
        this(cc, rewardName);
        init();
        this.cr = cr;
        toLog = true;
        loadChance();
    }

    @Override
    public int compareTo(Reward otherReward)
    {
        return (int) (getChance()*1000 - otherReward.getChance()*1000);
    }

    public void init()
    {
        commands = new ArrayList<String>();
        needsMoreConfig = false;
        toLog = false;
        chance = -1;
    }

    public void runCommands(Player p)
    {
        if(isGiveDisplayItem())
        {
            ItemBuilder stack = new ItemBuilder(displayBuilder);

            try
            {
                if (!isGiveDisplayItemLore())
                {
                    ItemMeta im = stack.im();
                    im.setLore(null);
                    stack.setIm(im);
                }
            }
            catch(Exception exc) { }

            if(!isGiveDisplayItemName())
            {
                stack.removeDisplayName();
            }

            Utils.addItemAndDropRest(p, stack.get());
        }

        for (String command : getCommands())
        {
            try
            {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), applyCommandPlaceHolders(p, command));
            }
            catch (Exception exc)
            {
                ChatUtils
                        .log("PLEASE READ THIS: Specialized Crates has attempted to run a command for a reward that has produced an error. " +
                                "Please contact the author of the plugin who's command is run to fix the issue because THIS IS NOT A SPECIALIZED" +
                                "CRATES ISSUE, it is the issue of the plugin who's command was run. Command: " + command);
            }
        }
        //new RewardLimitEvent(this, PlayerManager.get(cc, p).getPdm().getCurrentRewardLimitUses(this), 1).addTo(PlayerManager.get(cc, p).getPdm());
    }

    public String applyCommandPlaceHolders(Player p, String cmd)
    {
        cmd = cmd.replaceAll("%player%", p.getName());
        cmd = cmd.replaceAll("%name%", p.getName());
        cmd = cmd.replaceAll("%playername%", p.getName());
        cmd = cmd.replace("{name}", p.getName());

        if (cmd.contains("%amount"))
        {
            String[] args = cmd.split("%amount");

            for (int i = 1; i < args.length; i++)
            {
                boolean firstVal = true;
                String firstNum = "";
                String secondNum = "";

                for (String letter : args[i].split(""))
                {
                    if (letter.equalsIgnoreCase("-"))
                    {
                        firstVal = false;
                    }
                    else if (letter.equalsIgnoreCase("%"))
                    {
                        break;
                    }
                    else if (Utils.isInt(letter))
                    {
                        if (firstVal)
                            firstNum = firstNum + letter;
                        else
                            secondNum = secondNum + letter;
                    }
                }

                int first = Integer.parseInt(firstNum);
                int second = Integer.parseInt(secondNum);

                try
                {
                    if (second - first == 0)
                    {
                        second = second + 1;
                    }
                    else if (second - first < 0)
                    {
                        int temp = second;
                        second = first;
                        first = temp;
                    }
                    int random = r.nextInt(second - first) + first;
                    String toReplace = "%amount" + firstNum + "-" + secondNum + "%";
                    cmd = cmd.replaceAll(toReplace, random + "");
                }
                catch (Exception exc)
                {
                    ChatUtils
                            .log("The %amountX-X% placeholder is improperly formatted. Please use %amountX-Y where X is the starting value and Y is the ending (X is LESS THAN Y)");
                }
            }
        }

        return cmd;
    }

    public void writeToFile()
    {
        FileHandler fu = getCc().getRewardsFile();
        FileConfiguration fc = fu.get();
        fc.set(getPath("commands"), getCommands());
        fc.set(getPath("chance"), getChance());
        fc.set(getPath("rarity"), getRarity());
        fc.set(getPath("receive-limit"), /*getTotalUses()*/null);
        fc.set(getPath("give-display-item.value"), giveDisplayItem);
        fc.set(getPath("give-display-item.with-lore"), giveDisplayItemLore);
        fc.set(getPath("give-display-item.with-name"), giveDisplayItemName);

        saveBuilder.saveItem(getCc().getRewardsFile(), getPath("display-item"), false);

        fu.save();
    }

    public String delete(boolean forSure)
    {
        if (!forSure)
        {
            ArrayList<String> cratesThatUse = new ArrayList<>();
            for (Crate cs : Crate.getLoadedCrates().values())
            {
                if (!cs.isMultiCrate())
                {
                    for (Reward r : cs.getCs().getCr().getCrateRewards())
                    {
                        if (r.equals(this))
                        {
                            cratesThatUse.add(cs.getName());
                            break;
                        }
                    }
                }
            }

            return cratesThatUse.toString();
        }
        else
        {
            getCc().getRewardsFile().get().set(getRewardName(), null);
            getCc().getRewardsFile().save();
            CRewards.getAllRewards().remove(getRewardName());
        }
        return "";
    }

    public String applyVariablesTo(String s)
    {
        return ChatUtils.toChatColor(s.replace("%rewardname%", getRewardName()).
                replace("%displayname%", saveBuilder.getDisplayName()).
                replace("%writtenchance%", getChance() + "").
                replace("%rarity%", rarity)).
                replace("%chance%", getFormattedChance());
    }

    public String getFormattedChance()
    {
        if (toLog)
        {
            double ch = getChance() / cr.getTotalOdds();
            ch = ch * 100;
            return new DecimalFormat("#.##").format(ch);
        }
        else
        {
            return -1 + "";
        }
    }

    public void loadChance()
    {
        try
        {
            setChance(getCc().getRewardsFile().get().getDouble(getPath("chance")));
        }
        catch (Exception exc)
        {
            needsMoreConfig = true;
            if (toLog)
            {
                setChance(-1);
                StatusLoggerEvent.REWARD_CHANCE_NONEXISTENT.log(getCr().getCrates(), new String[]{this.toString()});
            }
        }
    }

    @Deprecated
    public boolean loadFromConfig()
    {
        setFc(getCc().getRewardsFile().get());
        boolean success = true;
        needsMoreConfig = false;

        if(fc.contains(getPath("item")))
        {
            ChatUtils.log("Converting " + getRewardName() + " to new reward format.");
            RewardConverter rewardConverter = new RewardConverter(this);
            rewardConverter.loadFromConfig();
            rewardConverter.saveAllAsNull();

            saveBuilder.saveItem(cc.getRewardsFile(), getPath("display-item"), false);
            cc.getRewardsFile().save();
        }
        else
        {
            if(toLog)
                saveBuilder.loadItem(getCc().getRewardsFile(), getRewardName() + ".display-item", getCr().getCrates().getCs().getSl(),
                        StatusLoggerEvent.REWARD_ITEM_FAILURE, StatusLoggerEvent.REWARD_ENCHANT_INVALID,
                        StatusLoggerEvent.REWARD_POTION_INVALID, StatusLoggerEvent.REWARD_GLOW_FAILURE,
                        StatusLoggerEvent.REWARD_AMOUNT_INVALID, StatusLoggerEvent.REWARD_FLAG_FAILURE);
            else
                saveBuilder.loadItem(getCc().getRewardsFile(), getRewardName() + ".display-item");
        }

        if(!loadNonItemValsFromConfig())
            success = false;

        if (getRarity() == null)
            rarity = "default";

        displayBuilder = new ItemBuilder(saveBuilder);
        displayBuilder.setDisplayName(applyVariablesTo(saveBuilder.getDisplayName()));

        displayBuilder.clearLore();
        for(String loreLine : saveBuilder.getLore())
            displayBuilder.addLore(applyVariablesTo(loreLine));

        return success;
    }

    public boolean loadNonItemValsFromConfig()
    {
        boolean success = true;
        try
        {
            setRarity(getFc().getString(getPath("rarity")));
        }
        catch (Exception exc)
        {
            //needsMoreConfig = true;
            if (toLog)
            {
                StatusLoggerEvent.REWARD_RARITY_NONEXISTENT.log(getCr().getCrates(), new String[]{this.toString()});
                success = false;
            }
        }

        try
        {
            setGiveDisplayItem(getFc().getBoolean(getPath("give-display-item.value")));
        }
        catch(Exception exc)
        {
            setGiveDisplayItem(false);
        }

        try
        {
            setGiveDisplayItemLore(getFc().getBoolean(getPath("give-display-item.with-lore")));
        }
        catch(Exception exc)
        {
            setGiveDisplayItemLore(true);
        }

        try
        {
            if(getFc().contains(getPath("give-display-item.with-name")))
                setGiveDisplayItemName(getFc().getBoolean(getPath("give-display-item.with-name")));
        }
        catch(Exception exc)
        {
            setGiveDisplayItemName(true);
        }

        try
        {
            setCommands(getFc().getStringList(getPath("commands")));
        }
        catch (Exception exc)
        {
            if (toLog)
            {
                StatusLoggerEvent.REWARD_COMMAND_INVALID.log(getCr().getCrates(), new String[]{this.toString()});
                success = false;
            }
        }

        try
        {
            setTotalUses(getFc().getInt(getPath("receive-limit")));
        }
        catch (Exception exc)
        {
            setTotalUses(-1);
        }

        return success;
    }

    public String getDisplayName()
    {
        if (displayBuilder == null || displayBuilder.getDisplayName() == null)
            return rewardName;
        return displayBuilder.getDisplayName();
    }

    public void checkIsNeedMoreConfig()
    {
        needsMoreConfig = !(chance != -1 && saveBuilder.getDisplayName() != null && rarity != null && saveBuilder != null);
    }

    public boolean equals(Reward r)
    {
        return r.getRewardName().equalsIgnoreCase(getRewardName());
    }

    public String toString()
    {
        return getRewardName();
    }

    public String getPath(String s)
    {
        return getRewardName() + "." + s;
    }

    public boolean isGiveDisplayItemName()
    {
        return giveDisplayItemName;
    }

    public void setGiveDisplayItemName(boolean giveDisplayItemName)
    {
        this.giveDisplayItemName = giveDisplayItemName;
    }

    public List<String> getCommands()
    {
        return commands;
    }

    public void setCommands(List<String> list)
    {
        this.commands = list;
    }

    public String getRewardName()
    {
        return rewardName;
    }

    public void setRewardName(String rewardName)
    {
        this.rewardName = rewardName;
    }

    public String getRarity()
    {
        return rarity;
    }

    public void setRarity(String rarity)
    {
        this.rarity = rarity;
    }

    public Double getChance()
    {
        return chance;
    }

    public void setChance(Integer chance)
    {
        this.chance = chance;
    }

    public SpecializedCrates getCc()
    {
        return cc;
    }

    public void setCc(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    public int getTotalUses()
    {
        return totalUses;
    }

    public void setTotalUses(int totalUses)
    {
        this.totalUses = totalUses;
    }

    public FileConfiguration getFc()
    {
        return fc;
    }

    public void setFc(FileConfiguration fc)
    {
        this.fc = fc;
    }

    public void setChance(double chance)
    {
        this.chance = chance;
    }

    public CRewards getCr()
    {
        return cr;
    }

    public void setCr(CRewards cr)
    {
        this.cr = cr;
    }

    public boolean isNeedsMoreConfig()
    {
        return needsMoreConfig;
    }

    public void setNeedsMoreConfig(boolean needsMoreConfig)
    {
        this.needsMoreConfig = needsMoreConfig;
    }

    public boolean isGiveDisplayItem()
    {
        return giveDisplayItem;
    }

    public void setGiveDisplayItem(boolean giveDisplayItem)
    {
        this.giveDisplayItem = giveDisplayItem;
    }

    public boolean isGiveDisplayItemLore()
    {
        return giveDisplayItemLore;
    }

    public void setGiveDisplayItemLore(boolean giveDisplayItemLore)
    {
        this.giveDisplayItemLore = giveDisplayItemLore;
    }

    public ItemBuilder getDisplayBuilder()
    {
        return displayBuilder;
    }

    public void setDisplayBuilder(ItemBuilder displayBuilder)
    {
        this.displayBuilder = displayBuilder;
    }

    public ItemBuilder getSaveBuilder()
    {
        return saveBuilder;
    }

    public void setSaveBuilder(SaveableItemBuilder saveBuilder)
    {
        this.saveBuilder = saveBuilder;
    }

    public void setBuilder(ItemBuilder setBuilder)
    {
        this.saveBuilder = new SaveableItemBuilder(setBuilder);
        this.displayBuilder = new ItemBuilder(setBuilder);
    }
}
