package org.example1.dfPubBin.gui;

import org.example1.dfPubBin.data.GarbageType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * 用于标识这是本插件的 GUI
 */
public class GuiHolder implements InventoryHolder {

    private final GarbageType type;
    private final int pageIndex;

    public GuiHolder(GarbageType type, int pageIndex) {
        this.type = type;
        this.pageIndex = pageIndex;
    }

    public GarbageType getType() {
        return type;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
