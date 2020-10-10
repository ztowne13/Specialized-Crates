package me.ztowne13.customcrates.crates.types.animations;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.api.CrateOpenEvent;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.players.data.events.HistoryEvent;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public abstract class CrateAnimation {
    public static final long BASE_SPEED = 1;
    protected SpecializedCrates cc;
    protected Crate crate;
    protected FileHandler fu;
    protected String prefix;
    CrateAnimationType animationType;
    Random r = new Random();

    public CrateAnimation(Crate crate, CrateAnimationType animationType) {
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
     * @param update     Whether or not this is an update tick
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
     * opposed to just the default BASE_SPEED tick updates.
     */
    public abstract boolean updateTicks(AnimationDataHolder dataHolder);

    /**
     * This function checks if there needs to be a state changed, and executes that state change if so.
     *
     * @param dataHolder The data holder that stores the player's animation runtime information.
     * @param update     The result of the updateTicks() function, indicating whether a main update needs to
     *                   happen
     */
    public abstract void checkStateChange(AnimationDataHolder dataHolder, boolean update);

    /**
     * This function is meant to handle any clicks in the inventory during the animation
     *
     * @param slot The slot that was clicked in the inventory
     */
    public void handleClick(AnimationDataHolder dataHolder, int slot) {
    }

    /**
     * This function is meant to handle any keyboard key presses during the animation
     *
     * @param type The type of key that was pressed.
     */
    public void handleKeyPress(AnimationDataHolder dataHolder, KeyType type) {
        dataHolder.getClickedKeys().add(type);
    }

    public boolean startAnimation(Player p, Location l, boolean requireKeyInHand, boolean force) {
        if (force || canExecuteFor(p, requireKeyInHand)) {
            AnimationDataHolder dataHolder = getAnimationType().newDataHolderInstance(p, l, this);
            timer(dataHolder);
            playRequiredOpenActions(p, !requireKeyInHand, force);
            PlayerManager.get(getSc(), p).setCurrentAnimation(dataHolder);
            return true;
        }

        playFailToOpen(p, true, true);
        return false;
    }

    public void runAnimation(final AnimationDataHolder dataHolder) {
        long startTime = System.nanoTime();

        dataHolder.setIndividualTicks(dataHolder.getIndividualTicks() + (int) BASE_SPEED);
        dataHolder.setTotalTicks(dataHolder.getTotalTicks() + (int) BASE_SPEED);

        boolean update = updateTicks(dataHolder);

        checkStateChange(dataHolder, update);
        tickAnimation(dataHolder, update);

        dataHolder.getClickedKeys().clear();

        dataHolder.updateTickTime(startTime);
    }

    public void finishAnimation(Player p, List<Reward> rewards, boolean overrideAutoClose,
                                final PlacedCrate placedCrate) {
        PlayerManager pm = PlayerManager.get(getSc(), p);
        pm.setInRewardMenu(false);
        pm.setCurrentAnimation(null);

        new HistoryEvent(Utils.currentTimeParsed(), getCrate(), rewards, true)
                .addTo(PlayerManager.get(getSc(), p).getPdm());

        for (Reward r : rewards) {
            r.giveRewardToPlayer(p);
        }


        CrateOpenEvent crateOpenEvent = new CrateOpenEvent(p, rewards, crate, 1);
        Bukkit.getPluginManager().callEvent(crateOpenEvent);

        pm.closeCrate();
        p.closeInventory();

        // Handle the DYNAMIC crates
        if (getCrate().getSettings().getCrateType().isSpecialDynamicHandling() &&
                !getCrate().getSettings().getObtainType().isStatic()) {
            if (getCrate().getSettings().getCrateType().getCategory().equals(CrateAnimationType.Category.CHEST)) {
                Bukkit.getScheduler().runTaskLater(cc, () -> {
                    placedCrate.delete();
                    placedCrate.getL().getBlock().setType(Material.AIR);
                }, 20);
            } else {
                placedCrate.delete();
                placedCrate.getL().getBlock().setType(Material.AIR);
            }
        }
    }

    public void timer(final AnimationDataHolder dataHolder) {
        if (!dataHolder.getCurrentState().equals(AnimationDataHolder.State.COMPLETED) ||
                (!dataHolder.getCrateAnimation().getCrate().getSettings().isAutoClose() && !dataHolder.isFastTrack())) {
            runAnimation(dataHolder);

            if (dataHolder.isFastTrack()) {
                timer(dataHolder);
                return;
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(getSc(), () -> timer(dataHolder), BASE_SPEED);
        } else {
            endAnimation(dataHolder);
        }
    }

    public boolean canExecuteFor(Player p, boolean requireKeyInHand) {
        PlayerManager playerManager = PlayerManager.get(getSc(), p);
        CrateSettings crateSettings = getCrate().getSettings();

        boolean hasPropperOpeningTools =
                crateSettings.getKeyItemHandler().playerPassesKeyTest(p, requireKeyInHand) || !crateSettings.isRequireKey();
        boolean passesInventoryCheck = !playerManager.isInCrate() || playerManager.getOpenCrate().isMultiCrate();

        return hasPropperOpeningTools && passesInventoryCheck;
    }

    public void playFailToOpen(Player p, boolean playMessage, boolean failOpen) {
        if (p != null) {
            if ((Boolean) SettingsValue.PUSHBACK.getValue(getSc()) && !getCrate().isMultiCrate()) {
                p.setVelocity(p.getLocation().getDirection().multiply(-1));
            }
            if (playMessage) {
                if (failOpen)
                    Messages.FAIL_OPEN.msgSpecified(cc, p, new String[]{"%crate%", "%key%"},
                            new String[]{crate.getDisplayName(),
                                    crate.getSettings().getKeyItemHandler().getItem().getDisplayName(false)});
                else
                    Messages.ALREADY_OPENING_CRATE.msgSpecified(cc, p);
            }
        }
    }

    public void playRequiredOpenActions(Player p, boolean fromInv, boolean force) {
        PlayerManager pm = PlayerManager.get(cc, p);
        CrateSettings crateSettings = getCrate().getSettings();

        if (pm.getOpenCrate() != null && pm.getOpenCrate().isMultiCrate() &&
                !crateSettings.getObtainType().equals(ObtainType.STATIC)) {
            PlacedCrate pc = PlacedCrate.get(cc, pm.getLastOpenCrate());
            pc.delete();
        }

        pm.openCrate(this.getCrate());

        if (crateSettings.isRequireKey() && !force) {
            crateSettings.getKeyItemHandler().takeKeyFromPlayer(p, fromInv);
        }

        crateSettings.getActions().playAll(p, true);
    }

    public double getRandomTickTime(double basedOff) {
        return basedOff + r.nextInt(3) - r.nextInt(3);
    }

    public StatusLogger getStatusLogger() {
        return crate.getSettings().getStatusLogger();
    }

    public Crate getCrate() {
        return crate;
    }

    public SpecializedCrates getSc() {
        return cc;
    }

    public FileHandler getFileHandler() {
        return fu;
    }

    public CrateAnimationType getAnimationType() {
        return animationType;
    }

    public enum KeyType {
        ESC
    }
}
