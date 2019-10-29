package me.ztowne13.customcrates.crates.types.animations.keycrate;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimation;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.players.data.events.HistoryEvent;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AnimationKeyCrate extends CrateAnimation
{

    public AnimationKeyCrate(Crate crate)
    {
        super("", crate);
    }

    @Override
    public boolean tick(Player p, Location l, CrateState cs, boolean requireKeyInHand)
    {
        if (canExecuteFor(cs, CrateState.OPEN, p, requireKeyInHand))
        {
            Reward r = getCrates().getCs().getCr().getRandomReward(p);
            r.runCommands(p);

            ArrayList<Reward> rewards = new ArrayList<Reward>();
            rewards.add(r);

            getCrates().tick(l, cs, p, rewards);
            takeKeyFromPlayer(p, !requireKeyInHand);
            new HistoryEvent(Utils.currentTimeParsed(), getCrates(), rewards, true)
                    .addTo(PlayerManager.get(getCc(), p).getPdm());
            return true;
        }

        playFailToOpen(p);
        return false;
    }

    @Override
    public void loadValueFromConfig(StatusLogger sl)
    {

    }

    @Override
    public void finishUp(Player p)
    {

    }

}
