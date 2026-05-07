package com.enderio.enderio.client.foundation.model.item;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jspecify.annotations.Nullable;

public class HasFluidItemProperty implements ConditionalItemModelProperty {

    public static final MapCodec<HasFluidItemProperty> MAP_CODEC = MapCodec.unit(new HasFluidItemProperty());

    @Override
    public boolean get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i,
        ItemDisplayContext itemDisplayContext) {
        var access = ItemAccess.forStack(itemStack);
        var fluidHandler = access.getCapability(Capabilities.Fluid.ITEM);
        return fluidHandler != null && !ResourceHandlerUtil.isEmpty(fluidHandler);
    }

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }
}
