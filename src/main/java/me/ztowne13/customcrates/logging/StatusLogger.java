package me.ztowne13.customcrates.logging;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.HashMap;

public class StatusLogger
{
    CustomCrates cc;

    int failures;

    HashMap<String, ArrayList<String>> completedEvents = new HashMap<String, ArrayList<String>>();
    HashMap<String, ArrayList<String>> failedEvents = new HashMap<String, ArrayList<String>>();

    public StatusLogger(CustomCrates cc)
    {
        this.cc = cc;
    }

    public void addEvent(boolean success, String section, String event, String cause)
    {
        failures += success ? 0 : 1;
        HashMap<String, ArrayList<String>> map = success ? getCompletedEvents() : getFailedEvents();
        ArrayList<String> list = (map.keySet().contains(section) ? map.get(section) : new ArrayList<String>());
        list.add(event + "%CAUSE%" + cause);
        map.put(section, list);
    }

    public void logAll()
    {
        logAll(Bukkit.getConsoleSender(), false);
    }

    public void logAll(CommandSender sender, boolean forceOnlyFailures)
    {
        ArrayList<String> hasLogged = new ArrayList<String>();
        for (String s : getCompletedEvents().keySet())
        {
            if (!hasLogged.contains(s.toUpperCase()))
            {
                logSection(sender, s, forceOnlyFailures);
                hasLogged.add(s.toUpperCase());
            }
        }

        for (String s : getFailedEvents().keySet())
        {
            if (!hasLogged.contains(s.toUpperCase()))
            {
                logSection(sender, s, forceOnlyFailures);
                hasLogged.add(s.toUpperCase());
            }
        }

        if (getFailedEvents().isEmpty() &&
                SettingsValues.LOG_SUCCESSES.getValue(getCc()).toString().equalsIgnoreCase("FAILURES"))
        {
            ChatUtils.log("  &a+&f Success: there were no issues.");
        }
    }

    public void logSection(CommandSender sender, String section, boolean forceOnlyFailures)
    {
        boolean hasLoggedHeader = false;
        String toLog = SettingsValues.LOG_SUCCESSES.getValue(getCc()).toString();

        if (!toLog.equalsIgnoreCase("NOTHING") || forceOnlyFailures)
        {
            if (!toLog.equalsIgnoreCase("FAILURES") && !forceOnlyFailures)
            {
                hasLoggedHeader = true;
                logValue(sender, "  " + section);
                for (String checkSec : getCompletedEvents().keySet())
                {
                    if (checkSec.equalsIgnoreCase(section))
                    {
                        for (String s : getCompletedEvents().get(checkSec))
                        {
                            String[] split = s.split("%CAUSE%");
                            String event = split[0];
                            logValue(sender, "    &a+&f " + event);
                        }
                    }
                }
            }

            for (String checkSec : getFailedEvents().keySet())
            {
                if (checkSec.equalsIgnoreCase(section))
                {
                    if (!hasLoggedHeader)
                    {
                        logValue(sender, "  " + section);
                        hasLoggedHeader = true;
                    }

                    for (String s : getFailedEvents().get(checkSec))
                    {
                        String[] parsedEvent = s.split("%CAUSE%");
                        String event = parsedEvent[0];
                        String cause = parsedEvent[1];

                        logValue(sender, "    &c-&f " + event);

                        if (cause.equalsIgnoreCase("NONE"))
                        {
                            continue;
                        }

                        logValue(sender, "        &7CAUSE: " + cause);
                    }
                }
            }
        }
    }

    public void logValue(CommandSender sender, String s)
    {
        if (sender instanceof ConsoleCommandSender)
        {
            ChatUtils.log(s);
            return;
        }
        sender.sendMessage(ChatUtils.toChatColor(s));
    }

    public HashMap<String, ArrayList<String>> getCompletedEvents()
    {
        return completedEvents;
    }

    public void setCompletedEvents(HashMap<String, ArrayList<String>> completedEvents)
    {
        this.completedEvents = completedEvents;
    }

    public HashMap<String, ArrayList<String>> getFailedEvents()
    {
        return failedEvents;
    }

    public void setFailedEvents(HashMap<String, ArrayList<String>> failedEvents)
    {
        this.failedEvents = failedEvents;
    }

    public CustomCrates getCc()
    {
        return cc;
    }

    public void setCc(CustomCrates cc)
    {
        this.cc = cc;
    }

    public int getFailures()
    {
        return failures;
    }

    public void setFailures(int failures)
    {
        this.failures = failures;
    }
}
