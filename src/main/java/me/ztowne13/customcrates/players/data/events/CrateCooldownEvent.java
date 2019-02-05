package me.ztowne13.customcrates.players.data.events;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.utils.Utils;

/**
 * Created by ztowne13 on 8/5/15.
 */
public class CrateCooldownEvent extends DataEvent
{
    public Crate crates;
    long startTime;
    long cooldownTime;
    boolean start;

    public CrateCooldownEvent(Crate crates, long startTime, boolean start)
    {
        super(crates.getCc());

        this.crates = crates;
        this.startTime = startTime;
        this.cooldownTime = crates.getCs().getCooldown()*1000;
        this.start = start;
    }

    @Override
    public String getFormatted()
    {
        return getCrates().getName() + ";" + getStartTime();
    }

    @Override
    public void addTo(PlayerDataManager pdm)
    {
        if(getCooldownTime() > 0)
        {
            if (isStart())
            {
                int seconds = Math.round(getCooldownTime() / 1000);
                String cdTimeFormatted = Utils.ConvertSecondToHHMMString(seconds);
                Messages.COOLDOWN_START.msgSpecified(cc, pdm.getDh().getPm().getP(), new String[]{"%crate%", "%seconds%"}, new String[]{getCrates().getName(), cdTimeFormatted});
            }

            pdm.addCrateCooldowns(this, pdm.addStringToList(getFormatted(), pdm.getCrateCooldowns()));
        }
    }

    public void tickSecond(PlayerDataManager pdm)
    {
        if(isCooldownOverAsBoolean(pdm))
        {
            end(pdm);
        }
    }

    public boolean isCooldownOverAsBoolean(PlayerDataManager pdm)
    {
        return isCooldownOver(pdm) == -1;
    }

    public long isCooldownOver(PlayerDataManager pdm)
    {
        return getStartTime() + getCooldownTime() < System.currentTimeMillis() ? -1 : (getStartTime() + getCooldownTime() - System.currentTimeMillis()) / 1000L;
    }

    public void end(PlayerDataManager pdm)
    {
        int seconds = Math.round(getCooldownTime() / 1000);
        String cdTimeFormatted = Utils.ConvertSecondToHHMMString(seconds);
        Messages.COOLDOWN_END.msgSpecified(getCc(), pdm.getDh().getPm().getP(), new String[]{"%crate%", "%seconds%"}, new String[]{getCrates().getName(), cdTimeFormatted});
        pdm.removeCrateCooldowns(this, pdm.removeStringFromList(getFormatted(), pdm.getCrateCooldowns()));
    }

    public boolean matches(CrateCooldownEvent cce)
    {
        return getCrates().getName().equalsIgnoreCase(cce.getCrates().getName()) && getStartTime() == cce.getStartTime() && getCooldownTime() == cce.getCooldownTime();
    }

    public Crate getCrates()
    {
        return crates;
    }

    public void setCrates(Crate crates)
    {
        this.crates = crates;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    public long getCooldownTime()
    {
        return cooldownTime;
    }

    public void setCooldownTime(long cooldownTime)
    {
        this.cooldownTime = cooldownTime;
    }

    public boolean isStart()
    {
        return start;
    }

    public void setStart(boolean start)
    {
        this.start = start;
    }
}
