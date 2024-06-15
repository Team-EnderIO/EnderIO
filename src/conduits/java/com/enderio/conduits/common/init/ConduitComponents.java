package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ConduitComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(EnderIO.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> EXTRACTION_SPEED_UPGRADE_TIER = DATA_COMPONENT_TYPES
        .registerComponentType("extraction_speed_upgrade_tier", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static void register(IEventBus bus) {
        DATA_COMPONENT_TYPES.register(bus);
    }
}
