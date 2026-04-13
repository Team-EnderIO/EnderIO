package com.enderio.enderio.compat.ftb_ultimine;

import dev.ftb.mods.ftbultimine.api.neoforge.FTBUltimineEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public class FTBUltimineCompat {
    public static void init(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(FTBUltimineEvent.RegisterBlockSelectionHandler.class, FTBUltimineCompat::registerBlockSelectionHandler);
        NeoForge.EVENT_BUS.addListener(FTBUltimineEvent.RegisterBlockBreakHandler.class, FTBUltimineCompat::registerBlockBreakHandler);
    }

    private static void registerBlockSelectionHandler(FTBUltimineEvent.RegisterBlockSelectionHandler event) {
        event.getEventData().consumer().accept(ConduitBlockSelectionHandler.INSTANCE);
    }

    private static void registerBlockBreakHandler(FTBUltimineEvent.RegisterBlockBreakHandler event) {
        event.getEventData().consumer().accept(ConduitBlockBreakHandler.INSTANCE);
    }
}
