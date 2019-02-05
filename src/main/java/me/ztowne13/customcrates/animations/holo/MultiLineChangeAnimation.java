package me.ztowne13.customcrates.animations.holo;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.options.holograms.DynamicHologram;
import me.ztowne13.customcrates.utils.ChatUtils;

/**
 * Created by ztowne13 on 1/16/17.
 */
public class MultiLineChangeAnimation extends HoloAnimation
{
	String last = "";
	int count = 0;

	public MultiLineChangeAnimation(CustomCrates cc, DynamicHologram dh)
	{
		super(cc, dh);
	}

	@Override
	public void tick()
	{
		setIntTicks(getIntTicks() + 1);
		if(getIntTicks() == getCh().getSpeed())
		{
			setIntTicks(0);
			if(!getDh().getDisplayingRewardHologram())
			{
				update();
			}
		}
	}

	public void update()
	{
		update(false);
	}

	@Override
	public void update(boolean force)
	{
		if(count >= getCh().getPrefixes().size())
		{
			count = 0;
		}

		String s = getCh().getPrefixes().get(count);

		for(int i = 0; i < getCh().getLines().length; i++)
		{
			try
			{
				if(getCh().getLines()[i] == null)
				{
					break;
				}
				String currentLine = getCh().getLines()[i];
				currentLine = s + currentLine;
				if(!getLast().equals(s))
				{
					getDh().setLine(i, ChatUtils.toChatColor(currentLine));
				}
			}
			catch(Exception exc)
			{
				break;
			}
		}

		setLast(s);
		count++;
	}

	@Override
	public void stop()
	{

	}

	public String getLast()
	{
		return last;
	}

	public void setLast(String last)
	{
		this.last = last;
	}
}
