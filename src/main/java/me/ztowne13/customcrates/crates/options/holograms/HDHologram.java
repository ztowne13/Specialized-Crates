package me.ztowne13.customcrates.crates.options.holograms;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.utils.LocationUtils;
import org.bukkit.Location;

import java.util.HashMap;

public class HDHologram extends DynamicHologram
{
    Hologram h;
    HashMap<Integer, TextLine> lines = new HashMap<Integer, TextLine>();

    public HDHologram(CustomCrates cc, PlacedCrate cm)
    {
        super(cc, cm);
    }

    @Override
    public void create(Location l)
    {
        setH(HologramsAPI.createHologram(getCc(), l));
        teleport(l);
    }

    @Override
    public void addLine(String line)
    {
        TextLine tl = getH().appendTextLine(line);
        lines.put(getH().size() - 1, tl);
    }

    @Override
    public void delete()
    {
        if (h != null)
        {
            getH().delete();
        }
    }

    @Override
    public void teleport(Location l)
    {
        l.setY(l.getY() + getCm().getCholo().getHologramOffset());
        getH().teleport(LocationUtils.getLocationCentered(l));
    }

    @Override
    public void setLine(int lineNum, String line)
    {
        if (lines.containsKey(lineNum))
        {
            lines.get(lineNum).setText(line);
        }
        else
        {
            addLine(line);
        }
		/*getH().removeLine(lineNum);
		getH().insertTextLine(lineNum, line);*/
    }

    public Hologram getH()
    {
        return h;
    }

    public void setH(Hologram h)
    {
        this.h = h;
    }
}
