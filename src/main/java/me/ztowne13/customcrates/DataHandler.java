package me.ztowne13.customcrates;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.FileHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DataHandler
{
    String playerCmdsPath = "queued-player-commands";

    int LOGIN_WAIT = 20;

    SpecializedCrates cc;
    FileHandler dataFile;

    HashMap<UUID, ArrayList<QueuedGiveCommand>> quedGiveCommands = new HashMap<>();

    public DataHandler(SpecializedCrates cc, FileHandler dataFile)
    {
        this.cc = cc;
        this.dataFile = dataFile;
    }

    public void loadFromFile()
    {
        FileConfiguration fileConfig = dataFile.get();

        // Queued give commands
        ConfigurationSection configSection = fileConfig.getConfigurationSection(playerCmdsPath);
        if(configSection != null)
        {

            for (Object queuedCmd : configSection.getKeys(false))
            {
                String uuidStr = queuedCmd.toString();
                UUID uuid = UUID.fromString(uuidStr);

                ArrayList<QueuedGiveCommand> cmds = new ArrayList<>();

                for (String cmd : configSection.getStringList(uuidStr))
                {
                    QueuedGiveCommand queuedGiveCommand = new QueuedGiveCommand(cmd);
                    cmds.add(queuedGiveCommand);
                }

                quedGiveCommands.put(uuid, cmds);
            }
        }
    }

    public void saveToFile()
    {
        FileConfiguration fileConfig = dataFile.get();

        // Queued give commands
        for (UUID uuid : quedGiveCommands.keySet())
        {
            ArrayList<String> giveCmdsStr = new ArrayList<>();
            ArrayList<QueuedGiveCommand> giveCmds = quedGiveCommands.get(uuid);
            for (QueuedGiveCommand cmd : giveCmds)
            {
                giveCmdsStr.add(cmd.toString());
            }

            fileConfig.set(playerCmdsPath + "." + uuid.toString(), giveCmdsStr.toArray());
        }

        dataFile.save();
    }

    public void addQueuedGiveCommand(QueuedGiveCommand queuedGiveCommand)
    {
        UUID uuid = queuedGiveCommand.getUuid();

        ArrayList<QueuedGiveCommand> thisCmds;
        thisCmds = quedGiveCommands.containsKey(uuid) ? quedGiveCommands.get(uuid) : new ArrayList<QueuedGiveCommand>();
        thisCmds.add(queuedGiveCommand);

        quedGiveCommands.put(uuid, thisCmds);
    }

    public void playAllQueuedGiveCommands(final UUID uuid)
    {
        Bukkit.getScheduler().runTaskLater(cc, new Runnable()
        {
            @Override
            public void run()
            {
                if (quedGiveCommands.containsKey(uuid))
                {
                    for (QueuedGiveCommand cmd : quedGiveCommands.get(uuid))
                        cmd.run();

                    quedGiveCommands.remove(uuid);
                    dataFile.get().set(playerCmdsPath + "." + uuid.toString(), null);
                }
            }
        }, LOGIN_WAIT);
    }

    public class QueuedGiveCommand
    {
        UUID uuid;
        boolean key;
        boolean virtual;
        int amount;
        Crate crate;

        public QueuedGiveCommand(String cmd)
        {
            String[] args = cmd.split(";");
            this.uuid = UUID.fromString(args[0]);
            this.key = Boolean.valueOf(args[1]);
            this.virtual = Boolean.valueOf(args[2]);
            this.amount = Integer.parseInt(args[3]);
            this.crate = Crate.getCrate(cc, args[4]);
        }

        public QueuedGiveCommand(UUID uuid, boolean key, boolean virtual, int amount, Crate crate)
        {
            this.uuid = uuid;
            this.key = key;
            this.virtual = virtual;
            this.amount = amount;
            this.crate = crate;
        }

        public UUID getUuid()
        {
            return uuid;
        }

        public void run()
        {
            try
            {
                String give = key ? "givekey" : "givecrate";
                String crateName = crate.getName();
                String uuidToStr = uuid.toString();
                String amntStr = amount + "";
                // virtual

                String[] args = new String[virtual ? 5 : 4];

                args[0] = give;
                args[1] = crateName;
                args[2] = uuidToStr;
                args[3] = amntStr;
                if (virtual)
                    args[4] = "-v";

                // Crate = 3, Key = 4


                if (key)
                {
                    ChatUtils.log("[SpecializedCrate] Executing givecrate command for player that was offline.");
                    cc.getCommandCrate().getSubCommands().get(4).run(cc, cc.getCommandCrate(), args);
                }
                else
                {
                    ChatUtils.log("[SpecializedCrate] Executing givekey command for player that was offline.");
                    cc.getCommandCrate().getSubCommands().get(3).run(cc, cc.getCommandCrate(), args);
                }
            }
            catch (Exception exc)
            {
                ChatUtils
                        .log("&7Failed to run a qued givekey or givecrate command. The crate to give the player probably" +
                                " no longer exists. The qued command will be removed.");
                exc.printStackTrace();
            }
        }

        @Override
        public String toString()
        {
            return uuid + ";" + key + ";" + virtual + ";" + amount + ";" + crate.getName() + ";";
        }
    }
}
