package me.ztowne13.customcrates.crates.options.rewards;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.interfaces.items.CompressedEnchantment;
import me.ztowne13.customcrates.interfaces.items.CompressedPotionEffect;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.FileHandler;
import me.ztowne13.customcrates.utils.NMSUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Reward
{
    CustomCrates cc;
    FileConfiguration fc;
    Random r;

    CRewards cr;
    String rewardName, tempDisplayName;
    String rarity = "default";

    ItemBuilder itemBuilder;

    double chance;
    List<String> commands;
    int totalUses;
    boolean needsMoreConfig;

    boolean toLog;

    public Reward(CustomCrates cc, String rewardName)
    {
        init();
        needsMoreConfig = true;
        this.cc = cc;
        setRewardName(rewardName);
        itemBuilder = new ItemBuilder(DynamicMaterial.STONE, 1);
        itemBuilder.setDisplayName(rewardName);
        this.r = new Random();
    }

    public Reward(CustomCrates cc, CRewards cr, String rewardName)
    {
        this(cc, rewardName);
        init();
        this.cr = cr;
        toLog = true;
        loadChance();
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
        for (String command : getCommands())
        {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), applyCommandPlaceHolders(p, command));
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
                    if(second - first == 0)
                    {
                        second = second + 1;
                    }
                    else if(second - first < 0)
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
        fc.set(getPath("name"), itemBuilder.getDisplayNameStripped());
        fc.set(getPath("commands"), getCommands());
        fc.set(getPath("item"), DynamicMaterial.fromItemStack(itemBuilder.get()).name());
        fc.set(getPath("glow"), itemBuilder.isGlowing());
        fc.set(getPath("amount"), itemBuilder.get().getAmount());
        fc.set(getPath("head-player-name"), itemBuilder.getPlayerHeadName());
        fc.set(getPath("chance"), getChance());
        fc.set(getPath("rarity"), getRarity());
        fc.set(getPath("receive-limit"), /*getTotalUses()*/null);

        // Enchantments
        if (!itemBuilder.getEnchantments().isEmpty())
        {
            ArrayList<String> parsedEnchs = new ArrayList<>();
            for (CompressedEnchantment ench : itemBuilder.getEnchantments())
                parsedEnchs.add(ench.toString());

            fc.set(getPath("enchantments"), parsedEnchs);
        }
        else
            fc.set(getPath("enchantments"), null);

        // Potion Effects
        if (!itemBuilder.getPotionEffects().isEmpty())
        {
            ArrayList<String> parsedPots = new ArrayList<>();
            for (CompressedPotionEffect compressedPotionEffect : itemBuilder.getPotionEffects())
                parsedPots.add(compressedPotionEffect.toString());

            fc.set(getPath("potion-effects"), parsedPots);
        }
        else
            fc.set(getPath("potion-effects"), null);

        // Lore
        if (!itemBuilder.getLore().isEmpty())
            fc.set(getPath("lore"), itemBuilder.getLore());
        else
            fc.set(getPath("lore"), null);

        // NBT Tags
        if (NMSUtils.Version.v1_12.isServerVersionOrEarlier() && NMSUtils.Version.v1_8.isServerVersionOrLater())
        {
            if (!itemBuilder.getNBTTags().isEmpty())
                fc.set(getPath("nbt-tags"), itemBuilder.getNBTTags());
            else
                fc.set(getPath("nbt-tags"), null);
        }

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
                replace("%displayname%", itemBuilder.getDisplayName()).
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
            setChance(getCc().getRewardsFile().get().getInt(getPath("chance")));
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

    public boolean loadFromConfig()
    {
        setFc(getCc().getRewardsFile().get());
        boolean success = true;

        try
        {
            String unsplitMat = getFc().getString(getPath("item"));
            DynamicMaterial m = DynamicMaterial.fromString(unsplitMat);
            itemBuilder = new ItemBuilder(m, 1);
        }
        catch (Exception exc)
        {
            return false;
        }

        try
        {
            itemBuilder.setDisplayName(getFc().getString(getPath("name")));

        }
        catch (Exception exc)
        {
            itemBuilder.setDisplayName(itemBuilder.getStack().getType().name().toLowerCase());
            needsMoreConfig = true;
            if (toLog)
            {
                StatusLoggerEvent.REWARD_NAME_NONEXISTENT.log(getCr().getCrates(), new String[]{this.toString()});
                success = false;
            }
        }

        try
        {
            setRarity(getFc().getString(getPath("rarity")));
        }
        catch (Exception exc)
        {
            needsMoreConfig = true;
            if (toLog)
            {
                StatusLoggerEvent.REWARD_RARITY_NONEXISTENT.log(getCr().getCrates(), new String[]{this.toString()});
                success = false;
            }
        }

        try
        {
            itemBuilder.setGlowing(getFc().getBoolean(getPath("glow")));
        }
        catch (Exception exc)
        {
            itemBuilder.setGlowing(false);
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

        try
        {
            buildDisplayItemFromConfig();
        }
        catch (Exception exc)
        {
            needsMoreConfig = true;
            if (toLog)
            {
                StatusLoggerEvent.REWARD_ITEM_NONEXISTENT.log(getCr().getCrates(), new String[]{this.toString()});
                success = false;
            }
        }

        if (itemBuilder.getDisplayName() == null)
            itemBuilder.setDisplayName(rewardName);

        if (getRarity() == null)
            rarity = "default";

        return success;
    }

    public void buildDisplayItemFromConfig()
    {
        itemBuilder
                .setDisplayName(applyVariablesTo(cc.getSettings().getConfigValues().get("inv-reward-item-name").toString()));

        // If an item has a custom lore, apply that. Otherwise apply the general lore.
        if (getFc().contains(getPath("lore")))
        {

            for (String s : getFc().getStringList(getPath("lore")))
            {
                itemBuilder.addLore(applyVariablesTo(s));
            }
        }
        else
        {
            for (Object s : (ArrayList<String>) cc.getSettings().getConfigValues().get("inv-reward-item-lore"))
            {
                itemBuilder.addLore(applyVariablesTo(s.toString()));
            }
        }

        if (getFc().contains(getPath("head-player-name")))
        {
            itemBuilder.setPlayerHeadName(getFc().getString(getPath("head-player-name")));
        }

        if (getFc().contains(getPath("amount")))
        {
            try
            {
                int amnt = Integer.parseInt(getFc().getString(getPath("amount")));
                itemBuilder.getStack().setAmount(amnt);
            }
            catch (Exception exc)
            {
                StatusLoggerEvent.REWARD_AMOUNT_INVALID.log(getCr().getCrates(), new String[]{itemBuilder.getDisplayName()});
            }
        }

        if (NMSUtils.Version.v1_12.isServerVersionOrEarlier() && NMSUtils.Version.v1_8.isServerVersionOrLater())
        {
            if (getFc().contains(getPath("nbt-tags")))
            {
                for (String s : fc.getStringList(getPath("nbt-tags")))
                {
                    itemBuilder.addNBTTag(s);
                }
            }
        }

        if (getFc().contains(getPath("potion-effects")))
        {
            for (String unparsedPot : getFc().getStringList(getPath("potion-effects")))
            {
                try
                {
                    CompressedPotionEffect compressedPotionEffect = CompressedPotionEffect.fromString(unparsedPot);

                    if (compressedPotionEffect == null)
                        throw new Exception();
                    else
                        itemBuilder.addPotionEffect(compressedPotionEffect);
                }
                catch (Exception exc)
                {
                    StatusLoggerEvent.REWARD_POTION_INVALID
                            .log(getCr().getCrates(), new String[]{itemBuilder.getDisplayName(), unparsedPot});
                }
            }
        }

        if (getFc().contains(getPath("enchantments")))
        {
            String cause = getPath("enchantments") + " value is not a valid list of enchantments.";
            try
            {
                for (String s : getFc().getStringList(getPath("enchantments")))
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

                    itemBuilder.addEnchantment(ench, level);
                    continue;
                }
            }
            catch (Exception exc)
            {
                StatusLoggerEvent.REWARD_ENCHANT_INVALID
                        .log(getCr().getCrates(), new String[]{itemBuilder.getDisplayName(), cause});
            }
        }
    }

    public String getDisplayName()
    {
        if (itemBuilder == null || itemBuilder.getDisplayName() == null)
            return rewardName;
        return itemBuilder.getDisplayName();
    }

    public void checkIsNeedMoreConfig()
    {
        needsMoreConfig = !(chance != -1 && itemBuilder.getDisplayName() != null && rarity != null && itemBuilder != null);
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

    public CustomCrates getCc()
    {
        return cc;
    }

    public void setCc(CustomCrates cc)
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

    public String getTempDisplayName()
    {
        return tempDisplayName;
    }

    public void setTempDisplayName(String tempDisplayName)
    {
        this.tempDisplayName = tempDisplayName;
    }

    public ItemBuilder getItemBuilder()
    {
        return itemBuilder;
    }

    public void setItemBuilder(ItemBuilder itemBuilder)
    {
        this.itemBuilder = itemBuilder;
    }
}
