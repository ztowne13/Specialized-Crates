package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CRewards extends CSetting
{
    public static HashMap<String, Reward> allRewards = new HashMap<String, Reward>();

    Reward[] crateRewards;

    public CRewards(Crate crates)
    {
        super(crates, crates.getCc());
    }

    public Reward getByName(String s)
    {
        for (Reward r : getCrateRewards())
        {
            if (r.getDisplayName().equals(s) || r.getRewardName().equals(s))
                return r;
        }
        return null;
    }

    public void saveToFile()
    {
        String[] displayNameRewards = new String[crateRewards.length];
        for (int i = 0; i < displayNameRewards.length; i++)
        {
            displayNameRewards[i] = crateRewards[i].getRewardName();
        }
        getFu().get().set("rewards", Arrays.asList(displayNameRewards));
    }

    public void removeReward(String name)
    {
        Reward toRemove = getByName(name);
        Reward[] newRewards = new Reward[crateRewards.length - 1];

        int i = 0;
        boolean foundOne = false;
        for (Reward r : crateRewards)
        {
            if (!r.equals(toRemove) || foundOne)
            {
                newRewards[i] = r;
                i++;
            }
            else
            {
                foundOne = true;
            }
        }

        crateRewards = newRewards;
    }

    public boolean addReward(String rName)
    {
        if (allRewards.containsKey(rName))
        {
            Reward toAdd = allRewards.get(rName);
            if (!toAdd.isNeedsMoreConfig())
            {
                Reward[] newRewards =
                        new Reward[(crateRewards == null || crateRewards.length == 0 ? 0 : crateRewards.length) + 1];

                for (int i = 0; i < newRewards.length - 1; i++)
                {
                    newRewards[i] = crateRewards[i];
                }

                newRewards[newRewards.length - 1] = toAdd;

                crateRewards = newRewards;
                return true;
            }
        }
        return false;
    }

    public void loadFor(CrateSettingsBuilder csb, CrateState cs)
    {
        if (csb.hasV("rewards"))
        {
            int slot = 0;

            setCrateRewards(new Reward[getCrate().getSettings().getFc().getStringList("rewards").size()]);

            List<String> unparsedRewards = getCrate().getSettings().getFc().getStringList("rewards");

            for (int i = 0; i < unparsedRewards.size(); i++)
            {
                String s = unparsedRewards.get(i);

                Reward reward = new Reward(getCrate().getCc(), this, s);

                setReward(slot, reward);

                getAllRewards().put(s, reward);
                StatusLoggerEvent.REWARD_ADD_SUCCESS.log(getCrate(), new String[]{s});

                slot++;
            }

            Reward[] updatedRewards = new Reward[getCrateRewards().length];
            int count = 0;
            for (Reward r : getCrateRewards().clone())
            {
                if (r.loadFromConfig())
                {
                    updatedRewards[count] = r;
                    count++;
                }
            }

            Reward[] finalUpdate = new Reward[count];
            count = 0;
            for (Reward r : updatedRewards)
            {
                if (r != null)
                {
                    finalUpdate[count] = r;
                    count++;
                }
            }

            setCrateRewards(finalUpdate);
            return;
        }
        StatusLoggerEvent.REWARD_NONEXISTENT.log(getCrate());
    }

    public void setReward(Integer i, Reward reward)
    {
        getCrateRewards()[i] = reward;
    }

    public Reward getRandomReward(Player p)
    {
        double totalOdds = getTotalOdds();

        double randNum = getRandomNumber(totalOdds);

        double currentStackedOdds = 0;

        Reward[] crateRewardsClone = getCrateRewards();

        for (Reward r : crateRewardsClone)
        {
            double odds = r.getChance();
            currentStackedOdds += odds;
            if (randNum <= currentStackedOdds)
            {
                return r;
            }
        }

        return null;
    }

    public Double getRandomNumber(double outOfOdds)
    {
        Random r = new Random();

        return outOfOdds * r.nextDouble();
    }

    public Double getTotalOdds()
    {
        double totalOdds = 0;
        for (Reward r : getCrateRewards())
        {
            double odds = r.getChance();
            if(odds < 0)
                odds = 0;
            totalOdds += odds;
        }
        return totalOdds;
    }

    public static void loadAll(SpecializedCrates specializedCrates, Player player)
    {
        boolean newValues = false;

        for(String rName : specializedCrates.getRewardsFile().get().getKeys(false))
        {
            if (!getAllRewards().keySet().contains(rName))
            {
                if(!newValues)
                {
                    newValues = true;
                    ChatUtils.msgInfo(player, "It can take a while to load all of the rewards for the first time...");
                }
                Reward r = new Reward(specializedCrates, rName);
                r.loadFromConfig();
                r.loadChance();
                allRewards.put(rName, r);
            }
        }
    }

    public static boolean rewardNameExists(SpecializedCrates cc, String name)
    {
        for (String s : cc.getRewardsFile().get().getKeys(false))
        {
            if (s.equalsIgnoreCase(name))
            {
                return true;
            }
        }
        return false;
    }

    public Reward[] getCrateRewards()
    {
        return crateRewards;
    }

    public void setCrateRewards(Reward[] crateRewards)
    {
        this.crateRewards = crateRewards;
    }

    public static HashMap<String, Reward> getAllRewards()
    {
        return allRewards;
    }

    public static void setAllRewards(HashMap<String, Reward> allRewards)
    {
        CRewards.allRewards = allRewards;
    }
}
