package me.ztowne13.customcrates.crates.types.animations;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class AnimationDataHolder
{
    public enum State
    {
        PLAYING,

        WAITING,

        CLOSING,

        ENDING,

        SHUFFLING,

        UNCOVERING,

        COMPLETED
    }

    Player player;
    Location location;
    CrateAnimation crateAnimation;

    int totalTicks = 0;
    int individualTicks = 0;
    int waitingTicks = 0;

    State currentState = State.PLAYING;

    public AnimationDataHolder(Player player, Location location, CrateAnimation crateAnimation)
    {
        this.player = player;
        this.location = location;
        this.crateAnimation = crateAnimation;
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
}
