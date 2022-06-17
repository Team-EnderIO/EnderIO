package com.enderio.base.common.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TooltipUtil {

    public static Component style(MutableComponent component) {
        return component.withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);
    }

    public static MutableComponent withArgs(MutableComponent component, Object... args) {
        // TODO: component.getString() no longer includes placeholder markers.
        return Component.translatable(component.getString(), args);
    }

    public static Component styledWithArgs(MutableComponent component, Object... args) {
        return style(Component.translatable(component.getString(), args));
    }
}
