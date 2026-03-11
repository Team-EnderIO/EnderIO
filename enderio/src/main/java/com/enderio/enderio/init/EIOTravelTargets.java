package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.travel.TravelTarget;
import com.enderio.enderio.api.travel.TravelTargetSerializer;
import com.enderio.enderio.api.travel.TravelTargetType;
import com.enderio.enderio.content.enderface.EnderfaceTravelTarget;
import com.enderio.enderio.content.travel.travel_anchor.AnchorTravelTarget;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EIOTravelTargets {
    public static final DeferredRegister<TravelTargetType<?>> TRAVEL_TARGET_TYPES = DeferredRegister
            .create(EnderIORegistries.TRAVEL_TARGET_TYPES, EnderIO.MOD_ID);

    public static final DeferredRegister<TravelTargetSerializer<?>> TRAVEL_TARGET_SERIALIZERS = DeferredRegister
            .create(EnderIORegistries.TRAVEL_TARGET_SERIALIZERS, EnderIO.MOD_ID);

    public static final Supplier<TravelTargetType<AnchorTravelTarget>> TRAVEL_ANCHOR_TYPE = registerType(
            "travel_anchor");
    public static final Supplier<TravelTargetSerializer<AnchorTravelTarget>> TRAVEL_ANCHOR_SERIALIZER = TRAVEL_TARGET_SERIALIZERS
            .register("travel_anchor", AnchorTravelTarget.Serializer::new);

    public static final Supplier<TravelTargetType<EnderfaceTravelTarget>> ENDERFACE_TYPE = registerType("enderface");
    public static final Supplier<TravelTargetSerializer<EnderfaceTravelTarget>> ENDERFACE_SERIALIZER = TRAVEL_TARGET_SERIALIZERS
            .register("enderface", EnderfaceTravelTarget.Serializer::new);

    private static <T extends TravelTarget> Supplier<TravelTargetType<T>> registerType(String name) {
        return TRAVEL_TARGET_TYPES.register(name, TravelTargetType::simple);
    }

    public static void register(IEventBus bus) {
        TRAVEL_TARGET_TYPES.register(bus);
        TRAVEL_TARGET_SERIALIZERS.register(bus);
    }
}
