package com.enderio.enderio.content.glass;

import com.enderio.enderio.EnderIO;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class GlassLang {
    public static final MutableComponent EMITS_LIGHT = tooltip("emits_light");
    public static final MutableComponent BLOCKS_LIGHT = tooltip("blocks_light");

    private static MutableComponent tooltip(String path) {
        return create("tooltip", path);
    }

    private static MutableComponent create(String type, String path) {
        return Component.translatable(Util.makeDescriptionId(type, EnderIO.rl("glass/" + path)));
    }
}
