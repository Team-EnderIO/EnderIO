package com.enderio.conduits.common.conduit;

import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.EnderIOConduits;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class is used to sort conduit types for display.
 * This is needed, so upgrading conduits doesn't require shifting of types, but just recalculating the current connection
 */
@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ConduitSorter {
    private static final List<Holder<Conduit<?>>> SORTED_CONDUITS = new ArrayList<>();

    @SubscribeEvent
    public static void serverSortTypes(ServerStartedEvent event) {
        var conduitRegistry = event.getServer().registryAccess().registryOrThrow(EnderIOConduitsRegistries.Keys.CONDUIT);
        sortTypes(conduitRegistry);
    }

    @SubscribeEvent
    public static void clientSortTypes(ClientPlayerNetworkEvent.LoggingIn event) {
        var conduitRegistry = event.getPlayer().registryAccess().registryOrThrow(EnderIOConduitsRegistries.Keys.CONDUIT);
        sortTypes(conduitRegistry);
    }

    private static void sortTypes(Registry<Conduit<?>> registry) {
        SORTED_CONDUITS.clear();

        // TODO...
        /*List<ResourceLocation> tieredTypes = new ArrayList<>();
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
        }*/

        List<Holder<Conduit<?>>> unadded = new ArrayList<>();
        for (Holder<Conduit<?>> type : registry.holders().toList()) {
            //if (!(type instanceof TieredConduit)) {
            unadded.add(type);
            //}
        }

        unadded.sort(Comparator.comparing(Holder::getRegisteredName));
        SORTED_CONDUITS.addAll(unadded);
    }

    public static int getSortIndex(Holder<Conduit<?>> conduit) {
        return SORTED_CONDUITS.indexOf(conduit);
    }
}
