package me.ztowne13.customcrates.interfaces.items;

import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;

public interface SaveableItem extends EditableItem {
    void saveItem(FileHandler fileHandler, String prefix, boolean allowUnnamedItems);

    boolean loadItem(FileHandler fileHandler, String prefix, StatusLogger statusLogger, StatusLoggerEvent itemFailure,
                     StatusLoggerEvent improperEnchant, StatusLoggerEvent improperPotion,
                     StatusLoggerEvent improperGlow, StatusLoggerEvent improperAmount, StatusLoggerEvent invalidItemFlag);
}
