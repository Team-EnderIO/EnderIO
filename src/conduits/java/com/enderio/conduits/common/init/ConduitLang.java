package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.tterrag.registrate.Registrate;
import net.minecraft.network.chat.Component;

public class ConduitLang {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final Component CONDUIT_INSERT = REGISTRATE.addLang("gui", EnderIO.loc("conduit.insert"), "Insert");
    public static final Component CONDUIT_EXTRACT = REGISTRATE.addLang("gui", EnderIO.loc("conduit.extract"), "Extract");

    public static void register() {
    }
}
