package me.ztowne13.customcrates.crates.types.animations;

import me.ztowne13.customcrates.crates.Crate;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class AnimationDataHolder
{
    public enum State
    {
        PLAYING(true, false),

        WAITING(true, false),

        CLOSING(true, false),

        ENDING(true, true),

        CHOOSING(false, false),

        WAITING_NOCLOSE(false, false),

        SHUFFLING(false, false),

        UNCOVERING(false, false),

        COMPLETED(true, true);

        boolean forceCanFastTrack;
        boolean canFastTrack;

        State(boolean canFastTrack, boolean forceCanFastTrack)
        {
            this.canFastTrack = canFastTrack;
            this.forceCanFastTrack = forceCanFastTrack;
        }

        /**
         * This is the logic to see if the the animation is waiting to be closed, or can be fast-tracked.
         *
         * @param crate The crate to check whether or not fast-trak is enabled.
         * @return Whether nor not, given the current crate settings and crate state, the animation can be finished.
         */
        public boolean isCanFastTrack(Crate crate)
        {
            return (canFastTrack && crate.getSettings().isCanFastTrack()) || forceCanFastTrack;
        }
    }

    Player player;
    Location location;
    CrateAnimation crateAnimation;

    boolean fastTrack = false;

    int totalTicks = 0;
    int individualTicks = 0;
    int waitingTicks = 0;

    State currentState = State.PLAYING;

    ArrayList<CrateAnimation.KeyType> clickedKeys;

    public AnimationDataHolder(Player player, Location location, CrateAnimation crateAnimation)
    {
        this.player = player;
        this.location = location;
        this.crateAnimation = crateAnimation;

        clickedKeys = new ArrayList<>();
    }

    public void setFastTrack(boolean fastTrack, boolean early)
    {
        if(!early || getCrateAnimation().getCrate().getSettings().getCrateType().isCanFastTrackOnReload())
        {
            this.fastTrack = fastTrack;
        }
    }

    public int getTotalTicks()
    {
        return totalTicks;
    }

    public void setTotalTicks(int totalTicks)
    {
        this.totalTicks = totalTicks;
    }

    public int getIndividualTicks()
    {
        return individualTicks;
    }

    public void setIndividualTicks(int individualTicks)
    {
        this.individualTicks = individualTicks;
    }

    public State getCurrentState()
    {
        return currentState;
    }

    public void setCurrentState(State currentState)
    {
        this.currentState = currentState;
    }

    public Player getPlayer()
    {
        return player;
    }

    public Location getLocation()
    {
        return location;
    }

    public CrateAnimation getCrateAnimation()
    {
        return crateAnimation;
    }

    public int getWaitingTicks()
    {
        return waitingTicks;
    }

    public void setWaitingTicks(int waitingTicks)
    {
        this.waitingTicks = waitingTicks;
    }

    public ArrayList<CrateAnimation.KeyType> getClickedKeys()
    {
        return clickedKeys;
    }

    public boolean isFastTrack()
    {
        return fastTrack;
    }
}
