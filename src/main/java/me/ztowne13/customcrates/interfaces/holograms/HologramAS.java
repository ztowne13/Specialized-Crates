package me.ztowne13.customcrates.interfaces.holograms;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class HologramAS
{
    double SEPARATION = 0.25;

    CustomCrates cc;

    String name;
    Location location;

    List<String> lines;
    List<ArmorStand> stands;

    protected HologramAS(CustomCrates cc, String name, Location location)
    {
        this.cc = cc;
        this.name = name;
        this.location = location;

        lines = new ArrayList<>();
        stands = new ArrayList<>();
    }

    protected void deleteStands()
    {
        for(ArmorStand stand : getStands())
            stand.remove();
    }

    public void addLine(String line)
    {
//        int index = getLines().size();
//        double offset = -index*SEPARATION;
//
//        Location toSetLoc = getLocation().add(0, offset, 0);

        ArmorStand armorStand = spawnNewArmourStand(getLocation());
        armorStand.setCustomName(ChatUtils.toChatColor(line));

        getStands().add(armorStand);
        getLines().add(line);

        updateLines();
    }

    public void setLine(int i, String line)
    {
        if(i >= getLines().size())
        {
            addLine(line);
        }
        else
        {
            getLines().set(i, line);

            ArmorStand armorStand = getStands().get(i);
            armorStand.setCustomName(ChatUtils.toChatColor(line));
        }
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public void updateLines()
    {
        for(int i = 0; i < getLines().size(); i++)
        {
            double offset = (getLines().size() - 1.0 - i)*SEPARATION;

            getStands().get(i).remove();
            ArmorStand newStand = spawnNewArmourStand(getLocation().clone().add(0, offset - 2.5, 0));
            newStand.setCustomName(getLines().get(i));
            getStands().set(i, newStand);

            //getStands().get(i).teleport(getLocation().clone().add(0, offset - 2.5, 0));
        }
    }

    private ArmorStand spawnNewArmourStand(Location spawnLoc)
    {
        ArmorStand as = (ArmorStand) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);

        as.setGravity(false);
        as.setCanPickupItems(false);
        as.setCustomNameVisible(true);
        as.setVisible(false);

        return as;
    }

    public CustomCrates getCc()
    {
        return cc;
    }

    public String getName()
    {
        return name;
    }

    public Location getLocation()
    {
        return location;
    }

    public List<String> getLines()
    {
        return lines;
    }

    public List<ArmorStand> getStands()
    {
        return stands;
    }
}
