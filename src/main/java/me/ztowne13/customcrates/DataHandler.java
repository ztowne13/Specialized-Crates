package me.ztowne13.customcrates;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.utils.FileHandler;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DataHandler
{
    CustomCrates cc;
    FileHandler dataFile;

    HashMap<UUID,ArrayList<QueuedGiveCommand>> quedGiveCommands = new HashMap<>();

    public DataHandler(CustomCrates cc, FileHandler dataFile)
    {
        this.cc = cc;
        this.dataFile = dataFile;
    }

    public void loadFromFile()
    {
        FileConfiguration fileConfiguration = dataFile.getData();

        // Queued give commands
        for(Object queuedCmd : fileConfiguration.getList("givecommands"))
        {
            QueuedGiveCommand queuedGiveCommand = new QueuedGiveCommand(queuedCmd.toString());
            quedGiveCommands.put(queuedGiveCommand.getUuid(), queuedGiveCommand);
        }
    }

    public void saveToFile()
    {
        FileConfiguration fileConfiguration = dataFile.getData();

        // Queued give commands
        ArrayList<String> parsedGiveCommands = new ArrayList<>();
        for(QueuedGiveCommand queuedGiveCommand : quedGiveCommands.values())
        {
            for(queuedG)
            parsedGiveCommands.add(queuedGiveCommand.toString());
        }
        fileConfiguration.set("givecommands", parsedGiveCommands);

        dataFile.save();
    }

    public ArrayList<QueuedGiveCommand> getQueuedGiveCommand()
    {
        return quedGiveCommands.get(quedGiveCommands);
    }

    public void addQueuedGiveCommand(QueuedGiveCommand queuedGiveCommand)
    {
        quedGiveCommands.put(queuedGiveCommand.getUuid(), queuedGiveCommand);
    }

    public void removeQueuedGiveCommand(QueuedGiveCommand queuedGiveCommand)
    {
        quedGiveCommands.get(queuedGiveCommand.getUuid()).re
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

        @Override
        public String toString()
        {
            return uuid + ";" + key + ";" + virtual + ";" + amount + ";" + crate.getName() + ";";
        }
    }
}
