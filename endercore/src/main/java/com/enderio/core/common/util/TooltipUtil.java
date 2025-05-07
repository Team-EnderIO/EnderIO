package com.enderio.core.common.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

public class TooltipUtil {

    /**
     * Style a component italic and gray
     */
    public static MutableComponent style(MutableComponent component) {
        return component.withStyle(ChatFormatting.AQUA);
    }

    /**
     * Append arguments to a translatable component.
     * If you don't pass a translatable component, it will not be modified.
     */
    public static MutableComponent withArgs(MutableComponent component, Object... args) {
        // Translate with args.
        if (component.getContents() instanceof TranslatableContents translatableContents) {
            return Component.translatable(translatableContents.getKey(), args);
        }

        // Fallback.
        return component;
    }

    /**
     * Style component and fill its args
     */
    public static Component styledWithArgs(MutableComponent component, Object... args) {
        return style(withArgs(component, args));
    }

    /**
     * Translate, style and fill args
     */
    public static Component styledWithArgs(ResourceLocation key, Object... args) {
        return style(Component.translatable(key.toLanguageKey(), args));
    }
}
