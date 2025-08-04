package com.enderio.conduits.integration.ftb_ultimine;

import dev.ftb.mods.ftbultimine.api.blockbreaking.RegisterBlockBreakHandlerEvent;
import dev.ftb.mods.ftbultimine.api.blockselection.RegisterBlockSelectionHandlerEvent;

public class FTBUltimineCompat {
    // TODO: Temporary until project merges and we come up with a nice way of bundling these compat "modules" together
    public static void init() {
        RegisterBlockSelectionHandlerEvent.REGISTER.register(registry -> registry.registerHandler(ConduitBlockSelectionHandler.INSTANCE));
        RegisterBlockBreakHandlerEvent.REGISTER.register(registry -> registry.registerHandler(ConduitBlockBreakHandler.INSTANCE));
    }
}
