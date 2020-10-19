package me.ztowne13.customcrates.interfaces.files;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.DebugUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class FileHandler {

    private static Map<String, FileHandler> map = new HashMap<>();
    private final SpecializedCrates instance;
    private final FileDataLoader fileDataLoader;
    private String name;
    private String directory = "";
    private String loaded;
    private boolean canBeEdited;
    private boolean saveWithCustomSave;
    private boolean properLoad = false;
    private boolean newFile;
    private FileConfiguration data = null;
    private File dataFile = null;

    public FileHandler(SpecializedCrates instance, String name, String directory, boolean canBeEdited, boolean saveWithCustomSave,
                       boolean newFile) {
        this.name = name;
        this.instance = instance;
        this.directory = directory;
        this.canBeEdited = canBeEdited;
        this.saveWithCustomSave = saveWithCustomSave;
        this.newFile = newFile;

        this.fileDataLoader = new FileDataLoader(this);

        map.put(name, this);
    }

    public FileHandler(SpecializedCrates instance, String name, boolean canBeEdited, boolean saveWithCustomSave) {
        this.name = name;
        this.canBeEdited = canBeEdited;
        this.instance = instance;
        this.saveWithCustomSave = saveWithCustomSave;

        this.fileDataLoader = new FileDataLoader(this);

        map.put(name, this);
    }

    public static void clearLoaded() {
        map.clear();
        map = new HashMap<>();
    }

    public static Map<String, FileHandler> getMap() {
        return map;
    }

    public static void setMap(Map<String, FileHandler> map) {
        FileHandler.map = map;
    }

    public void loadFile() {
        instance.getDebugUtils().log("loadFile() - CALL", getClass(), false);

        if (getDataFile() == null) {
            setDataFile(new File(new File(instance.getDataFolder().getPath() + getDirectory()), getName()));

            if (getName().equalsIgnoreCase("Messages.yml") && !folderExists("Crates")) {
                new File(instance.getDataFolder().getPath() + getDirectory()).mkdir();

                try {
                    instance.firstLoadFiles();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        }
    }

    public void reload() {
        instance.getDebugUtils().log("reload() - CALL", getClass(), false);

        loadFile();

        try {
            data = YamlConfiguration.loadConfiguration(getDataFile());

            if (canBeEdited) {
                data.saveToString();
                if (name.equalsIgnoreCase("Rewards.YML") || !data.saveToString().equalsIgnoreCase("")) {
                    File defConfigFile = new File(instance.getDataFolder(), getName());
                    if (defConfigFile.exists()) {
                        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigFile);
                        getData().setDefaults(defConfig);
                    }


                    if (isCanBeEdited()) {
                        loadByByte();
                    }

                    properLoad = true;
                } else {
                    if (!newFile)
                        throw new NullPointerException("Failed to load the file " + getName());
                }
            } else {
                properLoad = true;
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            ChatUtils.log(new String[]{"Failed to load the " + name +
                    " file due to a critical error. Please fix the file and restart your server.",
                    "Oftentimes, if this is your first time loading the server, a simple reload or restart fixes the issue!"});
            properLoad = false;
        }
    }

    private void loadByByte() {
        instance.getDebugUtils().log("loadByByte() - CALL", getClass(), false);

        if (saveWithCustomSave) {
            try {
                FileInputStream fileInputSteamBefore = new FileInputStream(getDataFile());
                InputStreamReader fileInputSteam = new InputStreamReader(fileInputSteamBefore, StandardCharsets.UTF_8);

                setLoaded("");

                int content;
                while ((content = fileInputSteam.read()) != -1) {
                    setLoaded(getLoaded() + ((char) content));
                }

                getData().loadFromString(getLoaded());
            } catch (Exception exc) {
                // IGNORED
            }
        }
    }

    public boolean folderExists(String path) {
        try {
            for (File file : instance.getDataFolder().listFiles()) {
                if (file.isDirectory() && file.getName().equalsIgnoreCase(path.replace("/", ""))) {
                    return true;
                }
            }
        } catch (Exception exc) {
            // IGNORED
        }
        return false;
    }

    public void save() {
        instance.getDebugUtils().log("save() - CALL", getClass(), false);
        long curTime = System.currentTimeMillis();

        if (getData() == null || getDataFile() == null) {
            return;
        }

        try {
            if (properLoad || newFile) {
                if (!isSaveWithCustomSave() || newFile) {
                    get().save(getDataFile());
                } else {
                    saveByByte();
                }
            } else {
                ChatUtils.log(name + " file not saving to prevent it from further damage.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            instance.getLogger().log(Level.SEVERE, ex, () -> "Could not save config to " + getDataFile());
        }
        instance.getDebugUtils().log("save() - Time to complete: " + (System.currentTimeMillis() - curTime), getClass());
        if (DebugUtils.OUTPUT_FILE_SAVE_TIME) {
            ChatUtils.log("Time to save " + name + ": " + (System.currentTimeMillis() - curTime));
        }
    }

    private void saveByByte() {
        instance.getDebugUtils().log("saveByByte() - CALL", getClass(), false);
        ArrayList<String> bukkitLoad = new ArrayList<>();
        for (String s : getData().saveToString().split("\n")) {
            if (!isCommentLine(s)) {
                bukkitLoad.add(s);
            }
        }

        ArrayList<String> byteLoad = new ArrayList<>(Arrays.asList(getLoaded().split("\n")));

        StringBuilder modifiedString = new StringBuilder();

        HashMap<String, Integer> lastLevel = new HashMap<>();

        for (String s : bukkitLoad) {
            StringBuilder commentSec = new StringBuilder();
            String without = ChatUtils.stripFromWhitespace(s);

            if (!isCommentLine(s)) {
                String[] split1 = without.split(":");

                int id = 0;
                int currentLevel = 0;
                for (String bks : byteLoad) {
                    if (ChatUtils.stripFromWhitespace(bks).split(":")[0].equals(split1[0])) {
                        if (!lastLevel.containsKey(split1[0]) || lastLevel.get(split1[0]) < currentLevel) {
                            lastLevel.put(split1[0], currentLevel);
                            break;
                        }
                        currentLevel++;
                    }
                    id++;
                }

                for (int end = id - 1; end > -1; end--) {
                    String line = byteLoad.get(end);
                    if (isCommentLine(line)) {
                        commentSec.insert(0, line + "\n");
                    } else if (!line.equalsIgnoreCase(s)) {
                        break;
                    }
                }
            }
            modifiedString.append(commentSec).append(s).append("\n");
        }

        try {
            FileOutputStream fop = new FileOutputStream(getDataFile());

            byte[] contentInBytes = modifiedString.toString().getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copy(FileHandler dest) throws IOException {
        InputStream is;
        OutputStream os;

        is = new FileInputStream(getDataFile());
        os = new FileOutputStream(dest.getDataFile());
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }

        is.close();
        os.close();
    }

    public FileConfiguration get() {
        instance.getDebugUtils().log("get() - CALL", getClass(), false);
        if (getData() == null) {
            reload();
        }
        return getData();
    }

    public void saveDefaults() {
        loadFile();

        if (!dataFile.exists()) {
            instance.saveResource(directory + name, false);
        }

        if (dataFile == null) {
            reload();
        }
    }

    public boolean isCommentLine(String s) {
        return s.trim().length() == 0 || s.equalsIgnoreCase("\n") || ChatUtils.stripFromWhitespace(s).startsWith("#");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getLoaded() {
        return loaded;
    }

    public void setLoaded(String loaded) {
        this.loaded = loaded;
    }

    public FileConfiguration getData() {
        instance.getDebugUtils().log("getData() - CALL", getClass(), false);

        return data;
    }

    public void setData(FileConfiguration data) {
        this.data = data;
    }

    public File getDataFile() {
        return dataFile;
    }

    public void setDataFile(File dataFile) {
        this.dataFile = dataFile;
    }

    public boolean isCanBeEdited() {
        return canBeEdited;
    }

    public void setCanBeEdited(boolean canBeEdited) {
        this.canBeEdited = canBeEdited;
    }

    public boolean isProperLoad() {
        return properLoad;
    }

    public void setProperLoad(boolean properLoad) {
        this.properLoad = properLoad;
    }

    public boolean isSaveWithCustomSave() {
        return saveWithCustomSave;
    }

    public void setSaveWithCustomSave(boolean saveWithCustomSave) {
        this.saveWithCustomSave = saveWithCustomSave;
    }

    public FileDataLoader getFileDataLoader() {
        return fileDataLoader;
    }
}
