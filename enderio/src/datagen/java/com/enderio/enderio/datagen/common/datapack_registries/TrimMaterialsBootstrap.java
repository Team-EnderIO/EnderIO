package com.enderio.enderio.datagen.common.datapack_registries;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.init.EIOTrimMaterials;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

public class TrimMaterialsBootstrap {
    public static void bootstrap(BootstrapContext<TrimMaterial> context) {
        register(context, EIOTrimMaterials.CONDUCTIVE_ALLOY, 0xDA8A78);
        register(context, EIOTrimMaterials.ENERGETIC_ALLOY, 0xCD7D3A);
        register(context, EIOTrimMaterials.VIBRANT_ALLOY, 0xC1DD66);
        register(context, EIOTrimMaterials.REDSTONE_ALLOY, 0xF04F43);
        register(context, EIOTrimMaterials.PULSATING_ALLOY, 0x5FD899);
        register(context, EIOTrimMaterials.DARK_STEEL, 0x2E2D2E);
        register(context, EIOTrimMaterials.SOULARIUM, 0x584C3A);
        register(context, EIOTrimMaterials.END_STEEL, 0xCED191);
    }

    private static void register(BootstrapContext<TrimMaterial> context, ResourceKey<TrimMaterial> key, int color) {
        // Include the mod id in the palette path: NeoForge's item trim sprites omit the palette namespace.
        var palette = EnderIO.id("trim/enderio_" + key.identifier().getPath());
        var description = Component.translatable(Util.makeDescriptionId("trim_material", key.identifier()))
            .withStyle(Style.EMPTY.withColor(color));
        context.register(key, new TrimMaterial(palette, description));
    }
}
