package me.ztowne13.customcrates.crates.types.animations.keycrate;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.CrateAnimation;
import me.ztowne13.customcrates.logging.StatusLogger;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.players.data.events.HistoryEvent;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AnimationKeyCrate extends CrateAnimation
{

    public AnimationKeyCrate(Crate crate)
    {
        super("", crate);
        loadValueFromConfig(crates.getCs().getSl());
    }

    @Override
    public boolean tick(Player p, Location l, CrateState cs, boolean requireKeyInHand)
    {
        Bukkit.broadcastMessage("tick");
        if (canExecuteFor(cs, CrateState.OPEN, p, requireKeyInHand))
        {
            Bukkit.broadcastMessage("tick1");
            ArrayList<Reward> rewards = new ArrayList<Reward>();

            for(int i = 0; i < crates.getCs().COUNT; i++)
            {
                Bukkit.broadcastMessage("tick i: " + i);
                Reward r = getCrates().getCs().getCr().getRandomReward(p);
                r.runCommands(p);

                rewards.add(r);
            }

            Bukkit.broadcastMessage("tick3");

            getCrates().tick(l, cs, p, rewards);

            Bukkit.broadcastMessage("tick4");

            takeKeyFromPlayer(p, !requireKeyInHand);
            new HistoryEvent(Utils.currentTimeParsed(), getCrates(), rewards, true)
                    .addTo(PlayerManager.get(getCc(), p).getPdm());
            Bukkit.broadcastMessage("tick5");
            return true;
        }

        playFailToOpen(p);
        return false;
    }


    /*
    CrateType:
      Other:
        GiveKey:
          reward-count: 3
     */
    @Override
    public void loadValueFromConfig(StatusLogger sl)
    {

    }

    @Override
    public void finishUp(Player p)
    {

    }

}
