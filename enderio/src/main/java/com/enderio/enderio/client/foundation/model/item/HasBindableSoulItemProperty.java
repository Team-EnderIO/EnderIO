package com.enderio.enderio.client.foundation.model.item;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jspecify.annotations.Nullable;

public class HasBindableSoulItemProperty implements ConditionalItemModelProperty {

    public static final MapCodec<HasBindableSoulItemProperty> MAP_CODEC = MapCodec.unit(new HasBindableSoulItemProperty());

    @Override
    public boolean get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i,
        ItemDisplayContext itemDisplayContext) {
        if (itemStack.isEmpty()) {
            return false;
        }
        var source = ItemAccess.forStack(itemStack).getCapability(EnderIOCapabilities.BINDABLE_SOUL_SOURCE);
        return source != null && source.isSoulAvailable();
    }

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }
}
