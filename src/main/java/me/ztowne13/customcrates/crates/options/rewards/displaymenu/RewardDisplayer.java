package me.ztowne13.customcrates.crates.options.rewards.displaymenu;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom.CustomRewardDisplayer;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract class RewardDisplayer {
    private final Crate crate;
    private final FileHandler fileHandler;
    private String name = null;
    private boolean requirePermForPreview = false;

    public RewardDisplayer(Crate crate) {
        this.crate = crate;
        this.fileHandler = crate.getSettings().getFileHandler();
    }

    public abstract void open(Player player);

    public abstract InventoryBuilder createInventory(Player player);

    public abstract void load();

    public void openFor(Player player) {
        if (isRequirePermForPreview() && !player.hasPermission(getCrate().getSettings().getPermission())) {
            Messages.NO_PERMISSION_CRATE.msgSpecified(crate.getInstance(), player);
            return;
        }

        if (!(this instanceof CustomRewardDisplayer) && crate.getSettings().getReward().getCrateRewards().length > 54
                && (player.hasPermission("customcrates.admin") || player.hasPermission("specializedcrates.admin"))) {
            ChatUtils.msgHey(player,
                    "Just a heads up: you have more than 54 rewards in this crate, but only the &lCUSTOM &ereward display" +
                            " type supports multiple pages, so not all rewards are shown &o(note: only admins can see this message).");
        }

        open(player);
    }

    public String getInvName() {
        if (name == null)
            return ChatUtils.toChatColor(
                    getCrate().getInstance().getSettings().getConfigValues().get("inv-reward-display-name").toString()
                            .replace("%crate%", getCrate().getDisplayName()));
        else
            return ChatUtils.toChatColor(name);
    }

    public void loadDefaults() {
        FileConfiguration fc = fileHandler.get();

        if (fc.contains("reward-display.name")) {
            this.name = fc.getString("reward-display.name");
        }

        if (fc.contains("reward-display.require-permission")) {
            try {
                this.requirePermForPreview = fc.getBoolean("reward-display.require-permission");
            } catch (Exception exc) {
                // IGNORED
            }
        }
    }

    public void saveToFile() {
        getFileHandler().get().set("reward-display.type", getCrate().getSettings().getRewardDisplayType().name());
        getFileHandler().get().set("reward-display.name", name);
        getFileHandler().get().set("reward-display.require-permission", requirePermForPreview);
    }

    public Crate getCrate() {
        return crate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }

    public boolean isRequirePermForPreview() {
        return requirePermForPreview;
    }

    public void setRequirePermForPreview(boolean requirePermForPreview) {
        this.requirePermForPreview = requirePermForPreview;
    }
}
