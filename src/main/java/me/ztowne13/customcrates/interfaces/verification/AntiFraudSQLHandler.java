package me.ztowne13.customcrates.interfaces.verification;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.FileHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;


public class AntiFraudSQLHandler extends Thread
{
    boolean authenticated = true;

    SpecializedCrates specializedCrates;

    UUID id;

    public AntiFraudSQLHandler(SpecializedCrates specializedCrates)
    {
        this.specializedCrates = specializedCrates;

        FileHandler data = specializedCrates.getDataFile();
        FileConfiguration fc = data.get();

        if (fc.contains("server-id"))
        {
            id = UUID.fromString(fc.getString("server-id"));
        }
        else
        {
            id = UUID.randomUUID();
            fc.set("server-id", id.toString());
            data.save();
        }

        start();
        setName("SpecializedCrates-HTML");
    }

    @Override
    public void run()
    {
        try
        {
            if(!authenticate("SERVERID", id.toString()))
                authenticated = false;

            if(!authenticate("NONCE", AntiFraudPlaceholders.NONCE))
                authenticated = false;

            if(!authenticate("USER", AntiFraudPlaceholders.USER))
                authenticated = false;

            if(!authenticate("RESOURCE", AntiFraudPlaceholders.RESOURCE))
                authenticated = false;

            if (!authenticated)
            {
                ChatUtils.log(Messages.BLACKLISTED_PLUGIN.getMsg());
            }
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }

    }

    public boolean authenticate(String type, String toMatch) throws Exception
    {
        URL idsURL = new URL("http://vps210053.vps.ovh.ca/specializedcrates_service.php?type='" + type + "'");
        URLConnection connection = idsURL.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));

        String inputLine;

        while ((inputLine = in.readLine()) != null)
        {
            specializedCrates.getDu().log("run() - idsURL: " + inputLine, getClass());
            Object obj = new JSONParser().parse(inputLine);
            JSONArray jsonArray = (JSONArray) obj;

            for (int i = 0; i < jsonArray.size(); i++)
            {
                JSONObject jobj = (JSONObject) jsonArray.get(i);
                String toCompareVal = jobj.get("value").toString();

                if(toCompareVal.equals(toMatch))
                    return false;

                specializedCrates.getDu()
                        .log("authenticate() - i: " + i + ", val: " + jsonArray.get(i) + ", val2: " + jobj.get("value"), getClass());
            }
        }
        in.close();

        idsURL.openStream();

        return true;
    }

    public boolean isAuthenticated()
    {
        return authenticated;
    }
}