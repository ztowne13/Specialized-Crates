package me.ztowne13.customcrates.interfaces;

/**
 * Created by ztowne13 on 3/11/16.
 */
public class InventoryUtils {
    private InventoryUtils() {
        // EMPTY
    }

    public static int getRowsFor(int alreadyUserPerSlot, int needSlotSpaces) {
        return getRowsFor(alreadyUserPerSlot, needSlotSpaces, 1);
    }

    public static int getRowsFor(int alreadyUsedPerSlot, int needSlotSpaces, int page) {
        int itemsPerRow = 9 - alreadyUsedPerSlot;

        int fullInv = itemsPerRow * 6;
        int toSkip = (fullInv * (page - 1));
        needSlotSpaces -= toSkip;

        int extraRow = (needSlotSpaces % itemsPerRow == 0 ? 0 : 1);
        int fullRows = needSlotSpaces / itemsPerRow;
        int rows = fullRows + extraRow;

        return 9 * rows;
    }
}
