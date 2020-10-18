package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

import java.util.*;

public class CRewards extends CSetting {
    private static final Map<String, Reward> allRewards = new HashMap<>();

    private Reward[] crateRewards;
    private final Random random = new Random();

    public CRewards(Crate crates) {
        super(crates, crates.getCc());
    }

    public static void loadAll(SpecializedCrates instance, Player player) {
        boolean newValues = false;

        for (String rName : instance.getRewardsFile().get().getKeys(false)) {
            if (!getAllRewards().containsKey(rName)) {
                if (!newValues) {
                    newValues = true;
                    ChatUtils.msgInfo(player, "It can take a while to load all of the rewards for the first time...");
                }
                Reward reward = new Reward(instance, rName);
                reward.loadFromConfig();
                reward.loadChance();
                allRewards.put(rName, reward);
            }
        }
    }

    public static boolean rewardNameExists(SpecializedCrates instance, String name) {
        for (String s : instance.getRewardsFile().get().getKeys(false)) {
            if (s.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, Reward> getAllRewards() {
        return allRewards;
    }

    public static Map<String, Reward> getAllRewardsSorted(SpecializedCrates specializedCrates, RewardSortType rewardSortType) {
        LinkedHashMap<String, Reward> sortedRewards = new LinkedHashMap<>();
        switch (rewardSortType) {
            case CREATED_ORDER:
                for (String rewardName : specializedCrates.getRewardsFile().get().getKeys(false)) {
                    Reward reward = getAllRewards().get(rewardName);
                    if (reward != null) {
                        sortedRewards.put(rewardName, reward);
                    }
                }
                return sortedRewards;
            case ALPHABETICAL:
                ArrayList<String> keys = new ArrayList<>(getAllRewards().keySet());
                keys.sort(String.CASE_INSENSITIVE_ORDER);
                for (String key : keys) {
                    sortedRewards.put(key, getAllRewards().get(key));
                }
                return sortedRewards;
            case CHANCE:
                ArrayList<Reward> rewards = new ArrayList<>(getAllRewards().values());
                Collections.sort(rewards);
                for (Reward reward : rewards) {
                    sortedRewards.put(reward.getRewardName(), reward);
                }
                return sortedRewards;
        }

        return null;
    }

    public Reward getByName(String s) {
        for (Reward r : getCrateRewards()) {
            if (r.getDisplayName(false).equals(s) || r.getRewardName().equals(s))
                return r;
        }
        return null;
    }

    public void saveToFile() {
        String[] displayNameRewards = new String[crateRewards.length];
        for (int i = 0; i < displayNameRewards.length; i++) {
            displayNameRewards[i] = crateRewards[i].getRewardName();
        }
        getFileHandler().get().set("rewards", Arrays.asList(displayNameRewards));
    }

    public void removeReward(String name) {
        Reward toRemove = getByName(name);
        Reward[] newRewards = new Reward[crateRewards.length - 1];

        int i = 0;
        boolean foundOne = false;
        for (Reward r : crateRewards) {
            if (!r.equals(toRemove) || foundOne) {
                newRewards[i] = r;
                i++;
            } else {
                foundOne = true;
            }
        }

        crateRewards = newRewards;
    }

    public boolean addReward(String rName) {
        if (allRewards.containsKey(rName)) {
            Reward toAdd = allRewards.get(rName);
            if (!toAdd.isNeedsMoreConfig()) {
                Reward[] newRewards =
                        new Reward[(crateRewards == null || crateRewards.length == 0 ? 0 : crateRewards.length) + 1];

                if (crateRewards != null) {
                    System.arraycopy(crateRewards, 0, newRewards, 0, newRewards.length - 1);
                }

                newRewards[newRewards.length - 1] = toAdd;

                crateRewards = newRewards;
                return true;
            }
        }
        return false;
    }

    public void loadFor(CrateSettingsBuilder crateSettingsBuilder, CrateState crateState) {
        if (crateSettingsBuilder.hasV("rewards")) {
            int slot = 0;

            setCrateRewards(new Reward[getCrate().getSettings().getFc().getStringList("rewards").size()]);

            List<String> unparsedRewards = getCrate().getSettings().getFc().getStringList("rewards");

            for (String s : unparsedRewards) {
                Reward reward = new Reward(getCrate().getCc(), this, s);

                setReward(slot, reward);

                getAllRewards().put(s, reward);
                StatusLoggerEvent.REWARD_ADD_SUCCESS.log(getCrate(), new String[]{s});

                slot++;
            }

            Reward[] updatedRewards = new Reward[getCrateRewards().length];
            int count = 0;
            for (Reward reward : getCrateRewards().clone()) {
                if (reward.loadFromConfig()) {
                    updatedRewards[count] = reward;
                    count++;
                }
            }

            Reward[] finalUpdate = new Reward[count];
            count = 0;
            for (Reward r : updatedRewards) {
                if (r != null) {
                    finalUpdate[count] = r;
                    count++;
                }
            }

            setCrateRewards(finalUpdate);

            if (finalUpdate.length == 0) {
                StatusLoggerEvent.REWARDS_EMPTY.log(getCrate());
                getCrate().setDisabledByError(true);
            }

            return;
        }
        StatusLoggerEvent.REWARDS_PATH_NONEXISTENT.log(getCrate());
    }

    public void setReward(Integer i, Reward reward) {
        getCrateRewards()[i] = reward;
    }

    public Reward getRandomReward() {
        double totalOdds = getTotalOdds();

        double randNum = getRandomNumber(totalOdds);

        double currentStackedOdds = 0;

        Reward[] crateRewardsClone = getCrateRewards();

        for (Reward r : crateRewardsClone) {
            double odds = r.getChance();
            currentStackedOdds += odds;
            if (randNum <= currentStackedOdds) {
                return r;
            }
        }

        return null;
    }

    public Double getRandomNumber(double outOfOdds) {
        return outOfOdds * random.nextDouble();
    }

    public Double getTotalOdds() {
        double totalOdds = 0;
        for (Reward r : getCrateRewards()) {
            double odds = r.getChance();
            if (odds < 0)
                odds = 0;
            totalOdds += odds;
        }
        return totalOdds;
    }

    public Reward[] getCrateRewards() {
        return crateRewards;
    }

    public void setCrateRewards(Reward[] crateRewards) {
        this.crateRewards = crateRewards;
    }

    public enum RewardSortType {
        CREATED_ORDER("Created Order", "ALPHABETICAL",
                new String[]{
                        "The order in which the",
                        "rewards were created."
                }),
        ALPHABETICAL("Alphabetical Order", "CHANCE",
                new String[]{
                        "In order, from A-Z"
                }),
        CHANCE("Reward Chance Order", "CREATED_ORDER",
                new String[]{
                        "In order from the lowest",
                        "to highest chance"
                });

        String niceName;
        String[] niceDescription;
        String next;

        RewardSortType(String niceName, String next, String[] niceDescription) {
            this.niceName = niceName;
            this.niceDescription = niceDescription;
            this.next = next;
        }

        public RewardSortType getNext() {
            return valueOf(next);
        }

        public String getNiceName() {
            return niceName;
        }

        public String[] getNiceDescription() {
            return niceDescription;
        }
    }
}
