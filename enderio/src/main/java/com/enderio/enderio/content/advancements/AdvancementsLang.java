package com.enderio.enderio.content.advancements;

import com.enderio.enderio.EnderIO;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class AdvancementsLang {
    public static final MutableComponent PLACE_CAPACITOR_BANK_ADVANCEMENT_TITLE = advancements("place_capacitor_bank/title");
    public static final MutableComponent PLACE_CAPACITOR_BANK_ADVANCEMENT_DESCRIPTION = advancements("place_capacitor_bank/description");
    public static final MutableComponent MULTIBLOCK_CONNECTED_TEXTURES = hint("connected_textures/text");

    public static final Component USE_GLIDER_ADVANCEMENT_TITLE = advancements("use_glider/title");
    public static final Component USE_GLIDER_ADVANCEMENT_DESCRIPTION = advancements("use_glider/description");

    public static final Component RICH_ADVANCEMENT_TITLE = advancements("rich.title");
    public static final Component RICH_ADVANCEMENT_DESCRIPTION = advancements("rich/description");

    public static final Component RICHER_ADVANCEMENT_TITLE = advancements("richer/title");
    public static final Component RICHER_ADVANCEMENT_DESCRIPTION = advancements("richer/description");

    private static MutableComponent advancements(String path) {
        return create("advancements", path);
    }

    private static MutableComponent hint(String path) {
        return create("hint", path);
    }

    private static MutableComponent create(String type, String path) {
        return Component.translatable(Util.makeDescriptionId(type, EnderIO.rl(path)));
    }
}
