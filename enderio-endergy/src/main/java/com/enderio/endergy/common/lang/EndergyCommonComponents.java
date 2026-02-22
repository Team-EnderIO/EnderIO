package com.enderio.endergy.common.lang;

import com.enderio.enderio.EnderIO;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class EndergyCommonComponents {
    public static final MutableComponent CREATIVE_TAB_TITLE = create("itemGroup", "endergy");

    public static final MutableComponent TOTEMIC_CAPACITOR_TOOLTIP = create("tooltip", "totemic_capacitor/hint");

    private static MutableComponent gui(String path) {
        return create("gui", path);
    }

    private static MutableComponent tooltip(String path) {
        return create("tooltip", path);
    }

    private static MutableComponent message(String path) {
        return create("message", path);
    }

    private static MutableComponent create(String type, String path) {
        return Component.translatable(Util.makeDescriptionId(type, EnderIO.rl(path)));
    }
}
