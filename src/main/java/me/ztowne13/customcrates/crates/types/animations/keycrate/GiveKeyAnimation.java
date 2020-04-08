package me.ztowne13.customcrates.crates.types.animations.keycrate;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.animations.AnimationDataHolder;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimation;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GiveKeyAnimation extends CrateAnimation
{

    public GiveKeyAnimation(Crate crate)
    {
        super(crate, CrateAnimationType.GIVE_KEY);
    }

    @Override
    public void tickAnimation(AnimationDataHolder dataHolder, boolean update)
    {

    }

    @Override
    public void endAnimation(AnimationDataHolder dataHolder)
    {
        Player player = dataHolder.getPlayer();

        Reward r = getCrate().getSettings().getRewards().getRandomReward();
        ArrayList<Reward> rewards = new ArrayList<>();
        rewards.add(r);

        completeCrateRun(player, rewards, false, null);
        getCrate().tick(dataHolder.getLocation(), CrateState.OPEN, player, rewards);
    }

    @Override
    public boolean updateTicks(AnimationDataHolder dataHolder)
    {
        return false;
    }

    @Override
    public void checkStateChange(AnimationDataHolder dataHolder, boolean update)
    {
        switch(dataHolder.getCurrentState())
        {
            case PLAYING:
                dataHolder.setCurrentState(AnimationDataHolder.State.COMPLETED);
        }
    }

    @Override
    public void loadDataValues(StatusLogger sl)
    {

    }

}
