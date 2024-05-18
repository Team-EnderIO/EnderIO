package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.TieredConduit;
import com.enderio.api.registry.EnderIORegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class is used to sort conduit types for display.
 * This is needed, so upgrading conduits doesn't require shifting of types, but just recalculating the current connection
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ConduitTypeSorter {
    private static final List<ConduitType<?>> SORTED_TYPES = new ArrayList<>();

    @SubscribeEvent
    public static void afterRegistryFreeze(FMLCommonSetupEvent event) {
        List<ResourceLocation> tieredTypes = new ArrayList<>();
        for (ConduitType<?> value : EnderIORegistries.CONDUIT_TYPES) {
            if (value instanceof TieredConduit<?> tiered && !tieredTypes.contains(tiered.getType())) {
                tieredTypes.add(tiered.getType());
            }
        }

        tieredTypes.sort(ResourceLocation::compareTo);
        for (ResourceLocation tieredType : tieredTypes) {
            List<ConduitType<?>> typesInType = new ArrayList<>();
            for (ConduitType<?> type: EnderIORegistries.CONDUIT_TYPES) {
                if (type instanceof TieredConduit<?> tiered && tiered.getType().equals(tieredType)) {
                    typesInType.add(type);
                }
            }
            typesInType.sort(Comparator.comparing(EnderIORegistries.CONDUIT_TYPES::getKey));
            SORTED_TYPES.addAll(typesInType);
        }

        List<ConduitType<?>> unadded = new ArrayList<>();
        for (ConduitType<?> type: EnderIORegistries.CONDUIT_TYPES) {
            if (!(type instanceof TieredConduit)) {
                unadded.add(type);
            }
        }

        unadded.sort(Comparator.comparing(EnderIORegistries.CONDUIT_TYPES::getKey));
        SORTED_TYPES.addAll(unadded);
    }

    public static int getSortIndex(ConduitType<?> type) {
        return SORTED_TYPES.indexOf(type);
    }
}
