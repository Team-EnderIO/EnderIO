package com.enderio.core.common.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

public class TooltipUtil {

    public static Component style(MutableComponent component) {
        return component.withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);
    }

    public static MutableComponent withArgs(MutableComponent component, Object... args) {
        // Translate with args.
        if (component.getContents() instanceof TranslatableContents translatableContents) {
            return Component.translatable(translatableContents.getKey(), args);
        }

        // Fallback.
        return component;
    }

    public static Component styledWithArgs(MutableComponent component, Object... args) {
        return style(withArgs(component, args));
    }
}
