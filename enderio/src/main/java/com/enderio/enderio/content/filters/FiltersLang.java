package com.enderio.enderio.content.filters;

import com.enderio.enderio.EnderIO;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class FiltersLang {

    public static final MutableComponent CONFIGURED = tooltip("configured");
    public static final MutableComponent UNCONFIGURED_HINT = tooltip("unconfigured_hint").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
    public static final MutableComponent FILTER_CONFIG_NOT_ALLOWED_COMPONENT_MATCH = tooltip("not_allowed_component_match").withStyle(ChatFormatting.RED);

    public static final Component FILTER_ALLOW_LIST = gui("allow_list");
    public static final Component FILTER_DENY_LIST = gui("deny_list");

    public static final Component FILTER_MATCH_COMPONENTS = gui("match_components");
    public static final Component FILTER_IGNORE_COMPONENTS = gui("ignore_components");
    public static final Component DAMAGE_FILTER_MODE = gui("damage_filter");

    // TODO: Consider making these common keys
    public static final MutableComponent GUI_FILTER = gui("filter");
    public static final MutableComponent GUI_CONFIRM = gui("confirm");

    private static MutableComponent gui(String path) {
        return create("gui", path);
    }

    private static MutableComponent tooltip(String path) {
        return create("tooltip", path);
    }

    private static MutableComponent create(String type, String path) {
        return Component.translatable(Util.makeDescriptionId(type, EnderIO.rl("filter/" + path)));
    }
}
