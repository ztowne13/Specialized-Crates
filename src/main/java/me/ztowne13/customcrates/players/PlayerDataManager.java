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
    private final PlayerManager playerManager;
    private final List<HistoryEvent> historyEvents = new ArrayList<>();
    private final List<CrateCooldownEvent> crateCooldownEvents = new ArrayList<>();
    private boolean activatedLuckyChests = true;
    private DataHandler dataHandler;
    private boolean loaded = false;
    private String history = "";
    private String crateCooldowns = "";
    private String virtualCrates = "";
    private Map<Crate, VirtualCrateData> virtualCrateData = new HashMap<>();

    public PlayerDataManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
        this.dataHandler = playerManager.getDataHandler();
    }

    public void loadAllInformation() {
        playerManager.getInstance().getDu().log("loadAllInformation() - CALL", getClass());
        if (isSQL()) {
            playerManager.getInstance().getDu().log("loadAllInformation() - isSQL", getClass());
            Runnable runnable = () -> {
                String toSetUUID = "uuid='" + playerManager.getPlayer().getUniqueId() + "'";
                SQLDataHandler.getSql().insert(SQLDataHandler.TABLE,
                        toSetUUID + ", history='', crateCooldowns='', virtualCrates='', rewardLimits=''", true);

                try {
                    loadAllInformationHelper();
                } catch (Exception exc) {
                    ChatUtils.log("&4ERROR: &cFailed to load the SQL Database Handler");
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
        playerManager.getInstance().getDu().log("loadAllInformationHelper() - CALL (" + playerManager.getPlayer().getName() + ")", getClass());
        if (getDataHandler().hasDataValue("history")) {
            setHistory(getDataHandler().get("history").toString(), false);
        }
        if (getDataHandler().hasDataValue("crate-cooldowns")) {
            setCrateCooldowns(getDataHandler().get("crate-cooldowns").toString(), false);
        }
        if (getDataHandler().hasDataValue("virtual-crates")) {
            playerManager.getInstance().getDu().log("loadAllInformationHelper() - contains virtual-crates value: (" + getDataHandler().get("virtual-crates").toString() + ")", getClass());
            setVirtualCrates(getDataHandler().get("virtual-crates").toString());
        }

        getPlayerManager().getInstance().getDataHandler().playAllQueuedGiveCommands(getPlayerManager().getPlayer().getUniqueId());

        parseAll();

        loaded = true;
        if (DebugUtils.OUTPUT_PLAYER_DATA_LOAD_TIME) {
            ChatUtils.log("Loaded " + getPlayerManager().getPlayer().getName() + "'s data in " + (System.currentTimeMillis() - curTime) + "ms");
        }
    }

    public void parseAll() {
        int historyLimit = (int) SettingsValue.PLAYER_HISTORY_LIMIT.getValue(getPlayerManager().getInstance());
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
                    Crate crates = Crate.getCrate(playerManager.getInstance(), split[1]);
                    if (CrateUtils.isCrateUsable(crates)) {
                        ArrayList<Reward> rewards = new ArrayList<>();
                        for (String s : split[2].replace("[", "").replace("]", "").split("%newReward% ")) {
                            Reward r = crates.getSettings().getReward().getByName(s);

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
                    Crate crates = Crate.getCrate(getPlayerManager().getInstance(), split[0]);
                    long startTime = Long.parseLong(split[1]);

                    getCrateCooldownEvents().add(new CrateCooldownEvent(crates, startTime, false));
                }
            }
        }

        for (String unParsed : getVirtualCrates().split(",")) {
            if (!unParsed.equalsIgnoreCase("")) {
                String[] split = unParsed.split(";");
                if (Crate.exists(split[0])) {
                    Crate crate = Crate.getCrate(getPlayerManager().getInstance(), split[0]);
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
        he.getCrate().setLastOpenedName(getPlayerManager().getPlayer().getName());

        String rewards = he.getRewards().toString();

        he.getCrate().setLastOpenedReward(rewards.substring(1, rewards.length() - 1));
    }

    public void setVirtualCrateCrates(Crate crate, int crates) {
        playerManager.getInstance().getDu().log("setVirtualCrateCrates() - CALL (" + playerManager.getPlayer().getName() + ", " + crates + ")", getClass());
        VirtualCrateData vCD = getVCCrateData(crate);
        String temp = removeStringFromList(vCD.toString(), getVirtualCrates());
        vCD.setCrates(crates);
        setVirtualCrates(addStringToList(vCD.toString(), temp));
        getDataHandler().write("virtual-crates", getVirtualCrates());
    }

    public void setVirtualCrateKeys(Crate crate, int keys) {
        playerManager.getInstance().getDu().log("setVirtualCrateKeys() - CALL (" + playerManager.getPlayer().getName() + ", " + crate.getName() + ", " + keys + ")", getClass());
        VirtualCrateData vCD = getVCCrateData(crate);
        playerManager.getInstance().getDu().log("setVirtualCrateKeys() - Before: " + vCD.toString(), getClass());
        String temp = removeStringFromList(vCD.toString(), getVirtualCrates());
        vCD.setKeys(keys);
        playerManager.getInstance().getDu().log("setVirtualCrateKeys() - After: " + vCD.toString(), getClass());
        playerManager.getInstance().getDu().log("setVirtualCrateKeys() - Result: " + addStringToList(vCD.toString(), temp), getClass());
        setVirtualCrates(addStringToList(vCD.toString(), temp));
        getDataHandler().write("virtual-crates", getVirtualCrates());
    }

    public VirtualCrateData getVCCrateData(Crate crate) {
        playerManager.getInstance().getDu().log("getVCCrateData() - CALL (" + playerManager.getPlayer().getName() + ", " + crate.getName() + ")", getClass());
        if (getVirtualCrateData().containsKey(crate)) {
            playerManager.getInstance().getDu().log("getVCCrateData() - already contains", getClass());
            return getVirtualCrateData().get(crate);
        } else {
            playerManager.getInstance().getDu().log("getVCCrateData() - doesn't contain", getClass());
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
                getDataHandler().write("crate-cooldowns", crateCooldownsToSet);
                break;
            }
        }
    }

    public CrateCooldownEvent getCrateCooldownEventByCrates(Crate crates) {
        for (CrateCooldownEvent cce : getCrateCooldownEvents()) {
            if (cce.getCrate().getName().equalsIgnoreCase(crates.getName())) {
                return cce;
            }
        }
        return null;
    }

    public boolean isSQL() {
        return getDataHandler() instanceof SQLDataHandler;
    }

    public void setCrateCooldowns(String crateCooldowns, boolean write) {
        this.crateCooldowns = crateCooldowns;
        if (write)
            getDataHandler().write("crate-cooldowns", crateCooldowns);
    }

    public void setHistory(String history, boolean write) {
        this.history = history;
        if (write)
            getDataHandler().write("history", getHistory());
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

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public void setDataHandler(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    public List<HistoryEvent> getHistoryEvents() {
        return historyEvents;
    }

    public List<CrateCooldownEvent> getCrateCooldownEvents() {
        return crateCooldownEvents;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public boolean isActivatedLuckyChests() {
        return activatedLuckyChests;
    }

    public void setActivatedLuckyChests(boolean activatedLuckyChests) {
        this.activatedLuckyChests = activatedLuckyChests;
    }

    public String getVirtualCrates() {
        playerManager.getInstance().getDu().log("getVirtualCrates() - CALL (" + playerManager.getPlayer().getName() + ")", getClass());
        return virtualCrates;
    }

    public void setVirtualCrates(String virtualCrates) {
        playerManager.getInstance().getDu().log("setVirtualCrates() - CALL (" + playerManager.getPlayer().getName() + ")", getClass());
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
}
