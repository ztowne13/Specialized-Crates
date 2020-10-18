package me.ztowne13.customcrates.interfaces.igc.fileconfigs.rewards;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.CReward;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.InventoryUtils;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.buttons.IGCButtonType;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 3/21/16.
 */
public class IGCMenuRewards extends IGCMenu {
    int page;

    public IGCMenuRewards(SpecializedCrates cc, Player p, IGCMenu lastMenu, int page) {
        super(cc, p, lastMenu, "&7&l> &6&lRewards.YML PG" + page,
                new IGCButtonType[]{
                        IGCButtonType.REWARD_FILTER
                },
                new int[]{
                        8
                });
        this.page = page;
    }

    @Override
    public void openMenu() {
        boolean newValues = false;

        CReward.loadAll(getCc(), getP());

        int slots = Math.min(CReward.getAllRewards().size() - ((page - 1) * 28), 28);

        slots = InventoryUtils.getRowsFor(2, slots) + 9;


        setInventoryName("&7&l> &6&lRewards.YML PG" + page);
        InventoryBuilder ib = createDefault(slots, 27);

        ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());
        ib.setItem(9, IGCDefaultItems.RELOAD_BUTTON.getIb());
        ib.setItem(ib.getInv().getSize() - 9, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(4,
                new ItemBuilder(XMaterial.PAPER).setDisplayName("&aCreate a new Reward").setLore("&7Click me to create a new")
                        .addLore("&7reward."));

        ItemBuilder dragAndDrop = new ItemBuilder(XMaterial.CHEST_MINECART);
        dragAndDrop.setDisplayName("&aDrag and Drop Rewards");
        dragAndDrop.addLore("").addAutomaticLore("&f", 30,
                "Create up to 54 rewards at once! Add display names, lores, enchants, potion effects, amounts, and nbt-tags beforehand and have it all save at once!");
        ib.setItem(5, dragAndDrop);

        int i = 10;
        int toSkip = ((page - 1) * 28);
        int skipped = 0;
        int displayedRewards = 0;

        for (Reward r : CReward.getAllRewardsSorted(getCc(), (CReward.RewardSortType) getButtons()[0].getValue()).values()) {
            String rName = r.getRewardName();

            if (toSkip > skipped || displayedRewards >= 28) {
                skipped++;
                continue;
            }

            if (i % 9 == 8) {
                i += 2;
            }

            r.checkIsNeedMoreConfig();
            ItemBuilder newR;

            if (r.isNeedsMoreConfig())
                newR = new ItemBuilder(XMaterial.BARRIER).setDisplayName("&4&l" + rName)
                        .setLore("&cThis reward isn't fully configured,").addLore("&cplease fix it and reload the plugin.");
            else
                newR = new ItemBuilder(r.getDisplayBuilder().getStack()).setDisplayName("&a" + rName);

            newR.addLore("").addLore("&7Used by crates:").addLore("");
            for (String s : r.delete(false).replace("[", "").replace("]", "").split(", ")) {
                newR.addLore("&7- &f" + s);
            }

            newR.addLore("").addLore("&eClick to edit.");

            ib.setItem(i, newR);
            i++;
            displayedRewards++;
        }

        if (page != 1) {
            ib.setItem(2, new ItemBuilder(XMaterial.ARROW).setDisplayName("&aGo back a page"));
        }

        if ((CReward.getAllRewards().size() / 28) + (CReward.getAllRewards().size() % 28 == 0 ? 0 : 1) != page) {
            ib.setItem(6, new ItemBuilder(XMaterial.ARROW).setDisplayName("&aGo forward a page"));
        }

        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot) {
        if (slot == 0) {
            //getP().closeInventory();
            getCc().getRewardsFile().save();
            ChatUtils.msgSuccess(getP(), "Saved the Rewards.YML file.");
            //getCc().reload();
        } else if (slot == 2 && XMaterial.ARROW.isSimilar(getIb().getInv().getItem(slot))) {
            page--;
            open();
        } else if (slot == 6 && XMaterial.ARROW.isSimilar(getIb().getInv().getItem(slot))) {
            page++;
            open();
        } else if (slot == 9) {
            reload();
        } else if (slot == getIb().getInv().getSize() - 9) {
            up();
        } else if (slot == 5) {
            new IGCDragAndDrop(getCc(), getP(), this).open();
        } else if (slot == 4) {
            new InputMenu(getCc(), getP(), "rewardName", "null",
                    "No spaces allowed. No duplicate names. &7&oNote: These 'reward names' will never be seen by your player: they are just an 'identifier'.",
                    String.class, this, true);
        } else if (getIb().getInv().getItem(slot) != null) {
            String rName = ChatUtils.removeColor(getIb().getInv().getItem(slot).getItemMeta().getDisplayName());
            new IGCMenuReward(getCc(), getP(), this, rName).open();
        }
    }

    @Override
    public boolean handleInput(String value, String input) {
        if (value.equalsIgnoreCase("rewardName")) {
            if (!input.contains(" ")) {

                if (!CReward.rewardNameExists(getCc(), input)) {
                    new IGCMenuReward(getCc(), getP(), this, input).open();
                } else {
                    ChatUtils.msgError(getP(),
                            "This name already exists. &7&oNote: These 'reward names' will never be seen by your player: they are just an identifier.");
                }
            } else {
                ChatUtils.msgError(getP(),
                        "Your reward name cannot have a space. &7&oNote: These 'reward names' will never be seen by your player: they are just an identifier.");
            }
        }
        return false;
    }
}
