package com.enderio.enderio.content.capacitors;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class CapacitorLang {
    // Has 1 string arg.
    public static final MutableComponent CAPACITOR_TOOLTIP_BASE = create("modifier", "base", "tooltip");

    // Has 1 string arg.
    public static final MutableComponent CAPACITOR_TOOLTIP_ENERGY_CAPACITY = create("modifier",
        CapacitorModifier.ENERGY_CAPACITY.getSerializedName(), "tooltip");

    // Has 1 string arg.
    public static final MutableComponent CAPACITOR_TOOLTIP_ENERGY_USE = create("modifier",
        CapacitorModifier.ENERGY_USE.getSerializedName(), "tooltip");

    // Has 1 string arg.
    public static final MutableComponent CAPACITOR_TOOLTIP_FUEL_EFFICIENCY = create("modifier",
        CapacitorModifier.FUEL_EFFICIENCY.getSerializedName(), "tooltip");

    // Has 1 string arg.
    public static final MutableComponent CAPACITOR_TOOLTIP_BURNING_ENERGY_GENERATION = create("modifier",
        CapacitorModifier.BURNING_ENERGY_GENERATION.getSerializedName(), "tooltip");

    // region Loot Capacitor Flavour Text

    public static final MutableComponent LOOT_CAPACITOR_BASE_DUD = create("loot", "base", "dud");
    public static final MutableComponent LOOT_CAPACITOR_BASE_NORMAL = create("loot", "base", "normal");
    public static final MutableComponent LOOT_CAPACITOR_BASE_ENHANCED = create("loot", "base", "enhanced");
    public static final MutableComponent LOOT_CAPACITOR_BASE_WONDER = create("loot", "base", "wonder");
    public static final MutableComponent LOOT_CAPACITOR_BASE_IMPOSSIBLE = create("loot", "base", "impossible");

    public static final MutableComponent LOOT_CAPACITOR_TYPE_ENERGY_CAPACITY = create("loot", "type", "energy_capacity");
    public static final MutableComponent LOOT_CAPACITOR_TYPE_ENERGY_USE = create("loot", "type", "energy_use");
    public static final MutableComponent LOOT_CAPACITOR_TYPE_FUEL_EFFICIENCY = create("loot", "type", "fuel_efficiency");
    public static final MutableComponent LOOT_CAPACITOR_TYPE_BURNING_ENERGY_GENERATION = create("loot", "type", "burning_energy_generation");
    public static final MutableComponent LOOT_CAPACITOR_TYPE_UNKNOWN = create("loot", "type", "unknown", "Mystery");

    public static final MutableComponent LOOT_CAPACITOR_MODIFIER_FAILED = create("loot", "modifier", "failed");
    public static final MutableComponent LOOT_CAPACITOR_MODIFIER_SIMPLE = create("loot", "modifier", "simple");
    public static final MutableComponent LOOT_CAPACITOR_MODIFIER_NICE = create("loot", "modifier", "nice");
    public static final MutableComponent LOOT_CAPACITOR_MODIFIER_GOOD = create("loot", "modifier", "good");
    public static final MutableComponent LOOT_CAPACITOR_MODIFIER_ENHANCED = create("loot", "modifier", "enhanced");
    public static final MutableComponent LOOT_CAPACITOR_MODIFIER_PREMIUM = create("loot", "modifier", "premium");
    public static final MutableComponent LOOT_CAPACITOR_MODIFIER_INCREDIBLY = create("loot", "modifier", "incredibly");
    public static final MutableComponent LOOT_CAPACITOR_MODIFIER_UNSTABLE = create("loot", "modifier", "unstable");

    // endregion

    private static MutableComponent create(String... paths) {
        return Component.translatable(EnderIO.MOD_ID + ".capacitor." + String.join(".", paths));
    }
}
