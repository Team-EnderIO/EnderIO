package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.tterrag.registrate.Registrate;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ConduitLang {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final MutableComponent FLUID_RATE_TOOLTIP = REGISTRATE.addLang("tooltip", EnderIO.loc("conduit.fluid.rate"), "Transfer Rate %s mB/t");
    public static final Component MULTI_FLUID_TOOLTIP = REGISTRATE.addLang("tooltip", EnderIO.loc("conduit.fluid.multi"),
        "Allows multiple fluids to be transported on the same line");

    public static final Component CONDUIT_INSERT = REGISTRATE.addLang("gui", EnderIO.loc("conduit.insert"), "Insert");
    public static final Component CONDUIT_EXTRACT = REGISTRATE.addLang("gui", EnderIO.loc("conduit.extract"), "Extract");

    public static void register() {
    }
}
