package com.enderio.enderio.compat.jei;

import com.enderio.enderio.EnderIO;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Util;

public class JEILang {

    public static final MutableComponent FIRE_CRAFTING_TITLE = create("fire_crafting/title");
    public static final MutableComponent FIRE_CRAFTING_VALID_BLOCKS = create("fire_crafting/valid_blocks");
    public static final MutableComponent FIRE_CRAFTING_VALID_DIMENSIONS = create("fire_crafting/valid_dimensions");
    public static final MutableComponent FIRE_CRAFTING_CHANCE = create("fire_crafting/chance");
    public static final MutableComponent FIRE_CRAFTING_DROPS = create("fire_crafting/drops");

    public static final MutableComponent ALLOY_SMELTING_TITLE = create("alloy_smelting/title");
    public static final MutableComponent ENCHANTER_TITLE = create("enchanter/title");
    public static final MutableComponent SAG_MILL_TITLE = create("sag_mill/title");
    public static final MutableComponent SLICING_TITLE = create("slicing/title");
    public static final MutableComponent SOUL_BINDING_TITLE = create("soul_binding/title");
    public static final MutableComponent TANK_TITLE = create("tank/title");
    public static final MutableComponent SOUL_ENGINE_TITLE = create("soul_engine/title");
    public static final MutableComponent VAT_TITLE = create("vat/title");
    public static final MutableComponent WEATHER_CHANGE_TITLE = create("weather_change/title");

    private static MutableComponent create(String path) {
        return Component.translatable(Util.makeDescriptionId("jei", EnderIO.id(path)));
    }
}
