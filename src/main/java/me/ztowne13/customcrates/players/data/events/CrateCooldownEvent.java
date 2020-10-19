package me.ztowne13.customcrates.players.data.events;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.utils.Utils;

/**
 * Created by ztowne13 on 8/5/15.
 */
public class CrateCooldownEvent extends DataEvent {
    private final Crate crate;
    private final long cooldownTime;
    private final boolean start;
    private long startTime;

    public CrateCooldownEvent(Crate crate, long startTime, boolean start) {
        super(crate.getInstance());

        this.crate = crate;
        this.startTime = startTime;
        this.cooldownTime = crate.getSettings().getCooldown() * 1000L;
        this.start = start;
    }

    @Override
    public String getFormatted() {
        return getCrate().getName() + ";" + getStartTime();
    }

    @Override
    public void addTo(PlayerDataManager playerDataManager) {
        if (getCooldownTime() > 0) {
            if (isStart()) {
                long seconds = getCooldownTime() / 1000;
                String[] values = Utils.convertSecondToHHMMString(seconds);

                Messages.COOLDOWN_START.msgSpecified(instance, playerDataManager.getDataHandler().getPlayerManager().getPlayer(),
                        new String[]{"%crate%", "%days%", "%hours%", "%minutes%", "%seconds%"},
                        new String[]{getCrate().getDisplayName(), values[0], values[1], values[2], values[3]});
            }

            playerDataManager.addCrateCooldowns(this, playerDataManager.addStringToList(getFormatted(), playerDataManager.getCrateCooldowns()));
        }
    }

    public void tickSecond(PlayerDataManager playerDataManager) {
        if (isCooldownOverAsBoolean()) {
            end(playerDataManager);
        }
    }

    public boolean isCooldownOverAsBoolean() {
        return isCooldownOver() == -1;
    }

    public long isCooldownOver() {
        return getStartTime() + getCooldownTime() < System.currentTimeMillis() ? -1 :
                (getStartTime() + getCooldownTime() - System.currentTimeMillis()) / 1000L;
    }

    public void end(PlayerDataManager playerDataManager) {
        long seconds = getCooldownTime() / 1000;
        String[] values = Utils.convertSecondToHHMMString(seconds);
        Messages.COOLDOWN_END.msgSpecified(getInstance(), playerDataManager.getDataHandler().getPlayerManager().getPlayer(),
                new String[]{"%crate%", "%days%", "%hours%", "%minutes%", "%seconds%"},
                new String[]{getCrate().getDisplayName(), values[0], values[1], values[2], values[3]});
        playerDataManager.removeCrateCooldowns(this, playerDataManager.removeStringFromList(getFormatted(), playerDataManager.getCrateCooldowns()));
    }

    public boolean matches(CrateCooldownEvent crateCooldownEvent) {
        return getCrate().getName().equalsIgnoreCase(crateCooldownEvent.getCrate().getName()) && getStartTime() == crateCooldownEvent.getStartTime() &&
                getCooldownTime() == crateCooldownEvent.getCooldownTime();
    }

    public void playFailure(PlayerDataManager playerDataManager) {
        crate.getSettings().getCrateAnimation().playFailToOpen(playerDataManager.getPlayerManager().getPlayer(), false, true);
        long seconds = isCooldownOver();
        String[] values = Utils.convertSecondToHHMMString(seconds);
        playerDataManager.getPlayerManager().getPlayer().sendMessage(Messages.CRATE_ON_COOLDOWN.getFromConf(instance).replace("%crate%", crate.getDisplayName())
                .replace("%days%", values[0]).replace("%hours%", values[1]).replace("%minutes%", values[2])
                .replace("%seconds%", values[3]));

    }

    public Crate getCrate() {
        return crate;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getCooldownTime() {
        return cooldownTime;
    }

    public boolean isStart() {
        return start;
    }
}
