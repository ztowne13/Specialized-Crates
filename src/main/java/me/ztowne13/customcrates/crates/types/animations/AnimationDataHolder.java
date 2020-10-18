package me.ztowne13.customcrates.crates.types.animations;

import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.DebugUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AnimationDataHolder {
    private final Player player;
    private final Location location;
    private final CrateAnimation crateAnimation;
    private final List<CrateAnimation.KeyType> clickedKeys;
    private boolean fastTrack = false;
    private boolean fastTrackWaitTick = false;
    private long totalTickTime;
    private int totalTicks = 0;
    private int individualTicks = 0;
    private int waitingTicks = 0;
    private State currentState = State.PLAYING;

    public AnimationDataHolder(Player player, Location location, CrateAnimation crateAnimation) {
        this.player = player;
        this.location = location;
        this.crateAnimation = crateAnimation;

        clickedKeys = new ArrayList<>();
    }

    public void setFastTrack(boolean fastTrack, boolean force) {
        if (force) {
            this.fastTrack = fastTrack;
        } else {
            fastTrackWaitTick = true;
        }

    }

    public void updateTickTime(long startTime) {
        long curTime = System.nanoTime();
        long add = curTime - startTime;

        totalTickTime += add;

        if (DebugUtils.OUTPUT_AVERAGE_ANIMATION_TICK)
            ChatUtils.log("Average animation tick in nanoseconds (totalTime = " + totalTickTime + ", ticks = " + totalTicks + "): " +
                    ((double) totalTickTime / ((double) totalTicks)));
    }

    public int getTotalTicks() {
        return totalTicks;
    }

    public void setTotalTicks(int totalTicks) {
        this.totalTicks = totalTicks;
    }

    public int getIndividualTicks() {
        return individualTicks;
    }

    public void setIndividualTicks(int individualTicks) {
        this.individualTicks = individualTicks;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    public CrateAnimation getCrateAnimation() {
        return crateAnimation;
    }

    public int getWaitingTicks() {
        return waitingTicks;
    }

    public void setWaitingTicks(int waitingTicks) {
        this.waitingTicks = waitingTicks;
    }

    public List<CrateAnimation.KeyType> getClickedKeys() {
        return clickedKeys;
    }

    public boolean isFastTrack() {
        return fastTrack;
    }

    public boolean isFastTrackWaitTick() {
        return fastTrackWaitTick;
    }

    public enum State {
        PLAYING,

        WAITING,

        CLOSING,

        ENDING,

        SHUFFLING,

        UNCOVERING,

        COMPLETED
    }
}
