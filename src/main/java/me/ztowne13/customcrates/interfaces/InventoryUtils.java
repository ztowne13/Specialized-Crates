package me.ztowne13.customcrates.interfaces;

/**
 * Created by ztowne13 on 3/11/16.
 */
public class InventoryUtils
{
    public static int getRowsFor(int alreadyUsedPerSlot, int needSlotSpaces)
    {
        return 9 * (needSlotSpaces / (9 - alreadyUsedPerSlot) + (needSlotSpaces % (9 - alreadyUsedPerSlot) == 0 ? 0 : 1));
    }
}
