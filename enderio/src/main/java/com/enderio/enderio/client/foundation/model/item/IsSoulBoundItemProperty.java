package com.enderio.enderio.client.foundation.model.item;

import com.enderio.enderio.api.soul.SoulBoundUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class IsSoulBoundItemProperty implements ConditionalItemModelProperty {

    public static final MapCodec<IsSoulBoundItemProperty> MAP_CODEC = MapCodec.unit(new IsSoulBoundItemProperty());

    @Override
    public boolean get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i,
        ItemDisplayContext itemDisplayContext) {
        return SoulBoundUtils.isBound(itemStack);
    }

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }
}
