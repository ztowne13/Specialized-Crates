package me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.items.SaveableItemBuilder;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class DisplayPage {
    public static final String PREFIX = "reward-display.custom-display.pages";

    private final CustomRewardDisplayer customRewardDisplayer;
    private final int pageNum;
    private int slots;

    private String[][] unformattedInv;
    private Reward[][] rewards;
    private SaveableItemBuilder[][] builders;

    public DisplayPage(CustomRewardDisplayer customRewardDisplayer, int pageNum) {
        this.customRewardDisplayer = customRewardDisplayer;
        this.pageNum = pageNum;
    }

    public void save() {

        if (builders == null)
            buildFormat();

        FileHandler fileHandler = customRewardDisplayer.getCrate().getSettings().getFileHandler();
        FileConfiguration fc = fileHandler.get();

        ArrayList<String> format = new ArrayList<>();

        for (int x = 0; x < 6; x++) {
            StringBuilder lineFormat = new StringBuilder();

            for (int y = 0; y < 9; y++) {
                if (builders[x][y] == null) {
                    Reward reward = rewards[x][y];

                    if (reward == null)
                        lineFormat.append(",");
                    else
                        lineFormat.append(reward.getRewardName()).append(",");

                    continue;
                }

                SaveableItemBuilder stack = builders[x][y];

                boolean found = false;
                for (String symbol : customRewardDisplayer.getItems().keySet()) {
                    SaveableItemBuilder checkBuilder = customRewardDisplayer.getItems().get(symbol);
                    if (checkBuilder.equals(stack)) {
                        lineFormat.append(symbol).append(",");
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    String symbol = customRewardDisplayer.getNextSymbol();
                    customRewardDisplayer.getItems().put(symbol, stack);
                    lineFormat.append(symbol).append(",");
                }
            }
            format.add(lineFormat.substring(0, lineFormat.length() - 1));
        }

        // Trip uneccessary lines off the end.

        for (int i = 5; i >= 0; i--) {
            if (format.get(i).equals(",,,,,,,,"))
                format.remove(i);
            else
                break;
        }

        fc.set(PREFIX + "." + pageNum, format);
    }

    public boolean load() {
        FileHandler fileHandler = customRewardDisplayer.getCrate().getSettings().getFileHandler();
        FileConfiguration fc = fileHandler.get();

        if (!fc.contains(PREFIX + "." + pageNum)) {
            unformattedInv = new String[6][9];
            return false;
        }

        List<String> values = fc.getStringList(PREFIX + "." + pageNum);

        slots = values.size() * 9;

        unformattedInv = new String[values.size()][9];

        for (int i = 0; i < values.size(); i++) {
            String line = values.get(i).replace(", ", ",");
            String[] args = line.split(",");

            for (int j = 0; j < 9; j++) {
                if (args.length <= j)
                    unformattedInv[i][j] = null;
                else
                    unformattedInv[i][j] = args[j];
            }
        }

        return true;
    }

    public InventoryBuilder buildInventoryBuilder(Player player) {
        return buildInventoryBuilder(player, false);
    }

    public InventoryBuilder buildInventoryBuilder(Player player, boolean forceMaxSlots) {
        return buildInventoryBuilder(player, forceMaxSlots, "", null, true);
    }

    public InventoryBuilder buildInventoryBuilder(Player player, boolean forceMaxSlots, String invNameOverride, InventoryBuilder builder, boolean open) {
        InventoryBuilder ib;

        if (builder == null)
            ib = new InventoryBuilder(player, forceMaxSlots ? 54 : slots,
                    invNameOverride.equals("") ? customRewardDisplayer.getInvName() : invNameOverride);
        else
            ib = builder;

        if (rewards == null || builders == null) {
            buildFormat();
            return buildInventoryBuilder(player, forceMaxSlots, invNameOverride, builder, open);
        }

        for (int x = 0; x < (forceMaxSlots ? 6 : unformattedInv.length); x++) {
            for (int y = 0; y < (forceMaxSlots ? 9 : unformattedInv[0].length); y++) {
                int slot = (x * 9) + y;

                if (builders[x][y] == null) {
                    if (rewards[x][y] != null)
                        ib.setItem(slot, rewards[x][y].getDisplayBuilder());
                } else {
                    ib.setItem(slot, builders[x][y]);
                }
            }
        }

        if (builder == null && open) {
            final PlayerManager pm = PlayerManager.get(customRewardDisplayer.getCrate().getInstance(), player);
            pm.setNextPageInventoryCloseGrace(pm.getInstance().getTotalTicks() + 2L);

            final Inventory inv = ib.getInv();
            final DisplayPage thisPage = this;
            Bukkit.getScheduler().runTaskLater(pm.getInstance(), () -> {
                player.openInventory(inv);
                pm.setLastPage(thisPage);
                pm.setInRewardMenu(true);
            }, 2);
        }

        return ib;
    }

    public void buildFormat() {
        rewards = new Reward[6][9];
        builders = new SaveableItemBuilder[6][9];

        for (int x = 0; x < unformattedInv.length; x++) {
            for (int y = 0; y < unformattedInv[0].length; y++) {
                if (unformattedInv[x][y] != null) {
                    String symbol = unformattedInv[x][y];

                    if (customRewardDisplayer.getItems().containsKey(symbol)) {
                        builders[x][y] = customRewardDisplayer.getItems().get(symbol);
                    } else {
                        Reward reward = customRewardDisplayer.getCrate().getSettings().getReward().getByName(symbol);
                        if (reward != null)
                            rewards[x][y] = reward;
                    }
                }
            }
        }
    }

    public void handleInput(Player player, int slot) {
        SpecializedCrates sc = customRewardDisplayer.getCrate().getInstance();
        int x = slot / 9;
        sc.getDebugUtils().log("handleInput() - x: " + x, getClass());
        int y = slot % 9;
        sc.getDebugUtils().log("handleInput() - y: " + y, getClass());
        String symbolAt = unformattedInv[x][y];
        sc.getDebugUtils().log("handleInput() - symbolAt: " + symbolAt, getClass());
        sc.getDebugUtils().log("handleInput() - nextPageItem: " + customRewardDisplayer.getNextPageItem(), getClass());
        sc.getDebugUtils().log("handleInput() - backpageitem: " + customRewardDisplayer.getPrevPageItem(), getClass());

        // There's no item in the display at the spot.
        if (symbolAt == null)
            return;

        if (symbolAt.equalsIgnoreCase(customRewardDisplayer.getNextPageItem())) {
            if (customRewardDisplayer.getPages().containsKey(pageNum + 1))
                customRewardDisplayer.getPages().get(pageNum + 1).buildInventoryBuilder(player);
            else
                ChatUtils.msgError(player, "Page " + (pageNum + 1) + " does not exist. Please contact an administrator" +
                        "to create the next page of the reward-preview menu OR remove this 'next page arrow' because there" +
                        "is no next page.");
        } else if (symbolAt.equalsIgnoreCase(customRewardDisplayer.getPrevPageItem())) {
            if (customRewardDisplayer.getPages().containsKey(pageNum - 1))
                customRewardDisplayer.getPages().get(pageNum - 1).buildInventoryBuilder(player);
            else
                ChatUtils.msgError(player, "Page " + (pageNum - 1) + " does not exist. Please contact an administrator" +
                        "to create the previous page of the reward-preview menu OR remove this 'previous page arrow' because there" +
                        "is no previous page.");
        }
    }

    public List<Reward> rewardsAsList() {
        ArrayList<Reward> rewardsList = new ArrayList<>();

        if (rewards == null)
            buildFormat();

        for (Reward[] rewardList : rewards) {
            for (int i = 0; i < 9; i++) {
                if (rewardList[i] != null)
                    rewardsList.add(rewardList[i]);
            }
        }

        return rewardsList;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public Reward[][] getRewards() {
        return rewards;
    }

    public void setRewards(Reward[][] rewards) {
        this.rewards = rewards;
    }

    public String[][] getUnformattedInv() {
        return unformattedInv;
    }

    public void setUnformattedInv(String[][] unformattedInv) {
        this.unformattedInv = unformattedInv;
    }

    public ItemBuilder[][] getBuilders() {
        return builders;
    }

    public void setBuilders(SaveableItemBuilder[][] builders) {
        this.builders = builders;
    }


}
