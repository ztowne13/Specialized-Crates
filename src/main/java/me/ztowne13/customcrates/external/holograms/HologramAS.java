package me.ztowne13.customcrates.external.holograms;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class HologramAS extends Hologram
{
    double SEPARATION = 0.25;

    List<String> lines;
    List<ArmorStand> stands;

    protected HologramAS(SpecializedCrates cc, String name, Location location)
    {
        super(cc, name, location);

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

    @Override
    public void addLine(String line)
    {
        ArmorStand armorStand = spawnNewArmourStand(getLocation());
        armorStand.setCustomName(ChatUtils.toChatColor(line));

        getStands().add(armorStand);
        getLines().add(line);

        update();
    }

    @Override
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

    @Override
    public void update()
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
//        ArmorStand as = (ArmorStand) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
//
//        as.setGravity(false);
//        as.setCanPickupItems(false);
//        as.setCustomNameVisible(true);
//        as.setVisible(false);
//
//        return as;
        return null;
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
