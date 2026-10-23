package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

public class EIOTrimMaterials {
    public static final ResourceKey<TrimMaterial> CONDUCTIVE_ALLOY = key("conductive_alloy");
    public static final ResourceKey<TrimMaterial> ENERGETIC_ALLOY = key("energetic_alloy");
    public static final ResourceKey<TrimMaterial> VIBRANT_ALLOY = key("vibrant_alloy");
    public static final ResourceKey<TrimMaterial> REDSTONE_ALLOY = key("redstone_alloy");
    public static final ResourceKey<TrimMaterial> PULSATING_ALLOY = key("pulsating_alloy");
    public static final ResourceKey<TrimMaterial> DARK_STEEL = key("dark_steel");
    public static final ResourceKey<TrimMaterial> SOULARIUM = key("soularium");
    public static final ResourceKey<TrimMaterial> END_STEEL = key("end_steel");

    private static ResourceKey<TrimMaterial> key(String name) {
        return ResourceKey.create(Registries.TRIM_MATERIAL, EnderIO.id(name));
    }
}
