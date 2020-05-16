package me.ztowne13.customcrates.interfaces.igc.fileconfigs;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.RewardDisplayType;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.SimpleRewardDisplayer;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.crates.types.display.MaterialPlaceholder;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.crates.IGCCratesMain;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.items.SaveableItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

public class IGCMenuCrateOrMulticrate extends IGCMenu
{
    boolean multicrate = false;

    public IGCMenuCrateOrMulticrate(SpecializedCrates specializedCrates, Player p, IGCMenu lastMenu)
    {
        super(specializedCrates, p, lastMenu, "&7&l> &6&lCrates or Multicrate ");
    }

    @Override
    public void openMenu()
    {
        InventoryBuilder ib = createDefault(9);
        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());

        ItemBuilder normalCrate = new ItemBuilder(DynamicMaterial.BOOK, 1);
        normalCrate.setDisplayName("&aClassic Crate");
        normalCrate.addLore("")
                .addAutomaticLore("&f", 30, "This is your normal or average crate that you're probably looking for!");

        ItemBuilder multiCrate = new ItemBuilder(DynamicMaterial.BOOKSHELF, 1);
        multiCrate.setDisplayName("&aMulti Crate");
        multiCrate.addLore("")
                .addAutomaticLore("&f", 30, "This is a crate that is an inventory and has multiple crates in it!");

        getIb().setItem(3, normalCrate);
        getIb().setItem(5, multiCrate);

        getIb().open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot)
    {
        if (slot == 0)
            up();
        else if (slot == 3)
        {
            new InputMenu(getCc(), getP(), "crate name", "null", "Name the crate whatever you want.", String.class, this,
                    true);
            multicrate = false;
        }
        else if (slot == 5)
        {
            new InputMenu(getCc(), getP(), "crate name", "null", "Name the multicrate whatever you want.", String.class,
                    this, true);
            multicrate = true;
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        if (value.equalsIgnoreCase("crate name"))
        {
            if (!Crate.crateAlreadyExist(input))
            {
                if (!input.contains(" "))
                {
                    Crate newCrate = new Crate(getCc(), input, true, multicrate);
                    CrateSettings cs = newCrate.getSettings();
                    cs.setObtainType(ObtainType.STATIC);
                    cs.setPlaceholder(new MaterialPlaceholder(getCc()));

                    if(!multicrate)
                    {
                        cs.setCrateType(CrateAnimationType.INV_ROULETTE);
                        cs.setRequireKey(true);
                        cs.setRewardDisplayType(RewardDisplayType.IN_ORDER);
                        cs.setDisplayer(new SimpleRewardDisplayer(newCrate));
                    }

                    SaveableItemBuilder builder = new SaveableItemBuilder(DynamicMaterial.CHEST, 1);
                    builder.setDisplayName(input);

                    cs.getCrateItemHandler().setItem(builder);

                    newCrate.setEnabled(true);
                    newCrate.setCanBeEnabled(false);

                    if (!multicrate && !CRewards.getAllRewards().isEmpty())
                    {
                        cs.getRewards().addReward(CRewards.getAllRewards().values().iterator().next().getRewardName());
                    }

                    cs.saveAll();

                    new IGCCratesMain(getCc(), getP(), this, newCrate).open();
                    ChatUtils.msgSuccess(getP(), "Created a new crate with the name " + input);
                    //	new InputMenu(getCc(), getP(), "crate obtain method", "null", "Available obtain methods: " + Arrays.toString(ObtainType.values()), String.class, this);
                }
                else
                {
                    ChatUtils.msgError(getP(), "Crate names cannot have spaces in their names.");
                }
            }
            else
            {
                ChatUtils.msgError(getP(), "This crate name already exists!");
            }
        }
        return false;
    }
}
