package me.ztowne13.customcrates.crates.types.animations;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.players.data.events.HistoryEvent;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public abstract class CrateAnimation
{
    public static long BASE_SPEED = 1;

    protected SpecializedCrates cc;

    protected Crate crate;
    protected FileHandler fu;
    protected String prefix;
    CrateAnimationType animationType;

    public CrateAnimation(Crate crate, CrateAnimationType animationType)
    {
        this.animationType = animationType;
        this.prefix = animationType.getPrefixDotted();
        this.crate = crate;
        this.cc = crate.getCc();
        this.fu = cc.getCrateconfigFile();
    }

    /**
     * This is the function that is called every tick to update and redraw the animation.
     *
     * @param dataHolder The data holder that stores the player's animation runtime information.
     * @param update Whether or not this is an update tick
     */
    public abstract void tickAnimation(AnimationDataHolder dataHolder, boolean update);

    /**
     * This function is what is called when the state of the function is set to COMPLETED
     *
     * @param dataHolder The data holder that stores the player's animation runtime information.
     */
    public abstract void endAnimation(AnimationDataHolder dataHolder);

    /**
     * This function is responsible for loading and animation-based config values.
     *
     * @param sl The status logger to log any errors or successes to.
     */
    public abstract void loadDataValues(StatusLogger sl);

    /**
     * This function is responsible for calculating any tick updates. It is intended for
     * the animations that slow down, speed up, etc - information is changing every tick.
     *
     * @param dataHolder The data holder that stores the player's animation runtime information.
     * @return This value indicates whether or not a main or quintessential update needs to happen,
     *         opposed to just the default BASE_SPEED tick updates.
     */
    public abstract boolean updateTicks(AnimationDataHolder dataHolder);

    /**
     * This function checks if there needs to be a state changed, and executes that state change if so.
     *
     * @param dataHolder The data holder that stores the player's animation runtime information.
     * @param update The result of the updateTicks() function, indicating whether a main update needs to
     *               happen
     */
    public abstract void checkStateChange(AnimationDataHolder dataHolder, boolean update);

    /**
     * This function is meant to handle any clicks in the inventory during the animation
     *
     * @param slot The slot that was clicked in the inventory
     */
    public void onClick(AnimationDataHolder dataHolder, int slot) {}

    public boolean startAnimation(Player p, Location l, CrateState cs, boolean requireKeyInHand, boolean force)
    {
        if (force || canExecuteFor(cs, CrateState.OPEN, p, requireKeyInHand))
        {
            AnimationDataHolder dataHolder = getAnimationType().newDataHolderInstance(p, l, this);
            runAnimation(dataHolder);
            playRequiredOpenActions(p, !requireKeyInHand, force);
            PlayerManager.get(getSc(), p).setCurrentAnimation(dataHolder);
            return true;
        }

        playFailToOpen(p, true, true);
        return false;
    }

    public void runAnimation(final AnimationDataHolder dataHolder)
    {
        if (!dataHolder.getCurrentState().equals(AnimationDataHolder.State.COMPLETED))
        {
            Bukkit.getScheduler().scheduleSyncDelayedTask(getSc(), new Runnable()
            {
                @Override
                public void run()
                {
                    dataHolder.setIndividualTicks(dataHolder.getIndividualTicks() + (int) BASE_SPEED);
                    dataHolder.setTotalTicks(dataHolder.getTotalTicks() + (int) BASE_SPEED);

                    boolean update = updateTicks(dataHolder);

                    checkStateChange(dataHolder, update);
                    tickAnimation(dataHolder, update);

                    runAnimation(dataHolder);
                }
            }, BASE_SPEED);
        }
        else
        {
            endAnimation(dataHolder);
        }
    }

    public boolean playerPassesKeyTest(Player p, boolean requireKeyInHand)
    {
        if(p.getItemInHand() == null)
        {
            return false;
        }

        CrateSettings settings = getCrate().getSettings();
        return !settings.isRequireKey() ||
                (requireKeyInHand ?
                        crate.getSettings().getKeyItemHandler().keyMatchesToStack(p.getItemInHand(), true) :
                        crate.getSettings().getKeyItemHandler().hasKeyInInventory(p)) ||
                PlayerManager.get(cc, p).getPdm().getVCCrateData(getCrate()).getKeys() > 0;
    }

    public boolean canExecuteFor(CrateState current, CrateState cs, Player p, boolean requireKeyInHand)
    {
        PlayerManager playerManager = PlayerManager.get(getSc(), p);
        boolean isOpenAction = current.equals(cs);
        boolean hasPropperOpeningTools = playerPassesKeyTest(p, requireKeyInHand) || !getCrate().getSettings().isRequireKey();
        boolean passesInventoryCheck = !playerManager.isInCrate() || playerManager.getOpenCrate().isMultiCrate();

        return isOpenAction && hasPropperOpeningTools && passesInventoryCheck;
    }

    public void playFailToOpen(Player p, boolean playMessage, boolean failOpen)
    {
        if (!(p == null))
        {
            if ((Boolean) SettingsValues.PUSHBACK.getValue(getSc()) && !getCrate().isMultiCrate())
            {
                p.setVelocity(p.getLocation().getDirection().multiply(-1));
            }
            if (playMessage)
            {
                if(failOpen)
                    Messages.FAIL_OPEN.msgSpecified(cc, p, new String[]{"%crate%"}, new String[]{crate.getDisplayName()});
                else
                    Messages.ALREADY_OPENING_CRATE.msgSpecified(cc, p);
            }
        }
    }

    public void playRequiredOpenActions(Player p, boolean fromInv, boolean force)
    {
        PlayerManager pm = PlayerManager.get(cc, p);

        if (!(pm.getOpenCrate() == null) && pm.getOpenCrate().isMultiCrate() && !getCrate().getSettings().getObtainType().equals(ObtainType.STATIC))
        {
            PlacedCrate pc = PlacedCrate.get(cc, pm.getLastOpenCrate());
            pc.delete();
        }

        pm.openCrate(this.getCrate());

        if (getCrate().getSettings().isRequireKey() && !force)
        {
            takeKeyFromPlayer(p, fromInv);
        }

        getCrate().getSettings().getActions().playAll(p, true);
    }

    public void takeKeyFromPlayer(Player p, boolean fromInv)
    {
        boolean prioritzePhysical = (boolean) cc.getSettings().getConfigValues().get("prioritize-physical-key");
        if (!takeKeyFromPlayer(p, fromInv, prioritzePhysical))
        {
            takeKeyFromPlayer(p, fromInv, !prioritzePhysical);
        }
    }

    public boolean takeKeyFromPlayer(Player p, boolean fromInv, boolean checkPhysical)
    {

        if (checkPhysical)
        {
            if (fromInv)
            {
                for (int i = 0; i < 36; i++)
                {
                    try
                    {
                        if (!(p.getInventory().getItem(i) == null))
                        {
                            ItemStack stack = p.getInventory().getItem(i);
                            if (getCrate().getSettings().getKeyItemHandler().keyMatchesToStack(stack, true))
                            {
                                if (stack.getAmount() == 1)
                                {
                                    p.getInventory().setItem(i, null);
                                }
                                else
                                {
                                    stack.setAmount(stack.getAmount() - 1);
                                }
                                return true;
                            }
                        }
                    }
                    catch (Exception exc)
                    {
                        exc.printStackTrace();
                    }
                }
            }
            else
            {
                ItemStack stack = p.getItemInHand();
                if (getCrate().getSettings().getKeyItemHandler().keyMatchesToStack(stack, true))
                {
                    if (p.getItemInHand().getAmount() == 1)
                    {
                        p.setItemInHand(null);
                    }
                    else
                    {
                        ItemStack st = p.getItemInHand();
                        st.setAmount(st.getAmount() - 1);
                        p.setItemInHand(st);
                    }
                    return true;
                }
            }
        }
        else
        {
            PlayerDataManager pdm = PlayerManager.get(cc, p).getPdm();
            if (pdm.getVCCrateData(crate).getKeys() > 0)
            {
                pdm.setVirtualCrateKeys(crate, pdm.getVCCrateData(crate).getKeys() - 1);
                return true;
            }
        }
        return false;
    }

    public void completeCrateRun(Player p, ArrayList<Reward> rewards, boolean overrideAutoClose, final PlacedCrate placedCrate)
    {
        PlayerManager pm = PlayerManager.get(getSc(), p);
        pm.setInRewardMenu(false);
        pm.setCurrentAnimation(null);

        new HistoryEvent(Utils.currentTimeParsed(), getCrate(), rewards, true)
                .addTo(PlayerManager.get(getSc(), p).getPdm());

        if (!getCrate().getSettings().isAutoClose() || overrideAutoClose)
        {
            pm.setWaitingForClose(rewards);
            return;
        }

        for (Reward r : rewards)
        {
            r.runCommands(p);
        }

        pm.closeCrate();
        p.closeInventory();

        // Handle the DYNAMIC crates
        if(getCrate().getSettings().getCrateType().isSpecialDynamicHandling() && !getCrate().getSettings().getObtainType().isStatic())
        {
            if(getCrate().getSettings().getCrateType().getCategory().equals(CrateAnimationType.Category.CHEST))
            {
                Bukkit.getScheduler().runTaskLater(cc, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        placedCrate.delete();
                        placedCrate.getL().getBlock().setType(Material.AIR);
                    }
                }, 20);
            }
            else
            {
                placedCrate.delete();
                placedCrate.getL().getBlock().setType(Material.AIR);
            }
        }
    }

    public double getRandomTickTime(double basedOff)
    {
        Random r = new Random();
        return basedOff + r.nextInt(3) - r.nextInt(3);
    }

    public StatusLogger getStatusLogger()
    {
        return crate.getSettings().getStatusLogger();
    }

    public Crate getCrate()
    {
        return crate;
    }

    public SpecializedCrates getSc()
    {
        return cc;
    }

    public FileHandler getFileHandler()
    {
        return fu;
    }

    public CrateAnimationType getAnimationType()
    {
        return animationType;
    }
}
