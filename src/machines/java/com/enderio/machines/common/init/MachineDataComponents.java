package com.enderio.machines.common.init;

import com.enderio.EnderIO;
import com.enderio.api.attachment.CoordinateSelection;
import com.enderio.api.attachment.StoredEntityData;
import com.enderio.api.capacitor.CapacitorData;
import com.enderio.api.grindingball.GrindingBallData;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.base.common.component.BlockPaint;
import com.enderio.machines.common.io.IOConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MachineDataComponents {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, EnderIO.MODID);

    public static Supplier<DataComponentType<IOConfig>> IO_CONFIG = DATA_COMPONENT_TYPES.register("io_config",
        () -> DataComponentType.<IOConfig>builder().persistent(IOConfig.CODEC).networkSynchronized(IOConfig.STREAM_CODEC).build());

    public static Supplier<DataComponentType<RedstoneControl>> REDSTONE_CONTROL = DATA_COMPONENT_TYPES.register("redstone_control",
        () -> DataComponentType.<RedstoneControl>builder().persistent(RedstoneControl.CODEC).networkSynchronized(RedstoneControl.STREAM_CODEC).build());

    public static void register(IEventBus bus) {
        DATA_COMPONENT_TYPES.register(bus);
    }
}
