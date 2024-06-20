package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.TieredConduit;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class is used to sort conduittypes for display. This is needed, so upgrading conduits doesn't require shifting of types, but just recalculating the current connection
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConduitTypeSorter {
    private static final List<ConduitType<?>> SORTED_TYPES = new ArrayList<>();

    @SubscribeEvent
    public static void afterRegistryFreeze(FMLCommonSetupEvent event) {
        ForgeRegistry<ConduitType<?>> registry = ConduitTypes.getRegistry();
        List<ResourceLocation> tieredTypes = new ArrayList<>();
        for (ConduitType<?> value : registry.getValues()) {
            if (value instanceof TieredConduit<?> tiered && !tieredTypes.contains(tiered.getType())) {
                tieredTypes.add(tiered.getType());
            }
        }
        tieredTypes.sort(ResourceLocation::compareTo);
        for (ResourceLocation tieredType : tieredTypes) {
            List<ConduitType<?>> typesInType = new ArrayList<>();
            for (ConduitType<?> type: registry.getValues()) {
                if (type instanceof TieredConduit<?> tiered && tiered.getType().equals(tieredType)) {
                    typesInType.add(type);
                }
            }
            typesInType.sort(Comparator.comparing(registry::getKey));
            SORTED_TYPES.addAll(typesInType);
        }
        List<ConduitType<?>> unadded = new ArrayList<>();
        for (ConduitType<?> type: registry.getValues()) {
            if (!(type instanceof TieredConduit)) {
                unadded.add(type);
            }
        }
        unadded.sort(Comparator.comparing(registry::getKey));
        SORTED_TYPES.addAll(unadded);
    }

    public static int getSortIndex(ConduitType<?> type) {
        return SORTED_TYPES.indexOf(type);
    }
}
