package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.tterrag.registrate.Registrate;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class ConduitLang {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final Component CONDUIT_INSERT = addTranslation("gui", EnderIO.loc("conduit.insert"), "Insert");
    public static final Component CONDUIT_EXTRACT = addTranslation("gui", EnderIO.loc("conduit.extract"), "Extract");

    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
        return EnderIO.registrate().addLang(prefix, id, translation);
    }

    private static MutableComponent addTranslation(String prefix, ResourceLocation path, String name, String translation) {
        return EnderIO.registrate().addLang(prefix, new ResourceLocation(path.getNamespace(), path.getPath() + "/" + name), translation);
    }

    public static void register() {
    }
}
