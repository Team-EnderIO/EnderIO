package com.enderio.core.common.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class TooltipUtil {
    public static Component style(MutableComponent component) {
        return component.withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);
    }

    public static TranslatableComponent withArgs(TranslatableComponent component, Object... args) {
        return new TranslatableComponent(component.getKey(), args);
    }
}
