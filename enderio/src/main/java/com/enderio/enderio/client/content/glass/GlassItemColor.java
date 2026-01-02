package com.enderio.enderio.client.content.glass;

import com.enderio.enderio.init.EIOBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class GlassItemColor implements ItemTintSource {
    public static final MapCodec<GlassItemColor> CODEC = MapCodec.unit(new GlassItemColor());

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        for (var glassBlocks : EIOBlocks.GLASS_BLOCKS.values()) {
            for (var entry : glassBlocks.COLORS.entrySet()) {
                if (stack.is(entry.getValue().asItem())) {
                    return ARGB.opaque(entry.getKey().getMapColor().col);
                }
            }
        }
        return 0xFFFFFFFF;
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}
