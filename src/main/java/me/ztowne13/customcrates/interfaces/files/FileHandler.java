package me.ztowne13.customcrates.interfaces.files;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

public class FileHandler
{

    static HashMap<String, FileHandler> map = new HashMap<String, FileHandler>();

    String name;
    String directory = "";
    String loaded;
    SpecializedCrates cc;

    boolean canBeEdited;
    boolean saveWithCustomSave;

    boolean properLoad = false;
    boolean newFile;

    FileDataLoader fileDataLoader;
    private FileConfiguration data = null;
    private File dataFile = null;

    public FileHandler(SpecializedCrates cc, String name, String directory, boolean canBeEdited, boolean saveWithCustomSave,
                       boolean newFile)
    {
        this.name = name;
        this.cc = cc;
        this.directory = directory;
        this.canBeEdited = canBeEdited;
        this.saveWithCustomSave = saveWithCustomSave;
        this.newFile = newFile;

        this.fileDataLoader = new FileDataLoader(this);

        map.put(name, this);
    }

    public FileHandler(SpecializedCrates cc, String name, boolean canBeEdited, boolean saveWithCustomSave)
    {
        this.name = name;
        this.canBeEdited = canBeEdited;
        this.cc = cc;
        this.saveWithCustomSave = saveWithCustomSave;

        this.fileDataLoader = new FileDataLoader(this);

        map.put(name, this);
    }

    public void loadFile()
    {
        cc.getDu().log("loadFile() - CALL", getClass(), true);

        if (getDataFile() == null)
        {
            setDataFile(new File(new File(getCc().getDataFolder().getPath() + getDirectory()), getName()));

            if (getName().equalsIgnoreCase("Messages.yml"))
            {
                if (!folderExists("Crates"))
                {
                    new File(getCc().getDataFolder().getPath() + getDirectory()).mkdir();

                    try
                    {
                        getCc().firstLoadFiles();
                    }
                    catch (Exception exc)
                    {
                        exc.printStackTrace();
                    }

                }
            }
        }
    }

    public void reload()
    {
        cc.getDu().log("reload() - CALL", getClass(), true);

        loadFile();

        try
        {
            data = YamlConfiguration.loadConfiguration(getDataFile());

            if (canBeEdited)
            {
                if (data.saveToString() != null &&
                        (name.equalsIgnoreCase("Rewards.YML") || !data.saveToString().equalsIgnoreCase("")))
                {
                    File defConfigFile = new File(getCc().getDataFolder(), getName());
                    if (defConfigFile.exists())
                    {
                        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigFile);
                        getData().setDefaults(defConfig);
                    }


                    if (isCanBeEdited())
                    {
                        loadByByte();
                    }

                    properLoad = true;
                }
                else
                {
                    if (!newFile)
                        throw new NullPointerException("Failed to load the file " + getName());
                }
            }
            else
            {
                properLoad = true;
            }
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
            ChatUtils.log(new String[]{"Failed to load the " + name +
                    " file due to a critical error. Please fix the file and restart your server.",
                    "Oftentimes, if this is your first time loading the server, a simple reload or restart fixes the issue!"});
            properLoad = false;
        }
    }

    private void loadByByte()
    {
        cc.getDu().log("loadByByte() - CALL", getClass(), true);

        if (saveWithCustomSave)
        {
            try
            {
                FileInputStream fileInputSteamBefore = new FileInputStream(getDataFile());
                InputStreamReader fileInputSteam = new InputStreamReader(fileInputSteamBefore, Charset.forName("UTF-8"));

                setLoaded("");

                int content;
                while ((content = fileInputSteam.read()) != -1)
                {
                    setLoaded(getLoaded() + ((char) content));
                }

                getData().loadFromString(getLoaded());
            }
            catch (Exception exc)
            {

            }
        }
    }

    public boolean folderExists(String path)
    {
        try
        {
            for (File file : getCc().getDataFolder().listFiles())
            {
                if (file.isDirectory() && file.getName().equalsIgnoreCase(path.replace("/", "")))
                {
                    return true;
                }
            }
        }
        catch (Exception exc)
        {

        }
        return false;
    }

    public void save()
    {
        cc.getDu().log("save() - CALL", getClass(), true);
        long curTime = System.currentTimeMillis();

        if (getData() == null || getDataFile() == null)
        {
            return;
        }

        try
        {
            if (properLoad || newFile)
            {
                if (!isSaveWithCustomSave() || newFile)
                {
                    get().save(getDataFile());
                }
                else
                {
                    saveByByte();
                }
            }
            else
            {
                ChatUtils.log(name + " file not saving to prevent it from further damage.");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            getCc().getLogger().log(Level.SEVERE, "Could not save config to " + getDataFile(), ex);
        }
        cc.getDu().log("save() - Time to complete: " + (System.currentTimeMillis() - curTime), getClass());
    }

    private void saveByByte()
    {
        cc.getDu().log("saveByByte() - CALL", getClass(), true);
        ArrayList<String> bukkitLoad = new ArrayList<>();
        for (String s : getData().saveToString().split("\n"))
        {
            if (!isCommentLine(s))
            {
                bukkitLoad.add(s);
            }
        }

        ArrayList<String> byteLoad = new ArrayList<>(Arrays.asList(getLoaded().split("\n")));

        String modifiedString = "";

        HashMap<String, Integer> lastLevel = new HashMap<String, Integer>();

        for (String s : bukkitLoad)
        {
            String commentSec = "";
            String without = ChatUtils.stripFromWhitespace(s);

            if (!isCommentLine(s))
            {
                String[] split1 = without.split(":");

                int id = 0;
                int currentLevel = 0;
                for (String bks : byteLoad)
                {
                    if (ChatUtils.stripFromWhitespace(bks).split(":")[0].equals(split1[0]))
                    {
                        if (!lastLevel.containsKey(split1[0]) || lastLevel.get(split1[0]) < currentLevel)
                        {
                            lastLevel.put(split1[0], currentLevel);
                            break;
                        }
                        currentLevel++;
                    }
                    id++;
                }

                for (int end = id - 1; end > -1; end--)
                {
                    String line = byteLoad.get(end);
                    if (isCommentLine(line))
                    {
                        commentSec = line + "\n" + commentSec;
                    }
                    else if (!line.equalsIgnoreCase(s))
                    {
                        break;
                    }
                }
            }
            modifiedString += commentSec + s + "\n";
        }

        try
        {
            FileOutputStream fop = new FileOutputStream(getDataFile());

            byte[] contentInBytes = modifiedString.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public FileConfiguration get()
    {
        cc.getDu().log("get() - CALL", getClass(), false);
        if (getData() == null)
        {
            reload();
        }
        return getData();
    }

    public void saveDefaults()
    {
        loadFile();

        if (!dataFile.exists())
        {
            cc.saveResource(directory + name, false);
        }

        if (dataFile == null)
        {
            reload();
        }
    }

    public boolean isCommentLine(String s)
    {
        return s.trim().length() == 0 || s.equalsIgnoreCase("\n") || ChatUtils.stripFromWhitespace(s).startsWith("#");
    }

    public static void clearLoaded()
    {
        map.clear();
        map = new HashMap<String, FileHandler>();
    }

    public static HashMap<String, FileHandler> getMap()
    {
        return map;
    }

    public static void setMap(HashMap<String, FileHandler> map)
    {
        FileHandler.map = map;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDirectory()
    {
        return directory;
    }

    public void setDirectory(String directory)
    {
        this.directory = directory;
    }

    public String getLoaded()
    {
        return loaded;
    }

    public void setLoaded(String loaded)
    {
        this.loaded = loaded;
    }

    public SpecializedCrates getCc()
    {
        return cc;
    }

    public void setCc(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    public FileConfiguration getData()
    {
        cc.getDu().log("getData() - CALL", getClass(), false);

        return data;
    }

    public void setData(FileConfiguration data)
    {
        this.data = data;
    }

    public File getDataFile()
    {
        return dataFile;
    }

    public void setDataFile(File dataFile)
    {
        this.dataFile = dataFile;
    }

    public boolean isCanBeEdited()
    {
        return canBeEdited;
    }

    public void setCanBeEdited(boolean canBeEdited)
    {
        this.canBeEdited = canBeEdited;
    }

    public boolean isProperLoad()
    {
        return properLoad;
    }

    public void setProperLoad(boolean properLoad)
    {
        this.properLoad = properLoad;
    }

    public boolean isSaveWithCustomSave()
    {
        return saveWithCustomSave;
    }

    public void setSaveWithCustomSave(boolean saveWithCustomSave)
    {
        this.saveWithCustomSave = saveWithCustomSave;
    }

    public FileDataLoader getFileDataLoader()
    {
        return fileDataLoader;
    }
}
