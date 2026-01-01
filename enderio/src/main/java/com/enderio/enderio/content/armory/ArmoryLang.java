package com.enderio.enderio.content.armory;

import com.enderio.enderio.EnderIO;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Util;

public class ArmoryLang {

    public static final MutableComponent ENDER_HEAD_DROP_CHANCE = tooltip("ender_head_chance");
    public static final MutableComponent DURABILITY_AMOUNT = tooltip("durability/amount");

    private static MutableComponent tooltip(String path) {
        return create("tooltip", path);
    }

    private static MutableComponent create(String type, String path) {
        return Component.translatable(Util.makeDescriptionId(type, EnderIO.id("armory/" + path)));
    }
}
