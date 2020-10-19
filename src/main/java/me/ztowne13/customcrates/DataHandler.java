package me.ztowne13.customcrates;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class DataHandler {
    private static final String PLAYER_CMDS_PATH = "queued-player-commands";
    private static final int LOGIN_WAIT = 20;

    private final SpecializedCrates instance;
    private final FileHandler dataFile;
    private final Map<UUID, List<QueuedGiveCommand>> queuedGiveCommands = new HashMap<>();

    public DataHandler(SpecializedCrates instance, FileHandler dataFile) {
        this.instance = instance;
        this.dataFile = dataFile;
    }

    public void loadFromFile() {
        FileConfiguration fileConfig = dataFile.get();

        // Queued give commands
        ConfigurationSection configSection = fileConfig.getConfigurationSection(PLAYER_CMDS_PATH);
        if (configSection != null) {

            for (Object queuedCmd : configSection.getKeys(false)) {
                String uuidStr = queuedCmd.toString();
                UUID uuid = UUID.fromString(uuidStr);

                ArrayList<QueuedGiveCommand> cmds = new ArrayList<>();

                for (String cmd : configSection.getStringList(uuidStr)) {
                    QueuedGiveCommand queuedGiveCommand = new QueuedGiveCommand(cmd);
                    if (queuedGiveCommand.isStillExists()) {
                        cmds.add(queuedGiveCommand);
                    }
                }

                queuedGiveCommands.put(uuid, cmds);
            }
        }
    }

    public void saveToFile() {
        FileConfiguration fileConfig = dataFile.get();

        // Queued give commands
        for (Map.Entry<UUID, List<QueuedGiveCommand>> entry : queuedGiveCommands.entrySet()) {
            ArrayList<String> giveCmdsStr = new ArrayList<>();
            for (QueuedGiveCommand cmd : entry.getValue()) {
                giveCmdsStr.add(cmd.toString());
            }

            fileConfig.set(PLAYER_CMDS_PATH + "." + entry.getKey().toString(), giveCmdsStr.toArray());
        }

        dataFile.save();
    }

    public void addQueuedGiveCommand(QueuedGiveCommand queuedGiveCommand) {
        UUID uuid = queuedGiveCommand.getUuid();

        List<QueuedGiveCommand> thisCmds;
        thisCmds = queuedGiveCommands.getOrDefault(uuid, new ArrayList<>());
        thisCmds.add(queuedGiveCommand);

        queuedGiveCommands.put(uuid, thisCmds);
    }

    public void playAllQueuedGiveCommands(final UUID uuid) {
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            if (queuedGiveCommands.containsKey(uuid)) {
                for (QueuedGiveCommand cmd : queuedGiveCommands.get(uuid))
                    cmd.run();

                queuedGiveCommands.remove(uuid);
                dataFile.get().set(PLAYER_CMDS_PATH + "." + uuid.toString(), null);
            }
        }, LOGIN_WAIT);
    }

    public Map<UUID, List<QueuedGiveCommand>> getQueuedGiveCommands() {
        return queuedGiveCommands;
    }

    public class QueuedGiveCommand {
        UUID uuid;
        boolean key;
        boolean virtual;
        int amount;
        Crate crate;
        boolean stillExists = true;

        public QueuedGiveCommand(String cmd) {
            String[] args = cmd.split(";");
            this.uuid = UUID.fromString(args[0]);
            this.key = Boolean.parseBoolean(args[1]);
            this.virtual = Boolean.parseBoolean(args[2]);
            this.amount = Integer.parseInt(args[3]);
            try {
                if (Crate.existsNotCaseSensitive(args[4])) {
                    this.crate = Crate.getCrate(instance, args[4], false);
                } else {
                    stillExists = false;
                }
            } catch (Exception exc) {
                stillExists = false;
            }
        }

        public QueuedGiveCommand(UUID uuid, boolean key, boolean virtual, int amount, Crate crate) {
            this.uuid = uuid;
            this.key = key;
            this.virtual = virtual;
            this.amount = amount;
            this.crate = crate;
        }

        public UUID getUuid() {
            return uuid;
        }

        public void run() {
            try {
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


                if (key) {
                    ChatUtils.log("[SpecializedCrate] Executing givecrate command for player that was offline.");
                    instance.getCommandCrate().getSubCommands().get(4).run(instance, instance.getCommandCrate(), args);
                } else {
                    ChatUtils.log("[SpecializedCrate] Executing givekey command for player that was offline.");
                    instance.getCommandCrate().getSubCommands().get(3).run(instance, instance.getCommandCrate(), args);
                }
            } catch (Exception exc) {
                ChatUtils
                        .log("&7Failed to run a qued givekey or givecrate command. The crate to give the player probably" +
                                " no longer exists. The qued command will be removed.");
                exc.printStackTrace();
            }
        }

        public boolean isStillExists() {
            return stillExists;
        }

        public Crate getCrate() {
            return crate;
        }

        @Override
        public String toString() {
            return uuid + ";" + key + ";" + virtual + ";" + amount + ";" + crate.getName() + ";";
        }
    }
}
