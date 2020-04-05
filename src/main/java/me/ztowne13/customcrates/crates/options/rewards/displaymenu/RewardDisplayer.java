package me.ztowne13.customcrates.crates.options.rewards.displaymenu;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.FileHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract class RewardDisplayer
{
    Crate crates;
    String name = null;
    boolean multiplePages = false;

    FileHandler fileHandler;

    public RewardDisplayer(Crate crates)
    {
        this.crates = crates;
        this.fileHandler = crates.getSettings().getFileHandler();
    }

    public abstract void openFor(Player p);

    public abstract InventoryBuilder createInventory(Player p);

    public abstract void load();

    public String getInvName()
    {
        if (name == null)
            return ChatUtils.toChatColor(
                    getCrates().getCc().getSettings().getConfigValues().get("inv-reward-display-name").toString()
                            .replace("%crate%", getCrates().getName()));
        else
            return ChatUtils.toChatColor(name);
    }

    public void loadDefaults()
    {
        FileConfiguration fc = fileHandler.get();

        if(fc.contains("reward-display.name"))
        {
            this.name = fc.getString("reward-display.name");
        }
    }

    public Crate getCrates()
    {
        return crates;
    }

    public void setCrates(Crate crates)
    {
        this.crates = crates;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public FileHandler getFileHandler()
    {
        return fileHandler;
    }

    public void setFileHandler(FileHandler fileHandler)
    {
        this.fileHandler = fileHandler;
    }
}
