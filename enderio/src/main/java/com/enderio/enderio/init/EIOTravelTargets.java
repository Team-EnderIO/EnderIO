package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.poi.EnderPOI;
import com.enderio.enderio.api.poi.EnderPOISerializer;
import com.enderio.enderio.api.poi.EnderPOIType;
import com.enderio.enderio.content.enderface.EnderfaceTravelTarget;
import com.enderio.enderio.content.travel.travel_anchor.AnchorTravelTarget;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EIOTravelTargets {
    public static final DeferredRegister<EnderPOIType<?>> TRAVEL_TARGET_TYPES = DeferredRegister
            .create(EnderIORegistries.TRAVEL_TARGET_TYPES, EnderIO.MOD_ID);

    public static final DeferredRegister<EnderPOISerializer<?>> TRAVEL_TARGET_SERIALIZERS = DeferredRegister
            .create(EnderIORegistries.TRAVEL_TARGET_SERIALIZERS, EnderIO.MOD_ID);

    public static final Supplier<EnderPOIType<AnchorTravelTarget>> TRAVEL_ANCHOR_TYPE = registerType(
            "travel_anchor");
    public static final Supplier<EnderPOISerializer<AnchorTravelTarget>> TRAVEL_ANCHOR_SERIALIZER = TRAVEL_TARGET_SERIALIZERS
            .register("travel_anchor", AnchorTravelTarget.Serializer::new);

    public static final Supplier<EnderPOIType<EnderfaceTravelTarget>> ENDERFACE_TYPE = registerType("enderface");
    public static final Supplier<EnderPOISerializer<EnderfaceTravelTarget>> ENDERFACE_SERIALIZER = TRAVEL_TARGET_SERIALIZERS
            .register("enderface", EnderfaceTravelTarget.Serializer::new);

    private static <T extends EnderPOI> Supplier<EnderPOIType<T>> registerType(String name) {
        return TRAVEL_TARGET_TYPES.register(name, EnderPOIType::simple);
    }

    public static void register(IEventBus bus) {
        TRAVEL_TARGET_TYPES.register(bus);
        TRAVEL_TARGET_SERIALIZERS.register(bus);
    }
}
