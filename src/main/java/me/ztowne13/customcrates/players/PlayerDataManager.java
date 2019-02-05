package me.ztowne13.customcrates.players;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.players.data.DataHandler;
import me.ztowne13.customcrates.players.data.VirtualCrateData;
import me.ztowne13.customcrates.players.data.events.CrateCooldownEvent;
import me.ztowne13.customcrates.players.data.events.HistoryEvent;
import me.ztowne13.customcrates.utils.CrateUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ztowne13 on 8/5/15.
 */
public class PlayerDataManager
{
    PlayerManager pm;
    DataHandler dh;

    String history = "";
    ArrayList<HistoryEvent> historyEvents = new ArrayList<HistoryEvent>();
    String crateCooldowns = "";
    ArrayList<CrateCooldownEvent> crateCooldownEvents = new ArrayList<CrateCooldownEvent>();
    String virtualCrates = "";
    HashMap<Crate, VirtualCrateData> virtualCrateData = new HashMap<>();

    boolean activatedLuckyChests = true;

    /*String rewardLimits = "";
    ArrayList<RewardLimitEvent> rewardLimitEvents = new ArrayList<RewardLimitEvent>();*/

    public PlayerDataManager(PlayerManager pm)
    {
        this.pm = pm;
        this.dh = pm.getDh();
    }

    public void loadAllInformation()
    {
        if (getDh().hasDataValue("history"))
        {
            setHistory(getDh().get("history").toString());
        }
        if (getDh().hasDataValue("crate-cooldowns"))
        {
            setCrateCooldowns(getDh().get("crate-cooldowns").toString());
        }
        if(getDh().hasDataValue("virtual-crates"))
        {
            setVirtualCrates(getDh().get("virtual-crates").toString());
        }
       /* if (dh.hasDataValue("reward-limits")) {
            rewardLimits = dh.get("reward-limits").toString();
        }*/

        parseAll();
    }

    public void parseAll()
    {
        for (String unParsed : getHistory().split(","))
        {
            if(!unParsed.equalsIgnoreCase(""))
            {
                String[] split = unParsed.split(";");

                if(Crate.exists(split[1]))
                {
                    Crate crates = Crate.getCrate(getDh().getCc(), split[1]);
                    if(CrateUtils.isCrateUsable(crates))
                    {
                        ArrayList<Reward> rewards = new ArrayList<Reward>();
                        for(String s : split[2].replace("[", "").replace("]","").split("%newReward% "))
                        {
                            Reward r = crates.getCs().getCr().getByName(s);

                            if(rewards != null)
                            {
                                rewards.add(r);
                            }
                        }

                        boolean success = Boolean.valueOf(split[3].toUpperCase());

                        getHistoryEvents().add(0, new HistoryEvent(split[0], crates, rewards, success));
                    }
                }
            }
        }

        for (String unParsed : getCrateCooldowns().split(","))
        {
            if(!unParsed.equalsIgnoreCase(""))
            {
                String[] split = unParsed.split(";");

                if(Crate.exists(split[0]))
                {
                    Crate crates = Crate.getCrate(getPm().getCc(), split[0]);
                    long startTime = Long.valueOf(split[1]);

                    getCrateCooldownEvents().add(new CrateCooldownEvent(crates, startTime, false));
                }
            }
        }

        for(String unParsed : getVirtualCrates().split(","))
        {
            if(!unParsed.equalsIgnoreCase(""))
            {
                String[] split = unParsed.split(";");
                if(Crate.exists(split[0]))
                {
                    Crate crate = Crate.getCrate(getPm().getCc(), split[0]);
                    try
                    {
                        int crates = Integer.parseInt(split[1]);
                        int keys = Integer.parseInt(split[2]);
                        getVirtualCrateData().put(crate, new VirtualCrateData(crate, crates, keys));
                    }
                    catch(Exception exc)
                    {
                        exc.printStackTrace();
                    }
                }
            }
        }

        /*for(String unParsed : getRewardLimits().split(","))
        {
            if(!unParsed.equalsIgnoreCase(""))
            {
                try {
                    String[] split = unParsed.split(";");
                    Reward r = CRewards.allRewards.get(split[0]);
                    int currentUses = Integer.parseInt(split[1]);
                    rewardLimitEvents.add(new RewardLimitEvent(r, currentUses, 0));
                }
                catch(Exception exc)
                {
                    continue;
                }
            }
        }*/
    }

    public String removeStringFromList(String toRemove, String list)
    {
        String newList = "";

        for(String parsed : list.replace(" ", "").split(","))
        {
            if(!parsed.equals(toRemove))
            {
                newList = newList.equals("") ? parsed : "," + parsed;
            }
        }

        return newList;
    }

    public String addStringToList(String toAdd, String list)
    {
        if (!list.equalsIgnoreCase(""))
        {
            return toAdd + "," + list;
        }

        return toAdd;
    }

    public void addHistory(HistoryEvent he, String history)
    {
        setHistory(history);
        getHistoryEvents().add(he);
    }

    public void removeHistory(HistoryEvent he, String history)
    {
        setHistory(history);
        for (HistoryEvent he2 : getHistoryEvents())
        {
            if (he.matches(he2))
            {
                getHistoryEvents().remove(he2);
                getDh().write("history", getHistory());
                break;
            }
        }
    }

   public void setVirtualCrateCrates(Crate crate, int crates)
    {
        VirtualCrateData vCD = getVCCrateData(crate);
        String temp = removeStringFromList(vCD.toString(), getVirtualCrates());
        vCD.setCrates(crates);
        setVirtualCrates(addStringToList(vCD.toString(), temp));
        getDh().write("virtual-crates", getVirtualCrates());
    }

    public void setVirtualCrateKeys(Crate crate, int keys)
    {
        VirtualCrateData vCD = getVCCrateData(crate);
        String temp = removeStringFromList(vCD.toString(), getVirtualCrates());
        vCD.setKeys(keys);
        setVirtualCrates(addStringToList(vCD.toString(), temp));
        getDh().write("virtual-crates", getVirtualCrates());
    }

    public void updateHistory()
    {

    }


    public VirtualCrateData getVCCrateData(Crate crate)
    {
        if(getVirtualCrateData().containsKey(crate))
        {
            return getVirtualCrateData().get(crate);
        }
        else
        {
            getVirtualCrateData().put(crate, new VirtualCrateData(crate, 0, 0));
            return getVCCrateData(crate);
        }
    }

    public void addCrateCooldowns(CrateCooldownEvent cce, String crateCooldownsToSet)
    {
        setCrateCooldowns(crateCooldownsToSet);
        getCrateCooldownEvents().add(cce);
    }

    public void removeCrateCooldowns(CrateCooldownEvent cce, String crateCooldownsToSet)
    {
        setCrateCooldowns(crateCooldownsToSet);
        for (CrateCooldownEvent cce2 : getCrateCooldownEvents())
        {
            if (cce.matches(cce2))
            {
                getCrateCooldownEvents().remove(cce2);
                getDh().write("crate-cooldowns", crateCooldownsToSet);
                break;
            }
        }
    }

    public CrateCooldownEvent getCrateCooldownEventByCrates(Crate crates)
    {
        for (CrateCooldownEvent cce : getCrateCooldownEvents())
        {
            if (cce.getCrates().getName().equalsIgnoreCase(crates.getName()))
            {
                return cce;
            }
        }
        return null;
    }

    public void setCrateCooldowns(String crateCooldowns)
    {
        this.crateCooldowns = crateCooldowns;
        getDh().write("crate-cooldowns", crateCooldowns);
    }

    public void setHistory(String history)
    {
        this.history = history;
        getDh().write("history", getHistory());
    }

    public String getHistory()
    {
        return history;
    }

    public String getCrateCooldowns() {
        return crateCooldowns;
    }

    public DataHandler getDh() {
        return dh;
    }

    public void setDh(DataHandler dh) {
        this.dh = dh;
    }

    public ArrayList<HistoryEvent> getHistoryEvents() {
        return historyEvents;
    }

    public void setHistoryEvents(ArrayList<HistoryEvent> historyEvents) {
        this.historyEvents = historyEvents;
    }

    public ArrayList<CrateCooldownEvent> getCrateCooldownEvents() {
        return crateCooldownEvents;
    }

    public void setCrateCooldownEvents(ArrayList<CrateCooldownEvent> crateCooldownEvents) {
        this.crateCooldownEvents = crateCooldownEvents;
    }

    public PlayerManager getPm()
    {
        return pm;
    }

    public void setPm(PlayerManager pm)
    {
        this.pm = pm;
    }

    public boolean isActivatedLuckyChests()
    {
        return activatedLuckyChests;
    }

    public void setActivatedLuckyChests(boolean activatedLuckyChests)
    {
        this.activatedLuckyChests = activatedLuckyChests;
    }

    public String getVirtualCrates()
    {
        return virtualCrates;
    }

    public void setVirtualCrates(String virtualCrates)
    {
        this.virtualCrates = virtualCrates;
    }

    public HashMap<Crate, VirtualCrateData> getVirtualCrateData()
    {
        return virtualCrateData;
    }

    public void setVirtualCrateData(HashMap<Crate, VirtualCrateData> virtualCrateData)
    {
        this.virtualCrateData = virtualCrateData;
    }

    /* public String getRewardLimits() {
        return rewardLimits;
    }

    public void setRewardLimits(String rewardLimits) {
        this.rewardLimits = rewardLimits;
    }

    public ArrayList<RewardLimitEvent> getRewardLimitEvents() {
        return rewardLimitEvents;
    }

    public void setRewardLimitEvents(ArrayList<RewardLimitEvent> rewardLimitEvents) {
        this.rewardLimitEvents = rewardLimitEvents;
    }

    public void addRewardLimit(RewardLimitEvent rle, String rewardLimitsToSet) {
        setRewardLimits(rewardLimitsToSet);
        rewardLimitEvents.add(rle);
    }

    public void removeRewardLimit(RewardLimitEvent rle, String rewardLimitsToSet) {
        setCrateCooldowns(rewardLimitsToSet);
        for (RewardLimitEvent rle2 : rewardLimitEvents) {
            if (rle.matches(rle2)) {
                rewardLimitEvents.remove(rle2);
                break;
            }
        }
    }

    public RewardLimitEvent getRewardLimitEventByMatch(RewardLimitEvent e)
    {
        for(RewardLimitEvent rle: getRewardLimitEvents())
        {
            if(rle.matches(e))
            {
                return e;
            }
        }
        return null;
    }

    public int getCurrentRewardLimitUses(Reward r)
    {
        for(RewardLimitEvent rle: getRewardLimitEvents())
        {
            if(rle.r.getRewardName().equalsIgnoreCase(r.getRewardName()))
            {
                return rle.currentUses;
            }
        }
        return 0;
    }*/
}
