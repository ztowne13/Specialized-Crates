package me.ztowne13.customcrates.crates.types.animations.keycrate;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.CrateAnimation;
import me.ztowne13.customcrates.logging.StatusLogger;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.players.data.events.HistoryEvent;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AnimationKeyCrate extends CrateAnimation
{
    int count;

    public AnimationKeyCrate(Crate crate)
    {
        super("", crate);
    }

    @Override
    public boolean tick(Player p, Location l, CrateState cs, boolean requireKeyInHand)
    {
        if (canExecuteFor(cs, CrateState.OPEN, p, requireKeyInHand))
        {
            ArrayList<Reward> rewards = new ArrayList<Reward>();

            for(int i = 0; i < count; i++)
            {
                Reward r = getCrates().getCs().getCr().getRandomReward(p);
                r.runCommands(p);

                rewards.add(r);
            }

            getCrates().tick(l, cs, p, rewards);
            takeKeyFromPlayer(p, !requireKeyInHand);
            new HistoryEvent(Utils.currentTimeParsed(), getCrates(), rewards, true)
                    .addTo(PlayerManager.get(getCc(), p).getPdm());
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
        FileConfiguration fc = cc.getCrateconfigFile().get();
        count = fc.getInt("CrateType.Other.GiveKey.reward-count");
    }

    @Override
    public void finishUp(Player p)
    {

    }

}
