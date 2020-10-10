package me.ztowne13.customcrates.players;

import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.sql.SQLQueryThread;
import me.ztowne13.customcrates.players.data.DataHandler;
import me.ztowne13.customcrates.players.data.SQLDataHandler;
import me.ztowne13.customcrates.players.data.VirtualCrateData;
import me.ztowne13.customcrates.players.data.events.CrateCooldownEvent;
import me.ztowne13.customcrates.players.data.events.HistoryEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.CrateUtils;
import me.ztowne13.customcrates.utils.DebugUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ztowne13 on 8/5/15.
 */
public class PlayerDataManager {
    PlayerManager pm;
    DataHandler dh;

    boolean loaded = false;

    String history = "";
    List<HistoryEvent> historyEvents = new ArrayList<>();
    String crateCooldowns = "";
    List<CrateCooldownEvent> crateCooldownEvents = new ArrayList<>();
    String virtualCrates = "";
    Map<Crate, VirtualCrateData> virtualCrateData = new HashMap<>();

    boolean activatedLuckyChests = true;

    /*String rewardLimits = "";
    ArrayList<RewardLimitEvent> rewardLimitEvents = new ArrayList<RewardLimitEvent>();*/

    public PlayerDataManager(PlayerManager pm) {
        this.pm = pm;
        this.dh = pm.getDh();
    }

    public void loadAllInformation() {
        pm.getCc().getDu().log("loadAllInformation() - CALL", getClass());
        if (isSQL()) {
            pm.getCc().getDu().log("loadAllInformation() - isSQL", getClass());
            Runnable runnable = () -> {
                String toSetUUID = "uuid='" + pm.getP().getUniqueId() + "'";
                SQLDataHandler.sql.insert(SQLDataHandler.table,
                        toSetUUID + ", history='', crateCooldowns='', virtualCrates='', rewardLimits=''", true);

                try {
                    loadAllInformationHelper();
                } catch (Exception exc) {
                    ChatUtils.log("&4ERROR: &cFailed to load the SQL Database Handler");
//                        setDh(new FlatFileDataHandler(getPm()));
//                        loadAllInformation();
                    return;
                }

                loaded = true;
            };

            SQLQueryThread.addQuery(runnable);
        } else {
            loadAllInformationHelper();
            loaded = true;
        }
    }

    public void loadAllInformationHelper() {
        long curTime = System.currentTimeMillis();
        pm.getCc().getDu().log("loadAllInformationHelper() - CALL (" + pm.getP().getName() + ")", getClass());
        if (getDh().hasDataValue("history")) {
            setHistory(getDh().get("history").toString(), false);
        }
        if (getDh().hasDataValue("crate-cooldowns")) {
            setCrateCooldowns(getDh().get("crate-cooldowns").toString(), false);
        }
        if (getDh().hasDataValue("virtual-crates")) {
            pm.getCc().getDu().log("loadAllInformationHelper() - contains virtual-crates value: (" + getDh().get("virtual-crates").toString() + ")", getClass());
            setVirtualCrates(getDh().get("virtual-crates").toString());
        }
       /* if (dh.hasDataValue("reward-limits")) {
            rewardLimits = dh.get("reward-limits").toString();
        }*/

        getPm().getCc().getDataHandler().playAllQueuedGiveCommands(getPm().getP().getUniqueId());

        parseAll();

        loaded = true;
        if (DebugUtils.OUTPUT_PLAYER_DATA_LOAD_TIME) {
            ChatUtils.log("Loaded " + getPm().getP().getName() + "'s data in " + (System.currentTimeMillis() - curTime) + "ms");
        }
    }

    public void parseAll() {
        int historyLimit = (int) SettingsValue.PLAYER_HISTORY_LIMIT.getValue(getPm().getCc());
        int count = 0;
        String recreatedHistory = "";
        for (String unParsed : getHistory().split(",")) {
            recreatedHistory = (recreatedHistory.equalsIgnoreCase("") ? unParsed : recreatedHistory + "," + unParsed);
            count++;
            if (count > historyLimit && historyLimit >= 0) {
                setHistory(recreatedHistory, false);
                break;
            }

            if (!unParsed.equalsIgnoreCase("")) {
                String[] split = unParsed.split(";");

                if (Crate.exists(split[1])) {
                    Crate crates = Crate.getCrate(getDh().getCc(), split[1]);
                    if (CrateUtils.isCrateUsable(crates)) {
                        ArrayList<Reward> rewards = new ArrayList<>();
                        for (String s : split[2].replace("[", "").replace("]", "").split("%newReward% ")) {
                            Reward r = crates.getSettings().getRewards().getByName(s);

                            rewards.add(r);
                        }

                        boolean success = Boolean.parseBoolean(split[3].toUpperCase());

                        getHistoryEvents().add(0, new HistoryEvent(split[0], crates, rewards, success));
                    }
                }
            }
        }

        for (String unParsed : getCrateCooldowns().split(",")) {
            if (!unParsed.equalsIgnoreCase("")) {
                String[] split = unParsed.split(";");

                if (Crate.exists(split[0])) {
                    Crate crates = Crate.getCrate(getPm().getCc(), split[0]);
                    long startTime = Long.parseLong(split[1]);

                    getCrateCooldownEvents().add(new CrateCooldownEvent(crates, startTime, false));
                }
            }
        }

        for (String unParsed : getVirtualCrates().split(",")) {
            if (!unParsed.equalsIgnoreCase("")) {
                String[] split = unParsed.split(";");
                if (Crate.exists(split[0])) {
                    Crate crate = Crate.getCrate(getPm().getCc(), split[0]);
                    try {
                        int crates = Integer.parseInt(split[1]);
                        int keys = Integer.parseInt(split[2]);
                        getVirtualCrateData().put(crate, new VirtualCrateData(crate, crates, keys));
                    } catch (Exception exc) {
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

    public String removeStringFromList(String toRemove, String list) {
        String newList = "";

        for (String parsed : list.replace(" ", "").split(",")) {
            if (!parsed.equals(toRemove)) {
                newList = newList.equals("") ? parsed : newList + "," + parsed;
            }
        }

        return newList;
    }

    public String addStringToList(String toAdd, String list) {
        if (!list.equalsIgnoreCase("")) {
            return toAdd + "," + list;
        }

        return toAdd;
    }

    public void addHistory(HistoryEvent he, String history) {
        setHistory(history);
        getHistoryEvents().add(he);
        he.getCrates().setLastOpenedName(getPm().getP().getName());

        String rewards = he.getRewards().toString();

        he.getCrates().setLastOpenedReward(rewards.substring(1, rewards.length() - 1));
    }

    public void setVirtualCrateCrates(Crate crate, int crates) {
        pm.getCc().getDu().log("setVirtualCrateCrates() - CALL (" + pm.getP().getName() + ", " + crates + ")", getClass());
        VirtualCrateData vCD = getVCCrateData(crate);
        String temp = removeStringFromList(vCD.toString(), getVirtualCrates());
        vCD.setCrates(crates);
        setVirtualCrates(addStringToList(vCD.toString(), temp));
        getDh().write("virtual-crates", getVirtualCrates());
    }

    public void setVirtualCrateKeys(Crate crate, int keys) {
        pm.getCc().getDu().log("setVirtualCrateKeys() - CALL (" + pm.getP().getName() + ", " + crate.getName() + ", " + keys + ")", getClass());
        VirtualCrateData vCD = getVCCrateData(crate);
        pm.getCc().getDu().log("setVirtualCrateKeys() - Before: " + vCD.toString(), getClass());
        String temp = removeStringFromList(vCD.toString(), getVirtualCrates());
        vCD.setKeys(keys);
        pm.getCc().getDu().log("setVirtualCrateKeys() - After: " + vCD.toString(), getClass());
        pm.getCc().getDu().log("setVirtualCrateKeys() - Result: " + addStringToList(vCD.toString(), temp), getClass());
        setVirtualCrates(addStringToList(vCD.toString(), temp));
        getDh().write("virtual-crates", getVirtualCrates());
    }

    public VirtualCrateData getVCCrateData(Crate crate) {
        pm.getCc().getDu().log("getVCCrateData() - CALL (" + pm.getP().getName() + ", " + crate.getName() + ")", getClass());
        if (getVirtualCrateData().containsKey(crate)) {
            pm.getCc().getDu().log("getVCCrateData() - already contains", getClass());
            return getVirtualCrateData().get(crate);
        } else {
            pm.getCc().getDu().log("getVCCrateData() - doesn't contain", getClass());
            getVirtualCrateData().put(crate, new VirtualCrateData(crate, 0, 0));
            return getVCCrateData(crate);
        }
    }

    public void addCrateCooldowns(CrateCooldownEvent cce, String crateCooldownsToSet) {
        setCrateCooldowns(crateCooldownsToSet);
        getCrateCooldownEvents().add(cce);
    }

    public void removeCrateCooldowns(CrateCooldownEvent cce, String crateCooldownsToSet) {
        setCrateCooldowns(crateCooldownsToSet);
        for (CrateCooldownEvent cce2 : getCrateCooldownEvents()) {
            if (cce.matches(cce2)) {
                getCrateCooldownEvents().remove(cce2);
                getDh().write("crate-cooldowns", crateCooldownsToSet);
                break;
            }
        }
    }

    public CrateCooldownEvent getCrateCooldownEventByCrates(Crate crates) {
        for (CrateCooldownEvent cce : getCrateCooldownEvents()) {
            if (cce.getCrates().getName().equalsIgnoreCase(crates.getName())) {
                return cce;
            }
        }
        return null;
    }

    public boolean isSQL() {
        return getDh() instanceof SQLDataHandler;
    }

    public void setCrateCooldowns(String crateCooldowns, boolean write) {
        this.crateCooldowns = crateCooldowns;
        if (write)
            getDh().write("crate-cooldowns", crateCooldowns);
    }

    public void setHistory(String history, boolean write) {
        this.history = history;
        if (write)
            getDh().write("history", getHistory());
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        setHistory(history, true);
    }

    public String getCrateCooldowns() {
        return crateCooldowns;
    }

    public void setCrateCooldowns(String crateCooldowns) {
        setCrateCooldowns(crateCooldowns, true);
    }

    public DataHandler getDh() {
        return dh;
    }

    public void setDh(DataHandler dh) {
        this.dh = dh;
    }

    public List<HistoryEvent> getHistoryEvents() {
        return historyEvents;
    }

    public List<CrateCooldownEvent> getCrateCooldownEvents() {
        return crateCooldownEvents;
    }

    public PlayerManager getPm() {
        return pm;
    }

    public void setPm(PlayerManager pm) {
        this.pm = pm;
    }

    public boolean isActivatedLuckyChests() {
        return activatedLuckyChests;
    }

    public void setActivatedLuckyChests(boolean activatedLuckyChests) {
        this.activatedLuckyChests = activatedLuckyChests;
    }

    public String getVirtualCrates() {
        pm.getCc().getDu().log("getVirtualCrates() - CALL (" + pm.getP().getName() + ")", getClass());
        return virtualCrates;
    }

    public void setVirtualCrates(String virtualCrates) {
        pm.getCc().getDu().log("setVirtualCrates() - CALL (" + pm.getP().getName() + ")", getClass());
        this.virtualCrates = virtualCrates;
    }

    public Map<Crate, VirtualCrateData> getVirtualCrateData() {
        return virtualCrateData;
    }

    public void setVirtualCrateData(Map<Crate, VirtualCrateData> virtualCrateData) {
        this.virtualCrateData = virtualCrateData;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
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
