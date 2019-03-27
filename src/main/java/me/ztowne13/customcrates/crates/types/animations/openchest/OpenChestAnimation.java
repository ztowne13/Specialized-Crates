package me.ztowne13.customcrates.crates.types.animations.openchest;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.CrateType;
import me.ztowne13.customcrates.crates.types.InventoryCrate;
import me.ztowne13.customcrates.logging.StatusLogger;
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class OpenChestAnimation extends InventoryCrate
{
    public static ArrayList<Item> items = new ArrayList<>();

    String invName, prefix;
    int openDuration;

    Location loc;
    Reward reward;


    public OpenChestAnimation(Inventory inventory, Crate crate)
    {
        super(inventory, crate);
        prefix = CrateType.BLOCK_CRATEOPEN.getPrefix() + ".";
    }

    @Override
    public boolean tick(Player p, Location l, CrateState cs, boolean requireKeyInHand)
    {
        this.loc = l;

        if (canExecuteFor(cs, CrateState.OPEN, p, requireKeyInHand))
        {
            playAnimation(p, l);
            playRequiredOpenActions(p, !requireKeyInHand);
            return true;
        }

        playFailToOpen(p);
        return false;
    }

    public void playAnimation(final Player p, final Location l)
    {
        reward = getCrates().getCs().getCr().getRandomReward(p);

        Location upOne = l.clone();
        upOne.setY(upOne.getY() + 1);
        upOne.setX(upOne.getX() + .5);
        upOne.setZ(upOne.getZ() + .5);

        final Item item = l.getWorld().dropItem(upOne, reward.getDisplayItem());
        item.setPickupDelay(100000);
        item.setVelocity(new Vector(0, item.getVelocity().getY(), 0));
        items.add(item);

        new NMSChestState().playChestAction(l.getBlock(), true);

        Bukkit.getScheduler().runTaskLater(cc, new Runnable(){
            @Override
            public void run()
            {
                new NMSChestState().playChestAction(l.getBlock(), false);
                item.remove();
                finishUp(p);
            }
        }, openDuration);
    }

//    public void changeChestState(Location loc, boolean open) {
//        for (Player p : loc.getWorld().getPlayers()) {
//            p.playNote(loc, (byte) 1, (byte) (open ? 1 : 0));
//        }
//    }

    @Override
    public void loadValueFromConfig(StatusLogger sl)
    {
        FileConfiguration fc = getFu().get();

        invName = fc.getString(prefix + "inv-name");

        openDuration = FileUtils.loadInt(prefix + "chest-open-duration", 60, sl,
                StatusLoggerEvent.ANIMATION_OPENCHEST_CHEST_OPEN_DURATION_SUCCESS,
                StatusLoggerEvent.ANIMATION_OPENCHEST_CHEST_OPEN_DURATION_INVALID);
    }

    @Override
    public void finishUp(Player p)
    {
        ArrayList<Reward> rewards = new ArrayList<Reward>();
        rewards.add(reward);

        completeCrateRun(p, rewards, false);
        getCrates().tick(loc, CrateState.OPEN, p, rewards);
    }

    public static void removeAllItems()
    {
        for(Item item : items)
            item.remove();
    }
}
