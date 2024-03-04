package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.regilite.data.RegiliteDataProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class ConduitLang {

    public static final Component CONDUIT_INSERT = addTranslation("gui", EnderIO.loc("conduit.insert"), "Insert");
    public static final Component CONDUIT_EXTRACT = addTranslation("gui", EnderIO.loc("conduit.extract"), "Extract");

    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
        return EnderIO.getRegilite().addTranslation(prefix, id, translation);
    }

    private static MutableComponent addTranslation(String prefix, ResourceLocation path, String name, String translation) {
        return EnderIO.getRegilite().addTranslation(prefix, new ResourceLocation(path.getNamespace(), path.getPath() + "/" + name), translation);
    }

    public static void register() {
    }
}
