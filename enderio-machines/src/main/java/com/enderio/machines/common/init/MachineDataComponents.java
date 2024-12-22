package com.enderio.machines.common.init;

import com.enderio.EnderIOBase;
import com.enderio.base.api.grindingball.GrindingBallData;
import com.enderio.base.api.misc.RedstoneControl;
import com.enderio.machines.common.attachment.ActionRange;
import com.enderio.machines.common.blocks.alloy.AlloySmelterMode;
import com.enderio.machines.common.io.IOConfig;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MachineDataComponents {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister
            .create(Registries.DATA_COMPONENT_TYPE, EnderIOBase.REGISTRY_NAMESPACE);

    public static Supplier<DataComponentType<IOConfig>> IO_CONFIG = savedAndSynced("io_config", IOConfig.CODEC,
            IOConfig.STREAM_CODEC);

    public static Supplier<DataComponentType<RedstoneControl>> REDSTONE_CONTROL = savedAndSynced("redstone_control",
            RedstoneControl.CODEC, RedstoneControl.STREAM_CODEC);

    public static Supplier<DataComponentType<ActionRange>> ACTION_RANGE = savedAndSynced("action_range",
            ActionRange.CODEC, ActionRange.STREAM_CODEC);

    public static Supplier<DataComponentType<Boolean>> IS_RANGE_VISIBLE = savedAndSynced("is_range_visible", Codec.BOOL,
            ByteBufCodecs.BOOL);

    // region Machine-Specific

    public static Supplier<DataComponentType<AlloySmelterMode>> ALLOY_SMELTER_MODE = saved("alloy_smelter_mode",
            AlloySmelterMode.CODEC);
    public static Supplier<DataComponentType<Integer>> ALLOY_SMELTER_PROCESSED_INPUTS = saved(
            "alloy_smelter_processed_inputs", Codec.INT);

    public static Supplier<DataComponentType<Integer>> PRIMITIVE_ALLOY_SMELTER_BURN_TIME = saved(
            "primitive_alloy_smelter_burn_time", Codec.INT);
    public static Supplier<DataComponentType<Integer>> PRIMITIVE_ALLOY_SMELTER_BURN_DURATION = saved(
            "primitive_alloy_smelter_burn_duration", Codec.INT);

    // Could use the GRINDING_BALL component, but then you could use a sag mill as a
    // grinding ball :P
    public static Supplier<DataComponentType<GrindingBallData>> SAG_MILL_GRINDING_BALL = saved("sag_mill_grinding_ball",
            GrindingBallData.CODEC);
    public static Supplier<DataComponentType<Integer>> SAG_MILL_GRINDING_BALL_DAMAGE = saved(
            "sag_mill_grinding_ball_damage", Codec.INT);

    // endregion

    private static <T> Supplier<DataComponentType<T>> saved(String name, Codec<T> codec) {
        return DATA_COMPONENT_TYPES.register(name, () -> DataComponentType.<T>builder().persistent(codec).build());
    }

    private static <T> Supplier<DataComponentType<T>> savedAndSynced(String name, Codec<T> codec,
            StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return DATA_COMPONENT_TYPES.register(name,
                () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).build());
    }

    public static void register(IEventBus bus) {
        DATA_COMPONENT_TYPES.register(bus);
    }
}
