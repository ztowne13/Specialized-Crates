package me.ztowne13.customcrates.utils;

/**
 * Created by ztowne13 on 3/11/16.
 */
public class InventoryUtils
{
	public static int getRowsFor(int alreadyUsedPerSlot, int needSlotSpaces)
	{
		/*int rows = ((needSlotSpaces / 9) + (needSlotSpaces % 9 == 0 ? 0 : 1));
		int totalExtraSlots = rows * alreadyUsedPerSlot;
		int totalRows = (((totalExtraSlots + (rows * 9)) / 9) + 1) * 9;*/
		return 9 * (needSlotSpaces / (9 - alreadyUsedPerSlot)  + (needSlotSpaces % (9 - alreadyUsedPerSlot) == 0 ? 0 : 1));
	}
}
