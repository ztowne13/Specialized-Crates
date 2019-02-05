package me.ztowne13.customcrates.players;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.gui.ingame.IGCMenu;
import me.ztowne13.customcrates.players.data.*;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerManager 
{
	static HashMap<UUID,PlayerManager> pManagers = new HashMap<UUID,PlayerManager>();
	
	CustomCrates cc;
	
	Player p;
	
	DataHandler dh;
	PlayerDataManager pdm;

	//CrateHead openCrate = null;
	PlacedCrate lastOpenedPlacedCrate = null;
	Crate openCrate = null;
	Location lastOpenCrate = null;
	private IGCMenu openMenu = null, lastOpenMenu = null;
	boolean inRewardMenu = false, canClose = true, deleteCrate = false, useVirtualCrate = false;

	ArrayList<Reward> waitingForClose = null;

	long cmdCooldown = 0;
	String lastCooldown = "NONE";
	
	public PlayerManager(CustomCrates cc, Player p)
	{
		this.cc = cc;
		this.p = p;
		this.dh = getSpecifiedDataHandler();
		this.pdm = new PlayerDataManager(this);

		getPdm().setDh(getDh());
		getPdm().loadAllInformation();
		getpManagers().put(p.getUniqueId(), this);
	}

	public void remove()
	{
		getpManagers().remove(getP().getUniqueId());
	}
	
	public DataHandler getSpecifiedDataHandler()
	{
		try
		{
			StorageType st = StorageType.valueOf(ChatUtils.stripFromWhitespace(SettingsValues.STORE_DATA.getValue(getCc()).toString().toUpperCase()));
			switch(st)
			{
			case MYSQL:
				Utils.addToInfoLog(cc, "Storage Type", "MYSQL");
				return new SQLDataHandler(this);
			case FLATFILE:
				Utils.addToInfoLog(cc, "Storage Type", "FLATFILE");
				return new FlatFileDataHandler(this);
			case PLAYERFILES:
				Utils.addToInfoLog(cc, "Storage Type", "PLAYERFILES");
				return new IndividualFileDataHandler(this);
			default:
				ChatUtils.log(new String[]{"store-data value in the config.YML is not a valid storage type.", "  It must be: MYSQL, FLATFILE, PLAYERFILES"});
				Utils.addToInfoLog(cc, "StorageType", "FLATFILE");
				return new FlatFileDataHandler(this);
			}
		}
		catch(Exception exc)
		{
			ChatUtils.log(new String[]{"store-data value in the config.YML is not a valid storage type.", "  It must be: MYSQL, FLATFILE, PLAYERFILES"});
		}
		return null;
	}

	public static PlayerManager get(CustomCrates cc, Player p)
	{
		return getpManagers().containsKey(p.getUniqueId()) ? getpManagers().get(p.getUniqueId()) : new PlayerManager(cc, p);
	}

	public static void clearLoaded()
	{
		getpManagers().clear();
		setpManagers(new HashMap<UUID,PlayerManager>());
	}

	public boolean isInCrate()
	{
		return openCrate != null;
	}
	
	public boolean isDeleteCrate()
	{
		return deleteCrate;
	}
	
	public void setDeleteCrate(boolean b)
	{
		this.deleteCrate = b;
	}
	
	public void openCrate(Crate ch)
	{
		openCrate = ch;
	}
	
	public void closeCrate()
	{
		openCrate = null;
		useVirtualCrate = false;
	}
	
	public Crate getOpenCrate()
	{
		return openCrate;
	}
	
	public boolean isInRewardMenu() 
	{
		return inRewardMenu;
	}

	public void setInRewardMenu(boolean inRewardMenu) 
	{
		this.inRewardMenu = inRewardMenu;
	}

	public boolean isCanClose() 
	{
		return canClose;
	}

	public void setCanClose(boolean canClose)
	{
		this.canClose = canClose;
	}

	public Player getP() 
	{
		return p;
	}

	public void setP(Player p) 
	{
		this.p = p;
	}

	public CustomCrates getCc()
	{
		return cc;
	}

	public void setCc(CustomCrates cc) 
	{
		this.cc = cc;
	}

	public DataHandler getDh()
	{
		return dh;
	}

	public void setDh(DataHandler dh)
	{
		this.dh = dh;
	}

	public boolean isWaitingForClose()
	{
		return waitingForClose != null;
	}

	public void setWaitingForClose(ArrayList<Reward> waitingForClose)
	{
		this.waitingForClose = waitingForClose;
	}
	
	public ArrayList<Reward> getWaitingForClose()
	{
		return waitingForClose;
	}

	public PlayerDataManager getPdm() {
		return pdm;
	}

	public void setPdm(PlayerDataManager pdm) {
		this.pdm = pdm;
	}

	public long getCmdCooldown() {
		return cmdCooldown;
	}

	public void setCmdCooldown(long cmdCooldown) {
		this.cmdCooldown = cmdCooldown;
	}

	public String getLastCooldown() {
		return lastCooldown;
	}

	public void setLastCooldown(String lastCooldown) {
		this.lastCooldown = lastCooldown;
	}

	public static HashMap<UUID, PlayerManager> getpManagers()
	{
		return pManagers;
	}

	public static void setpManagers(HashMap<UUID, PlayerManager> pManagers)
	{
		PlayerManager.pManagers = pManagers;
	}

	public IGCMenu getOpenMenu()
	{
		return openMenu;
	}

	public void setOpenMenu(IGCMenu openMenu)
	{
		this.openMenu = openMenu;
		if(!(openMenu == null))
		{
			this.lastOpenMenu = openMenu;
		}
	}

	public boolean isInOpenMenu()
	{
		return !(this.openMenu == null);
	}

	public IGCMenu getLastOpenMenu()
	{
		return lastOpenMenu;
	}

	public void setLastOpenMenu(IGCMenu lastOpenMenu)
	{
		this.lastOpenMenu = lastOpenMenu;
	}

	public Location getLastOpenCrate()
	{
		return lastOpenCrate;
	}

	public void setLastOpenCrate(Location lastOpenCrate)
	{
		this.lastOpenCrate = lastOpenCrate;
	}

	public void setOpenCrate(Crate openCrate)
	{
		this.openCrate = openCrate;
	}

	public boolean isUseVirtualCrate()
	{
		return useVirtualCrate;
	}

	public void setUseVirtualCrate(boolean useVirtualCrate)
	{
		this.useVirtualCrate = useVirtualCrate;
	}

	public void setLastOpenedPlacedCrate(PlacedCrate lastOpenedPlacedCrate)
	{
		this.lastOpenedPlacedCrate = lastOpenedPlacedCrate;
	}

	public PlacedCrate getLastOpenedPlacedCrate()
	{
		return lastOpenedPlacedCrate;
	}
}
