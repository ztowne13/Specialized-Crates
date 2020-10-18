package me.ztowne13.customcrates.crates;

import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class SettingsConverter {
    private SettingsConverter() {
        // EMPTY
    }

    public static void convertParticles(FileHandler fileHandler, String path) {
        FileConfiguration fc = fileHandler.get();
        boolean isSet = !fc.getStringList(path).isEmpty();

        if (isSet) {
            ChatUtils.log("Converting particles for " + path + "...");
            List<String> list = fc.getStringList(path);
            int i = 0;
            for (String s : list) {
                try {
                    i++;
                    String section = i + "";

                    String[] split = s.replace(" ", "").split(",");
                    String type = split[0].toUpperCase();
                    float rangeX = Float.parseFloat(split[1]);
                    float rangeY = Float.parseFloat(split[2]);
                    float rangeZ = Float.parseFloat(split[3]);
                    float speed = Float.parseFloat(split[4]);
                    int amnt = Integer.parseInt(split[5]);
                    String animation = "NONE";

                    if (split.length >= 7)
                        animation = split[6].toUpperCase();

                    //String section = findNextAvailableNumber(fileHandler, path);

                    fc.set(path + "." + section + ".type", type);
                    fc.set(path + "." + section + ".range-x", rangeX);
                    fc.set(path + "." + section + ".range-y", rangeY);
                    fc.set(path + "." + section + ".range-z", rangeZ);
                    fc.set(path + "." + section + ".speed", speed);
                    fc.set(path + "." + section + ".amount", amnt);
                    fc.set(path + "." + section + ".animation", animation);
                    fc.set(path + "." + section + ".center-x", 0);
                    fc.set(path + "." + section + ".center-y", 0);
                    fc.set(path + "." + section + ".center-z", 0);
                    fileHandler.save();
                    ChatUtils.log("Success. Converted " + s);
                } catch (Exception exc) {
                    //exc.printStackTrace();
                    ChatUtils
                            .log("FAILED TO CONVERT PARTICLES. This is like due to a misformatted particle that wasn't in use anyways and can be ignored.");
                }
            }
        }
    }

//    public static String findNextAvailableNumber(FileHandler fileHandler, String path)
//    {
//        ConfigurationSection configSec = fileHandler.get().getConfigurationSection(path);
//        for(int i = 0; i < 1000; i++)
//        {
//            if(!configSec.getKeys(false).contains(i + ""))
//            {
//                return i + "";
//            }
//        }
//        return null;
//    }
}
