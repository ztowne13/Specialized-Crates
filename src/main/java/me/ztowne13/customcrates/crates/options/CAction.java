package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.actions.ActionEffect;
import me.ztowne13.customcrates.crates.options.actions.BukkitActionEffect;
import me.ztowne13.customcrates.crates.options.actions.NMSActionEffect;
import me.ztowne13.customcrates.crates.options.holograms.DynamicHologram;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.crates.types.animations.block.OpenChestAnimation;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CAction extends CSetting {
    private Map<String, Map<String, List<String>>> actions = new HashMap<>();

    public CAction(Crate crate) {
        super(crate, crate.getInstance());
    }

    @Override
    public void loadFor(CrateSettingsBuilder crateSettingsBuilder, CrateState crateState) {
        if (crateSettingsBuilder.hasValue("open.actions")) {
            List<String> list = getCrate().getSettings().getFileConfiguration().getStringList("open.actions");
            for (String s : list) {
                addEntryByString("DEFAULT", s);
            }
        }

        if (crateSettingsBuilder.hasValue("open.crate-tiers")) {
            for (String tier : getCrate().getSettings().getFileConfiguration().getConfigurationSection("open.crate-tiers").getKeys(false)) {
                if (crateSettingsBuilder.hasValue("open.crate-tiers." + tier + ".actions")) {
                    List<String> list =
                            getCrate().getSettings().getFileConfiguration().getStringList("open.crate-tiers." + tier + ".actions");
                    for (String s : list) {
                        addEntryByString(tier, s);
                    }
                }
            }
        }
    }

    public void saveToFile() {
        if (!actions.isEmpty()) {
            for (Map.Entry<String, Map<String, List<String>>> entry : actions.entrySet()) {
                ArrayList<String> toSetList = new ArrayList<>();
                String tier = entry.getKey();
                String path = "open." + (tier.equalsIgnoreCase("DEFAULT") ? "" : "crate-tiers." + tier + ".") + "actions";

                for (Map.Entry<String, List<String>> entry1 : entry.getValue().entrySet()) {
                    String actionType = entry1.getKey();
                    for (String action : entry1.getValue()) {
                        toSetList.add(ChatUtils.fromChatColor(actionType + ", " + action));
                    }
                }

                getFileHandler().get().set(path, toSetList);
            }
        }
    }

    public void addEntry(String type, String action, String tier) {
        Map<String, List<String>> map = getActions().getOrDefault(tier, new HashMap<>());

        List<String> list = map.getOrDefault(type, new ArrayList<>());
        list.add(action);
        map.put(type, list);

        StatusLoggerEvent.ACTION_ADD.log(getCrate(), new String[]{action, tier});
        getActions().put(tier, map);
    }

    public void removeEntry(String type, String action, String tier) {
        getActions().get(tier).get(type).remove(action);
    }

    public void addEntryByString(String crateTier, String toAdd) {
        String[] split = toAdd.split(",");
        String type = split[0].replace(" ", "").replace(",", "");

        if (split.length == 1) {
            addEntry(type, "", crateTier);
            return;
        }

        String action = "";

        boolean b = false;
        for (String words : split) {
            if (b)
                action = words + " ";
            b = true;
        }

        if (action.startsWith(" "))
            action = action.substring(1);

        action = action.substring(0, action.length() - 1);
        action = ChatUtils.toChatColor(action);

        addEntry(type, action, crateTier);
    }

    public void playAll(Player player, boolean pre) {
        playAll(player, new ArrayList<>(), pre);
    }

    public void playAll(Player player, List<Reward> rewards, boolean pre) {
        playAll(player, null, rewards, pre);
    }

    public void playAll(Player player, PlacedCrate placedCrate, List<Reward> rewards, boolean pre) {
        instance.getDu().log("playAll() - CALL (pre: " + pre + ")", getClass());

        if (rewards.isEmpty() && !pre)
            return;

        ActionEffect actionEffect =
                VersionUtils.Version.v1_12.isServerVersionOrLater() ? new BukkitActionEffect(instance) : new NMSActionEffect(instance);
        actionEffect.newTitle();
        boolean toRunTitle = false;

        ArrayList<String> rewardsAsDisplayname = new ArrayList<>();
        for (Reward r : rewards) {
            rewardsAsDisplayname.add(r.getDisplayName(true));
        }

        for (String tier : getActions().keySet()) {
            if (pre || (tier.equalsIgnoreCase("DEFAULT") &&
                    !getActions().containsKey(rewards.get(0).getRarity().toLowerCase())) ||
                    rewards.get(0).getRarity().equalsIgnoreCase(tier)) {
                for (String actionVal : getActions().get(tier).keySet()) {
                    String s = actionVal.toUpperCase();
                    for (String msg : getActions().get(tier).get(s)) {
                        if (pre) {
                            if (actionVal.startsWith("PRE_")) {
                                s = actionVal.substring(4);
                            } else {
                                continue;
                            }
                        } else {
                            if (actionVal.startsWith("PRE_")) {
                                continue;
                            }
                        }

                        String rewardsAsString = rewardsAsDisplayname.toString();
                        rewardsAsString = rewardsAsString.substring(1, rewardsAsString.length() - 1);

                        msg = msg.replace("%player%", player.getName()).replace("%name%", player.getName());
                        msg = msg.replace("%displayname%", player.getDisplayName()).replace("%nickname%", player.getDisplayName())
                                .replace("%nick%", player.getDisplayName());
                        msg = msg.replace("%crate%", getCrate().getName()).replace("%cratename%", getCrate().getDisplayName());
                        msg = msg.replace("%reward%", rewardsAsString).replace("%rewards%", rewardsAsString);
                        msg = ChatUtils.toChatColor(msg);

                        if (s.equalsIgnoreCase("MESSAGE")) {
                            player.sendMessage(msg);
                        } else if (s.equalsIgnoreCase("BROADCAST")) {
                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                onlinePlayer.sendMessage(msg);
                            }
                        } else if (s.equalsIgnoreCase("ACTIONBAR")) {
                            actionEffect.getActionBarExecutor().play(player, msg);
                        } else if (s.equalsIgnoreCase("TITLE")) {
                            actionEffect.setDisplayTitle(msg);
                            toRunTitle = true;
                        } else if (s.equalsIgnoreCase("SUBTITLE")) {
                            actionEffect.setDisplaySubtitle(msg);
                            toRunTitle = true;
                        } else if (s.equalsIgnoreCase("COMMAND")) {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), msg);
                        } else {
                            ChatUtils.log(new String[]{
                                    "Please note that an action type called " + s + " was attempted to be run",
                                    "    However, this action type does not exist. Valid types are:",
                                    "      MESSAGE, BROADCAST, ACTIONBAR, TITLE, SUBTITLE, and COMMAND"});
                        }
                    }
                }
            }
        }

        if (toRunTitle) {
            actionEffect.playTitle(player);
        }

        if (!pre) {
            if (!crate.getSettings().getCrateType().equals(CrateAnimationType.BLOCK_CRATEOPEN) ||
                    !((OpenChestAnimation) crate.getSettings().getCrateAnimation()).isEarlyRewardHologram()) {
                if (crate.getSettings().getObtainType().isStatic() ||
                        crate.getSettings().getCrateType().isSpecialDynamicHandling())
                    playRewardHologram(player, rewardsAsDisplayname);
            }
        }
    }

    public void playRewardHologram(Player player, List<String> rewards, double additionalYOffset) {
        playRewardHologram(player, rewards, additionalYOffset, false, null, -1);
    }

    public void playRewardHologram(Player player, List<String> rewards) {
        playRewardHologram(player, rewards, 0, false, null, -1);
    }

    public void playRewardHologram(Player player, List<String> rewards, double additionalYOffset, boolean attach, Item item,
                                   int openDuration) {
        if (rewards.isEmpty())
            return;

        final PlayerManager playerManager = PlayerManager.get(instance, player);
        final PlacedCrate placedCrate = playerManager.getLastOpenedPlacedCrate();
        if (placedCrate == null) {
            return;
        }

        String msg = placedCrate.getCrate().getSettings().getHologram().getRewardHologram();
        if (msg.isEmpty()) {
            return;
        }

        String rewardsAsString = rewards.toString().substring(1, rewards.toString().length() - 1);
        msg = msg.replace("%player%", player.getName()).replace("%name%", player.getName());
        msg = msg.replace("%displayname%", player.getDisplayName()).replace("%nickname%", player.getDisplayName())
                .replace("%nick%", player.getDisplayName());
        msg = msg.replace("%crate%", getCrate().getName()).replace("%cratename%", getCrate().getDisplayName());
        msg = msg.replace("%reward%", rewardsAsString).replace("%rewards%", rewardsAsString);

        msg = ChatUtils.toChatColor(msg);

        final DynamicHologram dynamicHologram = placedCrate.getHologram().getDynamicHologram();
        dynamicHologram.setDisplayingRewardHologram(true);
        dynamicHologram.delete();

        Location rewardLoc = placedCrate.getLocation().clone();
        rewardLoc.setY(rewardLoc.getY() - .3 + getCrate().getSettings().getHologram().getRewardHoloYOffset() +
                additionalYOffset);
        dynamicHologram.create(rewardLoc);
        dynamicHologram.addLine(msg);

        if (attach) {
            attachTo(item, msg);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
            dynamicHologram.delete();

            final Location cloneY = placedCrate.getLocation().clone();
            cloneY.setY(cloneY.getY() + .5);

            if (placedCrate.getCrate().getSettings().getObtainType().equals(ObtainType.STATIC)) {
                placedCrate.getCrate().getSettings().getHologram().createHologram(cloneY, dynamicHologram);
            }

            playerManager.setLastOpenedPlacedCrate(null);
            dynamicHologram.setDisplayingRewardHologram(false);

            if (dynamicHologram.getHoloAnimation() != null) {
                dynamicHologram.getHoloAnimation().update(true);
            }

        }, attach ? openDuration : getCrate().getSettings().getHologram().getRewardHoloDuration());
    }

    public void attachTo(Item item, String rewardName) {
        Entity real = null;

        for (Entity entity : item.getLocation().getChunk().getEntities()) {
            if (item.getLocation().distance(entity.getLocation()) < 2 &&
                    ChatUtils.removeColor(rewardName).equalsIgnoreCase(ChatUtils.removeColor(entity.getName())) &&
                    !entity.equals(item)) {
                real = entity;
                break;
            }
        }

        try {
            if (real != null) {
                if (VersionUtils.Version.v1_13.isServerVersionOrLater())
                    item.addPassenger(real);
                else
                    item.setPassenger(real);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

    public Map<String, Map<String, List<String>>> getActions() {
        return actions;
    }

    public void setActions(Map<String, Map<String, List<String>>> actions) {
        this.actions = actions;
    }
}
