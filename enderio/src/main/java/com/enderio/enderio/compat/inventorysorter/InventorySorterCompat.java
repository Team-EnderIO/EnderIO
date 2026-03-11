package com.enderio.enderio.compat.inventorysorter;

import com.enderio.enderio.content.enchanter.EnchanterMenu;
import com.enderio.enderio.content.filters.fluid.FluidFilterSlot;
import com.enderio.enderio.content.filters.item.ItemFilterSlot;
import com.enderio.enderio.foundation.menu.GhostMachineSlot;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.foundation.menu.PreviewMachineSlot;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public class InventorySorterCompat {
    public static void init(IEventBus eventBus) {
        eventBus.addListener(InventorySorterCompat::sendIMC);
    }

    private static void sendIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("inventorysorter", "slotblacklist", ItemFilterSlot.class::getName);
        InterModComms.sendTo("inventorysorter", "slotblacklist", FluidFilterSlot.class::getName);
        InterModComms.sendTo("inventorysorter", "slotblacklist", MachineSlot.class::getName);
        InterModComms.sendTo("inventorysorter", "slotblacklist", GhostMachineSlot.class::getName);
        InterModComms.sendTo("inventorysorter", "slotblacklist", PreviewMachineSlot.class::getName);
        InterModComms.sendTo("inventorysorter", "slotblacklist",
            EnchanterMenu.EnchanterOutputMachineSlot.class::getName);
    }
}
