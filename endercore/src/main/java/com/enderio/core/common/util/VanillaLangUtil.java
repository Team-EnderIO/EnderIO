package com.enderio.core.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;

public class VanillaLangUtil {
    public static MutableComponent getColorComponent(DyeColor color) {
        return Component.translatable("color.minecraft." + color.getName());
    }
}
